package org.nashua.tt151.libraries;

import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is a conveniency class for controllers. It allows you to check to see if a button 
 * was pressed or released since the last loop. It allows supports a two-state toggle.
 * NOTE: You must call the queryButtons() method on all controller instances at the beginning of each loop.
 * This is to prevent values changing in the middle of a loop.
 * @author Brian Ashworth 
 * @author Kareem El-Faramawi
 * @version 1.1
 * 1.1 - Modified to include dead band for conveniency axis methods
 */
public abstract class Controller {
	/**
	 * This listener is one of the many options for dealing with button events
	 */
	public static interface ButtonListener {
		/**
		 * This listener method gets called when a button is pressed
		 * @param opt Toggle state
		 */
		public void onPress( boolean opt );

		/**
		 * This listener method gets called when a button is released
		 * @param opt Toggle state
		 */
		public void onRelease( boolean opt );
	}

	/**
	 * This class holds the possible states for each button
	 */
	public static class State {
		/**
		 * This state is used when the button has never been pressed or that the release 
		 * state has already been read
		 */
		public static final int OFF = 0;
		/**
		 * This state is used when the button is pressed
		 */
		public static final int PRESSED = 1;
		/**
		 * This state is used when the button is removed
		 */
		public static final int RELEASED = 2;
	}
	/**
	 * This variable is used for the index of the state
	 */
	private static final int V_IS_PRESSED = 0;
	/**
	 * This variable is used for the index of the toggle option
	 */
	private static final int V_OPTION = 1;
	/**
	 * This is the buffer that stores the information
	 */
	private int[][] state;
	/**
	 * This is all the registered ButtonListeners
	 */
	private ButtonListener[] listeners;
	/**
	 * This is the number of buttons on the controller
	 */
	private int slots = 0;

	/**
	 * A conveniency object for the controller
	 * @param slots The number of buttons on the controller
	 */
	public Controller( int slots ) {
		this.slots = slots;
		state = new int[2][slots];
		listeners = new ButtonListener[slots];
	}

	/**
	 * Registers a ButtonListener with the controller
	 * @param button The button index
	 * @param pl The ButtonListener instance
	 */
	public void addButtonListener( int button, ButtonListener pl ) {
		// Register ButtonListener For Button button
		listeners[button - 1] = pl;
	}

	/**
	 * Remove all ButtonListeners
	 */
	public void clearListeners() {
		// Clear Listeners
		listeners = new ButtonListener[12];
	}

	/**
	 * Reset the state of the button
	 * @param button Button index
	 */
	public void clearState( int button ) {
		// Turn Button button OFF
		state[V_IS_PRESSED][button - 1] = State.OFF;
	}

	/**
	 * Reset the state of all buttons
	 */
	public void clearAll() {
		// Clear All Items
		clearListeners();
		for ( int i = 1; i < slots + 1; i++ ) {
			clearState( i );
		}
	}

	/**
	 * Get the toggle state of the button
	 * @param button The button index
	 * @return The toggle state (0 is false; 1 is true)
	 */
	public boolean getOption( int button ) {
		// Return The Option Of Button button
		return state[V_OPTION][button - 1] == 1;
	}

	/**
	 * Retrieves the raw value of an axis
	 * @param axis The axis number
	 * @return The raw value of an axis
	 */
	public abstract double getRawAxis( int axis );

	/**
	 * Retrieves whether a button is currently pressed
	 * @param button Button index
	 * @return Whether the button is currently pressed
	 */
	public abstract boolean getRawButton( int button );

	/**
	 * Retrieve the state of a button
	 * @param button The button index
	 * @return State of the button
	 */
	public int getState( int button ) {
		// Return The State Of Button button
		return state[V_IS_PRESSED][button - 1];
	}

	/**
	 * Retrieve whether the button is pressed
	 * @param button The button index
	 * @return Whether the button is pressed
	 */
	public boolean isPressed( int button ) {
		// Return If Button button Is Pressed
		return state[V_IS_PRESSED][button - 1] == State.PRESSED;
	}

	/**
	 * Remove the button listener for the button
	 * @param button Button index
	 */
	public void removeButtonListener( int button ) {
		// Remove ButtonListener For Button button
		listeners[button - 1] = null;
	}

