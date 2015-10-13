//import Neural_Network_Recursive.*;

import Neural_Network.NeuralNetwork;
import Neural_Network.NeuralNode;
import Neural_Network.NeuralUtils;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_features2d;
import org.bytedeco.javacpp.opencv_features2d.*;
import sun.misc.FloatingDecimal;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.Arrays;

import static Neural_Network.NeuralUtils.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;



/**
 * Created by Jayden on 7/31/2015.
 */
public class SetupNeural {
    public static double[][] inputRead;
    public static double[][] expectedRead;
    public static void main(String[] args) {
        double[][] inputSingle = {{0},{1}};
        double[][] expectedInverter = {{1},{0}};
        double[][] expectedConstant = {{0},{1}};
        double[][] inputLogicGates = {{0,0},{1,0},{0,1},{1,1}};
        double[][] expectedAND = {{0},{0},{0},{1}};
        double[][] expectedOR = {{0},{1},{1},{1}};
        double[][] expectedNOR = {{1},{0},{0},{0}};
        double[][] expectedNAND = {{1},{1},{1},{0}};
        double[][] expectedXOR = {{0},{1},{1},{0}};
        double[][] expectedTRUE = {{1},{1},{1},{1}};
        double[][] expectedFALSE = {{0},{0},{0},{0}};
        double[][] expectedHalfAdder = {{0,0},{0,1},{0,1},{1,1}};

        double[][] inputThree = {{0,0,0},{1,0,0},{0,1,1},{1,1,0}};
        double[][] expectedRandom = {{0.1},{0.2},{0.3},{0.4}};


        double[][] inputX = {{-50},{35.3},{90},{49.82}};
        double[][] expectedY = {{2500},{1246},{8100},{2482}};
        double[][] nottestedX = {{0},{2},{4},{8},{16}};
        double[][] nottestedY = {{0},{4},{16},{64},{256}};
        double[] notTestedRead = {0.4189,0.3566,0.0934,0.735};//0.25
        double[] notTestedRead2 = {0.3738,0.194,0.0414,0.5766};//0.08152
        double[] notTestedRead3 = {0.3869,0.3257,0.0799,0.778};//0.33375

        /*
        double[][] input =inputRead;
        double[][] expected = expectedHalfAdder;
        createTests(readFile("dataSpeed.csv"),",",5);
        printComparison(inputRead, expectedRead, null);
        List<List<Double>> inputList = d2List2D(inputRead);
        List<List<Double>> expectedList = d2List2D(expectedRead);
       // List<List<Double>> inputList = d2List2D(input);
        //List<List<Double>> expectedList = d2List2D(expected);
        //oneNeuron(input,expected,100,1000);
        //multipleNeuron(input, expected, 100, 1000,1);
        NeuralNetwork network = new NeuralNetwork(inputList,expectedList);
        network.trainNetworkOnAllSets();
        if ( network.getOutput() == null) System.out.println("Error occured: network produced no output");
        printComparison(inputRead,TwoDListTod(network.getOutput()),expectedRead);
        printList(Arrays.asList(d2D(notTestedRead)));
        System.out.println(network.runNetwork(Arrays.asList(d2D(notTestedRead))));
        printList(Arrays.asList(d2D(notTestedRead2)));
        System.out.println(network.runNetwork(Arrays.asList(d2D(notTestedRead2))));
        printList(Arrays.asList(d2D(notTestedRead3)));
        System.out.println(network.runNetwork(Arrays.asList(d2D(notTestedRead3))));

        //printComparison(input,TwoDListTod(network.getOutput()),expected);

        /*double[][] output = new double[nottestedX.length][nottestedY[0].length];
        for (int i = 0 ;i < nottestedX.length;i++) {
            output[i] = network.runNetwork(nottestedX[i]);
        }
        printComparison(nottestedX,output,nottestedY);*/
        //printComparison(inputRead,null,null);
        //printList(Arrays.asList(d2D(expectedRead)));*/
        /* finding system paths
        String[] paths = System.getProperty("java.library.path").split("\\;");
        for (String path : paths) {
            System.out.println(path);
        }
        /* above is neural network stuff */
        /*
        NumberRecognition n = new NumberRecognition();
        try {
            n.trainWithImage("mnistData.bmp","mnistRes.bmp");
            System.out.println(n.classify("five.png"));
        }catch (Exception e){
            e.printStackTrace();
        }
*/

        IplImage image = cvLoadImage("graph5.png",CV_32FC1);
        IplImage scan = cvLoadImage("scan0006.tif",CV_32FC1);


        //IplImage src = convertToSize(scan,1024,498);
        IplImage src = convertToSize(scan,512);

        cvShowImage("hi",src);
        cvWaitKey();
        //IplImage src = scan;
        houghLookDown imageAnalyse = new houghLookDown(src,"2,2,0.0..2,0.0...");
        //Mat vcSrc = imread("35.png",CV_8UC1);

        //Blobbing b = new Blobbing(new Mat(src));
        //System.out.println(detectNumber(src));
    }
    public static double detectNumber(IplImage newImage){
        try {
            cvShowImage("hi", newImage);
            cvWaitKey();
        } catch (Exception e) {
            System.out.println(newImage.width() + " " + newImage.height());
        }
        IplImage srcImage = cvCreateImage(cvGetSize(newImage),8,3);
        cvCopy(newImage,srcImage);

        NumberRecognition n = new NumberRecognition();
        try {
            n.trainWithImage("mnistData.bmp", "mnistRes.bmp");
        }catch (Exception e){
            e.printStackTrace();
        }

        IplImage resultImage = cvCloneImage(srcImage);
        IplImage grey = cvCreateImage(cvGetSize(srcImage), 8, 1);
        cvCvtColor(srcImage,grey,CV_BGR2GRAY);
        IplImage masked = cvCreateImage(cvGetSize(srcImage), 8, 1);
        int lowThresh = 90;
        cvCanny(grey,masked,lowThresh,lowThresh*3,3);

        CvMemStorage mem = CvMemStorage.create();
        CvSeq contours = new CvSeq();
        CvSeq ptr = new CvSeq();
        CvSeq ptr2 = new CvSeq();
        CvRect boundbox;
        CvRect boundbox2;
        List<CvRect> correctBoxes = new ArrayList<CvRect>();
        cvFindContours(masked, mem, contours, Loader.sizeof(CvContour.class) , CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE, cvPoint(0,0));
        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
            boolean isCorrect = true;
            boundbox = cvBoundingRect(ptr,0);
            for (ptr2 = contours; ptr2 != null; ptr2 = ptr2.h_next()) {
                boundbox2 = cvBoundingRect(ptr2,0);
                int bx2 = boundbox.x()+boundbox.width();
                int b2x2 = boundbox2.x()+boundbox2.width();
                int by2 = boundbox.y()+boundbox.height();
                int b2y2 = boundbox2.y()+boundbox2.height();
                if ( boundbox.x() > boundbox2.x() && bx2 < b2x2 &&
                        boundbox.y() > boundbox2.y() && by2 < b2y2) {
                    isCorrect = false;
                    break;
                }
            }
            if ( isCorrect ) correctBoxes.add(boundbox);
        }
        List<Double[]> numberLoc = new ArrayList<Double[]>();

