package org.nashua.tt151.systems;

import com.sun.squawk.io.BufferedReader;
import com.sun.squawk.io.BufferedWriter;
import com.sun.squawk.microedition.io.FileConnection;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.templates.Dash;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.microedition.io.Connector;
import org.nashua.tt151.libraries.Controller.DualAction;

/**
 * 2014 Robot Shooter System
 * Handles control of the main shooter winch, the winch that angles the shooter,
 * the decoupler, and the third arm, as well as all relevant sensors
 *
 * @author Kareem El-Faramawi
 */
public class Shooter extends Subsystem {
	private static Shooter INSTANCE;
	private final static Talon winch = new Talon( 5 );
	private final static Talon winchThatWinchesTheWinch = new Talon( 8 );
	private final static Talon decoupler = new Talon( 7 );
	private final static Talon thirdArmWheel = new Talon( 9 );
	private final static AnalogPotentiometer pot = new AnalogPotentiometer( 1 );
	private final static DigitalInput limLoaded = new DigitalInput( 5 ); //Limit switch that signifies if the shooter has been pulled back
	private final static DigitalInput limCoupled = new DigitalInput( 6 ); //Limit switch that signifies if the winch is currently coupled to the axle
	private final static DigitalInput limDecoupled = new DigitalInput( 7 ); //Limit switch that signifies if the winch has been decoupled

	//This is the raw initial position of the arm, it's shifted pot value is 1.0 to prevent accidental rollover
	private double initialPotVal = 3.8645959820000004;

	//All pot positions are relative to the start position (1.0)
	public static final double ZERO_POT_VAL = 1.05;
	public static final double HIGH_STANCE_POT_VAL = 1.4783563810000002;
	public static final double CARRY_STANCE_POT_VAL = 1.6172340399999996;
	public static final double LOW_STANCE_POT_VAL = 2.4766223549999995;
	public static final double PICKUP_STANCE_POT_VAL = 2.73;

	//Flags for auto vs manual control of motors
	private boolean autoWinching = false;
	private boolean autoDecoupling = false;
	private boolean autoAngling = false;

	//Path to the file where the starting pot value will be stored
	private final String FILEPATH = "file:///potval.txt";

	private Shooter() {
		// Attempts to load the initial pot value if it has been saved to the CRio
		try {
			//Create a connection to the file on the CRio
			FileConnection file = ( FileConnection ) Connector.open( FILEPATH, Connector.READ );
			//If the file exists, open an input stream and attempt to read in the pot value
			if ( file.exists() ) {
				BufferedReader reader = new BufferedReader( new InputStreamReader( file.openInputStream() ) );
				try {
					initialPotVal = Double.parseDouble( reader.readLine() );
				} catch ( NumberFormatException e ) {
					System.err.println( "FAILED TO READ SHOOTER POT VALUE" );
				}
				reader.close();
			}
			file.close();
		} catch ( IOException e ) {
			System.err.println( e.getMessage() );
		}

	}

