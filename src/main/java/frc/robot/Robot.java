// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
import java.util.HashSet;

import javax.swing.text.Position;

import edu.wpi.first.apriltag.AprilTagDetector;
import edu.wpi.first.apriltag.AprilTagDetector.Config;
import edu.wpi.first.apriltag.AprilTagDetection;
import edu.wpi.first.apriltag.AprilTagPoseEstimator;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.Swerve;
import frc.robot.subsystems.cameraSubsystem;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSink;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.TimedRobot;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  public static CTREConfigs ctreConfigs;

  private Command m_autonomousCommand;
  private Command m_balanceCommand;
  private Command m_turnCommand;
  private Command m_shooterCommand;

  private RobotContainer m_robotContainer;
  
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    ctreConfigs = new CTREConfigs();

    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();


    //cameraSubsystem camera = new cameraSubsystem();
    //camera.startCamera();

}


  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();

  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
    m_robotContainer.s_lightSubsystem.disableInit();
  }

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    if (m_robotContainer.m_Chooser.getSelected() != null) {
      m_robotContainer.m_Chooser.getSelected().schedule();
    }
    

    //m_shooterCommand = m_robotContainer.getShooterCommand();
    //m_turnCommand = m_robotContainer.getTurnCommand();
/*     m_autonomousCommand = m_robotContainer.getAutonomousCommand();
 */    //m_balanceCommand = m_robotContainer.getBalanceCommand();

    
    /*if(m_turnCommand != null) {
      m_turnCommand.schedule();
    }*/
/*
    m_robotContainer.s_ScoreCubeHigh.initialize();
    Timer.delay(0.25);
    m_robotContainer.s_IntakeSubsystem.wheelForward();
    Timer.delay(1.25);
    m_robotContainer.s_IntakeReset.initialize();
    m_robotContainer.s_IntakeSubsystem.resetAll();

    Timer.delay(0.5);
    */

    /*m_balanceCommand = m_robotContainer.getBalanceCommand();
    if (m_balanceCommand != null) {
      m_balanceCommand.schedule();
    }*/

    // schedule the autonomous commands (example)
    /*if (m_shooterCommand != null) {
      m_shooterCommand.schedule();
    }*/
    
    /* if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    } */
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {


    
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.]
    Command autoComm = m_robotContainer.m_Chooser.getSelected();
        if (autoComm != null && autoComm.isScheduled()) {
      // m_autonomousCommand.cancel();
      // m_balanceCommand.cancel();
      autoComm.cancel();
    }
  }
  public static double translationSpeed;
  public static double strafeSpeed;
  public double translationTarget;
  public double strafeTarget;
  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    translationTarget = -Math.cbrt(Math.pow(RobotContainer.getTranslationAxis(), 2))
    * Swerve.getSpdMult() * RobotContainer.getTranslationAxis()/
    Math.abs(RobotContainer.getTranslationAxis());
    strafeTarget = -Math.cbrt(Math.pow(RobotContainer.getStrafeAxis(), 2)
    * Swerve.getSpdMult() * (RobotContainer.getStrafeAxis()/
    Math.abs(RobotContainer.getStrafeAxis())));

    strafeSpeed += (strafeTarget - strafeSpeed) / 20;
    translationSpeed += (translationTarget - translationSpeed) / 20;
    if (Math.round(strafeSpeed)*100 == 0) {
      strafeSpeed = 0;
    }
    if (Math.round(translationSpeed)*100 == 0) {
      translationSpeed = 0;
    }


  }

  public static double getStrafeSpeed(){
    return strafeSpeed;
  }

  public static double getTranslationSpeed(){
    return translationSpeed;
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}