        for ( CvRect boundBox : correctBoxes ) {
            Double[] xAndVal = new Double[2];
            if ( ((boundBox.x() <= 2 ||  (masked.width() - (boundBox.x() + boundBox.width())) <= 2)
            && boundBox.height() <= 0.3*masked.height()) ||
                    ((boundBox.y() <= 2 || (masked.height()-(boundBox.y()+boundBox.height())) <= 2)
                    &&  boundBox.width() <= 0.3*masked.width())) continue;
            int calcx = masked.width() - (boundBox.x() + boundBox.width());
            System.out.println("x and y: "+masked.width()+" " +(calcx)+ " " +masked.height()+" " +(boundBox.y() + boundBox.height()));
            /*cvRectangle(resultImage, cvPoint(boundBox.x(), boundBox.y()),
                    cvPoint(boundBox.x() + boundBox.width(), boundBox.y() + boundBox.height()),
                    cvScalar(255, 255, 0, 0), 1, 0, 0);*/
            cvDrawRect(newImage,cvPoint(boundBox.x(), boundBox.y()),
                    cvPoint(boundBox.x() + boundBox.width(), boundBox.y() + boundBox.height()),
                    CvScalar.CYAN, 1, CV_AA, 0);
            xAndVal[0] = (double)boundBox.x();
            cvSetImageROI(srcImage, boundBox);
            try {
                //scale image to 28x28 and only send in non threshed
                //System.out.println("width and height: " + boundBox.width() + " " + boundBox.height());
                cvErode(srcImage,srcImage);
                IplImage a = addSpaceToSides(srcImage, boundBox);
                a = convertToSize(a,28,28);
                xAndVal[1] = n.classify(a);
                System.out.println(n.classify(a));
                //cvShowImage("28,28", a);
                //cvWaitKey();
            } catch (Exception e) {
                e.printStackTrace();
            }
            cvResetImageROI(srcImage);

            numberLoc.add(xAndVal);
        }
        //cvShowImage("this image",resultImage);
        //cvWaitKey();
        Collections.sort(numberLoc, new Comparator<Double[]>() {
            @Override
            public int compare(Double[] o1, Double[] o2) {
                return Double.compare(o1[0],o2[0]);
            }
        });
        double value = 0;
        for (int i=0; i < numberLoc.size();i++) {
            try {
                value = value * 10 + numberLoc.get(i)[1];
            }catch(Exception e) {}
            //System.out.print(numberLoc.get(i)[1]+" ");
        }
        return value;
    }
    public static IplImage addSpaceToSides(IplImage img,CvRect roiRect) {
        IplImage img2 = cvCloneImage(img);
        //darken image
        Mat darker = new Mat(img2);
        darker.convertTo(darker, -1, 1.0f, -60);
        Mat invertcolormatrix= new Mat(darker.rows(),darker.cols(), darker.type(), new Scalar(255,255,255,255));

        //subtract(invertcolormatrix, darker, darker);
        threshold(darker, darker, 180, 255, CV_THRESH_BINARY);
        //subtract(invertcolormatrix, darker, darker);

        CvSize size = new CvSize();
        img2 = darker.asIplImage();

        int spacing = 3;
        size.width(roiRect.height()+spacing);
        size.height(roiRect.height()+spacing);
        IplImage image = cvCreateImage(size,img2.depth(),img2.nChannels());
        //cvShowImage("name",img2);
        //cvWaitKey();
        /*
        if ( roiRect.height() <= roiRect.width()) {
            System.out.println("doesn't need space added");
            return img2;
        }*/

        cvSet(image,cvScalar(255,255,255,0));
        CvRect rect = new CvRect();
        //todo make this method space the height if sides smaller
        rect.x((roiRect.height() - roiRect.width()) / 2 + spacing/2);
        rect.y(spacing/2);
        rect.width(roiRect.width());
        rect.height(roiRect.height());
        cvSetImageROI(image, rect);
        try {
            cvCopy(img2, image);
        } catch (Exception e) {
            cvResetImageROI(image);
            return img2;
        }
        cvResetImageROI(image);

        int lowThresh = 90;
        //cvCanny(darker.asIplImage(),darker.asIplImage(),lowThresh,lowThresh*3,3);
        //cvShowImage("name",image);
        //cvWaitKey();
        return image;
    }
    public static IplImage convertToSize(IplImage img,int height) {
        int maxHW = img.height();
        double ratio = (double)height /maxHW;
        return convertToSize(img,(int)Math.round(img.width()*ratio),(int)Math.round(img.height()*ratio));
    }
    public static IplImage convertToSize(IplImage img,int width, int height) {
        CvSize size = img.cvSize();
        size.height(height);
        size.width(width);
        IplImage newImage = cvCreateImage(size,8,3);
        cvResize(img,newImage,CV_INTER_CUBIC);
        return newImage;
    }
    public static void createTests(String testFile) {
        String[] separateTests = testFile.split("\n");
        inputRead = new double[separateTests.length][];
        expectedRead = new double[separateTests.length][];
        int i = 0;
        for (String tests : separateTests) {
            String[] inputAndExpected = tests.split("\t");
            inputRead[i] = new double[inputAndExpected[0].length()];
            expectedRead[i] = new double[1];
            int j = 0;

            for (char c : inputAndExpected[0].toCharArray()) {
                inputRead[i][j] = Character.getNumericValue(c);
                j++;
            }
            expectedRead[i][0] = Double.parseDouble(inputAndExpected[1]);
            i++;
        }

    }
    public static void createTests(String testFile, String delimiter,int numToUse) {
        String[] separateTests = testFile.split("\n");
        inputRead = new double[separateTests.length][];
        expectedRead = new double[separateTests.length][];
        int i = 0;
        for (String tests : separateTests) {
            String[] inputAndExpected = tests.split(",");
            inputRead[i] = new double[numToUse - 1];
            expectedRead[i] = new double[1];
            for (int j=0;j < numToUse -1;j++) {;
                inputRead[i][j] = Double.parseDouble(inputAndExpected[j]);
            }
            expectedRead[i][0] = Double.parseDouble(inputAndExpected[numToUse-1]);
            i++;
        }
    }
    public static String readFile(String filename)
    {
        try {
            String content = null;
            File file = new File(filename); //for ex foo.txt
            FileReader reader = null;
            try {
                reader = new FileReader(file);
                char[] chars = new char[(int) file.length()];
                reader.read(chars);
                content = new String(chars);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    reader.close();
                }
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    //hidden layer + out layer
    public static void multipleNeuron(double[][] inputSet, double[][] expectedSet,int learningTime,int numReviews,int numHiddenNeurons) {
        //setup neuron with correct number of inputs "neurons"
        int numInputs = inputSet[0].length;
        int setLength = inputSet.length;

        //make neurons
        NeuralNode[] J = new NeuralNode[numHiddenNeurons];
        for (int i=0;i< numHiddenNeurons;i++) {
            J[i] = new NeuralNode(numInputs);
        }
        NeuralNode K1 = new NeuralNode(numHiddenNeurons);

        //train
        for (int review = 0;review<numReviews;review++) {
            for (int inputs = 0; inputs < setLength; inputs++) {
                for (int iteration = 0; iteration < learningTime; iteration++) {
                    //run network forward
                    for (int i=0;i<numHiddenNeurons;i++) {
                        J[i].setInput(Arrays.asList(d2D(inputSet[inputs])));
                    }
                    K1.setInput(Arrays.asList(J));

                    //compute rate for each output node
                    K1.calcRateChange(expectedSet[inputs][0]);

                    //compute rate for each hidden node
                    for (int i=0;i<numHiddenNeurons;i++) {
                        J[i].calcRateChange(i,Arrays.asList(K1));
                    }


                    //update weights and biases
                    K1.updateWeightBios(Arrays.asList(J));
                    for (int i=0;i<numHiddenNeurons;i++) {
                        J[i].updateWeightBios(Arrays.asList(d2D(inputSet[inputs])));
                    }
                }
            }
        }
        double[][] output = new double[inputSet.length][expectedSet[0].length];

        for (int inputs =0;inputs <setLength;inputs++) {
            for (int i=0;i<numHiddenNeurons;i++) {
                J[i].setInput(Arrays.asList(d2D(inputSet[inputs])));
            }
            K1.setInput(Arrays.asList(J));
            for (int input = 0; input < numInputs;input++) {
                output[inputs][0] = K1.getOutput();
            }
        }
        printComparison(inputSet,output,expectedSet);
        System.out.println();
    }
    public static void oneNeuron(double[][] inputSet, double[][] expectedSet,int learningTime,int numReviews) {
        //setup neuron with correct number of inputs "neurons"
        int numInputs = inputSet[0].length;
        int setLength = inputSet.length;
        NeuralNode J1 = new NeuralNode(numInputs);
        for (int review = 0;review<numReviews;review++) {
            for (int inputs = 0; inputs < setLength; inputs++) {
                for (int iteration = 0; iteration < learningTime; iteration++) {
                    //run network forward
                    J1.setInput(Arrays.asList(d2D(inputSet[inputs])));

                    //compute rate for each output node
                    J1.calcRateChange(expectedSet[inputs][0]);

                    //compute rate for each hidden node
                    //no hidden nodes

                    //update weights and biases
                    J1.updateWeightBios(Arrays.asList(d2D(inputSet[inputs])));
                }
            }
        }

        //store results in an 2d array
        double[][] output = new double[inputSet.length][expectedSet[0].length];
        for (int inputs =0;inputs <setLength;inputs++) {
            J1.setInput(Arrays.asList(d2D(inputSet[inputs])));
            for (int input = 0; input < numInputs;input++) {
                output[inputs][0] = J1.getOutput();
            }
        }

        printComparison(inputSet,output,expectedSet);

        System.out.println();
    }


}






















/*  SETUP FOR RECURSIVE NEURAL
try {
            Neural_Link I1Link = new Neural_Link(0.3);
            Neural_Link I2Link = new Neural_Link(0.5);
            Neural_Link J1Link = new Neural_Link(0.5);

            Neural_InputNode I1 = new Neural_InputNode(0);
            Neural_InputNode I2 = new Neural_InputNode(1);

            Neural_HiddenNode J1 = new Neural_HiddenNode(I1Link,I2Link,null,J1Link);

            Neural_OutputNode K1 = new Neural_OutputNode(J1Link);

            System.out.println(J1.getOutput());


        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
 */


/*

Bullshit read file
        try {
            InputStream is = new FileInputStream("imageData1D.txt");
            int size = is.available();
            /*
            List<List<Double>> testSets = new ArrayList<List<Double>>();
            List<Double> expectedSets = new ArrayList<Double>();
            int part = 0;
            int setNow = 0;
            testSets.add(new ArrayList<Double>());
            String expect = "";
            char read;
            int state = 0;
            for(int i=0; i< size; i++) {
                read = (char)is.read();
                if (state == 0) {
                    if (read == '\t') {
                        state=1;
                        part=0;
                    } else {
                        double d = (double) Character.getNumericValue(read);
                        testSets.get(setNow).add(part, d);
                        part+=1;
                    }
                }
                if (state == 1) {
                    if (read == '\r') ;
                    else if (read == '\n') {
                        //System.out.println(expect);
                        double d = (double) Float.parseFloat(expect);
                        expectedSets.add(setNow,d);
                        state=0;
                        setNow+=1;
                        testSets.add(new ArrayList<Double>());
                    } else {
                        expect += read;
                    }
                }
                System.out.print((char) is.read());
            }
            //System.out.println(testSets);
            //System.out.println(expectedSets);

char c;
for (int i=0;i<size;i++) {
        c = (char)is.available();
        is.
        System.out.print(c);
        }
        is.close();
        } catch (Exception e) {
        e.printStackTrace();
        }

 */