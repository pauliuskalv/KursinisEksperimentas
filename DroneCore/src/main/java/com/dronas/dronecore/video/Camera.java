package com.dronas.dronecore.video;

import com.dronas.dronecore.conf.TomlConfReader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class Camera {
    private final int CAMERA_FRAME_WIDTH = TomlConfReader.getParameterInteger("video_capture.frame_width");
    private final int CAMERA_FRAME_HEIGHT = TomlConfReader.getParameterInteger("video_capture.frame_height");
    private final String CAMERA_FRAME_FORMAT = TomlConfReader.getParameterString("video_capture.frame_format");
    private final int CAMERA_FRAME_QUALITY = TomlConfReader.getParameterInteger("video_capture.frame_quality");

    private VideoCapture mVideoCapture;

    public Camera() {
        mVideoCapture = new VideoCapture();
        mVideoCapture.set(Videoio.CAP_PROP_FRAME_HEIGHT, CAMERA_FRAME_HEIGHT);
        mVideoCapture.set(Videoio.CAP_PROP_FRAME_WIDTH, CAMERA_FRAME_WIDTH);
        // mVideoCapture.set(Videoio.CAP_MODE_RGB, 1);

        mVideoCapture.open(Videoio.CAP_ANY);
    }

    public byte[] grabFrame() {
        Mat toCapture = new Mat(CAMERA_FRAME_HEIGHT, CAMERA_FRAME_WIDTH, CvType.CV_8U);
        MatOfByte toSend = new MatOfByte();

        if (mVideoCapture.read(toCapture)) {
            Imgcodecs.imencode(
                    CAMERA_FRAME_FORMAT,
                    toCapture,
                    toSend,
                    new MatOfInt(
                            Imgcodecs.CV_IMWRITE_JPEG_QUALITY,
                            CAMERA_FRAME_QUALITY,
                            Imgcodecs.CV_IMWRITE_JPEG_OPTIMIZE,
                            CAMERA_FRAME_QUALITY
                    ));

            byte[] imageBytes = new byte[(int) toSend.total() * (int) toSend.elemSize()];
            toSend.get(0, 0, imageBytes);

            return imageBytes;
        }
        else
            return null;
    }

    public int getFrameWidth() {
        return this.CAMERA_FRAME_WIDTH;
    }

    public int getFrameHeight() {
        return this.CAMERA_FRAME_HEIGHT;
    }
}
