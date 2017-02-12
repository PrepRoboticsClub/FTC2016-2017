package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by robotadmin on 12/1/16.
 */

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Mecanum-auto", group="Autonomous")
public class MecanumAuto extends LinearOpMode{
    ProjectMecanum robot       = new ProjectMecanum();
    float speed = 0;
    float angle = 0;
    float speedMultiplier = .7f;
    VectorF vectorF;
    float direction = 0;

    float frontLeft;
    float frontRight;
    float backLeft;
    float backRight;

    float greatestNum = 0;



    @Override
    public void runOpMode() throws InterruptedException {

        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        params.vuforiaLicenseKey = "AdLSetb/////AAAAGbm2rHS0ck7Fgxwjtp3Cbclm98DyEfx+PLZ+VgF6AjcoFsOoMwgjWair2KgZLmc9MwR74NxG2WqBPqWs4ocmgQ0DyEnDW0tSzgUhH/UgBobUpmHrqSY5htttuRw6OKo9/A+3t39YCQj0+qxjsIj6cg/bStC8lI11ZMYukRnCSKLQQOVGxAbe0CuL7cBQ34gc8hqxOzk1gVXyj+U9XxxjKnJ18qiCcisprtAaRuRB6xzP8MzUQoql0Ajn8ldXW3mZSKjc3tq0LPYDwYmAaKkAxNz/jabhUk3m4Gyti5ApeYtw8yWA0AkKum8Fb8W/VTnc6FckH4BXgXOcDng++FTw9vihPiSJ7a36I2hU1Q8+NnuC";
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;
        VuforiaLocalizer localizer = ClassFactory.createVuforiaLocalizer(params);
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4);
        VuforiaTrackables beacons = localizer.loadTrackablesFromAsset("FTC_2016-17");

        beacons.get(0).setName("Wheels");
        beacons.get(1).setName("Tools");
        beacons.get(2).setName("Legos");
        beacons.get(3).setName("Gears");
        beacons.activate();

        robot.init(hardwareMap);

        waitForStart();
        //Move 5 feet
        while(opModeIsActive()) {
            OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) beacons.get(0).getListener()).getPose();
            if(pose != null){

                VectorF translation = pose.getTranslation();
                while(translation.get(1) > 30) {
                    //negative 0 turn left
                    if (translation.get(0) < 6 && translation.get(0) > -6) {
                        //straight
                        SetPower(0, -1, 0);
                    } else if (translation.get(0) > 0) {
                        //Left
                        SetPower((int) translation.get(0), -1, 0);
                    } else {
                        //Right
                        SetPower((int) translation.get(0), -1, 0);
                    }
                }
            }
        }
        SetPower(0, 0, 0);
    }
    //strafex = gamepad1.left_stick_x
    //strafey = gamepad1.left_stick_y
    //direction = gamepad1.right_stick_x
    void SetPower(int strafex, int strafey, int direction){
        vectorF = new VectorF(-strafex, strafey);
        speed = vectorF.magnitude();
        vectorF = new VectorF(vectorF.get(0) / speed, vectorF.get(1) / speed);
        angle = (float) Math.atan2(vectorF.get(0), vectorF.get(1));
        direction = direction * -1;

        frontLeft = (float) (speed * Math.sin(angle + Math.PI / 4) + direction) * speedMultiplier;
        frontRight = (float) (speed * Math.cos(angle + Math.PI / 4) - direction) * speedMultiplier;
        backLeft = (float) (speed * Math.cos(angle + Math.PI / 4) + direction) * speedMultiplier;
        backRight = (float) (speed * Math.sin(angle + Math.PI / 4) - direction) * speedMultiplier;

        greatestNum = Math.abs(frontLeft);
        if (Math.abs(frontRight) > greatestNum) {
            greatestNum = Math.abs(frontRight);
        }
        if (Math.abs(backLeft) > greatestNum) {
            greatestNum = Math.abs(backLeft);
        }
        if (Math.abs(backRight) > greatestNum) {
            greatestNum = Math.abs(backRight);
        }
        if (greatestNum > 1) {
            frontLeft /= greatestNum;
            frontRight /= greatestNum;
            backLeft /= greatestNum;
            backRight /= greatestNum;
        }

        robot.frontLeft.setPower(Float.isNaN(frontLeft) ? 0 : frontLeft);
        robot.frontRight.setPower(Float.isNaN(frontRight) ? 0 : frontRight);
        robot.backLeft.setPower(Float.isNaN(backLeft) ? 0 : backLeft);
        robot.backRight.setPower(Float.isNaN(backRight) ? 0 : backRight);
    }
}
