package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.SimpleRobot;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.nashua.tt151.libraries.Controller.DualAction;
import org.nashua.tt151.systems.DriveTrain;
import org.nashua.tt151.systems.Shooter;

/**
 * Main entry point for the robot. Calls the tele-op methods in all robot
 * subsystems and handles connection to the dashboard
 *
 * @author Kareem El-Faramawi
 */
public class Alpha extends SimpleRobot {
	//2 Logitech DualAction F310 D-mode controllers
	public final DualAction driver = new DualAction( 1, 0.1 );
	public final DualAction shooter = new DualAction( 2, 0.1 );
	private static volatile boolean enabled;
	//Dashboard connection
	private Dash dash;
	private boolean tryingToConnect = false;

	public static boolean isRobotEnabled() {
		return enabled;
	}

	/**
	 * Attempts to establish a connection the the dashboard
	 */
	private void connectToDashboard() {
		if ( tryingToConnect ) {
			return;
		}
		tryingToConnect = true;
		new Thread() {
			public void run() {
				try {
					//Try to create a new dashboard connection
					if ( tryingToConnect && dash == null ) {
						dash = new Dash( "10.1.51.5", new Dash.ConnectionListener() {
							public void onConnect() {
								System.out.println( "[Connected]" );
							}

							public void onDisconnect() {
								System.out.println( "[Disconnected]" );
							}

							public void onDataReceived( String msg ) {
								System.out.println( "[MSG " + msg + "]" );
							}
						} );
					}
				} catch ( IOException ex ) { //Thrown if the connection attempt failed
					System.out.println( "[ERR Failed to connect to dashboard: " + ex.getMessage() + "]" );
					dash = null;
				}
				tryingToConnect = false;
			}
		}.start();
	}

	public void robotInit() {
		//Initial attempt to connect to the dashboard
		connectToDashboard();
		/*
		 * Tests the connection to the dashboard every 100ms and tries to
		 * reestablish the connection if the test fails
		 */
		new Timer().scheduleAtFixedRate( new TimerTask() {
			int failedCount = 0;

			public void run() {
				enabled = isEnabled();
				if ( dash != null ) { //If a connection is already made
					try {
						DriveTrain.getInstance().updateDashboard( dash );
						Shooter.getInstance().updateDashboard( dash );
						updateStatus( dash );

						failedCount = 0;
					} catch ( IOException ex ) { //Thrown if the message failed to send
						System.out.println( "Failed to update dashboard: " + ex.getMessage() );
						//Allow up to 3 connection failures before destroying the connection
						if ( ++failedCount <= 3 ) {
							failedCount = 0;
							dash = null; //End the connection
							connectToDashboard(); //Try to reestablish the connection
						}
					}
				} else { //If no connection exists, create a connection
					failedCount = 0;
					connectToDashboard();
				}
			}
		}, 1, 10 );
	}

	private void updateStatus( Dash dash ) throws IOException {
		if ( isEnabled() && isAutonomous() ) {
			dash.sendStatus( Dash.Status.AUTONOMOUS );
		} else if ( isEnabled() && isOperatorControl() ) {
			dash.sendStatus( Dash.Status.TELEOP );
		} else {
			dash.sendStatus( Dash.Status.CONNECTED );
		}
	}

	public void autonomous() {
		try {
			long startTime = System.currentTimeMillis();
			DriveTrain.getInstance().init();
			Shooter.getInstance().init();
			Shooter.getInstance().windWinch( shooter );
			DriveTrain.getInstance().travelDistance( 12.5 * 12, 0.5, 8000 );
			while ( !Shooter.getInstance().isLoaded() && System.currentTimeMillis() - startTime < 7000 ) {
				Thread.sleep( 1 );
			}
//          Shooter.getInstance().fire( shooter );
		} catch ( InterruptedException ex ) {
			ex.printStackTrace();
		}
	}

	public void operatorControl() {
		// Initialize all subsystems
		DriveTrain.getInstance().init();
		Shooter.getInstance().init();
		// Operator control loop
		while ( isEnabled() && isOperatorControl() ) {
			//Poll the controllers for any changes in button states
			driver.queryButtons();
			shooter.queryButtons();

			//Call the tele-op methods in all subsystems
			DriveTrain.getInstance().operatorControl( driver, shooter );
			Shooter.getInstance().operatorControl( driver, shooter );
		}
	}

	public void test() {
		while ( isEnabled() && isTest() ) {
			try {
				System.out.println( dash.queryTargetHot() );
			} catch ( IOException ex ) {
				ex.printStackTrace();
			}
		}
	}
}
