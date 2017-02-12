package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by robotadmin on 12/1/16.
 */

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Pushbot-Auto", group="Autonomous")
public class Autonomous extends LinearOpMode{
    HardwarePushbot robot       = new HardwarePushbot();

    @Override
    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap);

        waitForStart();
        sleep(10000);
        //Move 5 feet
        while(opModeIsActive()) {
            robot.rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightMotor.setTargetPosition(3628);
            robot.leftMotor.setTargetPosition(3628);
            robot.rightMotor.setPower(1);
            robot.leftMotor.setPower(1);
        }

    }
}
