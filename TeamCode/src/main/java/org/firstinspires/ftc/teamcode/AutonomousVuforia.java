package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by robotadmin on 12/1/16.
 */

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="AutonomousVuforia", group="Autonomous")
public class AutonomousVuforia extends LinearOpMode{
    HardwarePushbot robot       = new HardwarePushbot();
    VuforiaTrackables beacons;

    @Override
    public void runOpMode() throws InterruptedException {

        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        params.vuforiaLicenseKey = "AdLSetb/////AAAAGbm2rHS0ck7Fgxwjtp3Cbclm98DyEfx+PLZ+VgF6AjcoFsOoMwgjWair2KgZLmc9MwR74NxG2WqBPqWs4ocmgQ0DyEnDW0tSzgUhH/UgBobUpmHrqSY5htttuRw6OKo9/A+3t39YCQj0+qxjsIj6cg/bStC8lI11ZMYukRnCSKLQQOVGxAbe0CuL7cBQ34gc8hqxOzk1gVXyj+U9XxxjKnJ18qiCcisprtAaRuRB6xzP8MzUQoql0Ajn8ldXW3mZSKjc3tq0LPYDwYmAaKkAxNz/jabhUk3m4Gyti5ApeYtw8yWA0AkKum8Fb8W/VTnc6FckH4BXgXOcDng++FTw9vihPiSJ7a36I2hU1Q8+NnuC";
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;
        VuforiaLocalizer localizer = ClassFactory.createVuforiaLocalizer(params);
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4);
        beacons = localizer.loadTrackablesFromAsset("FTC_2016-17");

        beacons.get(0).setName("Wheels");
        beacons.get(1).setName("Tools");
        beacons.get(2).setName("Legos");
        beacons.get(3).setName("Gears");

        robot.init(hardwareMap);

        waitForStart();
        beacons.activate();
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        VuforiaTrackableDefaultListener gears = (VuforiaTrackableDefaultListener) beacons.get(3).getListener();

        robot.leftMotor.setPower(0.2);
        robot.rightMotor.setPower(0.2);

        while(opModeIsActive() && gears.getRawPose() == null) {
            idle();
            telemetry.addData("Say", "rawposenull");
            updateTelemetry(telemetry);
        }

        VectorF angles = anglesFromTarget(gears);
        VectorF trans = navOffWall(gears.getPose().getTranslation(), Math.toDegrees(angles.get(0)) - 90, new VectorF(500, 0, 0));

        angles = anglesFromTarget(gears);
        trans = navOffWall(gears.getPose().getTranslation(), Math.toDegrees(angles.get(0)) - 90, new VectorF(500, 0, 0));

        telemetry.addData("Say", "Active 1");
        updateTelemetry(telemetry);

        if(trans.get(0) > 0){
            robot.leftMotor.setPower(0.2);
            robot.rightMotor.setPower(-0.2);
        } else{
            robot.leftMotor.setPower(-0.2);
            robot.rightMotor.setPower(0.2);
        }

        do{
            telemetry.addData("Say", "do while");
            updateTelemetry(telemetry);
            if(gears.getPose() != null){
                trans = navOffWall(gears.getPose().getTranslation(), Math.toDegrees(angles.get(0)) - 90, new VectorF(500, 0, 0));
            }
            idle();
        } while(opModeIsActive() && Math.abs(trans.get(0)) > 30);

        robot.leftMotor.setPower(0);
        robot.rightMotor.setPower(0);

        robot.leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //INSERT CM FROM ROTATION POINT TO PHONE INSTEAD OF 0
        robot.leftMotor.setTargetPosition((int)(robot.leftMotor.getCurrentPosition() + ((Math.hypot(trans.get(0), trans.get(2)) + 0) / 314 * 144)));
        robot.rightMotor.setTargetPosition((int)(robot.rightMotor.getCurrentPosition() + ((Math.hypot(trans.get(0), trans.get(2)) + 0) / 314 * 144)));


        robot.leftMotor.setPower(0.3);
        robot.rightMotor.setPower(0.3);

        while(opModeIsActive() && robot.leftMotor.isBusy() && robot.rightMotor.isBusy()){
            idle();
            telemetry.addData("Say", "idle");
            updateTelemetry(telemetry);
        }

        robot.leftMotor.setPower(0);
        robot.rightMotor.setPower(0);

        robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        while(opModeIsActive() && (gears.getPose() == null || Math.abs(gears.getPose().getTranslation().get(0)) > 10)){
            if(gears.getPose() != null){
                if(gears.getPose().getTranslation().get(0) > 0){
                    robot.leftMotor.setPower(-0.3);
                    robot.rightMotor.setPower(0.3);
                }
                else{
                    robot.leftMotor.setPower(0.3);
                    robot.rightMotor.setPower(-0.3);
                }
            }else{
                robot.leftMotor.setPower(-0.3);
                robot.rightMotor.setPower(0.3);
            }
            telemetry.addData("Say", "active 2");
            updateTelemetry(telemetry);
        }

        robot.leftMotor.setPower(0);
        robot.rightMotor.setPower(0);
    }

    public VectorF navOffWall(VectorF trans, double robotAngle, VectorF offWall){
        return new VectorF((float) (trans.get(0) - offWall.get(0) * Math.sin(Math.toRadians(robotAngle)) - offWall.get(2) * Math.cos(Math.toRadians(robotAngle))), trans.get(1), (float) (trans.get(2) + offWall.get(0) * Math.cos(Math.toRadians(robotAngle)) - offWall.get(2) * Math.sin(Math.toRadians(robotAngle))));
    }

    public VectorF anglesFromTarget(VuforiaTrackableDefaultListener image){
        float [] data = image.getRawPose().getData();
        float [] [] rotation = {{data[0], data[1]}, {data[4], data[5], data[6]}, {data[8], data[9], data[10]}};
        double thetaX = Math.atan2(rotation[2][1], rotation[2][2]);
        double thetaY = Math.atan2(-rotation[2][0], Math.sqrt(rotation[2][1] * rotation[2][1] + rotation[2][2] * rotation[2][2]));
        double thetaZ = Math.atan2(rotation[1][0], rotation[0][0]);
        return new VectorF((float)thetaX, (float)thetaY, (float)thetaZ);
    }
}
