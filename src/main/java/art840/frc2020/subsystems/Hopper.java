package art840.frc2020.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import art840.frc2020.map.Map;
import art840.frc2020.util.SparkMaxFactory;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Hopper extends SubsystemBase {
    public static class Config {
        public int motorPort;
        public boolean motorInvert;
    }
    
    final Config config;
    
    final CANSparkMax leftmotor;
    final CANSparkMax rightmotor;
    
    public Hopper() {
        this(Map.map.getHopperConfig());
    }

    public Hopper(final Config hopperConfig) {
        config = hopperConfig;
        
        // Ideally make it so the setInverted uses config.motorInvert, but only 1 should be inverted
        leftmotor = SparkMaxFactory.create(config.motorPort, MotorType.kBrushless);
        leftmotor.setInverted(true);
        leftmotor.setIdleMode(IdleMode.kCoast);
        
        rightmotor = SparkMaxFactory.create(config.motorPort, MotorType.kBrushless);
        rightmotor.setInverted(false);
        rightmotor.setIdleMode(IdleMode.kCoast);
    }
    
    //We dont need PID, we just want it on, off, or unjamming (reverse) 
    public final void on() {
        leftmotor.set(1);
        rightmotor.set(1);
    }
    
    public final void unjam() {
        leftmotor.set(-0.2);
        rightmotor.set(0.2);
    }
    
    public final void off() {
        leftmotor.set(0);
        rightmotor.set(0);
    }
}
