//import Neural_Network_Recursive.*;

import Neural_Network.NeuralNetwork;
import Neural_Network.NeuralNode;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_features2d;
import org.bytedeco.javacpp.opencv_features2d.*;
import sun.misc.FloatingDecimal;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        /* javacv stuff */

        NumberRecognition n = new NumberRecognition();
        /*try {
            n.trainWithImage("mnistData.bmp","mnistRes.bmp");
            System.out.println(n.classify("one.png"));
        }catch (Exception e){
            e.printStackTrace();
        }*/

        IplImage image = cvLoadImage("35.png");
        IplImage scan = cvLoadImage("scan0007.tif");
        //IplImage src = cvLoadImage("line2.png");
        //cvResize(image,src);
        IplImage imageToUse = image;
        int maxHW = imageToUse.height();
        double ratio = (double)256/maxHW;
        CvSize size = imageToUse.cvSize();
        size.height((int)Math.round(imageToUse.height() * ratio));
        size.width((int)Math.round(imageToUse.width() * ratio));
        System.out.println(size.height() + " " + size.width());

        IplImage src = cvCreateImage(size,8,3);
        //cvResize(imageToUse,src);
        src = imageToUse;

        /*Hough imageAnalyse = new Hough(src);
        imageAnalyse.getCircles();
        imageAnalyse.getLines();
        imageAnalyse.showImage();

*/

        SimpleBlobDetector.Params params = new SimpleBlobDetector.Params();
        // Change thresholds
        params.minThreshold(50);
        params.maxThreshold(500);
// Filter by Circularity
        params.filterByCircularity(false);
        //params.minCircularity((float)0.1);

// Filter by Convexity
        params.filterByConvexity(false);
        //params.minConvexity((float)0.87);

// Filter by Inertia
        params.filterByInertia(false);
        //params.minInertiaRatio((float)0.01);
        Mat blob = new Mat(src);
        KeyPoint kp = new KeyPoint();
        blur(blob, blob, new Size(6, 6), new Point(-1,-1),BORDER_DEFAULT);
        threshold(blob,blob,230,255,CV_THRESH_BINARY);

// Filter by Area.
        params.filterByArea(false);
        params.minArea(100);
        SimpleBlobDetector det = new SimpleBlobDetector(params);

        det.detect(blob, kp);


        imshow("blob: ", blob);
        cvWaitKey();

        opencv_features2d.drawKeypoints(blob, kp, blob, new Scalar(0, 255), DrawMatchesFlags.DRAW_RICH_KEYPOINTS);
        imshow("blob: ",blob);
        cvWaitKey();
        //houghLookDown imageAnalyse = new houghLookDown(src);

        /*
        IplImage grey = cvCreateImage(cvGetSize(src), 8, 1);
        cvCvtColor(src, grey, CV_BGR2GRAY);
        IplImage masked = cvCreateImage(cvGetSize(src), 8, 1);
        int lowThresh = 90;
        cvCanny(grey,masked,lowThresh,lowThresh*3,3);
        cvShowImage("result",masked);
        cvWaitKey();*/

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