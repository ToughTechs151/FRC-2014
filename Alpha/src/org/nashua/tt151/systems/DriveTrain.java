package org.nashua.tt151.systems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.templates.Alpha;
import edu.wpi.first.wpilibj.templates.Dash;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.nashua.tt151.libraries.Controller.DualAction;
import org.nashua.tt151.util.MathTools;

/**
 * 2014 Drive Train Control System
 *
 * @author Kareem El-Faramawi
 */
public class DriveTrain extends Subsystem {
	private static DriveTrain INSTANCE;
	//Right side motors
	private static final Talon r1 = new Talon( 1, 1 );
	private static final Talon r2 = new Talon( 1, 2 );
	//Left side motors
	private static final Talon l1 = new Talon( 1, 3 );
	private static final Talon l2 = new Talon( 1, 4 );
	//Encoders on both transmissions
	private static final Encoder leftEncoder = new Encoder( 1, 1, 1, 2, true );
	private static final Encoder rightEncoder = new Encoder( 1, 3, 1, 4 );

	//Conversion factor from encoder ticks (256 ticks/rev) to distance travelled in inches by 4in diameter wheels
	private final double DISTANCE_PER_TICK = Math.PI / 64.0;

	private double mult = 0.75; //Multiplier for scaling the motor speeds

	private final double ADJUSTMENT_FACTOR = 0.002; //Scales down motor adjustments to reduce the coarseness of the compensation
	private final double MAX_ADJUSTMENT = 0.1; //Limits how far a motor can be adjusted
	private volatile double right = 0.0;
	private volatile double left = 0.0;
	private volatile double rightAdjustment = 0.0; //Amount to adjust the right side motors by
	private volatile double leftAdjustment = 0.0; //Amount to adjust the left side motors by

	private DriveTrain() {
		/*
		 * This year's chasse has been experiencing a problem where the robot
		 * will curve off to one side when it should be driving straight. To fix
		 * this issue, the following Thread will look at how far each side has
		 * travelled using readings from encoders and adjust the speeds of the
		 * motors to compensate for the slower side depending on the input
		 * joystick values. This algorithm will use the faster side as a
		 * reference, it will speed up the slower side by a maximum of
		 * MAX_ADJUSTMENT, then if the robot is still curving, it will slow down
		 * the faster side by a maximum of MAX_ADJUSTMENT.
		 */
		new Timer().scheduleAtFixedRate( new TimerTask() {
			double jValLeft; //Left joystick value
			double jValRight; //Right joystick value
			double tickLeft; //Ticks from the left encoder
			double tickRight; //Ticks fro the right encoder
			double jRatio; //Ratio of one joystick value to the other
			double error; //How far off in encoder ticks one side is from the other

			public void run() {
				if ( Alpha.isRobotEnabled() ) {
					try {
						//Get joystick values
						jValLeft = -left * mult;
						jValRight = right * mult;
						//Reset encoder ticks
						tickLeft = leftEncoder.get();
						tickRight = rightEncoder.get();
						//Let the robot run for a small dt
						Thread.sleep( 5 );
						//Get the actual number of ticks during the dt
						tickLeft = leftEncoder.get() - tickLeft;
						tickRight = rightEncoder.get() - tickRight;
						//Only make adjustments if the joysticks pass a small deadzone
						if ( Math.abs( jValRight ) >= 0.1 && Math.abs( jValLeft ) >= 0.1 ) {
							//Use the faster side as a reference for the adjustment
							if ( Math.abs( jValRight ) > Math.abs( jValLeft ) ) { //Right side is faster
								jRatio = jValRight / jValLeft; //Ratio of right:left joystick value
								error = tickLeft - ( tickRight / jRatio ); //Difference between expected and actual left ticks
								leftAdjustment += error * ADJUSTMENT_FACTOR; //Ajusts the left side speed be a scaled down error

								//If the left side has been adjusted too far
								if ( Math.abs( leftAdjustment ) > MAX_ADJUSTMENT ) {
									//Slow down the right side by the difference between MAX_ADJUSTMENT and leftAdjustment
									if ( leftAdjustment > MAX_ADJUSTMENT ) {
										rightAdjustment += MAX_ADJUSTMENT - leftAdjustment;
									} else {
										rightAdjustment += -MAX_ADJUSTMENT - leftAdjustment;
									}
								}
							} else if ( Math.abs( tickLeft ) > Math.abs( tickRight ) ) { //Left side is faster
								jRatio = jValLeft / jValRight; //Ratio of left:right joystick value
								error = tickRight - ( tickLeft / jRatio ); //Difference between expected and actual right ticks
								rightAdjustment += error * ADJUSTMENT_FACTOR; //Ajusts the right side speed be a scaled down error

								//If the right side has been adjusted too far
								if ( Math.abs( rightAdjustment ) > MAX_ADJUSTMENT ) {
									//Slow down the left side by the difference between MAX_ADJUSTMENT and rightAdjustment
									if ( rightAdjustment > MAX_ADJUSTMENT ) {
										leftAdjustment += MAX_ADJUSTMENT - rightAdjustment;
									} else {
										leftAdjustment += -MAX_ADJUSTMENT - rightAdjustment;
									}
								}
							}
						} else {
							// Reset the adjustments if the robot is not moving
							rightAdjustment = 0.0;
							leftAdjustment = 0.0;
						}
						//Force both adjustments to be within the range [-MAX_ADJUSTMENT, MAX_ADJUSTMENT]
						leftAdjustment = MathTools.clamp( -MAX_ADJUSTMENT, MAX_ADJUSTMENT, leftAdjustment );
						rightAdjustment = MathTools.clamp( -MAX_ADJUSTMENT, MAX_ADJUSTMENT, rightAdjustment );
					} catch ( InterruptedException ex ) {
						System.err.println( ex.getMessage() );
					}

				}
			}
		}, 1, 1 );
	}

