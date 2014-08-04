package org.nashua.tt151.device;

import java.awt.Color;
import java.awt.Graphics;

public class BlankDevice extends Device {
	public BlankDevice( int slot ) {
		super( slot, "BLANK" );
	}
	
	public void paintComponent( Graphics g ) {
		g.setColor( getBackground() );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		drawSlot( g );
		drawStatusLight( g, Color.GRAY );
	}
	
}
