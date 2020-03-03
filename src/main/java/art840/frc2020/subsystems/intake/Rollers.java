package art840.frc2020.subsystems.intake;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import art840.frc2020.map.Map;
import art840.frc2020.util.SparkMaxFactory;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Rollers extends SubsystemBase {
 public static class Config{
    // Motor
    public int Motor;
    
    public MotorType motorType = MotorType.kBrushless;
    
    //Controller 
    public double voltsFull;
    public double voltsReverse;
    public double rampTime;
 }   
 public final class Motors{
     final CANSparkMax motor;
  
private Motors() {
    motor = SparkMaxFactory.createMaster(config.motor, config.motorType);     
 }
 } 
    final Config config;
    public final Motors motors;
    
    public Rollers(){
        this(Map.map.intake.rollers);
    }
    public Rollers(final Config _config){
        config = _config;
        
        motors = new Motors();
    }
 
 public final void setOn() {
     
 }
 
 public final void setOff() {
     
 }
 
}
