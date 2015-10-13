import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_features2d;

import static org.bytedeco.javacpp.opencv_highgui.cvWaitKey;
import static org.bytedeco.javacpp.opencv_highgui.imshow;

/**
 * Created by Jayden on 9/15/2015.
 */
public class Blobbing {
    public Blobbing(opencv_core.Mat src) {
        opencv_features2d.SimpleBlobDetector.Params params = new opencv_features2d.SimpleBlobDetector.Params();

        // Change thresholds
        params.minThreshold(10);
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
        opencv_core.Mat blob = new opencv_core.Mat(src);
        opencv_features2d.KeyPoint kp = new opencv_features2d.KeyPoint();
        //blur(blob, blob, new Size(6, 6), new Point(-1,-1),BORDER_DEFAULT);
        //threshold(blob,blob,230,255,CV_THRESH_BINARY);

        // Filter by Area.
        params.filterByArea(false);
        params.minArea(100);
        opencv_features2d.SimpleBlobDetector det = new opencv_features2d.SimpleBlobDetector(params);

        det.detect(blob, kp);

        opencv_features2d.drawKeypoints(blob, kp, blob, new opencv_core.Scalar(0, 255), opencv_features2d.DrawMatchesFlags.DRAW_RICH_KEYPOINTS);
        imshow("blob: ",blob);
        cvWaitKey();
    }
}
