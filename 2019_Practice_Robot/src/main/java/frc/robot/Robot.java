/**
 * FRC Team 3555
 * 
 * 2019 Practice Robot Code
 */

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import frc.robot.systems.AutoState;
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

  // Start time
  public static long stateStartTime = 0l;

  // Climb down stop time
  public static long climbDownStopTime = 0l;

  // Robot state
  public static AutoState robotState = AutoState.ClimbDwnWaitCmd;

  // Declare objects for robot
  private Camera camera;
  private DriveBase drivetrain;
  private Joystick joystick;

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
    climbDownStopTime = stateStartTime + 3000;

    robotState = AutoState.DwnPistonExtend;
  }

  /**
   * This method is called periodically during auto
   */
  @Override
  public void autonomousPeriodic() {
    switch (robotState) {
    case DwnPistonExtend:
      // ACTIVATE SOLENOID 4
      System.out.println("[code] Extending climber pistons...");

      if (System.currentTimeMillis() >= climbDownStopTime) {
        stateStartTime = System.currentTimeMillis();
        robotState = AutoState.DwnDriveFwdY;
      }
      break;
    case DwnDriveFwdY:
      if (drivetrain.drive(DRIVE_DOWN_POWER, DRIVE_DOWN_POWER, 1, stateStartTime)) {
        stateStartTime = System.currentTimeMillis();
        climbDownStopTime = stateStartTime + 3000;

        robotState = AutoState.DwnPistonRetract;
      }
      break;
    case DwnPistonRetract:
      // ACTIVATE SOLENOID 5
      System.out.println("[code] Retracting climber pistons...");

      if (System.currentTimeMillis() >= climbDownStopTime) {
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
      robotState = AutoState.ClimbDwnDone;
      break;
    }

    // Update drivetrain output with joystick
    drivetrain.arcadeDrive(joystick.getX(), joystick.getY(), joystick.getZ(), joystick.getMagnitude());
  }

  /**
   * This method is called to initialize teleop
   */
  @Override
  public void teleopInit() {
    System.out.println("[code] Initializing teleop...");

    // Always enable joystick when teleop starts in case we are testing or something
    // is wrong
    drivetrain.enableJoystick();
  }

  /**
   * This method is called periodically during teleop
   */
  @Override
  public void teleopPeriodic() {
    // Update drivetrain output with joystick
    drivetrain.arcadeDrive(joystick.getX(), joystick.getY(), joystick.getZ(), joystick.getMagnitude());
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