	/**
	 * Query the controller for any button changes
	 */
	public void queryButtons() {
		// Query Button States
		for ( int b = 0; b < slots; b++ ) {
			if ( getRawButton( b + 1 ) && state[V_IS_PRESSED][b] != State.PRESSED ) {
				// pressed
				state[V_IS_PRESSED][b] = State.PRESSED;
				if ( listeners[b] != null ) {
					System.out.println( listeners[b] == null );
					listeners[b].onPress( state[V_OPTION][b] == 1 );
				}
			} else if ( !getRawButton( b + 1 ) && state[V_IS_PRESSED][b] == State.PRESSED ) {
				// released
				state[V_IS_PRESSED][b] = State.RELEASED;
				state[V_OPTION][b] = state[V_OPTION][b] == 0 ? 1 : 0; // toggle option
				if ( listeners[b] != null ) {
					System.out.println( listeners[b] == null );
					listeners[b].onRelease( state[V_OPTION][b] == 1 );
				}
			}
		}
	}

	/**
	 * Retrieves whether the button was released since the last loop
	 * @param button Button index
	 * @return Whether the button was released since the last loop
	 */
	public boolean wasReleased( int button ) {
		// Return If Button button Is Released
		boolean x = state[V_IS_PRESSED][button - 1] == State.RELEASED;
		if ( x ) {
			clearState( button );
		}
		return x;
	}

	/**
	 * Logitech DualAction F310 Controller (D Mode). The mappings were found using 
	 * the "Devices and Printers" item in the Control Panel
	 * @author Brian Ashworth
	 * @version 1.1
	 */
	public static class DualAction extends Controller {
		private double PAD_THRESHOLD = 0.5;

		/**
		 * The axis mappings for the controller
		 */
		public static abstract class Axis {
			/**
			 * This is the mapping for the x axis on the directional pad
			 */
			public static final int DPAD_X = 5;
			/**
			 * This is the mapping for the y axis on the directional pad
			 */
			public static final int DPAD_Y = 6;
			/**
			 * This is the mapping for the x axis on the left joystick
			 */
			public static final int LEFT_X = 1;
			/**
			 * This is the mapping for the y axis on the left joystick
			 */
			public static final int LEFT_Y = 2;
			/**
			 * This is the mapping for the x axis on the right joystick
			 */
			public static final int RIGHT_X = 3;
			/**
			 * This is the mapping for the y axis on the right joystick
			 */
			public static final int RIGHT_Y = 4;
		}

		/**
		 * The button mappings for the controller
		 */
		public static class Button {
			/**
			 * This is the mapping for the A button
			 */
			public static final int A = 2;
			/**
			 * This is the mapping for the B button
			 */
			public static final int B = 3;
			/**
			 * This is the mapping for the back button
			 */
			public static final int BACK = 9;
			/**
			 * This is the mapping for the left bumper
			 */
			public static final int LEFT_BUMPER = 5;
			/**
			 * This is the mapping for the left joystick being pressed down
			 */
			public static final int LEFT_JOY_DOWN = 11;
			/**
			 * This is the mapping for the left trigger
			 */
			public static final int LEFT_TRIGGER = 7;
			/**
			 * This is the mapping for the right bumper
			 */
			public static final int RIGHT_BUMPER = 6;
			/**
			 * This is the mapping for the right joystick being pressed down
			 */
			public static final int RIGHT_JOY_DOWN = 12;
			/**
			 * This is the mapping for the right trigger
			 */
			public static final int RIGHT_TRIGGER = 8;
			/**
			 * This is the mapping for the select button
			 */
			public static final int START = 10;
			/**
			 * This is the mapping for the X button
			 */
			public static final int X = 1;
			/**
			 * This is the mapping for the Y button
			 */
			public static final int Y = 4;
			/**
			 * This is a fake mapping for the D-Pad Up. (Only use for RAW)
			 */
			public static final int DPAD_UP = 16;
			/**
			 * This is a fake mapping for the D-Pad Left. (Only use for RAW)
			 */
			public static final int DPAD_LEFT = 15;
			/**
			 * This is a fake mapping for the D-Pad Right. (Only use for RAW)
			 */
			public static final int DPAD_RIGHT = 14;
			/**
			 * This is a fake mapping for the D-Pad Down. (Only use for RAW)
			 */
			public static final int DPAD_DOWN = 13;
		}
		/**
		 * This joystick object that is used in the backend
		 */
		private Joystick joy;
		private double deadBand;

