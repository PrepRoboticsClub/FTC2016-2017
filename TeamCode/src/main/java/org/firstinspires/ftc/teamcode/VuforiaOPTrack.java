package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
//na na na na na na na na na na na na na na na na BATMAN

/**
 * Created by robotadmin on 11/17/16.
 */
@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="VuforiaTrack", group="Test")
public class VuforiaOPTrack extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Project1 robot = new Project1();
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

        while(opModeIsActive()){
            for(VuforiaTrackable beac: beacons) {
                OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) beac.getListener()).getPose();
                if(pose != null){
                    VectorF translation = pose.getTranslation();

                    telemetry.addData(beac.getName() + "-Translation", translation);
                    //negative 0 turn left
                    //if(translation.get(0) < 10 && translation.get(0) > -10){
                    //    robot.rightMotor.setPower(.1);
                    //    telemetry.addData("STRAIGHT", translation.get(0)/ 5000);
                    //    robot.leftMotor.setPower(.1);
                    //}else
                    if(translation.get(0) > 0){
                        robot.leftMotor.setPower(translation.get(0)/ 5000 + .1);
                        telemetry.addData("powerL", translation.get(0)/ 5000);
                        robot.rightMotor.setPower(.05);
                    }else{
                        robot.rightMotor.setPower(Math.abs(translation.get(0))/ 5000 + .1);
                        telemetry.addData("powerR", Math.abs(translation.get(0))/ 5000);
                        robot.leftMotor.setPower(.05);
                    }
                }
            }
            telemetry.update();
        }
    }
}