	public static DriveTrain getInstance() {
		if ( INSTANCE == null ) {
			INSTANCE = new DriveTrain();
		}
		return INSTANCE;
	}

	public void init() {
		//Reset all encoders and stop all motors
		leftEncoder.start();
		leftEncoder.reset();
		leftEncoder.setDistancePerPulse( DISTANCE_PER_TICK );
		rightEncoder.start();
		rightEncoder.reset();
		rightEncoder.setDistancePerPulse( DISTANCE_PER_TICK );
		set( 0, 0 );
	}

	public void updateDashboard( Dash dash ) throws IOException {
		dash.sendPWM( r1.get(), "Right 1", r1.getChannel(), Dash.PWMType.TALON );
		dash.sendPWM( r2.get(), "Right 2", r2.getChannel(), Dash.PWMType.TALON );
		dash.sendPWM( l1.get(), "Left 1", l1.getChannel(), Dash.PWMType.TALON );
		dash.sendPWM( l2.get(), "Left 2", l2.getChannel(), Dash.PWMType.TALON );
		dash.sendDigitalIO( leftEncoder.getDistance(), "Left", 1, Dash.DIGIOType.ENCODER );
		dash.sendDigitalIO( rightEncoder.getDistance(), "Right", 3, Dash.DIGIOType.ENCODER );
	}

	public void operatorControl( DualAction driver, DualAction shooter ) {
		//Maps left trigger to creep mode (25% power)
		if ( driver.getRawButton( DualAction.Button.LEFT_TRIGGER ) ) {
			mult = 0.4; //Creep 40% power
		} else {
			mult = 0.75; //Default 75% power
		}
		//Sets the motors to the scaled joystick values
		set( driver.getRightY() * mult, -driver.getLeftY() * mult );
	}

	/**
	 * Sets the speeds of the left and right motors. All speed setting should be
	 * done through this method so the left and right speed compensations are
	 * included.
	 *
	 * @param right Value to set the right motors to
	 * @param left  Value to set the left motors to
	 */
	public void set( double right, double left ) {
		//All motor values are clamped to be within the range [-1.0, 1.0]
		//All motors are adjusted by a small value depending on how much slower one side is going than the other
		this.right = right;
		this.left = left;
		r1.set( MathTools.clamp( -1.0, 1.0, right + rightAdjustment ) );
		r2.set( MathTools.clamp( -1.0, 1.0, right + rightAdjustment ) );
		l1.set( MathTools.clamp( -1.0, 1.0, left - leftAdjustment ) );
		l2.set( MathTools.clamp( -1.0, 1.0, left - leftAdjustment ) );
	}

	public void travelDistance( double inches, double speed, long timeout ) {
		if ( inches != 0.0 ) {
			long time = System.currentTimeMillis();
			double avgEnc = ( leftEncoder.getDistance() + rightEncoder.getDistance() ) / 2.0;
			double leftSpeed = inches > 0.0 ? speed : -speed;
			double rightSpeed = inches > 0.0 ? -speed : speed;
			while ( Math.abs( ( ( leftEncoder.getDistance() + rightEncoder.getDistance() ) / 2.0 ) - avgEnc ) < Math.abs( inches ) && System.currentTimeMillis() - time < timeout ) {
				set( rightSpeed, leftSpeed );
			}
			set( 0.0, 0.0 );
		}
	}
}
