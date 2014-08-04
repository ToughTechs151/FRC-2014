package edu.wpi.first.wpilibj.templates;

import com.sun.squawk.io.BufferedWriter;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Relay;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import org.nashua.tt151.libraries.parsers.ProtocolParsing;
import org.nashua.tt151.util.MathTools;

/**
 * Server connection to the dashboard on the laptop. Handles the reading and
 * sending of data between the dashboard and robot. Uses the bidirectional TCP
 * port 1735 as specified by FIRST.
 *
 * @author Kareem El-Faramawi
 */
public class Dash {
	/**
	 * Connection object that holds the connection to the dashboard.
	 */
	private Connection con;

	/**
	 * Connection from cRIO to laptop
	 *
	 * @param host IP Address of the laptop
	 * @param cl   Listener for this connection
	 * @throws IOException
	 */
	public Dash( String host, ConnectionListener cl ) throws IOException {
		con = new Connection( host, 1735, cl );
	}

	public void logMatchInfo() throws IOException {
		logMessage( "MATCH:" + DriverStation.getInstance().getAlliance() + ":" + DriverStation.getInstance().getLocation() );
	}

	public void logMessage( String msg ) throws IOException {
		con.send( "LOG:" + msg );
	}

	public double queryCameraTurnAngle() throws NumberFormatException, IOException {
		con.send( ProtocolParsing.StringParser.createMessage( ProtocolParsing.Command.Query, ProtocolParsing.Key.CamTurnAngle, "", new String[] {} ) );
		// Wait for result
		String[][] result = null;
		while ( result == null ) {
			result = con.getKeyFromQueue( ProtocolParsing.Key.CamTurnAngle );
		}
		// Verify non null value and parse the double from the string
		if ( result.length > 1 && result[1].length > 0 ) {
			return Double.parseDouble( result[1][0] );
		}
		// Return -1 on null result
		return -1;
	}
	
	public boolean queryTargetHot() throws IOException {
		con.send( ProtocolParsing.StringParser.createMessage( ProtocolParsing.Command.Query, ProtocolParsing.Key.TargetHot, "", new String[] {} ) );
		// Wait for result
		String[][] result = null;
		while ( result == null ) {
			result = con.getKeyFromQueue( ProtocolParsing.Key.TargetHot );
		}
		// Verify non null value and parse the boolean from the string
		if ( result.length > 1 && result[1].length > 0 ) {
			return result[1][0].equals( "1" );
		}
		// Return false on null result
		return false;
	}

	/**
	 * Send the value of an analog sensor to the dashboard to display
	 *
	 * @param value Value of the analog sensor
	 * @param name  Name of the sensor to display on the dashboard
	 * @param slot  PWM Slot on the analog module
	 * @param at    The analog device type (gyro, accelerometer, etc)
	 * @throws IOException
	 */
	public void sendAnalog( double value, String name, int slot, AnalogType at ) throws IOException {
		con.send( ProtocolParsing.StringParser.createMessage( ProtocolParsing.Command.Send, ProtocolParsing.Key.AnalogValue, "" + MathTools.round( value, 2 ), new String[] { name, "" + slot, at.toString() } ) );
	}

	/**
	 * Send the value of a PWM device to the dashboard
	 *
	 * @param value Value of the PWM device
	 * @param name  Name of the device to be diplayed
	 * @param slot  PWM slot on the sidecar
	 * @param pt    The PWM device type
	 * @throws IOException
	 */
	public void sendPWM( double value, String name, int slot, PWMType pt ) throws IOException {
		con.send( ProtocolParsing.StringParser.createMessage( ProtocolParsing.Command.Send, ProtocolParsing.Key.PWMValue, "" + MathTools.round( value, 2 ), new String[] { name, "" + slot, pt.toString() } ) );
	}

