import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class GrabVideoFrame {

    public static void main(String... args) throws FrameGrabber.Exception {
        ClassLoader classLoader = GrabVideoFrame.class.getClassLoader();
        String videoPath = Objects.requireNonNull(classLoader.getResource("videoSample.mp4")).getPath();
        grabAndLoad(videoPath);
    }

    public static void grabAndLoad(String videoPath) throws FrameGrabber.Exception {
        Java2DFrameConverter converter = new Java2DFrameConverter();
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoPath);
        frameGrabber.start();

        Frame frame;
        double frameRate = frameGrabber.getFrameRate();
        System.out.println("Video has " + frameGrabber.getLengthInFrames() + " frames and has frame rate of " + frameRate);

        // grab the first frame
        frameGrabber.setFrameNumber(1);
        frame = frameGrabber.grab();
        BufferedImage bufferedImage = converter.convert(frame);
        System.out.println("First Frame" + ", Width: " + bufferedImage.getWidth() + ", Height: " + bufferedImage.getHeight());

        // grab the second frame
        frameGrabber.setFrameNumber(2);
        frame = frameGrabber.grab();
        bufferedImage = converter.convert(frame);
        System.out.println("Second Frame" + ", Width: " + bufferedImage.getWidth() + ", Height: " + bufferedImage.getHeight());

        // grab all the frames and save
        File folderName = new File("image");
        if (!Files.exists(folderName.toPath())) {
            folderName.mkdir();
        }

        try {
            for(int i = 1; i < frameGrabber.getLengthInFrames(); i++) {
                frameGrabber.setFrameNumber(i);
                frame = frameGrabber.grab();
                bufferedImage = converter.convert(frame);

                String imagePath = "image/"+i+".jpg";
                ImageIO.write(bufferedImage, "jpg", new File(imagePath));
                System.out.println(i + " image wrote.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            frameGrabber.stop();
        }
    }
}
