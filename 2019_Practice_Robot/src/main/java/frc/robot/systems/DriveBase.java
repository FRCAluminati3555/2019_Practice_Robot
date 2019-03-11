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

    // Drive status
    private boolean joystickEnabled = false;

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
     * Returns true if the joystick is enabled
     * 
     * @return True if the joystick is enabled
     */
    public boolean getJoystickEnabled() {
        return joystickEnabled;
    }

    /**
     * Enables or disables the joystick
     */
    private void setJoystickEnabled(boolean joystickEnabled) {
        this.joystickEnabled = joystickEnabled;
    }

    /**
     * Disables the joystick and stops the drive
     */
    public void disableJoystick() {
        setJoystickEnabled(false);
        drive(0, 0);

        System.out.println("[code] Joystick disabled");
    }

    /**
     * Enables the joystick
     */
    public void enableJoystick() {
        setJoystickEnabled(true);
        drive(0, 0);

        System.out.println("[code] Joystick enabled");
    }

    /**
     * Drives the motors with percentages (don't need to invert rightPower)
     * 
     * @param leftPower  The left power
     * @param rightPower The right power
     */
    public void drive(double leftPower, double rightPower) {
        blm.set(ControlMode.PercentOutput, leftPower);
        brm.set(ControlMode.PercentOutput, rightPower);
    }

    /**
     * Drives the motors with percentages for a period of time (don't need to invert
     * rightPower)
     * 
     * @param leftPower  The left power
     * @param rightPower The right power
     * @param seconds    The time to drive
     * @param startTiem  The time the maneuver was first started
     * @return True if maneuver is complete
     */
    public boolean drive(double leftPower, double rightPower, double seconds, long startTime) {
        // Compute time in milliseconds
        long waitTime = (int) (1000 * seconds);

        // Maneuver done?
        if (System.currentTimeMillis() >= startTime + waitTime) {
            // Stop robot and return true
            drive(0, 0);

            return true;
        }

        // Call other drive method if the maneuver is not complete
        drive(leftPower, rightPower);

        // Return false because the maneuver is not completed
        return false;
    }

    /**
     * This method tells the drivetrain to drive based on the joystick input
     * 
     * @param controlX  Joystick x
     * @param controlY  Joystick y
     * @param throttle  Joystick z
     * @param magnitude Joystick magnitude
     */
    public void arcadeDrive(double controlX, double controlY, double throttle, double magnitude) {
        // Do nothing if the joystick is disabled
        if (!getJoystickEnabled()) {
            return;
        }

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

        blm.set(ControlMode.PercentOutput, leftPower);
        brm.set(ControlMode.PercentOutput, rightPower);
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
