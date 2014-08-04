package org.nashua.tt151;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import org.nashua.tt151.ServerConnection.ConnectionListener;
import org.nashua.tt151.device.AnalogDevice;
import org.nashua.tt151.device.AnalogDevice.AnalogType;
import org.nashua.tt151.device.DigitalIODevice;
import org.nashua.tt151.device.DigitalIODevice.DIGIOType;
import org.nashua.tt151.device.PWMDevice;
import org.nashua.tt151.device.PWMDevice.PWMType;
import org.nashua.tt151.device.RelayDevice;
import org.nashua.tt151.device.RelayDevice.Direction;
import org.nashua.tt151.device.RelayDevice.Value;
import org.nashua.tt151.libraries.parsers.ProtocolParsing.Command;
import org.nashua.tt151.libraries.parsers.ProtocolParsing.Key;
import org.nashua.tt151.libraries.parsers.ProtocolParsing.StringParser;
import org.nashua.tt151.libraries.parsers.ValueParser;
import org.nashua.tt151.module.BandwidthModule;
import org.nashua.tt151.module.CameraModule;
import org.nashua.tt151.module.ConnectionModule;
import org.nashua.tt151.module.ConnectionModule.State;
import org.nashua.tt151.module.DeviceModule;
import org.nashua.tt151.module.InstructionsModule;
import org.nashua.tt151.module.LEDModule;
import org.nashua.tt151.ui.SleekFrame;
import org.nashua.tt151.ui.Splash;
import org.nashua.tt151.util.FileIOHelper;

public class Dashboard {
	public static SleekFrame frame;
	public static DeviceModule devices = new DeviceModule();
	public static ConnectionModule state = new ConnectionModule();
	public static CameraModule camera = new CameraModule();
	public static LEDModule led = new LEDModule();
	public static InstructionsModule instructions = new InstructionsModule();
	public static BandwidthModule bandwidth = new BandwidthModule();
	private int port = 1735;
	private ServerConnection server;
	
	public static void main( String[] args ) {
		new Dashboard();
	}
	
	public Dashboard() {
		Logger.logLine( "[FRC 2014 Dashboard]" );
		Logger.logLine( Calendar.getInstance().getTime().toString() );
		Logger.logLine( "Camera IP Address: " + CameraModule.CAM_IP );
		Logger.logLine( "Port: " + port );
		
		Splash.display(); // Display splash screen
		
		// Create window
		frame = new SleekFrame( "Dashboard 2014" );
		frame.setIconImage( FileIOHelper.loadImage( "/tticon.png" ) );
		frame.setTitleFont( new Font( "OCR A Std", Font.PLAIN, 20 ) );
		
		final int HEIGHT;
		final int WIDTH = devices.getWidth() + camera.getWidth() + instructions.getWidth();
		
		JPanel panel = new JPanel( null );
		panel.setBackground( Color.GRAY.darker().darker() );
		
		// Position all modules
		devices.setBounds( 0, 0, devices.getWidth(), devices.getHeight() );
		camera.setBounds( devices.getWidth(), 0, camera.getWidth(), camera.getHeight() );
		instructions.setBounds( devices.getWidth() + camera.getWidth(), 0, instructions.getWidth(), instructions.getHeight() );
		led.setBounds( devices.getWidth(), camera.getHeight(), camera.getWidth(), devices.getHeight() - camera.getHeight() );
		bandwidth.setBounds( devices.getWidth() + camera.getWidth(), instructions.getHeight(), instructions.getWidth(), devices.getHeight() - instructions.getHeight() );
		state.setBounds( 0, devices.getHeight(), WIDTH, 30 );
		
		HEIGHT = devices.getHeight() + state.getHeight();
		
		// Add all modules
		panel.add( devices );
		panel.add( camera );
		panel.add( instructions );
		panel.add( led );
		panel.add( bandwidth );
		panel.add( state );
		
		// Display frame
		panel.setBounds( SleekFrame.BORDER, SleekFrame.TITLE_HEIGHT, WIDTH, HEIGHT );
		frame.setLayout( null );
		frame.add( panel );
		frame.setSize( WIDTH, HEIGHT );
		frame.setVisible( true );
		
		new Timer().schedule( new TimerTask() {
			public void run() {
				frame.repaint();
			}
		}, 1, 1 );
		
		// Create server connection
		try {
			server = new ServerConnection( port, new ConnectionListener() {
				public void onConnect( Socket s ) {
					Logger.logLine( "Connected to: " + s + " at " + Calendar.getInstance().getTime().toString() );
					state.setState( State.CONNECTED );
					devices.getSidecar().clearAllPWMDevices();
					devices.getSidecar().clearAllRelayDevices();
					devices.getSidecar().clearAllDIODevices();
					devices.getAnalogModule().clearAllAnalogDevices();
				}
				
				public void onDataReceived( Socket s, String msg ) {
					System.out.println( msg );
					if ( msg.startsWith( "LOG:" ) ) {
						Logger.logLine( "[ROBOT] " + msg.substring( msg.indexOf( ':' ) + 1 ) );
					} else if ( StringParser.isSend( msg ) ) {
						Key key = StringParser.getKey( msg );
						String val = StringParser.getValue( msg );
						String[] args = StringParser.getArgs( msg );
						if ( key.equals( Key.AnalogValue ) ) {
							devices.getAnalogModule().registerAnalogDevice( new AnalogDevice( ValueParser.ParseInt( args[1] ), args[0], ValueParser.ParseDouble( val ), AnalogType.getFromShorthand( args[2].charAt( 0 ) ) ) );
						} else if ( key.equals( Key.PWMValue ) ) {
							devices.getSidecar().registerPWMDevice( new PWMDevice( ValueParser.ParseInt( args[1] ), args[0], ValueParser.ParseDouble( val ), PWMType.getFromShorthand( args[2].charAt( 0 ) ) ) );
						} else if ( key.equals( Key.RelayValue ) ) {
							devices.getSidecar().registerRelayDevice( new RelayDevice( ValueParser.ParseInt( args[1] ), args[0], Direction.getFromShorthand( args[2].charAt( 0 ) ), Value.getFromShorthand( val.charAt( 0 ) ) ) );
						} else if ( key.equals( Key.DigitalIO ) ) {
							devices.getSidecar().registerDIODevice( new DigitalIODevice( ValueParser.ParseInt( args[1] ), args[0], ValueParser.ParseDouble( val ), DIGIOType.getFromShorthand( args[2].charAt( 0 ) ) ) );
						} else if ( key.equals( Key.Status ) ) {
							state.setState( State.getFromShorthand( val.charAt( 0 ) ) );
						}
					} else if ( StringParser.isQuery( msg ) ) {
						Key key = StringParser.getKey( msg );
						if ( key.equals( Key.TargetHot ) ) {
							server.send( StringParser.createMessage( Command.Reply, Key.TargetHot, camera.isHot() ? "1" : "0", new String[] {} ) );
						}
					}
				}
				
				public void onDisconnect( Socket s ) {
					Logger.logLine( "Disconnected from: " + s + " at " + Calendar.getInstance().getTime().toString() );
					state.setState( State.DISCONNECTED );
					devices.getSidecar().clearAllPWMDevices();
					devices.getSidecar().clearAllRelayDevices();
					devices.getSidecar().clearAllDIODevices();
					devices.getAnalogModule().clearAllAnalogDevices();
				}
			} );
		} catch ( IOException e ) {
			Logger.logLine( "Port " + port + " is already in use. Dashboard shutting down." );
			frame.dispatchEvent( new WindowEvent( frame, WindowEvent.WINDOW_CLOSING ) );
		}
	}
}
