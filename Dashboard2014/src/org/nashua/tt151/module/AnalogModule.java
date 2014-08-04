package org.nashua.tt151.module;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

import org.nashua.tt151.device.AnalogDevice;
import org.nashua.tt151.device.Device;
import org.nashua.tt151.device.DevicePanel;

public class AnalogModule extends JPanel {
	public static final int MAX_ANALOG_SLOTS = 8;
	private static final int TITLE_HEIGHT = 15;
	private static final int BORDER = 2;
	
	private DevicePanel<AnalogDevice> analog = new DevicePanel<AnalogDevice>( "ANALOG", MAX_ANALOG_SLOTS );
	private String name;
	
	public AnalogModule( String name ) {
		this.name = name;
		setBackground( Color.GRAY.darker().darker().darker() );
		fixSize();
	}
	
	private void fixSize() {
		setPreferredSize( new Dimension( analog.getWidth() + BORDER * 2, TITLE_HEIGHT + BORDER + analog.getHeight() ) );
		setSize( getPreferredSize() );
	}
	
	public void paintComponent( Graphics g ) {
		fixSize();
		g.setColor( getBackground() );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		FontMetrics fm = g.getFontMetrics();
		g.setColor( Color.WHITE );
		g.drawString( name, getWidth() / 2 - fm.stringWidth( name ) / 2, TITLE_HEIGHT / 2 + fm.getAscent() / 2 );
		analog.paintComponent( g.create( BORDER, TITLE_HEIGHT, analog.getWidth(), analog.getHeight() ) );
	}
	
	public void clearAnalogDevice(int slot) {
		analog.clearDevice( slot );
	}
	
	public void clearAllAnalogDevices() {
		analog.clearAllDevices();
	}
	
	public void registerAnalogDevice(AnalogDevice d) {
		analog.registerDevice( d );
	}
	
	public AnalogDevice getAnalogDevice(int slot) {
		Device d = analog.getDevice( slot );
		return (AnalogDevice) ( d instanceof AnalogDevice ? d : null );
	}
	
}
