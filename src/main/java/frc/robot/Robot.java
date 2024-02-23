// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;;;;

/**
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {
  private DifferentialDrive m_robotDrive;
  private Joystick m_driver;
  private Joystick m_operator;
  private Long shootTime=0L; // the time for shooting mechanism

  private final WPI_VictorSPX m_leftFrontMotor = new WPI_VictorSPX(4);
  private final WPI_VictorSPX m_rightFrontMotor = new WPI_VictorSPX(5);
  private final WPI_VictorSPX m_leftRearMotor = new WPI_VictorSPX(51);
  private final WPI_VictorSPX m_rightRearMotor = new WPI_VictorSPX(3);

  private final WPI_VictorSPX m_shooter_wheel = new WPI_VictorSPX(2);
  private final WPI_VictorSPX m_shooter_feed = new WPI_VictorSPX(10);
  private final WPI_VictorSPX m_shooter_load = new WPI_VictorSPX(20);
  // private final WPI_VictorSPX m_intake_drive = new WPI_VictorSPX(9);
  // private final WPI_VictorSPX m_intake_fold = new WPI_VictorSPX(10);
  // private final WPI_VictorSPX m_leftRearMotor = new WPI_VictorSPX(1); we dont know the number
  @Override
  public void robotInit() {
    SendableRegistry.addChild(m_robotDrive, m_leftFrontMotor);
    SendableRegistry.addChild(m_robotDrive, m_rightFrontMotor);

    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_rightFrontMotor.setInverted(true);
    m_shooter_load.setInverted(true);

    // Make the rears follow the fronts...
    m_leftRearMotor.follow((m_leftFrontMotor));
    m_rightRearMotor.follow((m_rightFrontMotor));

    m_robotDrive = new DifferentialDrive(m_leftFrontMotor::set, m_rightFrontMotor::set);
    m_driver = new Joystick(0);
    m_operator = new Joystick(1);
  }
   //overrides the main code for 2 seconds so you cant move for 2 seconds
  @Override
  public void teleopPeriodic() {
    m_robotDrive.tankDrive(-m_driver.getRawAxis(1), -m_driver.getRawAxis(5));


    // Right trigger controls shooter wheel speed
    m_shooter_wheel.set(m_operator.getRawAxis((3)));

    // Right bumper moves note from feeder to shooter wheel
    var feed_speed = m_operator.getRawButton(6) ? 1.0 : 0.0;
    m_shooter_feed.set(feed_speed);

    // turning on the shooter wheel then shooter feed and shooter load in correct order at the correct time then turning it off
    // if the button is not being pressed
    if ((m_operator.getRawAxis(3) >= 0) && (shootTime == 0L)) {
      shootTime=System.currentTimeMillis();
    }
    //starts the actual procces of shooting the note
    if ((m_operator.getRawAxis(3) >= 0) && (shootTime + 1500 <= System.currentTimeMillis())) {
      m_shooter_load.set(1.0);
      Timer.delay(0.5);
      m_shooter_feed.set(1.0);
      Timer.delay(1.0);
    }
    // right trigger controls the launcher
    if (!(m_operator.getRawAxis(3) != 0)) {
      shootTime = 0L;
    }
    
    // Left bumper controls the loader
    var loader_speed = m_operator.getRawButton(5) ? 1.0 : 0.0;
    m_shooter_load.set(loader_speed);

  }
}
