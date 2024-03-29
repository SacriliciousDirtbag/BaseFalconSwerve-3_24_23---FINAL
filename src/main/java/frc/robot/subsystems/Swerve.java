package frc.robot.subsystems;

import frc.robot.SwerveModule;
import frc.robot.Constants;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;

import com.ctre.phoenix.sensors.Pigeon2;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Swerve extends SubsystemBase {
    public SwerveDriveOdometry swerveOdometry;
    public SwerveModule[] mSwerveMods;
    public Pigeon2 gyro;

     public Swerve() {
        gyro = new Pigeon2(Constants.Swerve.pigeonID, "1056_Canivore");
        gyro.configFactoryDefault();
        zeroGyro();

        mSwerveMods = new SwerveModule[] {
            new SwerveModule(0, Constants.Swerve.Mod0.constants),
            new SwerveModule(1, Constants.Swerve.Mod1.constants),
            new SwerveModule(2, Constants.Swerve.Mod2.constants),
            new SwerveModule(3, Constants.Swerve.Mod3.constants)
        };

        /* By pausing init for a second before setting module offsets, we avoid a bug with inverting motors.
         * See https://github.com/Team364/BaseFalconSwerve/issues/8 for more info.
         */
        Timer.delay(1);
        resetModulesToAbsolute();


        swerveOdometry = new SwerveDriveOdometry(Constants.Swerve.swerveKinematics, getYaw(), getModulePositions());
    } 
   

    public void drive(Translation2d translation, double rotation, boolean fieldRelative, boolean isOpenLoop) {
        SwerveModuleState[] swerveModuleStates =
            Constants.Swerve.swerveKinematics.toSwerveModuleStates(
                fieldRelative ? ChassisSpeeds.fromFieldRelativeSpeeds(
                                    translation.getX(), 
                                    translation.getY(), 
                                    rotation, 
                                    getYaw()
                                )
                                : new ChassisSpeeds(
                                    translation.getX(), 
                                    translation.getY(), 
                                    rotation)
                                );
        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, Constants.Swerve.maxSpeed);

        for(SwerveModule mod : mSwerveMods){
            mod.setDesiredState(swerveModuleStates[mod.moduleNumber], isOpenLoop);
        }
    }    

    /* Used by SwerveControllerCommand in Auto */
    public void setModuleStates(SwerveModuleState[] desiredStates) {
        SwerveDriveKinematics.desaturateWheelSpeeds(desiredStates, Constants.Swerve.maxSpeed);
        
        for(SwerveModule mod : mSwerveMods){
            mod.setDesiredState(desiredStates[mod.moduleNumber], false);
        }
    }    

    public Pose2d getPose() {
        return swerveOdometry.getPoseMeters();
    }

    public void resetOdometry(Pose2d pose) {
        swerveOdometry.resetPosition(getYaw(), getModulePositions(), pose);
    }

    public SwerveModuleState[] getModuleStates(){
        SwerveModuleState[] states = new SwerveModuleState[4];
        for(SwerveModule mod : mSwerveMods){
            states[mod.moduleNumber] = mod.getState();
        }
        return states;
    }

    public SwerveModulePosition[] getModulePositions(){
        SwerveModulePosition[] positions = new SwerveModulePosition[4];
        for(SwerveModule mod : mSwerveMods){
            positions[mod.moduleNumber] = mod.getPosition();
        }
        return positions;
    }

    public static double getSpdMult(){
        return SpeedMultiplier;
    }

    

    

    public void zeroGyro(){
        gyro.setYaw(0);
    }
    public static Double SpeedMultiplier = 1.0;
    public static void mode1(){
        SpeedMultiplier = 0.03;
        System.out.print(SpeedMultiplier);
    }
    public static void mode0(){
        SpeedMultiplier = 1.0;
    }
    public Rotation2d getYaw() {
        return (Constants.Swerve.invertGyro) ? Rotation2d.fromDegrees(360 - gyro.getYaw()) : Rotation2d.fromDegrees(gyro.getYaw());
    }

    /*public Rotation2d getPitch() {
        return (Constants.Swerve.invertGyro) ? Rotation2d.fromDegrees(360 - gyro.getPitch()) : Rotation2d.fromDegrees(gyro.getPitch());
    }*/

    public void resetModulesToAbsolute(){
        for(SwerveModule mod : mSwerveMods){
            mod.resetToAbsolute();
        }
    }

    public boolean isTiltedForward(){
        return gyro.getRoll() < -4;
    }

    public boolean isTiltedBackwards(){
        return gyro.getRoll() > 4;
    }

    @Override
    public void periodic(){
        isTiltedBackwards();
        isTiltedForward();

        swerveOdometry.update(getYaw(), getModulePositions());
        SmartDashboard.putNumber("Gyro Yaw", gyro.getYaw());
        SmartDashboard.putNumber("Gyro Pitch", gyro.getRoll());

        SmartDashboard.putBoolean("IsTiltedForward", isTiltedForward());
        SmartDashboard.putBoolean("isTiltedBack", isTiltedBackwards());


        for(SwerveModule mod : mSwerveMods){
            SmartDashboard.putNumber("Mod " + mod.moduleNumber + "Angle Cancoder", mod.getCanCoder().getDegrees());
            SmartDashboard.putNumber("Mod " + mod.moduleNumber + "Angle Integrated", mod.getPosition().angle.getDegrees());
            SmartDashboard.putNumber("Mod " + mod.moduleNumber + " Velocity", mod.getState().speedMetersPerSecond);
        }
    }


    public void drive(Rotation2d rotation2d, double x, boolean fieldRelative, boolean isOpenLoop) {
    }
}