		/**
		 * Creates a Logitech DualAction F310 Controller (D Mode)
		 * @param slot The joystick slot number
		 */
		public DualAction( int slot ) {
			super( 12 ); // There are 12 buttons on the controller
			deadBand = 0;
			joy = new Joystick( slot );
		}

		/**
		 * Creates a Logitech DualAction F310 Controller (D Mode)
		 * @param slot The joystick slot number
		 * @param deadBand The value the joystick must exceed to register
		 */
		public DualAction( int slot, double deadBand ) {
			super( 12 ); // There are 12 buttons on the controller
			this.deadBand = deadBand;
			joy = new Joystick( slot );
		}

		/**
		 * Retrieves the joystick object that is used in the backend
		 * @return The joystick object that is used in the backend
		 */
		public Joystick getJoystick() {
			return joy;
		}

		/**
		 * Retrieves the raw value of the axis
		 * @param axis The axis number
		 * @return The raw value of the axis. If the value is 
		 */
		public double getRawAxis( int axis ) {
			double temp = joy.getRawAxis( axis );
			if ( temp == 0 ) {
				return temp;
			} else if ( temp > 0 ) {
				return temp > deadBand ? temp : 0;
			} else {
				return temp < -deadBand ? temp : 0;
			}
		}

		/**
		 * Retrieves the raw value of the button
		 * @param button The button number
		 * @return The raw value of the button
		 */
		public boolean getRawButton( int button ) {
			if ( button == Button.DPAD_LEFT ) {
				return isPadLeft();
			} else if ( button == Button.DPAD_DOWN ) {
				return isPadDown();
			} else if ( button == Button.DPAD_RIGHT ) {
				return isPadRight();
			} else if ( button == Button.DPAD_UP ) {
				return isPadUp();
			}
			return joy.getRawButton( button );
		}

		/**
		 * Retrieves the x value of the left joystick
		 * @return X value of the left joystick
		 */
		public double getLeftX() {
			return getRawAxis( Axis.LEFT_X );
		}

		/**
		 * Retrieves the y value of the left joystick
		 * @return Y value of the left joystick
		 */
		public double getLeftY() {
			return getRawAxis( Axis.LEFT_Y );
		}

		/**
		 * Retrieves the x value of the right joystick
		 * @return X value of the right joystick
		 */
		public double getRightX() {
			return getRawAxis( Axis.RIGHT_X );
		}

		/**
		 * Retrieves the y value of the right joystick
		 * @return Y value of the right joystick
		 */
		public double getRightY() {
			return getRawAxis( Axis.RIGHT_Y );
		}

		/**
		 * Retrieves the x value of the directional pad
		 * @return X value of the directional pad
		 */
		public double getPadX() {
			return joy.getRawAxis( Axis.DPAD_X );
		}

		/**
		 * Retrieves the y value of the directional pad
		 * @return Y value of the directional pad
		 */
		public double getPadY() {
			return joy.getRawAxis( Axis.DPAD_Y );
		}

		/**
		 * Conveniency method for determining whether or not D-Pad is DOWN
		 * @return Whether or not the D-Pad is DOWN
		 */
		public boolean isPadDown() {
			return getPadY() > PAD_THRESHOLD;
		}

		/**
		 * Conveniency method for determining whether or not D-Pad is LEFT
		 * @return Whether or not the D-Pad is LEFT
		 */
		public boolean isPadLeft() {
			return getPadX() < -PAD_THRESHOLD;
		}

		/**
		 * Conveniency method for determining whether or not D-Pad is RIGHT
		 * @return Whether or not the D-Pad is RIGHT
		 */
		public boolean isPadRight() {
			return getPadX() > PAD_THRESHOLD;
		}

		/**
		 * Conveniency method for determining whether or not D-Pad is UP
		 * @return Whether or not the D-Pad is UP
		 */
		public boolean isPadUp() {
			return getPadY() < -PAD_THRESHOLD;
		}
	}
}