	/**
	 * Send the value of the relay to the dashboard for displaying
	 *
	 * @param v    Relay Value
	 * @param name Name of the relay to display
	 * @param slot Slot number (1 through 8)
	 * @param dir  Relay Direction
	 * @throws IOException
	 */
	public void sendRelay( Relay.Value v, String name, int slot, Relay.Direction dir ) throws IOException {
		con.send( ProtocolParsing.StringParser.createMessage( ProtocolParsing.Command.Send, ProtocolParsing.Key.RelayValue, "" + new char[] { '0', '+', '+', '-' }[v.value], new String[] { name, "" + slot, "" + new char[] { 'B', '+', '-' }[dir.value] } ) );
	}

	/**
	 * Send the value of a digital IO device to the dashboard
	 *
	 * @param value Value of the digital IO device
	 * @param name  Name of the device to be displayed
	 * @param slot  Digital IO slot on the sidecar
	 * @param dt    The digital IO device type
	 * @throws IOException
	 */
	public void sendDigitalIO( double value, String name, int slot, DIGIOType dt ) throws IOException {
		con.send( ProtocolParsing.StringParser.createMessage( ProtocolParsing.Command.Send, ProtocolParsing.Key.DigitalIO, "" + MathTools.round( value, 2 ), new String[] { name, "" + slot, dt.toString() } ) );
	}

	/**
	 * Sends the current status of the robot to the dashboard
	 *
	 * @param s Status of the robot
	 * @throws IOException
	 */
	public void sendStatus( Status s ) throws IOException {
		con.send( ProtocolParsing.StringParser.createMessage( ProtocolParsing.Command.Send, ProtocolParsing.Key.Status, s.toString(), new String[] {} ) );
	}

	/**
	 * Send a raw message to the dashboard
	 *
	 * @param msg Message to send
	 * @throws IOException
	 */
	public void sendRaw( String msg ) throws IOException {
		con.send( msg );
	}

	/**
	 * Analog Device Type
	 */
	public static final class AnalogType {
		/**
		 * Used to identify an accelerometer
		 */
		public static final AnalogType ACCELEROMETER = new AnalogType( 'A' );
		/**
		 * Used to identify a gyro
		 */
		public static final AnalogType GYRO = new AnalogType( 'G' );
		/**
		 * Used to identify an unknown analog value
		 */
		public static final AnalogType UNKNOWN = new AnalogType( 'U' );
		/**
		 * Used to identify a potentiometer
		 */
		public static final AnalogType POTENTIOMETER = new AnalogType( 'P' );
		/**
		 * Shorthand character that identifies the device type
		 */
		public char shorthand;

		/**
		 * An analog device identifier
		 *
		 * @param shorthand Character that identifies the device type
		 */
		private AnalogType( char shorthand ) {
			this.shorthand = shorthand;
		}

		/**
		 * Retrieves the shorthand character
		 *
		 * @return shorthand character that identifies the device type
		 */
		public char getShorthand() {
			return shorthand;
		}

		/**
		 * Override of java.lang.Object.toString. Turns the object into a String
		 *
		 * @return String representation of the object
		 */
		public String toString() {
			return "" + shorthand;
		}
	}

	/**
	 * PWM Device Identifier
	 */
	public static final class PWMType {
		/**
		 * Used to identify a jaguar
		 */
		public static final PWMType JAGUAR = new PWMType( 'J' );
		/**
		 * Used to identify a servo
		 */
		public static final PWMType SERVO = new PWMType( 'S' );
		/**
		 * Used to identify a talon
		 */
		public static final PWMType TALON = new PWMType( 'T' );
		/**
		 * Used to identify an unknown PWM device
		 */
		public static final PWMType UNKNOWN = new PWMType( 'U' );
		/**
		 * Used to identify a victor
		 */
		public static final PWMType VICTOR = new PWMType( 'V' );
		/**
		 * Shorthand character that identifies a PWM device type
		 */
		public char shorthand;

		/**
		 * PWM Device Identifier
		 *
		 * @param shorthand Character that represents the device type
		 */
		private PWMType( char shorthand ) {
			this.shorthand = shorthand;
		}

		/**
		 * Retrieves the character that identifies the device type
		 *
		 * @return The device type identifier
		 */
		public char getShorthand() {
			return shorthand;
		}

		/**
		 * Override of java.lang.Object.toString. Returns a string
		 * representation of the object.
		 *
		 * @return String representation of the object.
		 */
		public String toString() {
			return "" + shorthand;
		}
	}