	/**
	 * Sets the current potentiometer value as the initial value and saves it to
	 * the CRio, so all future shooter positions are relative to the current
	 * position.
	 */
	private void calibrateShooterPosition() {
		try {
			//Create a connection to the file on the CRio
			FileConnection file = ( FileConnection ) Connector.open( FILEPATH, Connector.WRITE );
			//Force the creation of a blank file
			file.create();
			//Open an output stream
			BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( file.openOutputStream() ) );
			//Update the zero position of the pot and write it to the file
			initialPotVal = pot.get();
			writer.write( "" + initialPotVal );
			writer.flush();
			writer.close();
			file.close();
		} catch ( IOException e ) {
			System.err.println( e.getMessage() );
		}
	}

	public static Shooter getInstance() {
		if ( INSTANCE == null ) {
			INSTANCE = new Shooter();
		}
		return INSTANCE;
	}

	public void init() {
		//Stop all motors
		winch.set( 0.0 );
		winchThatWinchesTheWinch.set( 0.0 );
		decoupler.set( 0.0 );
		thirdArmWheel.set( 0.0 );
	}

	public void updateDashboard( Dash dash ) throws IOException {
		dash.sendPWM( winch.get(), "Winch", winch.getChannel(), Dash.PWMType.TALON );
		dash.sendPWM( winchThatWinchesTheWinch.get(), "WTWTW", winchThatWinchesTheWinch.getChannel(), Dash.PWMType.TALON );
		dash.sendPWM( decoupler.get(), "Decoupler", decoupler.getChannel(), Dash.PWMType.TALON );
		dash.sendPWM( thirdArmWheel.get(), "3rd Arm", thirdArmWheel.getChannel(), Dash.PWMType.TALON );
		dash.sendAnalog( potVal(), "WTWTW Pot", 1, Dash.AnalogType.POTENTIOMETER );
		dash.sendDigitalIO( limLoaded.get() ? 1.0 : 0.0, "Loaded", limLoaded.getChannel(), Dash.DIGIOType.LIMIT_SWITCH );
		dash.sendDigitalIO( limCoupled.get() ? 1.0 : 0.0, "Coupled", limCoupled.getChannel(), Dash.DIGIOType.LIMIT_SWITCH );
		dash.sendDigitalIO( limDecoupled.get() ? 1.0 : 0.0, "Decoupled", limDecoupled.getChannel(), Dash.DIGIOType.LIMIT_SWITCH );
	}

	public void operatorControl( DualAction driver, DualAction shooter ) {
		//*******************Winch that Winches the Winch*********************//
		//Map the back key to shooter angle calibration
		if ( shooter.wasReleased( DualAction.Button.BACK ) ) {
			calibrateShooterPosition();
		}

		/*
		 * Maps the clicking down of both joysticks to the legal position of the
		 * shooter. The requirement of both buttons is to prevent accidental
		 * movement to this position
		 */
		if ( shooter.getRawButton( DualAction.Button.LEFT_JOY_DOWN ) && shooter.getRawButton( DualAction.Button.RIGHT_JOY_DOWN ) ) {
			turnToStance( ZERO_POT_VAL, shooter );
		}

		//Maps the Y button to the high goal position of the shooter
		if ( shooter.wasReleased( DualAction.Button.Y ) ) {
			turnToStance( HIGH_STANCE_POT_VAL, shooter );
		}

		//Maps the X button to the carrying position of the shooter
		if ( shooter.wasReleased( DualAction.Button.X ) ) {
			turnToStance( CARRY_STANCE_POT_VAL, shooter );
		}

		//Maps the B button to the low goal position of the shooter
		if ( shooter.wasReleased( DualAction.Button.B ) ) {
			turnToStance( LOW_STANCE_POT_VAL, shooter );
		}

		//Maps the A button to the pickup position of the shooter
		if ( shooter.wasReleased( DualAction.Button.A ) ) {
			turnToStance( PICKUP_STANCE_POT_VAL, shooter );
		}

		/*
		 * Allows the shooter angle to be manually controlled if it is not
		 * already moving to a preset position
		 */
		if ( !autoAngling ) {
			winchThatWinchesTheWinch.set( shooter.getLeftY() );
		}
		//********************************************************************//

		//*******************************Winch********************************//
		//Maps the right bumper to the automatic winding back of the winch
		if ( shooter.wasReleased( DualAction.Button.RIGHT_BUMPER ) ) {
			windWinch( shooter );
		}

		/*
		 * Allows the winch to be manually controlled if it is not already
		 * winding back and if the shooter is not firing
		 */
		if ( !autoWinching && !autoDecoupling ) {
			winch.set( shooter.getRightY() );
		}
		//********************************************************************//

		//*****************************Decoupler******************************//
		//Maps the left bumper to the firing of the shooter
		if ( shooter.wasReleased( DualAction.Button.LEFT_BUMPER ) ) {
			fire( shooter );
		}

		/*
		 * Allows the decoupler motor to be manually controlled if the shooter
		 * is not already firing. The decoupler is a sensistive system, so the
		 * manual control has been scaled down to prevent the motor from being
		 * run too far and possibly breaking the decoupler.
		 */
		if ( !autoDecoupling ) {
			if ( shooter.isPadLeft() ) {
				decoupler.set( -0.25 );
			} else if ( shooter.isPadRight() ) {
				decoupler.set( 0.25 );
			} else {
				decoupler.set( 0.0 );
			}
		}
		//********************************************************************//

		//*************************Third Arm Wheel****************************//
		//Maps the third wheel control to both triggers
		//Hold the left trigger to drive the wheel backwards (push ball out)
		//Hold the right trigger to drive the wheel forwards (pull ball in)
		if ( shooter.getRawButton( DualAction.Button.LEFT_TRIGGER ) != shooter.getRawButton( DualAction.Button.RIGHT_TRIGGER ) ) {
			thirdArmWheel.set( shooter.getRawButton( DualAction.Button.LEFT_TRIGGER ) ? -1.0 : 1.0 );
		} else {
			thirdArmWheel.set( 0.0 );
		}
		//********************************************************************//
	}

	/**
	 * Shifts the potentiometer value to account for the rollover based on an
	 * initial value to use as a reference.
	 *
	 * The pot rolls over at 5.0, so the raw pot value is shifted forward by the
	 * difference between the calibrated zero value and the rollover value, a
	 * 1.0 padding is added to prevent accidental rollover when moving
	 * backwards, then it is modded by 5.0
	 *
	 * @return Shifted potentiometer value
	 */
	private double potVal() {
		return ( pot.get() + 6.0 - initialPotVal ) % 5.0;
	}

	/**
	 * Decouples and recouples the winch axle to shoot. Includes a joystick and
	 * time override in case the sensors are not working properly or the driver
	 * needs to stop the decoupler without emergency stopping.
	 *
	 * @param joy Overriding joystick
	 */
	public void fire( final DualAction joy ) {
		/*
		 * Initial conditions before firing:
		 * -The winch is coupled to the axle
		 * -The decoupler is not already moving
		 */
		if ( /*
				 * !limCoupled.get() && limDecoupled.get() &&
				 */ decoupler.get() == 0.0 && !autoDecoupling ) {
			autoDecoupling = true;
			new Thread() {
				public void run() {
					try {
						long time = System.currentTimeMillis();
						/*
						 * Run the decoupler out until the limit switch that
						 * signifies that the axle is decoupled is pressed or
						 * until 1.25s passes
						 */
						while ( limDecoupled.get() && System.currentTimeMillis() - time < 1250 ) {
							decoupler.set( -1.0 );
							//Stop the auto firing action if the driver tries to manually control the decoupler
							if ( joy.isPadLeft() || joy.isPadRight() ) {
								decoupler.set( 0.0 );
								autoDecoupling = false;
								return;
							}
						}
						/*
						 * Stop the decoupler and wait for one second the make
						 * sure the firing has completed before recoupling
						 */
						decoupler.set( 0.0 );
						sleep( 1000 );
						time = System.currentTimeMillis();
						/*
						 * Run the decoupler back in until the limit switch that
						 * signifies that the axle is coupled to the winch is
						 * pressed or until 1.25s passes
						 */
						while ( limCoupled.get() && System.currentTimeMillis() - time < 1250 ) {
							decoupler.set( 1.0 );
							winch.set( 1.0 ); //Turn the winch while recoupling to make sure it catches onto the axle
							//Stop the auto firing action if the driver tries to manually control the decoupler
							if ( joy.isPadLeft() || joy.isPadRight() ) {
								decoupler.set( 0.0 );
								winch.set( 0.0 );
								autoDecoupling = false;
								return;
							}
						}
						//Stop all motors and end the auto firing action
						decoupler.set( 0.0 );
						winch.set( 0.0 );
						autoDecoupling = false;
					} catch ( InterruptedException ex ) {
						ex.printStackTrace();
					}
				}
			}.start();
		}
	}

	/**
	 * Winds the winch back until a limit switch is hit. Includes a joystick and
	 * time override in case the sensors are not working properly or the driver
	 * sees that the winch needs to be manually stopped.
	 *
	 * @param joy Overriding joystick
	 */
	public void windWinch( final DualAction joy ) {
		//Initial condition: the winch is not already running
		if ( !autoWinching && winch.get() == 0.0 ) {
			autoWinching = true;
			new Thread() {
				public void run() {
					/*
					 * Winds the winch back until the limit switch that
					 * signifies that the shooter is loaded is pressed, 9s
					 * passes, or the driver tries to manually control the winch
					 */
					long time = System.currentTimeMillis();
					while ( limLoaded.get() && joy.getRightY() == 0.0 && System.currentTimeMillis() - time < 9000 ) {
						winch.set( 1.0 );
					}
					//Stop the winch and end the auto winching action
					autoWinching = false;
					winch.set( 0.0 );
				}
			}.start();
		}
	}

	/**
	 * Turns the shooter to a specific angle denoted by a shifted potentiometer
	 * value. Includes a joystick override in case the driver seen any problems
	 * or wants to change the angle manually.
	 *
	 * @param stance Desired potentiometer value
	 * @param joy    Overriding joystick
	 */
	public void turnToStance( final double stance, final DualAction joy ) {
		//Initial condition: the winch that winches the winch is not already running
		if ( winchThatWinchesTheWinch.get() == 0.0 && !autoAngling ) {
			autoAngling = true;
			//Check if the shooter needs to be angled up or down
			if ( potVal() > stance ) { //The shooter needs to be raised
				new Thread() {
					public void run() {
						//Raise the shooter until it reaches the desired angle or the driver interferes
						while ( potVal() >= stance && joy.getLeftY() == 0.0 ) {
							winchThatWinchesTheWinch.set( -1.0 );
						}
						//Stop the winch and end the auto angling action
						autoAngling = false;
						winchThatWinchesTheWinch.set( 0.0 );
					}
				}.start();
			} else { //The shooter needs to be lowered
				new Thread() {
					public void run() {
						//Lower the shooter until it reaches the desired angle or the driver interferes
						while ( potVal() <= stance && joy.getLeftY() == 0.0 ) {
							winchThatWinchesTheWinch.set( 1.0 );
						}
						//Stop the winch and end the auto angling action
						autoAngling = false;
						winchThatWinchesTheWinch.set( 0.0 );
					}
				}.start();
			}
		}
	}

	public boolean isLoaded() {
		return !autoWinching;
	}
}
