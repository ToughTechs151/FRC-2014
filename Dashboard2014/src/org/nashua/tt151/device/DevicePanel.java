package org.nashua.tt151.device;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;

import javax.swing.JPanel;

public class DevicePanel<T extends Device> extends JPanel {
	protected static final int TITLE_HEIGHT = 15;
	protected static final int BORDER = 2;
	protected T[] devices;
	private String name;
	private int slots;
	public int blankHeight = Device.HEIGHT;
	
	public DevicePanel( String name, int slots ) {
		devices = (T[]) new Device[slots];
		this.name = name;
		setPreferredSize( new Dimension( Device.WIDTH + BORDER*2, 1 ) );
		setSize( getPreferredSize() );
		clearAllDevices();
		setBackground( Color.GRAY.darker().darker().darker().darker() );
	}
	
	public void registerDevice( T device ) {
		devices[device.getSlot() - 1] = device;
		int h = TITLE_HEIGHT + BORDER;
		for(T t : devices) {
			if(t != null) {
				h += (Device) t instanceof BlankDevice ? blankHeight : t.getHeight();
			}
		}
		setPreferredSize( new Dimension( Device.WIDTH + BORDER*2, h ) );
		setSize( getPreferredSize() );
	}
	
	public void clearDevice( int slot ) {
		BlankDevice d = new BlankDevice( slot );
		d.setPreferredSize( new Dimension( d.getWidth(), blankHeight ) );
		d.setSize( d.getPreferredSize() );
		registerDevice( (T) d );
	}
	
	public void clearAllDevices() {
		for ( int i = 1; i <= devices.length; i++ ) {
			clearDevice( i );
		}
	}
	
	public T getDevice( int slot ) {
		return devices[slot - 1];
	}
	
	public T[] getDevices() {
		return devices;
	}
	
	public String getName() {
		return name;
	}
	
	public int getSlots() {
		return slots;
	}
	
	public boolean isSlotAvailable( int slot ) {
		return devices[slot - 1] instanceof BlankDevice;
	}
	
	public void paintComponent( Graphics g ) {
		g.setColor( getBackground() );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		FontMetrics fm = g.getFontMetrics();
		g.setColor( Color.WHITE );
		g.drawString( name, getWidth() / 2 - fm.stringWidth( name ) / 2, TITLE_HEIGHT/2 + fm.getAscent()/2 );
		int y = TITLE_HEIGHT;
		for ( int i = 0; i < devices.length; i++ ) {
			Device d = devices[i];
			d.paintComponent( g.create( BORDER, y, d.getWidth(), d.getHeight() ) );
			y += devices[i].getHeight();
		}
		
		Graphics2D g2d = (Graphics2D) g;
		Paint oldPaint = g2d.getPaint();
		g2d.setPaint( new LinearGradientPaint( 0, 0, getWidth(), 0, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { getBackground(), Color.WHITE, getBackground() } ) );
		y = TITLE_HEIGHT;
		for ( int i = 0; i < devices.length-1; i++ ) {
			y += devices[i].getHeight();
			g2d.drawLine( 0, y, getWidth(), y );
		}
		g2d.setPaint( oldPaint );
	}
}