	/**
	 * Digital IO Device Type
	 */
	public static final class DIGIOType {
		/**
		 * Used to identify a limit switch
		 */
		public static final DIGIOType LIMIT_SWITCH = new DIGIOType( 'L' );
		/**
		 * Used to identify an encoder
		 */
		public static final DIGIOType ENCODER = new DIGIOType( 'E' );
		/**
		 * Shorthand character that identifies a PWM device type
		 */
		public char shorthand;

		/**
		 * PWM Device Identifier
		 *
		 * @param shorthand Character that represents the device type
		 */
		private DIGIOType( char shorthand ) {
			this.shorthand = shorthand;
		}

		/**
		 * Retrieves the character that identifies the device type
		 *
		 * @return The device type identifier
		 */
		public char getShorthand() {
			return shorthand;
		}

		/**
		 * Override of java.lang.Object.toString. Returns a string
		 * representation of the object.
		 *
		 * @return String representation of the object.
		 */
		public String toString() {
			return "" + shorthand;
		}
	}

	/**
	 * Digital IO Device Type
	 */
	public static final class Status {
		/**
		 * Used to identify Disabled or Test
		 */
		public static final Status CONNECTED = new Status( 'C' );
		/**
		 * Used to identify Tele-Op
		 */
		public static final Status TELEOP = new Status( 'T' );
		/**
		 * Used to identify Autonomous
		 */
		public static final Status AUTONOMOUS = new Status( 'A' );

		/**
		 * Shorthand character that identifies a PWM device type
		 */
		public char shorthand;

		/**
		 * PWM Device Identifier
		 *
		 * @param shorthand Character that represents the device type
		 */
		private Status( char shorthand ) {
			this.shorthand = shorthand;
		}

		/**
		 * Retrieves the character that identifies the device type
		 *
		 * @return The device type identifier
		 */
		public char getShorthand() {
			return shorthand;
		}

		/**
		 * Override of java.lang.Object.toString. Returns a string
		 * representation of the object.
		 *
		 * @return String representation of the object.
		 */
		public String toString() {
			return "" + shorthand;
		}
	}

	/**
	 * The ConnectionListener interface has the method declarations for the
	 * methods that get called when an event happens on the communication
	 * channel. The following events are currently supported: onConnect,
	 * onDisconnect, and onDataReceived.
	 */
	public static interface ConnectionListener {
		/**
		 * This listener method gets called when a connection is made
		 */
		public void onConnect();

		/**
		 * This listener method gets called when a connection is lost
		 */
		public void onDisconnect();

		/**
		 * This listener method gets called when a message from the dashboard is
		 * received.
		 *
		 * @param msg Message from the dashboard
		 */
		public void onDataReceived( String msg );
	}

	/**
	 * The Connection class establishes the communication channel from the robot
	 * to the laptop (and vise versa).
	 */
	private static class Connection implements ConnectionListener {
		/**
		 * Listener for this connection
		 */
		private ConnectionListener listener;
		/**
		 * Socket connection between the laptop and the cRIO. This is the client
		 * side.
		 */
		private SocketConnection socket;
		/**
		 * Reader for the input stream. The input stream is to the cRIO
		 */
		private InputStreamReader reader;
		/**
		 * The Writer for the output stream. The output stream is from the cRIO.
		 */
		private BufferedWriter writer;
		/**
		 * Queue of queried items
		 */
		private String[][][] queue = new String[0][0][0];

