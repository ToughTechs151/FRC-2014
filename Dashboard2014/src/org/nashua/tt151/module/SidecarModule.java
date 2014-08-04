package org.nashua.tt151.module;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

import org.nashua.tt151.device.Device;
import org.nashua.tt151.device.DevicePanel;
import org.nashua.tt151.device.DigitalIODevice;
import org.nashua.tt151.device.PWMDevice;
import org.nashua.tt151.device.RelayDevice;

public class SidecarModule extends JPanel {
	public static final int MAX_PWM_SLOTS = 10;
	public static final int MAX_RELAY_SLOTS = 8;
	public static final int MAX_DIO_SLOTS = 14;
	private static final int TITLE_HEIGHT = 15;
	private static final int BORDER = 2;
	
	private DevicePanel<PWMDevice> pwm = new DevicePanel<PWMDevice>( "PWM", MAX_PWM_SLOTS );
	private DevicePanel<RelayDevice> relay = new DevicePanel<RelayDevice>( "RELAY", MAX_RELAY_SLOTS );
	private DevicePanel<DigitalIODevice> dio = new DevicePanel<DigitalIODevice>( "DIGITAL IO", MAX_DIO_SLOTS );
	private String name;
	
	public SidecarModule( String name ) {
		this.name = name;
		setBackground( Color.GRAY.darker().darker().darker() );
		dio.blankHeight = Device.HEIGHT / 2;
		dio.clearAllDevices();
		fixSize();
	}
	
	private void fixSize() {
		setPreferredSize( new Dimension( pwm.getWidth() + relay.getWidth() + dio.getWidth() + BORDER * 2, TITLE_HEIGHT + BORDER + Math.max( pwm.getHeight(), Math.max( relay.getHeight(), dio.getHeight() ) ) ) );
		setSize( getPreferredSize() );
	}
	
	public void paintComponent( Graphics g ) {
		fixSize();
		g.setColor( getBackground() );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		FontMetrics fm = g.getFontMetrics();
		g.setColor( Color.WHITE );
		g.drawString( name, getWidth() / 2 - fm.stringWidth( name ) / 2, TITLE_HEIGHT / 2 + fm.getAscent() / 2 );
		
		int x = BORDER;
		pwm.paintComponent( g.create( x, TITLE_HEIGHT, pwm.getWidth(), pwm.getHeight() ) );
		x += pwm.getWidth();
		relay.paintComponent( g.create( x, TITLE_HEIGHT, relay.getWidth(), relay.getHeight() ) );
		x += relay.getWidth();
		dio.paintComponent( g.create( x, TITLE_HEIGHT, dio.getWidth(), dio.getHeight() ) );
	}
	
	public void clearPWMDevice( int slot ) {
		pwm.clearDevice( slot );
	}
	
	public void clearRelayDevice( int slot ) {
		relay.clearDevice( slot );
	}
	
	public void clearDIODevice( int slot ) {
		dio.clearDevice( slot );
	}
	
	public void clearAllPWMDevices() {
		pwm.clearAllDevices();
	}
	
	public void clearAllRelayDevices() {
		relay.clearAllDevices();
	}
	
	public void clearAllDIODevices() {
		dio.clearAllDevices();
	}
	
	public void registerPWMDevice( PWMDevice d ) {
		pwm.registerDevice( d );
	}
	
	public void registerRelayDevice( RelayDevice d ) {
		relay.registerDevice( d );
	}
	
	public void registerDIODevice( DigitalIODevice d ) {
		dio.registerDevice( d );
	}
	
	public PWMDevice getPWMDevice( int slot ) {
		Device d = pwm.getDevice( slot );
		return (PWMDevice) ( d instanceof PWMDevice ? d : null );
	}
	
	public RelayDevice getRelayDevice( int slot ) {
		Device d = relay.getDevice( slot );
		return (RelayDevice) ( d instanceof RelayDevice ? d : null );
	}
	
	public DigitalIODevice getDIODevice( int slot ) {
		Device d = dio.getDevice( slot );
		return (DigitalIODevice) ( d instanceof DigitalIODevice ? d : null );
	}
	
}
