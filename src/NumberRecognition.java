import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_ml.*;
import static org.bytedeco.javacpp.opencv_core.*;

import java.awt.image.BufferedImage;
import java.io.*;

import org.bytedeco.javacpp.opencv_core.*;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.*;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.cvShowImage;
import static org.bytedeco.javacpp.opencv_highgui.cvWaitKey;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;

/**
 * Created by Jayden on 9/1/2015.
 */
public class NumberRecognition {
    private CvKNearest knn;

    public NumberRecognition() {
        knn = new CvKNearest();
    }
    public double classify(String s) throws Exception{
        return classify(s,2);
    }
    public double classify(IplImage image) throws Exception{
        return classify(image,2);
    }
    public double classify(String s,int k) throws Exception{
        IplImage image = cvLoadImage(s, CV_32FC1);
        if ( image == null ) throw new Exception(s+" not a recognised image");
        return classify(image,k);
    }
    public double classify(IplImage image, int k) throws Exception{
        if ( knn.get_sample_count() != 0 ) {
            //convert image to 28x28

            IplImage grey = cvCreateImage(cvGetSize(image), 8, 1);
            cvCvtColor(image, grey, CV_BGR2GRAY);
            Mat sample2 = new Mat(grey.asCvMat());

            sample2.convertTo(sample2, CV_32FC1, 1.0 / 255.0, 0);
            sample2 = sample2.reshape(1, sample2.cols() * sample2.rows());
            transpose(sample2, sample2);
            //cvShowImage("name",sample2.asCvMat());
            //cvWaitKey();
            return knn.find_nearest(sample2.asCvMat(), k);
        }else {
            throw new Exception("Network hasn't been trained");
        }
    }
    public void trainWithImage(String dataS,String resS) {
        Mat data = imread(dataS);
        Mat res = imread(resS);

        cvtColor(data,data,CV_BGR2GRAY);
        cvtColor(res,res,CV_BGR2GRAY);
        data.convertTo(data, CV_32FC1);
        res.convertTo(res, CV_32FC1);

        knn.train(data, res);
    }
    public void trainWithMnistFiles() {
        FileInputStream inImage = null;
        FileInputStream inLabel = null;
        String currentDir = new File(".").getAbsolutePath();
        System.out.println ("Current directory: " + currentDir);
        String inputImagePath = "train-images.idx3-ubyte";
        String inputLabelPath = "train-labels.idx1-ubyte";

        String outputPath = currentDir+"/MNIST_Database_ARGB/";

        int[] hashMap = new int[10];
        try {

            inImage = new FileInputStream(inputImagePath);
            inLabel = new FileInputStream(inputLabelPath);

            int magicNumberImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfRows  = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfColumns = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());

            int magicNumberLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());
            int numberOfLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());

            int numberOfPixels = numberOfRows * numberOfColumns;
            double[] imgPixels = new double[numberOfPixels];
            int numImages = 3000;
            CvMat matStorage = CvMat.create(numberOfImages ,numberOfColumns*numberOfRows,CV_32FC1);
            CvMat results = CvMat.create(numberOfImages,1,CV_32FC1);

            for(int i=0; i < numberOfImages; i++) {
                for(int p = 0; p < numberOfPixels; p++) {
                    double gray = 255 - inImage.read();
                    //imgPixels[p] = 0xFF000000 | (gray<<16) | (gray<<8) | gray;
                    imgPixels[p] = gray / 255.0;
                    //System.out.println(gray);
                }

                CvMat toShow = CvMat.create(28,28,CV_32FC1);
                toShow.put(imgPixels);
                cvWaitKey();
                double label = inLabel.read();
                matStorage.put(i*numberOfColumns*numberOfRows,imgPixels);
                results.put(i,label);

                //System.out.println(label);
                //cvGetMat(imageToShow, matStorage);
                hashMap[(int)label]++;
                if(i % 100 == 0) {
                    System.out.println("Number of images extracted: " + i + ", label: " + hashMap[(int) label]);
                    //System.out.println("dim: " + matStorage.rows() + " " +matStorage.cols());
                    //cvShowImage("image",matStorage);
                    //cvShowImage("result: ", toShow);
                    //cvWaitKey();

                }


                //File outputfile = new File(outputPath + label + "_0" + hashMap[label] + ".png");

                //ImageIO.write(image, "png", outputfile);
            }
            knn.train(matStorage, results);
            //cvShowImage("hmm:",results);
            //cvWaitKey();

            Mat toSaveData = new Mat(matStorage);
            toSaveData.convertTo(toSaveData,CV_8UC4);
            imwrite("mnistData.bmp", toSaveData);

            Mat toSaveRes = new Mat(results);
            toSaveRes.convertTo(toSaveRes, CV_8UC4);
            imwrite("mnistRes.bmp",toSaveRes);

            //imwrite("mnistData.png",new Mat(matStorage,true));
            //imwrite("mnistRes.png",new Mat(results,true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inImage != null) {
                try {
                    inImage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inLabel != null) {
                try {
                    inLabel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
