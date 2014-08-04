package org.nashua.tt151.libraries.parsers;

/**
 * A class containing the minimum and maximum values for each key described
 * in the protocol.
 * 
 * @author Brennan Ringey
 * @version 1.0
 */
public class ValueConstants {
	
	// No instantiation
	private ValueConstants() {}
	
	// Constants
	public static final double ANGLE_MIN = 0.0;
	public static final double ANGLE_MAX = 99.99;
	
	public static final double SHOOTER_MIN = 0.0;
	public static final double SHOOTER_MAX = 9999.999;
	
	public static final double ENCODER_MIN = 0.0;
	public static final double ENCODER_MAX = 9999.999;
	
	public static final short ANALOG_MIN = -9999;
	public static final short ANALOG_MAX = 9999;
	
	public static final short PWM_MIN = 0;
	public static final short PWM_MAX = 255;
	
	public static final short RELAY_MIN = -1;
	public static final short RELAY_MAX = 1;
	
	public static final short SOLENOID_MIN = 0;
	public static final short SOLENOID_MAX = 1;
}
