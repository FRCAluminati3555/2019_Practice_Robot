/**
 * FRC Team 3555
 * 
 * 2019 Practice Robot Code
 */

package frc.robot.systems;

/**
 * This enum lists all auto states
 *
 * @author Caleb Heydon
 */

public enum AutoState {
    ClimbDwnWaitCmd, DwnPistonExtend, DwnDriveFwdY, DwnPistonRetract, ClimbUpWaitCmd, UpLeverExtend, UpDriveFwdZ,
    UpPistonExtend, UpDriveFwdA, UpLeverRetract, UpDriveFwdB, UpPistonRetract, UpDriveFwdC, ClimbUpDone, ClimbDwnDone,
    DwnDriveFwdZ
}