		/**
		 * Establish a client connection from the cRIO to the dashboard
		 *
		 * @param host IP Address of laptop
		 * @param port Port that the dashboard is listening on
		 * @param cl   Listener for this connection
		 * @throws IOException
		 */
		public Connection( String host, int port, ConnectionListener cl ) throws IOException {
			// Set listener if null to this (dummy) or if not null to the listener
			listener = cl == null ? this : cl;

			// Connect to server (in this case the dashboard on the laptop)
			socket = ( SocketConnection ) Connector.open( "socket://" + host + ":" + port );
			reader = new InputStreamReader( socket.openDataInputStream() );
			writer = new BufferedWriter( new OutputStreamWriter( socket.openDataOutputStream() ) );

			// Listen for events every 10ms
			final java.util.Timer t = new java.util.Timer();
			t.schedule( new java.util.TimerTask() {
				public void run() {
					if ( socket == null ) {
						t.cancel();
						return;
					}
					try {
						if ( reader.ready() ) {
							// Read length byte
							int length = reader.read();
							// Read for length bytes
							int offset = 0;
							char[] buffer = new char[length];
							while ( length != 0 ) {
								int read = reader.read( buffer, offset, length );
								offset += read;
								length -= read;
								if ( read == -1 ) {
									break;
								}
							}
							// Turn buffer into String
							String msg = String.valueOf( buffer );
							// Check to see if disconnected
							if ( msg.equals( "[Disconnected]" ) ) {
								socket = null;
								reader = null;
								writer = null;
								listener.onDisconnect();
							} else if ( ProtocolParsing.StringParser.isReply( msg ) ) {
								// Add reply to the queue
								String key = ProtocolParsing.StringParser.getKey( msg ).shorthand;
								String value = ProtocolParsing.StringParser.getValue( msg );
								String[] args = ProtocolParsing.StringParser.getArgs( msg );
								updateQueue( key, value, args );
							} else {
								// Send to the connection listener
								listener.onDataReceived( msg );
							}
						}
					} catch ( IOException e ) {
						System.err.println( e.getClass() + ": " + e.getMessage() );
					}
				}
			}, 1, 10 );

			listener.onConnect();
		}

		/**
		 * Remove all null indices from queue and shrink down to size
		 */
		private void cleanQueue() {
			// Remove null indices from queue
			int nc = 0;
			for ( int i = 0; i < queue.length; i++ ) {
				if ( queue[i] == null ) {
					nc++;
				}
			}
			String[][][] newQueue = new String[queue.length - nc][3][0];
			int index = 0;
			for ( int i = 0; i < queue.length; i++ ) {
				if ( queue[i] != null ) {
					newQueue[index++] = queue[i];
				}
			}
		}

		/**
		 * Retrieve a key from the queue
		 *
		 * @param key Key to retrieve
		 * @return The value (and args) that correspond to the key or null if
		 *         not found
		 */
		public String[][] getKeyFromQueue( ProtocolParsing.Key key ) {
			return getKeyFromQueue( key.shorthand );
		}

		/**
		 * Retrieve a key from the queue
		 *
		 * @param key Key to retrieve
		 * @return The value (and args) that correspond to the key or null if
		 *         not found
		 */
		public String[][] getKeyFromQueue( String key ) {
			for ( int i = 0; i < queue.length; i++ ) {
				if ( queue[i] != null && queue[i][0] != null && queue[i][0][0].equals( key ) ) {
					String[][] value = queue[i];
					queue[i] = null;
					cleanQueue();
					return value;
				}
			}
			return null;
		}

		/**
		 * Dummy onConnect listener method
		 */
		public void onConnect() {
		}

		/**
		 * Dummy onDisconnect listener method
		 */
		public void onDisconnect() {
		}

		/**
		 * Dummy onDataReceived listener method
		 *
		 * @param msg Message received
		 */
		public void onDataReceived( String msg ) {
		}

		/**
		 * Send message to the dashboard
		 *
		 * @param msg Message to send
		 * @throws IOException
		 */
		public void send( String msg ) throws IOException {
			writer.write( "" + ( char ) msg.length() );
			writer.write( msg );
			writer.flush();
		}

		/**
		 * Add item to the queue
		 *
		 * @param key  Key of the queued item
		 * @param val  Value of the queued item
		 * @param args Arguments of the queued item
		 */
		private void updateQueue( String key, String val, String[] args ) {
			String[][][] newQueue = new String[queue.length + 1][3][0];
			System.arraycopy( queue, 0, newQueue, 0, queue.length );
			newQueue[queue.length] = new String[][] { new String[] { key }, new String[] { val }, args };
			queue = newQueue;
			cleanQueue();
		}
	}
}
