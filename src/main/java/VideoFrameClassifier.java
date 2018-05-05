
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import java.io.IOException;
import java.util.Arrays;

import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_highgui.*;

public class VideoFrameClassifier {

    private volatile Mat[] mat = new Mat[1];
    private String windowName;

    /**
     * Driver Class
     * Call: java -jar IR VideoFrameClassifier.jar [path to a video file]
     * @param args arguments
     * @throws IOException if path value could not be read
     */
    public static void main(String[] args) throws java.lang.Exception {

        if(args.length != 1) {
            System.out.println("Usage: java -jar VideoFrameClassifier.jar [path to the video]");
            throw new IllegalArgumentException("Incorrect number of arguments provided (1 expected, " + args.length
                    + " provided): " + Arrays.toString(args));
        }

        String videoPath = args[0];

        /*ClassLoader classLoader = VideoFrameClassifier.class.getClassLoader();
        String videoPath = Objects.requireNonNull(classLoader.getResource("videoSample2.mp4")).getPath();*/
        new VideoFrameClassifier().startRealTimeVideoDetection(videoPath);
    }

    /**
     * Captures the frame from video and tries to predict the pre-labelled object present.
     * @param videoFileName path of the video file
     * @throws Exception
     */
    public void startRealTimeVideoDetection(String videoFileName) throws Exception {

        windowName = "Video Frame Classification";
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoFileName);
        frameGrabber.start();

        Frame frame;
        double frameRate = frameGrabber.getFrameRate();
        System.out.println("Video has " + frameGrabber.getLengthInFrames() + " frames and has frame rate of " + frameRate);

        try {
            for(int i = 1; i < frameGrabber.getLengthInFrames(); i+=(int)frameRate) {
                frameGrabber.setFrameNumber(i);
                frame = frameGrabber.grab();
                mat[0] = new OpenCVFrameConverter.ToMat().convert(frame);
                TinyYoloModel.getInstance().markWithBoundingBox(mat[0], frame.imageWidth, frame.imageHeight, true, windowName);
                imshow(windowName, mat[0]);

                char key = (char) waitKey(20);
                // Exit on escape:
                if (key == 27) {
                    destroyAllWindows();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            frameGrabber.stop();
        }
    }
}