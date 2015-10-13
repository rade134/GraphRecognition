import org.bytedeco.javacpp.opencv_core;

import java.util.List;

import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_imgproc.CV_HOUGH_GRADIENT;
import static org.bytedeco.javacpp.opencv_imgproc.cvCanny;
import static org.bytedeco.javacpp.opencv_imgproc.cvHoughCircles;

/**
 * Created by Jayden on 10/13/2015.
 */
public class Configuration {
    //hough circle
    public static List<BinaryTree> getCircleHough(opencv_core.IplImage image) {
        opencv_core.IplImage masked = getMasked(image);
        opencv_core.CvMemStorage mem = opencv_core.CvMemStorage.create();
        opencv_core.CvSeq seq = cvHoughCircles(
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
        for (int i=)
    }
    public static opencv_core.IplImage getMasked(opencv_core.IplImage image) {
        opencv_core.IplImage masked = cvCreateImage(cvGetSize(image), 8, 1);
        int lowThresh = 100;
        cvCanny(image, masked, lowThresh, lowThresh * 3, 3);
        return masked;
    }
}
