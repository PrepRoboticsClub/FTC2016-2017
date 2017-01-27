package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.vuforia.CameraCalibration;
import com.vuforia.CameraDevice;
import com.vuforia.Frame;
import com.vuforia.HINT;
import com.vuforia.Image;
import com.vuforia.Matrix34F;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.Vec3F;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.internal.VuforiaPoseMatrix;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.Closeable;
import java.util.Arrays;



/**
 * Created by robotadmin on 12/1/16.
 */

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="AutonomousVuforia", group="Autonomous")
public class AutonomousVuforia extends LinearOpMode{

    HardwarePushbot robot       = new HardwarePushbot();
    VuforiaTrackables beacons;

    public static final Scalar blueLow = new Scalar(108, 0, 220);
    public static final Scalar blueHigh = new Scalar(178, 255, 255);

    public int BEACON_NOT_VISIBLE = 0;
    public int BEACON_RED_BLUE = 1;
    public int BEACON_BLUERED = 2;
    public int BEACON_ALL_BLUE = 3;
    public int BEACON_NO_BLUE = 4;

    //static {
    //    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    //}
    static {
        OpenCVLoader.initDebug();
    }


    @Override
    public void runOpMode() throws InterruptedException {

        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        params.vuforiaLicenseKey = "AdLSetb/////AAAAGbm2rHS0ck7Fgxwjtp3Cbclm98DyEfx+PLZ+VgF6AjcoFsOoMwgjWair2KgZLmc9MwR74NxG2WqBPqWs4ocmgQ0DyEnDW0tSzgUhH/UgBobUpmHrqSY5htttuRw6OKo9/A+3t39YCQj0+qxjsIj6cg/bStC8lI11ZMYukRnCSKLQQOVGxAbe0CuL7cBQ34gc8hqxOzk1gVXyj+U9XxxjKnJ18qiCcisprtAaRuRB6xzP8MzUQoql0Ajn8ldXW3mZSKjc3tq0LPYDwYmAaKkAxNz/jabhUk3m4Gyti5ApeYtw8yWA0AkKum8Fb8W/VTnc6FckH4BXgXOcDng++FTw9vihPiSJ7a36I2hU1Q8+NnuC";
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;
        VuforiaLocalizerImplSubclass localizer = new VuforiaLocalizerImplSubclass(params);
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4);
        beacons = localizer.loadTrackablesFromAsset("FTC_2016-17");

        beacons.get(0).setName("Wheels");
        beacons.get(1).setName("Tools");
        beacons.get(2).setName("Legos");
        beacons.get(3).setName("Gears");

        robot.init(hardwareMap);

        beacons.activate();
        waitForStart();
        robot.rightMotor.setPower(0);
        robot.leftMotor.setPower(0);

        while(opModeIsActive()) {
            //int config = getBeaconConfig(getImage(localizer.daFrame, PIXEL_FORMAT.RGB565), new VuforiaTrackableDefaultListener(beacons.get(3)), localizer.getCameraCalibration());
            int config = getBeaconConfig(localizer.rgb, beacons.get(3), localizer.getCameraCalibration());

            if (config == BEACON_ALL_BLUE) {
                telemetry.addData("Beacon: ", "ALL_BLUE");
                telemetry.update();
            } else if (config == BEACON_BLUERED) {
                telemetry.addData("Beacon: ", "BLUE_RED");
                telemetry.update();
            } else if (config == BEACON_NO_BLUE) {
                telemetry.addData("Beacon: ", "NO_BLUE");
                telemetry.update();
            } else if (config == BEACON_NOT_VISIBLE) {
                telemetry.addData("Beacon: ", "NOT_VISIBLE");
                telemetry.update();
            } else if (config == BEACON_RED_BLUE) {
                telemetry.addData("Beacon: ", "RED_BLUE");
                telemetry.update();
            }
        }
    }

    public Image getImage(VuforiaLocalizerImplSubclass.CloseableFrame frame, int pixelFormat){
        long numImgs = frame.getNumImages();

        for(int i = 0; i < numImgs; i++){
            if(frame.getImage(i).getFormat() == pixelFormat){
                return frame.getImage(i);
            }
        }

        return null;
    }

    public int getBeaconConfig(Image img, VuforiaTrackable beacon, CameraCalibration camCal) {

        OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) beacon.getListener()).getRawPose();
        telemetry.addData("Stuff", pose != null);
        telemetry.addData("Stuff", img != null);
        try {
            telemetry.addData("Stuff", img.getPixels() != null);
        } catch(Exception e){
            telemetry.addData("Stuff", e);
        }
        telemetry.update();

        if (pose != null && img != null && img.getPixels() != null) {
            Matrix34F rawPose = new Matrix34F();
            float[] poseData = Arrays.copyOfRange(pose.transposed().getData(), 0, 12);
            rawPose.setData(poseData);

            float[][] corners = new float[4][2];

            corners[0] = Tool.projectPoint(camCal, rawPose, new Vec3F(-127,276,0)).getData();
            corners[1] = Tool.projectPoint(camCal, rawPose, new Vec3F(127,276,0)).getData();
            corners[2] = Tool.projectPoint(camCal, rawPose, new Vec3F(127,92,0)).getData();
            corners[3] = Tool.projectPoint(camCal, rawPose, new Vec3F(-127,92,0)).getData();

            Bitmap bm = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.RGB_565);
            bm.copyPixelsFromBuffer(img.getPixels());

            Mat crop = new Mat(bm.getHeight(), bm.getWidth(), CvType.CV_8UC3);
            Utils.bitmapToMat(bm, crop);

            float x = Math.min(Math.min(corners[1][0], corners[3][0]), Math.min(corners[0][0], corners[2][0]));
            float y = Math.min(Math.min(corners[1][1], corners[3][1]), Math.min(corners[0][1], corners[2][1]));
            float width = Math.max(Math.abs(corners[0][0] - corners[2][0]), Math.abs(corners[1][0] - corners[3][0]));
            float height = Math.max(Math.abs(corners[0][1] - corners[2][1]), Math.abs(corners[1][1] - corners[3][1]));

            x = Math.max(x, 0);
            y = Math.max(y, 0);
            width = (x+width > crop.cols())? crop.cols() - x: width;
            height = (y+height > crop.rows())? crop.rows() - y: height;

            Mat cropped = new Mat(crop,new Rect((int) x, (int) y, (int) width, (int) height));

            Imgproc.cvtColor(cropped, cropped, Imgproc.COLOR_RGB2HSV_FULL);

            Mat mask = new Mat();
            Core.inRange(cropped, blueLow, blueHigh, mask);
            Moments mmnts = Imgproc.moments(mask, true);

            if (mmnts.get_m00() > mask.total() * 0.8) {
                return BEACON_ALL_BLUE;
            }else if(mmnts.get_m00() < mask.total() * 0.8){
                return BEACON_NO_BLUE;
            }

            if ((mmnts.get_m01() / mmnts.get_m00()) < cropped.rows() / 2) {

                return BEACON_RED_BLUE;
            } else {

                return BEACON_BLUERED;
            } // else

        }

        return BEACON_NOT_VISIBLE;
    }
}
