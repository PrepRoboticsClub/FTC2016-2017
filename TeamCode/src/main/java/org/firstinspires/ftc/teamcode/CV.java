package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.opencv.core.*;

import android.hardware.Camera;
import android.hardware.camera2.*;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Pushbot-CV", group="Autonomous")
public class CV extends LinearOpMode {
    public void runOpMode() throws InterruptedException {
        //Camera camera = null;

        /*
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                camera = Camera.open(i);
            }
        }*/

        Camera camera = null;

        if(camera!=null){ camera.release();}

        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                camera = Camera.open(i);
            }
        }

        camera.setPreviewCallback(previewCallback);
        camera.startPreview();

        camera.stopPreview();
        camera.release();
    }

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera)
        {
            Camera.Parameters parameters = camera.getParameters();
            int width = parameters.getPreviewSize().width;
            int height = parameters.getPreviewSize().height;
            Mat m = new Mat(width, height, CvType.CV_8UC3);
            m.put(0, 0, data);

            telemetry.addData("Channels", m.channels());
            telemetry.update();
        }
    };


}
