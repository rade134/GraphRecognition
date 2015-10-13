import org.bytedeco.javacpp.opencv_core.*;

import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;

/**
 * Created by Jayden on 10/13/2015.
 */
public class houghLookDown2 {

    public houghLookDown2(IplImage image) {
        //smooth image
        cvSmooth(image, image);
    }
}
