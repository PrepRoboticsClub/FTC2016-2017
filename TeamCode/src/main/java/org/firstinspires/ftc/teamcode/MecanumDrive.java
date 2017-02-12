package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

/**
 * Created by robotadmin on 12/1/16.
 */

@TeleOp(name="MecanumDrive", group="Mecanum")
public class MecanumDrive extends LinearOpMode{
    ProjectMecanum robot       = new ProjectMecanum();
    //Project3 robot       = new Project3();
    float speed = 0;
    float angle = 0;
    float speedMultiplier = 1f;
    VectorF vectorF;
    float direction = 0;

    float frontLeft;
    float frontRight;
    float backLeft;
    float backRight;

    float greatestNum = 0;

    boolean up = false;
    boolean down = false;
    boolean heldDown = false;
    boolean slow = false;
    int time;

    @Override
    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap);
        waitForStart();
        //Move 5 feet
        while(opModeIsActive()) {
            vectorF = new VectorF(-gamepad1.left_stick_x, gamepad1.left_stick_y);
            speed = vectorF.magnitude();
            vectorF = new VectorF(vectorF.get(0) / speed, vectorF.get(1) / speed);
            angle = (float) Math.atan2(vectorF.get(0), vectorF.get(1));
            direction = gamepad1.right_stick_x * -1;

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

            //Arm
            if(gamepad1.dpad_up){
                down = false;
                up = true;
            }else if(gamepad1.dpad_down){
                down = true;
                up = false;
            }


            if(up && !robot.touchSensor.isPressed()){
                robot.arm.setPower(-.21);
                telemetry.addData("arm", robot.arm.getCurrentPosition());
            }else if(robot.touchSensor.isPressed()){
                robot.arm.setPower(0);
                robot.arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }

            if(down){
                robot.arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                robot.arm.setPower(.15);
                time += 1;
            }

            if(time >= 25){
                time = 0;
                down = false;
                up = false;
                robot.arm.setPower(0);
                robot.arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }

            //Servo
            if(gamepad1.right_trigger > gamepad1.left_trigger){
                robot.pushy.setPosition(gamepad1.right_trigger/2 * .95+.5);
            }else{
                robot.pushy.setPosition(gamepad1.left_trigger/2 * -.95+.5);
            }
            //Slow
            if(gamepad1.a && !heldDown){
                slow = !slow;
                speedMultiplier = slow ? .4f : 1f;
                heldDown = true;
            }else{
                heldDown = false;
            }
        }

        robot.frontLeft.setPower(0);
        robot.frontRight.setPower(0);
        robot.backLeft.setPower(0);
        robot.backRight.setPower(0);
    }

    float lerp(float a, float b, float f)
    {
        return (a * (1.0f - f)) + (b * f);
    }
}
