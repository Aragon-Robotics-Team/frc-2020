// package art840.frc2020.commands;

// import art840.frc2020.Robot;
// import art840.frc2020.map.Map.Shooter;
// import art840.frc2020.subsystems.sensors.HallEffect;
// import art840.frc2020.subsystems.shooter.Turret;
// import edu.wpi.first.wpilibj2.command.CommandBase;
// import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

// public class StopTurret extends CommandBase {

//     HallEffect hallEffect = new HallEffect();
//     Turret turret = new Turret();
//     public StopTurret(boolean _magnetSensed){
//         addRequirements(Robot.shooter.turret);
//     }
    
//     public void periodic() { 
//     }
   
//     public void execute() {
//         if (hallEffect.teleopPeriodic() == true) {
//             turret.setDisabled(true);
//         } else {
//         }
//     }
    
//     public boolean isFinished() {
        
//     }
    
//     public void end() {
        
//     }
// }
