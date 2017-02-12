package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.I2cAddr;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by robotadmin on 12/1/16.
 */

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="ColorSensor", group="Autonomous")
public class ColorSensor extends LinearOpMode{
    Project3 robot       = new Project3();

    @Override
    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap);
        //robot.legacyModule.enableI2cReadMode(5, I2cAddr.zero(), 42, 10);
        waitForStart();
        //Move 5 feet
        while(opModeIsActive()) {
            telemetry.addData("ODS", robot.odsSensor.getLightDetected());
            telemetry.addData("Raw data", robot.odsSensor.getRawLightDetected());
            telemetry.update();
        }

    }
}
