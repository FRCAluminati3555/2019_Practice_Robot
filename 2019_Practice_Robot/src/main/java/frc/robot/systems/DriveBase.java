/**
 * FRC Team 3555
 * 
 * 2019 Practice Robot Code
 */

package frc.robot.systems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

/**
 * This class controls the drivetrain
 * 
 * @author Liam Poppleton
 * @author Caleb Heydon
 */

public class DriveBase {
    // Define joystick deadzone
    public static final double JOYSTICK_DEADZONE = 0.05;

    // Define reverse value
    public static final double REVERSE_POINT = 0.15;

    // Declare talons
    private TalonSRX flm;
    private TalonSRX frm;
    private TalonSRX blm;
    private TalonSRX brm;

    // Private instance for drivetrain
    private static DriveBase instance = new DriveBase();

    /**
     * This method returns the current instance of DriveBase
     * 
     * @return The instance
     */
    public static DriveBase getInstance() {
        return instance;
    }

    /**
     * Private constructor for DriveBase
     */
    private DriveBase() {
        // Initialize talon devices
        flm = new TalonSRX(41);
        frm = new TalonSRX(42);
        blm = new TalonSRX(43);
        brm = new TalonSRX(44);

        // Invert right side of drivetrain
        frm.setInverted(true);
        brm.setInverted(true);

        // Configure encoders
        blm.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        brm.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);

        // Zero encoders
        resetEncoders();

        // Set up slave talons
        flm.follow(blm);
        frm.follow(brm);
    }

    /**
     * This method tells the drivetrain to drive based on the joystick input
     */
    public void drive(ControlMode controlMode, double controlX, double controlY, double throttle, double magnitude) {
        // Invert controlY
        controlY = -controlY;

        // Invert throttle
        throttle = -throttle;

        // Correct throttle value
        throttle += 1;
        throttle *= 0.5;

        // Make sure the throttle is within bounds
        if (throttle < 0) {
            throttle = 0;
        } else if (throttle > 1) {
            throttle = 1;
        }

        // Check deadzone
        if (magnitude <= JOYSTICK_DEADZONE) {
            // Zero controls
            controlX = 0;
            controlY = 0;
        }

        // Invert controlX if needed
        if (controlY < -REVERSE_POINT) {
            controlX = -controlX;
        }

        // Square input
        boolean signX = (controlX >= 0) ? false : true;
        boolean signY = (controlY >= 0) ? false : true;

        controlX = Math.abs(controlX);
        controlY = Math.abs(controlY);

        controlX *= controlX;
        controlY *= controlY;

        if (signX) {
            controlX = -controlX;
        }

        if (signY) {
            controlY = -controlY;
        }

        // Apply throttle
        controlX *= throttle;
        controlY *= throttle;

        // Get drive values
        double leftPower = controlY + controlX;
        double rightPower = controlY - controlX;

        // Make sure the powers are within bounds
        if (leftPower < -1) {
            leftPower = -1;
        } else if (leftPower > 1) {
            leftPower = 1;
        }

        if (rightPower < -1) {
            rightPower = -1;
        } else if (rightPower > 1) {
            rightPower = 1;
        }

        blm.set(controlMode, leftPower);
        brm.set(controlMode, rightPower);
    }

    /**
     * This method zeros the encoders
     */
    public void resetEncoders() {
        blm.setSelectedSensorPosition(0);
        brm.setSelectedSensorPosition(0);
    }

    /**
     * Returns the left encoder value
     * 
     * @return The encoder value
     */
    public int getBLMValues() {
        return blm.getSelectedSensorPosition();
    }

    /**
     * Returns the right encoder value
     * 
     * @return The encoder value
     */
    public int getBRMValues() {
        return brm.getSelectedSensorPosition();
    }
}
