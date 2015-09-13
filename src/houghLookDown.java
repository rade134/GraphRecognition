import org.bytedeco.javacpp.opencv_core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.cvShowImage;
import static org.bytedeco.javacpp.opencv_highgui.cvWaitKey;
import static org.bytedeco.javacpp.opencv_imgproc.*;
/**
 * Created by Jayden on 8/30/2015.
 */
public class houghLookDown {

    private IplImage drawOn;
    private IplImage masked;
    private BinaryTree head;
    private CvSeq houghCircles;
    private List<Integer> IndicesTaken;

    public houghLookDown(IplImage image) {
        //smooth image
        cvSmooth(image, image);
        //find contours
        masked = cvCreateImage(cvGetSize(image), 8, 1);
        int lowThresh = 100;
        cvCanny(image, masked, lowThresh, lowThresh * 3, 3);
        //cvShowImage("Result", masked);
        //cvWaitKey(0);
        //find circles
        CvMemStorage mem = CvMemStorage.create();
        houghCircles = cvHoughCircles(
                masked, //Input image
                mem, //Memory Storage
                CV_HOUGH_GRADIENT, //Detection method
                1, //Inverse ratio
                20, //Minimum distance between the centers of the detected circles
                50, //Higher threshold for canny edge detector
                25, //Threshold at the center detection stage
                20, //min radius
                100 //max radius
        );

        drawOn = cvCloneImage(image);
        int minY = -1;
        int headIndex = -1;
        //make a list
        for (int i=0; i<houghCircles.total();i++) {
            CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(houghCircles, i));
            if ( circle.y() < minY || minY == -1) {
                minY = (int)circle.y();
                headIndex = i;
            }
        }
        if ( headIndex != -1 ) {
            head = new BinaryTree();
            IndicesTaken = new ArrayList<Integer>();
            head.value = headIndex;
            IndicesTaken.add(headIndex);
            setupBinary(head, 1);
            listBinaryTree(head);
            System.out.println();
            //System.out.println(head.value + "," +
            //                head.left.value + " " + head.right.value + "," +
            //                head.left.left.value+ " " + head.left.right.value + " "+head.right.left.value+ " " +head.right.right.value + ",");

            //draw circles
            CvFont font = new CvFont();
            cvInitFont(font, CV_FONT_HERSHEY_SIMPLEX, 1.0, 1.0, 0, 1, CV_AA);
            for (int i = 0; i < houghCircles.total(); i++) {
                //if ( IndicesTaken.contains(i)) {
                CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(houghCircles, i));
                cvPutText(drawOn, String.valueOf(i), cvPoint((int) circle.x(), (int) circle.y()), font, CvScalar.BLACK);
                int radius = Math.round(circle.z());
                cvCircle(drawOn, cvPoint((int) circle.x(), (int) circle.y()), radius, CvScalar.GREEN, 1, CV_AA, 0);
                //}
            }
            cvShowImage("result", drawOn);
            cvShowImage("mask",masked);
        }else{
            System.out.println("no head was found");
        }
        cvWaitKey();
    }
    public void listBinaryTree(BinaryTree head) {
        System.out.print(head.value);
        int c = 0;
        if ( head.left != null) {
            System.out.print(",");
            listBinaryTree(head.left);
            c++;
        }
        if ( head.right != null) {
            if ( c == 0) System.out.print(",");
            listBinaryTree(head.right);
        }
        System.out.print(".");
    }

    public void setupBinary(BinaryTree head,int layer) {
        int numToFind = (int)Math.round(Math.pow(2,layer));
        List<Integer> found = new ArrayList<Integer>(numToFind);

        CvPoint3D32f headC = new CvPoint3D32f(cvGetSeqElem(houghCircles, head.value));


        for (int i=0; i < numToFind; i++) {
            int minY = drawOn.cvSize().height();
            int headIndex = 0;
            for (int j = 0; j < houghCircles.total(); j++) {
                CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(houghCircles, j));
                if (circle.y() < minY && headC.y() < circle.y() && !found.contains(j) && !IndicesTaken.contains(j)) {
                    minY = (int) circle.y();
                    headIndex = j;
                }
            }
            found.add(headIndex);
        }
        List<Integer> indices = new ArrayList<Integer>();
        for (int i=0;i < 2; i++) {
            double minRatio = 10;
            Integer headIndex = null;
            for (Integer j : found) {
                CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(houghCircles, j));
                float ratio = Math.abs(headC.x() - circle.x()) / Math.abs(headC.y() - circle.y());
                //System.out.println(ratio);
                if ( ratio < minRatio && !IndicesTaken.contains(j) && lineDetected(head.value,j)) {
                    minRatio = ratio;
                    headIndex = j;
                }
            }
            if ( headIndex != null ) {
                IndicesTaken.add(headIndex);
                System.out.println("2nd Sel: " +headIndex);
                indices.add(headIndex);
            }
        }
        if ( indices.size() != 0) {
            for (int i=0 ; i < found.size();i++) {
                System.out.println("sel1: " + found.get(i));
            }
        }
        Integer indiceLeft = null;
        Integer indiceRight = null;

        if ( indices.size() == 2) {
            CvPoint3D32f circle0 = new CvPoint3D32f(cvGetSeqElem(houghCircles, indices.get(0)));
            CvPoint3D32f circle1 = new CvPoint3D32f(cvGetSeqElem(houghCircles, indices.get(1)));
            if ( circle0.x() < circle1.x()) {
                indiceLeft = indices.get(0);
                indiceRight = indices.get(1);
            }else{
                indiceLeft = indices.get(1);
                indiceRight = indices.get(0);
            }
        }else if ( indices.size() == 1 ) {
            CvPoint3D32f circle0 = new CvPoint3D32f(cvGetSeqElem(houghCircles, indices.get(0)));
            if (circle0.x() < headC.x()) {
                indiceLeft = indices.get(0);
            }else{
                indiceRight = indices.get(0);
            }
        }


        if (indiceLeft != null) {
            BinaryTree newHead = new BinaryTree();
            head.left = newHead;
            newHead.value = indiceLeft;
            setupBinary(newHead, layer + 1);
        }
        if ( indiceRight != null) {
            BinaryTree newHead = new BinaryTree();
            head.right = newHead;
            newHead.value = indiceRight;
            setupBinary(newHead, layer + 1);
        }
        System.out.println("End of function");

    }
    public boolean lineDetected(int a, int b) {
        //System.out.println(a + " " + b);
        CvPoint3D32f circlea = new CvPoint3D32f(cvGetSeqElem(houghCircles, a));
        CvPoint3D32f circleb = new CvPoint3D32f(cvGetSeqElem(houghCircles, b));
        CvPoint centera = cvPointFrom32f(new CvPoint2D32f(cvGetSeqElem(houghCircles, a)));
        CvPoint centerb = cvPointFrom32f(new CvPoint2D32f(cvGetSeqElem(houghCircles, b)));
        double circleDistance = Math.sqrt(Math.pow(centera.x() - centerb.x(), 2) + Math.pow(centera.y() - centerb.y(), 2));
        double circle1Radii = circlea.z();
        double circle2Radii = circleb.z();

        int xMin = Math.min(centera.x(), centerb.x());
        int yMin = Math.min(centera.y(), centerb.y());
        int xDiff = Math.abs(centera.x() - centerb.x());
        int yDiff = Math.abs(centera.y() - centerb.y());
        CvRect imageSection = new CvRect();
        imageSection.x(xMin);
        imageSection.y(yMin);
        imageSection.width(xDiff);
        imageSection.height(yDiff);
        cvSetImageROI(masked, imageSection);
        cvSetImageROI(drawOn, imageSection);

        CvMemStorage memLines = CvMemStorage.create();
        if (xDiff > 0 && yDiff > 0) {
            CvSeq houghLines = cvHoughLines2(
                    masked,
                    memLines,
                    CV_HOUGH_PROBABILISTIC,
                    1,
                    CV_PI / 360,
                    5,
                    0.50*(circleDistance-circle1Radii-circle2Radii),
                    25
            );
            for (int k = 0; k < houghLines.total(); k++) {
                CvPoint[] cvPts = new CvPoint[2];
                cvPts[0] = new CvPoint(cvGetSeqElem(houghLines, k)).position(0);
                cvPts[1] = new CvPoint(cvGetSeqElem(houghLines, k)).position(1);
                int c1x = centera.x() - xMin;
                int c2x = centerb.x() - xMin;
                int c1y = centera.y() - yMin;
                int c2y = centerb.y() - yMin;

                //check if the end points are on the edge of the circle
                double distPt1C1 = Math.abs(Math.sqrt(Math.pow(c1x - cvPts[0].x(), 2) + Math.pow(c1y - cvPts[0].y(), 2)) - circle1Radii);
                double distPt2C1 = Math.abs(Math.sqrt(Math.pow(c1x - cvPts[1].x(), 2) + Math.pow(c1y - cvPts[1].y(), 2)) - circle1Radii);
                double distPt1C2 = Math.abs(Math.sqrt(Math.pow(c2x - cvPts[0].x(), 2) + Math.pow(c2y - cvPts[0].y(), 2)) - circle2Radii);
                double distPt2C2 = Math.abs(Math.sqrt(Math.pow(c2x - cvPts[1].x(), 2) + Math.pow(c2y - cvPts[1].y(), 2)) - circle2Radii);
                //the closest point to circle i
                double distToC1;
                //closest point to circle j
                double distToC2;
                if (distPt1C1 > distPt2C1) {
                    distToC1 = distPt2C1;
                    distToC2 = distPt1C2;
                } else {
                    distToC1 = distPt1C1;
                    distToC2 = distPt2C2;
                }
                //threshold
                int t = ((int) (0.4 * (circleDistance - circle1Radii - circle2Radii)));

                //linePts.set(k,cvPts);
                // && distToC1 < t && distToC2 < t
                double lineDist = Math.sqrt(Math.pow(cvPts[0].x() - cvPts[1].x(), 2) + Math.pow(cvPts[0].y() - cvPts[1].y(), 2));
                if (circleDistance > circle1Radii + circle2Radii + 5) {
                    cvLine(drawOn, cvPts[0], cvPts[1], CvScalar.MAGENTA, 1, CV_AA, 0);
                    //cvShowImage("Result", drawOn);
                    //cvWaitKey(0);
                    cvResetImageROI(masked);
                    cvResetImageROI(drawOn);
                    return true;
                }
            }
        }
        cvResetImageROI(masked);
        cvResetImageROI(drawOn);
        return false;
    }
}
