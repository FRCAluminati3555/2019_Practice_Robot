/**
 * FRC Team 3555
 * 
 * 2019 Practice Robot Code
 */

package frc.robot;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Joystick;
import frc.robot.systems.Camera;
import frc.robot.systems.DriveBase;

/**
 * This is the main robot class for the practice robot
 * 
 * @author Liam Poppleton
 * @author Caleb Heydon
 */

public class Robot extends TimedRobot {
  // Constants
  public static final double DRIVE_DOWN_POWER = 0.25;
  public static final long CLIMBER_PISTON_ACTUATION_TIME = 3000;
  public static final double DRIVE_UP_POWER = 0.25;

  public static final int STOP_AUTO_BUTTON_1 = 1;
  public static final int STOP_AUTO_BUTTON_2 = 2;

  public static final int CLIMB_UP_BUTTON_1 = 3;
  public static final int CLIMB_UP_BUTTON_2 = 4;

  // Start time
  public static long stateStartTime = 0l;

  // Climb down stop time
  public static long stateStopTime = 0l;

  // Robot state
  public static AutoState robotState = AutoState.ClimbDwnWaitCmd;

  // Declare objects for robot
  private Camera camera;
  private DriveBase drivetrain;
  private Joystick joystick;

  /**
   * This method returns true if the auto is enabled
   * 
   * @return Auto enabled
   */
  public boolean getAutoEnabled() {
    String directory = Filesystem.getDeployDirectory() + "/";
    int auto;

    // Read file
    try {
      Scanner scanner = new Scanner(new File(directory + "auto.conf"));
      auto = scanner.nextInt();
      scanner.close();
    } catch (IOException e) {
      // File does not exist
      return false;
    }

    // Auto enabled?
    if (auto == 1) {
      // Auto enabled
      return true;
    }

    // Auto is disabled
    return false;
  }

  /**
   * This method is called to initialize the robot
   */
  @Override
  public void robotInit() {
    // Print message to console
    System.out.println("[code] Initializing robot...");

    // Setup joystick
    joystick = new Joystick(1);
    System.out.println("[code] Joystick initialized");

    // Setup drivetrain
    drivetrain = DriveBase.getInstance();
    System.out.println("[code] Drivetrain initialized");

    // Initialize cameras
    camera = new Camera();

    camera.startCamera1();
    System.out.println("[code] Camera 1 initialized");

    camera.startCamera2();
    System.out.println("[code] Camera 2 initialized");

    // Check auto
    if (getAutoEnabled()) {
      System.out.println("[code] Auto enabled");
    } else {
      System.out.println("[code] Auto disabled");
    }

    System.out.println("[code] Robot initialized");
  }

  /**
   * This method is called periodically on the robot
   */
  @Override
  public void robotPeriodic() {

  }

  /**
   * This method is called to initialize auto
   */
  @Override
  public void autonomousInit() {
    System.out.println("[code] Initializing auto...");

    // Set first command
    stateStartTime = System.currentTimeMillis();
    stateStopTime = stateStartTime + CLIMBER_PISTON_ACTUATION_TIME;

    if (getAutoEnabled()) {
      // Auto is enabled
      robotState = AutoState.DwnPistonExtend;
    } else {
      // Auto is disabled
      drivetrain.enableJoystick();
      robotState = AutoState.ClimbDwnDone;
    }
  }

  /**
   * This method is called periodically during auto
   */
  @Override
  public void autonomousPeriodic() {
    // Check for an auto cancellation
    if (joystick.getRawButton(STOP_AUTO_BUTTON_1) && joystick.getRawButton(STOP_AUTO_BUTTON_2)) {
      // The drivers have cancelled the auto
      drivetrain.enableJoystick();
      robotState = AutoState.ClimbDwnDone;
      System.out.println("[code] Auto cancelled");
    }

    // Update drivetrain output with joystick if it is enabled
    drivetrain.arcadeDrive(joystick.getX(), joystick.getY(), joystick.getZ(), joystick.getMagnitude());

    if (drivetrain.getJoystickEnabled()) {
      // Process other joystick buttons here
    }

    // Process state
    switch (robotState) {
    case DwnPistonExtend:
      // ACTIVATE SOLENOID 4
      System.out.println("[code] Extending climber pistons...");

      if (System.currentTimeMillis() >= stateStopTime) {
        stateStartTime = System.currentTimeMillis();
        robotState = AutoState.DwnDriveFwdY;
      }
      break;
    case DwnDriveFwdY:
      if (drivetrain.drive(DRIVE_DOWN_POWER, DRIVE_DOWN_POWER, 1, stateStartTime)) {
        stateStartTime = System.currentTimeMillis();
        stateStopTime = stateStartTime + CLIMBER_PISTON_ACTUATION_TIME;

        robotState = AutoState.DwnPistonRetract;
      }
      break;
    case DwnPistonRetract:
      // ACTIVATE SOLENOID 5
      System.out.println("[code] Retracting climber pistons...");

      if (System.currentTimeMillis() >= stateStopTime) {
        stateStartTime = System.currentTimeMillis();
        robotState = AutoState.DwnDriveFwdZ;
      }
      break;
    case DwnDriveFwdZ:
      if (drivetrain.drive(DRIVE_DOWN_POWER, DRIVE_DOWN_POWER, 1, stateStartTime)) {
        drivetrain.enableJoystick();
        robotState = AutoState.ClimbDwnDone;
      }
      break;
    case ClimbDwnDone:
      break;
    default:
      // Something is wrong. Give control to the drivers
      System.out.println("[code] Invalid state detected");
      drivetrain.enableJoystick();
      robotState = AutoState.ClimbDwnDone;
      break;
    }
  }

