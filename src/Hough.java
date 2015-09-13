import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 * Created by Jayden on 8/9/2015.
 */
public class Hough {

    private IplImage image;
    private IplImage masked;
    private IplImage drawOn;
    private CvSeq houghCircles;
    private List<CvPoint[]> linePts;
    private int circlesFound = -1;
    private int headCircle = -1;
    private Map<Integer,List<Integer>> connections;

    public Hough(IplImage image) {
        this.image = image;

        //smooth image
        cvSmooth(this.image, this.image);

        //get greyscale image
        IplImage grey = cvCreateImage(cvGetSize(this.image), 8, 1);
        cvCvtColor(this.image, grey, CV_BGR2GRAY);


        //cvAdaptiveThreshold(this.image,this.image,255,CV_ADAPTIVE_THRESH_MEAN_C,CV_THRESH_BINARY,75,10);
        masked = cvCreateImage(cvGetSize(this.image), 8, 1);
System.out.println(cvGetElemType(masked));
        int lowThresh = 90;
        cvCanny(grey,masked,lowThresh,lowThresh*3,3);
        //cvAdaptiveThreshold(grey,masked,255,CV_ADAPTIVE_THRESH_MEAN_C,CV_THRESH_BINARY,75,10);


        drawOn = cvCloneImage(image);
        linePts = new ArrayList<CvPoint[]>();
    }

