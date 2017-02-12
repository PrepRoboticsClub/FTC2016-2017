package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

/**
 * Created by robotadmin on 12/1/16.
 */

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Mecanum-auto-test", group="Autonomous")
public class MecanumAutoTest extends LinearOpMode{
    ProjectMecanum robot       = new ProjectMecanum();
    float speed = 0;
    float angle = 0;
    float speedMultiplier = .5f;
    VectorF vectorF;
    float direction = 0;

    float frontLeft;
    float frontRight;
    float backLeft;
    float backRight;

    float greatestNum = 0;


    @Override
    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap);

        waitForStart();
        //Move 5 feet
        while(opModeIsActive()) {
            //Backwards
            SetPower(0, 1, 0);
            sleep(1000);
            //Forwards
            SetPower(0, -1, 0);
            sleep(1000);
            //Left
            SetPower(1, 0, 0);
            sleep(1000);
            //Right
            SetPower(-1, 0, 0);
            sleep(1000);
            requestOpModeStop();
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