  /**
   * This method is called to initialize teleop
   */
  @Override
  public void teleopInit() {
    System.out.println("[code] Initializing teleop...");

    // Set the auto state
    robotState = AutoState.ClimbUpWaitCmd;

    // Always enable joystick when teleop starts in case we are testing or something
    // is wrong
    drivetrain.enableJoystick();
  }

  /**
   * This method is called periodically during teleop
   */
  @Override
  public void teleopPeriodic() {
    // Check for an auto cancellation
    if (joystick.getRawButton(STOP_AUTO_BUTTON_1) && joystick.getRawButton(STOP_AUTO_BUTTON_2)) {
      // The drivers have cancelled the auto
      drivetrain.enableJoystick();
      robotState = AutoState.ClimbUpWaitCmd;
      System.out.println("[code] Auto cancelled");
    }

    // Update drivetrain output with joystick
    drivetrain.arcadeDrive(joystick.getX(), joystick.getY(), joystick.getZ(), joystick.getMagnitude());

    if (drivetrain.getJoystickEnabled()) {
      // Process other joystick buttons here
    }

    // Auto switch statement
    switch (robotState) {
    case ClimbUpWaitCmd:
      // Check for climb command
      if (joystick.getRawButton(CLIMB_UP_BUTTON_1) && joystick.getRawButton(CLIMB_UP_BUTTON_2)) {
        // Disable joystick
        drivetrain.disableJoystick();

        // Set state
        robotState = AutoState.UpLeverExtend;
      }
      break;
    case UpLeverExtend:
      // Run climber
      System.out.println("[code] Extending climber...");

      // RUN CLIMBER MOTOR
      // CHECK POTENTIOMETER

      // Set as done temporarily

      // Restart timer
      stateStartTime = System.currentTimeMillis();

      // Change state
      robotState = AutoState.UpDriveFwdZ;
      break;
    case UpDriveFwdZ:
      if (drivetrain.drive(DRIVE_UP_POWER, DRIVE_UP_POWER, 1, stateStartTime)) {
        stateStartTime = System.currentTimeMillis();
        stateStopTime = stateStartTime + CLIMBER_PISTON_ACTUATION_TIME;

        robotState = AutoState.UpPistonExtend;
      }
      break;
    case UpPistonExtend:
      // ACTIVATE SOLENOID 4
      System.out.println("[code] Extending climber pistons...");

      if (System.currentTimeMillis() >= stateStopTime) {
        stateStartTime = System.currentTimeMillis();
        robotState = AutoState.UpDriveFwdA;
      }
      break;
    case UpDriveFwdA:
      if (drivetrain.drive(DRIVE_UP_POWER, DRIVE_UP_POWER, 1, stateStartTime)) {
        robotState = AutoState.UpLeverRetract;
      }
      break;
    case UpLeverRetract:
      // Storing climber
      System.out.println("[code] Retracting climber...");

      // RUN CLIMBER MOTOR
      // CHECK POTENTIOMETER

      // Restart timer
      stateStartTime = System.currentTimeMillis();

      // Change state
      robotState = AutoState.UpDriveFwdB;
      break;
    case UpDriveFwdB:
      if (drivetrain.drive(DRIVE_UP_POWER, DRIVE_UP_POWER, 1, stateStartTime)) {
        stateStartTime = System.currentTimeMillis();
        stateStopTime = stateStartTime + CLIMBER_PISTON_ACTUATION_TIME;

        // Change state
        robotState = AutoState.UpPistonRetract;
      }
      break;
    case UpPistonRetract:
      System.out.println("[code] Retracting climber pistons...");
      // ACTIVATE SOLENOID 5

      if (System.currentTimeMillis() >= stateStopTime) {
        stateStartTime = System.currentTimeMillis();
        robotState = AutoState.UpDriveFwdC;
      }
      break;
    case UpDriveFwdC:
      if (drivetrain.drive(DRIVE_UP_POWER, DRIVE_UP_POWER, 1, stateStartTime)) {
        // Change state
        robotState = AutoState.ClimbUpDone;
      }
      break;
    case ClimbUpDone:
      // Enable joystick
      drivetrain.enableJoystick();

      // Set state
      robotState = AutoState.ClimbUpWaitCmd;
      break;
    default:
      // Something is wrong. Give control to the drivers
      System.out.println("[code] Invalid state detected");
      drivetrain.enableJoystick();
      robotState = AutoState.ClimbUpWaitCmd;
      break;
    }
  }

  /**
   * This method is called periodically during a robot test
   */
  @Override
  public void testPeriodic() {

  }

  /**
   * This method is called when the robot is disabled
   */
  @Override
  public void disabledInit() {
    System.out.println("[code] Robot disabled");
  }

  /**
   * This method is called periodically while the robot is disabled
   */
  @Override
  public void disabledPeriodic() {

  }
}
