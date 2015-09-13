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
    CvKNearest knn;

    public NumberRecognition() {

    }
    public void trainKNN() {
        int a = 28*28;
        int b = 28*28;
        CvMat matStorage = CvMat.create(a,b,CV_32FC1);
        double[] giveMe = new double[b];
        for (int i=0; i < a ; i++ ) {
            for (int j=0; j < b; j++ ) {
                giveMe[j] = randDouble();
                //System.out.println(giveMe[j]);
            }
            matStorage.put(i*28*28,giveMe);
            //System.out.println(i*28*28);
            //cvShowImage("wooo: ",matStorage);
            //cvWaitKey();
        }

        knn = new CvKNearest();
        //trainWithMnistFiles();
        Mat data = imread("minstData.png");
        Mat res = imread("mnistRes.png");
        data.convertTo(data,CV_32FC1,1.0 / 255.0, 0);
        data = data.reshape(1,data.cols()*data.rows());
        transpose(data, data);
        res = res.reshape(1,res.cols()*res.rows());
        transpose(res, res);
        res.convertTo(res,CV_32FC1,1.0 / 255.0, 0);
        knn.train(data.asCvMat(),res.asCvMat());
        //trainWithMnistFiles(3000,false);
        System.out.println(knn.get_sample_count() + " " + knn.get_var_count() + " " + knn.get_max_k());

        IplImage image = cvLoadImage("MNIST_Database_ARGB/5_02541.png",CV_32FC1);
        IplImage grey = cvCreateImage(cvGetSize(image), 8, 1);
        cvCvtColor(image, grey, CV_BGR2GRAY);
        Mat sample2 = new Mat(grey.asCvMat());

        sample2.convertTo(sample2, CV_32FC1, 1.0 / 255.0, 0);

        sample2 = sample2.reshape(1,sample2.cols()*sample2.rows());
        transpose(sample2, sample2);
        System.out.println(knn.find_nearest(sample2.asCvMat(), knn.get_max_k()));

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
            CvMat matStorage = CvMat.create(3000 ,numberOfColumns*numberOfRows,CV_32FC1);
            CvMat results = CvMat.create(3000,1,CV_32FC1);

            for(int i=0; i < 3000; i++) {
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
            cvShowImage("All data",matStorage);
            cvWaitKey();
            imwrite("mnistData.png",new Mat(matStorage,true));
            imwrite("mnistRes.png",new Mat(results,true));
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