    public CvSeq getCircles() {
        if (image == null) return null;

        //Obtain circles
        CvMemStorage mem = CvMemStorage.create();
        houghCircles = cvHoughCircles(
                masked, //Input image
                mem, //Memory Storage
                CV_HOUGH_GRADIENT, //Detection method
                1.3, //Inverse ratio
                180, //Minimum distance between the centers of the detected circles
                100, //Higher threshold for canny edge detector
                20, //Threshold at the center detection stage
                15, //min radius
                130 //max radius
        );
        circlesFound = houghCircles.total();

        return houghCircles;
    }
    public void getLines() {
        if (circlesFound != -1) {
            connections = new HashMap<Integer, List<Integer>>();

            for (int i=0;i<houghCircles.total()-1;i++) {
                if ( !connections.containsKey(i)) {
                    connections.put(i,new ArrayList<Integer>());
                }
                for (int j=i+1;j<houghCircles.total();j++) {
                    if (i != j) {
                        if ( !connections.containsKey(j)) {
                            connections.put(j,new ArrayList<Integer>());
                        }
                        CvRect imageSection = new CvRect();
                        CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(houghCircles, i));
                        CvPoint3D32f circle2 = new CvPoint3D32f(cvGetSeqElem(houghCircles, j));

                        CvPoint center = cvPointFrom32f(new CvPoint2D32f(cvGetSeqElem(houghCircles, i)));
                        CvPoint center2 = cvPointFrom32f(new CvPoint2D32f(cvGetSeqElem(houghCircles, j)));
                        double circleDistance = Math.sqrt(Math.pow(center.x() - center2.x(), 2) + Math.pow(center.y() - center2.y(), 2));
                        double circle1Radii = circle.z();
                        double circle2Radii = circle2.z();
                        int xMin = Math.min(center.x(), center2.x());
                        int yMin = Math.min(center.y(), center2.y());
                        int xDiff = Math.abs(center.x() - center2.x());
                        int yDiff = Math.abs(center.y() - center2.y());
                        imageSection.x(xMin);
                        imageSection.y(yMin);
                        imageSection.width(xDiff);
                        imageSection.height(yDiff);
                        cvSetImageROI(masked, imageSection);
                        cvSetImageROI(drawOn, imageSection);

                        CvMemStorage memLines = CvMemStorage.create();
                        //cvShowImage("Result", masked);
                        //System.out.println("line numbers: "+)
                        //cvWaitKey(0);
                        if ( xDiff > 0 && yDiff > 0) {
                            System.out.println((masked.getBufferedImageType()) + " " + CV_8UC1);
                            CvSeq houghLines = cvHoughLines2(
                                    masked,
                                    memLines,
                                    CV_HOUGH_PROBABILISTIC,
                                    1,
                                    CV_PI / 360,
                                    40,
                                    0.50*(circleDistance-circle1Radii-circle2Radii),
                                    0.3  *(circleDistance-circle1Radii-circle2Radii)
                            );
                            System.out.println("dist: " +(circleDistance- circle1Radii - circle2Radii));
                            System.out.println("circle: " +i+"-"+j+" !!");
                            for (int k = 0; k < houghLines.total(); k++) {
                                CvPoint[] cvPts = new CvPoint[2];
                                cvPts[0] = new CvPoint(cvGetSeqElem(houghLines, k)).position(0);
                                cvPts[1] = new CvPoint(cvGetSeqElem(houghLines, k)).position(1);
                                int c1x = center.x() - xMin;
                                int c2x = center2.x() - xMin;
                                int c1y = center.y() - yMin;
                                int c2y = center2.y() - yMin;

                                //check if the end points are on the edge of the circle
                                double distPt1C1 = Math.abs(Math.sqrt(Math.pow(c1x - cvPts[0].x(), 2) + Math.pow(c1y - cvPts[0].y(), 2)) - circle1Radii);
                                double distPt2C1 = Math.abs(Math.sqrt(Math.pow(c1x - cvPts[1].x(), 2) + Math.pow(c1y - cvPts[1].y(), 2))-circle1Radii);
                                double distPt1C2 = Math.abs(Math.sqrt(Math.pow(c2x - cvPts[0].x(), 2) + Math.pow(c2y - cvPts[0].y(), 2))-circle2Radii);
                                double distPt2C2 = Math.abs(Math.sqrt(Math.pow(c2x - cvPts[1].x(), 2) + Math.pow(c2y - cvPts[1].y(), 2))-circle2Radii);
                                //the closest point to circle i
                                double distToC1;
                                //closest point to circle j
                                double distToC2;
                                if ( distPt1C1 > distPt2C1 ) {
                                    distToC1 = distPt2C1;
                                    distToC2 = distPt1C2;
                                }else{
                                    distToC1 = distPt1C1;
                                    distToC2 = distPt2C2;
                                }
                                System.out.println(distToC1 + " " + distToC2);
                                //threshold
                                int t = ((int) (0.4 * (circleDistance - circle1Radii - circle2Radii)));

                                //linePts.set(k,cvPts);
                                // && distToC1 < t && distToC2 < t
                                double lineDist = Math.sqrt(Math.pow(cvPts[0].x() - cvPts[1].x(), 2) + Math.pow(cvPts[0].y() - cvPts[1].y(), 2));
                                if (circleDistance > circle1Radii +circle2Radii+5 && distToC1 < t && distToC2 < t) {
                                    if (!connections.get(i).contains(j)) {
                                        connections.get(i).add(j);
                                    }
                                    if (!connections.get(j).contains(i)) {
                                        connections.get(j).add(i);
                                    }
                                    cvLine(drawOn, cvPts[0], cvPts[1], CvScalar.MAGENTA, 1, CV_AA, 0);
                                    cvPts[0].x(cvPts[0].x() + xMin);
                                    cvPts[0].y(cvPts[0].y() + yMin);
                                    cvPts[1].x(cvPts[1].x() + xMin);
                                    cvPts[1].y(cvPts[1].y() + yMin);

                                    linePts.add(cvPts);
                                }
                            }
                            if ( i == 1 && j == 6) {
                                cvShowImage("Result", drawOn);
                                //System.out.println("line numbers: "+)
                                cvWaitKey(0);
                            }
                        }
                        cvShowImage("Result", drawOn);
                        cvWaitKey();
                        cvResetImageROI(masked);
                        cvResetImageROI(drawOn);
                    }
                }
            }
            headCircle = -1;
            double bestVector = 0;
        }
    }
    public void showImage() {
        if (houghCircles != null) {
            //System.out.println( "size: " +connections.get(headCircle).size());
            if (circlesFound != 0) {
                int averageSum = 0;
                for (int i = 0; i < houghCircles.total(); i++) {
                    CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(houghCircles, i));
                    averageSum += Math.round(circle.z());
                }
                int averageR = 0;
                if (houghCircles.total() != 0) {
                    averageR = averageSum / houghCircles.total();
                    //add 20 percent to size
                    averageR *= 1;
                }
                System.out.println("total circles: " + houghCircles.total());
                CvFont font = new CvFont();
                cvInitFont(font, CV_FONT_HERSHEY_SIMPLEX, 1.0, 1.0, 0, 1, CV_AA);
                for (int i = 0; i < houghCircles.total(); i++) {
                    //if ( connections.containsKey(i) ) {
                        //if (connections.get(i).size() != 0) {
                            CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(houghCircles, i));
                            CvPoint center = cvPointFrom32f(new CvPoint2D32f(cvGetSeqElem(houghCircles, i)));
                            int radius = Math.round(circle.z());
                            //int radius = (int) ((Math.round(circle.z()) * 1.5 + averageR * 1.5) / 2);
                            System.out.println(circle.x() + " " + circle.y() + " " + radius);
                            System.out.println(i);
                            cvPutText(drawOn,String.valueOf(i),cvPoint((int) circle.x(), (int) circle.y()),font,CvScalar.BLACK);
                            if (connections != null) {
                                if (connections.get(i).size() == 0) {
                                    cvCircle(drawOn, cvPoint((int) circle.x(), (int) circle.y()), radius, CvScalar.GREEN, 1, CV_AA, 0);
                                } else {
                                    cvCircle(drawOn, cvPoint((int) circle.x(), (int) circle.y()), radius, CvScalar.BLUE, 1, CV_AA, 0);

                                }
                            }else{
                                cvCircle(drawOn, cvPoint((int) circle.x(), (int) circle.y()), radius, CvScalar.GREEN, 1, CV_AA, 0);

                            }
                        //}
                    // }
                }
            }else System.out.println(" Couldn't find any circles ");
        }
        if (linePts != null) {
            /*
            if (linePts.size() != 0) {
                for (int i = 0; i < linePts.size(); i++) {

                    CvPoint pt1 = linePts.get(i)[0];
                    CvPoint pt2 = linePts.get(i)[1];

                    cvLine(drawOn, pt1, pt2, CvScalar.MAGENTA, 1, CV_AA, 0); // draw the segment on the image
                }
            }else System.out.println(" Couldn't find any lines ");*/
            for (Map.Entry<Integer, List<Integer>> entry : connections.entrySet()) {
                CvPoint pt1 = cvPointFrom32f(new CvPoint2D32f(cvGetSeqElem(houghCircles,entry.getKey())));
                for (Integer circle: entry.getValue()) {
                    CvPoint pt2 = cvPointFrom32f(new CvPoint2D32f(cvGetSeqElem(houghCircles,circle)));
                    cvLine(drawOn,pt1,pt2,CvScalar.BLACK,1,CV_AA,0);
                }
            }
        }/*
        for (int i=0;i<houghCircles.total()-1;i++) {
            for (int j=i+1;j<houghCircles.total();j++) {
                CvRect imageSection = new CvRect();
                CvPoint center = cvPointFrom32f(new CvPoint2D32f(cvGetSeqElem(houghCircles, i)));
                CvPoint center2 = cvPointFrom32f(new CvPoint2D32f(cvGetSeqElem(houghCircles, j)));
                imageSection.x(Math.min(center.x(),center2.x()));
                imageSection.y(Math.min(center.y(),center2.y()));
                imageSection.width(Math.abs(center.x() - center2.x()));
                imageSection.height(Math.abs(center.y() - center2.y()));
                cvSetImageROI(drawOn, imageSection);
                cvShowImage("Result", drawOn);
                cvWaitKey(0);
                cvResetImageROI(drawOn);
            }
        }
*/
        cvShowImage("result",drawOn);
        cvWaitKey();
    }
}
