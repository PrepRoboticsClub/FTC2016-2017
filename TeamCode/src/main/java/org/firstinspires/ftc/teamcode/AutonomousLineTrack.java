package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by robotadmin on 12/1/16.
 */

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Pushbot-Line", group="Autonomous")
public class AutonomousLineTrack extends LinearOpMode{
    Project1 robot = new Project1();

    @Override
    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap);


        waitForStart();
        robot.odsSensorL.enableLed(true);
        robot.odsSensorR.enableLed(true);
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotor.setPower(.5);
        robot.leftMotor.setPower(.5);
        while(opModeIsActive()) {
            telemetry.addData("ODSL", robot.odsSensorL.getLightDetected());
            telemetry.addData("ODSR", robot.odsSensorR.getLightDetected());
            telemetry.update();
            if(robot.odsSensorR.getLightDetected() > .05){
                robot.rightMotor.setPower(.1);
                robot.leftMotor.setPower(.2);
            }else if (robot.odsSensorR.getLightDetected() > .05){
                robot.leftMotor.setPower(.1);
                robot.rightMotor.setPower(.2);
            }else{
                robot.rightMotor.setPower(.2);
                robot.leftMotor.setPower(.2);
            }
            //use mad hacking skillz
        }
        robot.rightMotor.setPower(0);
        robot.leftMotor.setPower(0);

    }
}
