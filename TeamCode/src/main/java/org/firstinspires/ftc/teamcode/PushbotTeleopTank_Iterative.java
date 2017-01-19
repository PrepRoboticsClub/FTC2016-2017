/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.util.Range;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

@TeleOp(name="Pushbot: Teleop Tank", group="Pushbot")
// @Disabled
public class PushbotTeleopTank_Iterative extends OpMode{

    /* Declare OpMode members. */
    Project2 robot       = new Project2(); // use the class created to define a Pushbot's hardware
    VuforiaTrackables beacons;
    boolean slow = false;
    boolean up = false;
    boolean down = false;
    int time;
                                                         // could also use HardwarePushbotMatrix class.
   // double          clawOffset  = 0.0 ;                  // Servo mid position
    //final double    CLAW_SPEED  = 0.02 ;                 // sets rate to move servo


    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        //Vuforia
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

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");    //
        updateTelemetry(telemetry);
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        beacons.activate();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        //Vuforia
        for(VuforiaTrackable beac: beacons) {
            OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) beac.getListener()).getPose();
            //The Matrix is real
            if(pose != null){
                VectorF translation = pose.getTranslation();

                telemetry.addData(beac.getName() + "-Translation", translation);

                double DegreesToTurn = Math.toDegrees(Math.atan2(translation.get(1), translation.get(2)));

                telemetry.addData(beac.getName() + "-Degrees", DegreesToTurn);
            }
        }
        telemetry.update();

        double left;
        double right;

        // Run wheels in tank mode (note: The joystick goes negative when pushed forwards, so negate it)
        left = -gamepad1.left_stick_y;
        right = -gamepad1.right_stick_y;
        if(gamepad1.a){
            slow = !slow;
        }
        if(slow) {
            robot.leftMotor.setPower(left/6);
            robot.rightMotor.setPower(right/6);
        }else {
            robot.leftMotor.setPower(left);
            robot.rightMotor.setPower(right);
        }

        if(gamepad1.dpad_up){
            down = false;
            up = true;
        }else if(gamepad1.dpad_down){
            down = true;
            up = false;
        }

        if(up && !robot.touchSensor.isPressed()){
            robot.armMotor.setPower(-.21);
        }else if(robot.touchSensor.isPressed()){
            robot.armMotor.setPower(0);
            robot.armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        if(down){
            robot.armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            robot.armMotor.setPower(.15);
            time += 1;
        }

        if(time >= 25){
            time = 0;
            down = false;
            up = false;
            robot.armMotor.setPower(0);
            robot.armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        //if(gamepad1.right_trigger > .7f){
        //    robot.pushy.setPosition(.9);
        //}
        //else if (gamepad1.left_trigger > .7f){
        //    robot.pushy.setPosition(.1);
        //}

        if(gamepad1.right_trigger > gamepad1.left_trigger){
            robot.pushy.setPosition(gamepad1.right_trigger/2 * .95+.5);
        }else{
            robot.pushy.setPosition(gamepad1.left_trigger/2 * -.95+.5);
        }
                /* if(AARON.isJavelinMaster() = true){
        *   ECHO "YOU ARE WRONG"
        * }
        * else{
        *   ECHO "YOU ARE RIGHT"
        * }
        * /
        // Use gamepad left & right Bumpers to open and close the claw //TODO hack the system
        if (gamepad1.right_bumper)

            clawOffset += CLAW_SPEED;
        else if (gamepad1.left_bumper)
            clawOffset -= CLAW_SPEED;

        // Move both servos to new position.  Assume servos are mirror image of each other.
        clawOffset = Range.clip(clawOffset, -0.5, 0.5);
        robot.leftClaw.setPosition(robot.MID_SERVO + clawOffset);
        robot.rightClaw.setPosition(robot.MID_SERVO - clawOffset);

        // Use gamepad buttons to move the arm up (Y) and down (A)
        if (gamepad1.y)
            robot.armMotor.setPower(robot.ARM_UP_POWER);
        else if (gamepad1.a)
            robot.armMotor.setPower(robot.ARM_DOWN_POWER);
        else
            robot.armMotor.setPower(0.0);


        // Send telemetry message to signify robot running;
        telemetry.addData("claw",  "Offset = %.2f", clawOffset);*/
        updateTelemetry(telemetry);
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        robot.pushy.setPosition(robot.MID_SERVO);
        robot.armMotor.setPower(0);
    }

}
