package org.nashua.tt151.module;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

public class DeviceModule extends JPanel {
	private static final int BORDER = 2;
	
	private SidecarModule sidecar = new SidecarModule( "Sidecar" );
	private AnalogModule analog = new AnalogModule( "Analog Module" );

	public DeviceModule() {
		setBackground( Color.GRAY.darker().darker().darker() );
		fixSize();
	}
	
	public SidecarModule getSidecar() {
		return sidecar;
	}
	
	public AnalogModule getAnalogModule() {
		return analog;
	}
	
	private void fixSize() {
		setPreferredSize( new Dimension( sidecar.getWidth() + analog.getWidth() + BORDER*2, BORDER*2 + Math.max( sidecar.getHeight(), analog.getHeight() ) ) );
		setSize( getPreferredSize() );
	}
	
	public void paintComponent(Graphics g) {
		fixSize();
		g.setColor( getBackground() );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		
		int x = BORDER;
		sidecar.paintComponent( g.create( x, BORDER, sidecar.getWidth(), sidecar.getHeight() ) );
		x += sidecar.getWidth();
		analog.paintComponent( g.create( x, BORDER, analog.getWidth(), analog.getHeight() ) );
	}
}
