package org.nashua.tt151.device;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class DigitalIODevice extends Device {
	private final static Color ON = new Color( 0, 255, 0 );
	private final static Color OFF = new Color( 255, 0, 0 );
	private final static Color OTHER = new Color( 0, 255, 255 );
	
	public static double LIM_ON = 1.0;
	public static double LIM_OFF = 0.0;
	
	public enum DIGIOType {
		LIMIT_SWITCH( 'L' ),
		ENCODER( 'E' ),
		UNKNOWN( 'U' );
		
		private char sh;
		
		public static DIGIOType getFromShorthand( char sh ) {
			for ( DIGIOType d : DIGIOType.values() ) {
				if ( d.sh == sh ) {
					return d;
				}
			}
			return UNKNOWN;
		}
		
		private DIGIOType( char sh ) {
			this.sh = sh;
		}
	}
	
	private DIGIOType type;
	private double value;
	
	public DigitalIODevice( int slot, String name, double value, DIGIOType type ) {
		super( slot, name );
		this.value = value;
		this.type = type;
		if ( type != DIGIOType.ENCODER ) {
			setPreferredSize( new Dimension( WIDTH, HEIGHT / 2 ) );
			setSize( getPreferredSize() );
		}
	}
	
	public void paintComponent( Graphics g ) {
		// Draw bg
		g.setColor( getBackground() );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		
		FontMetrics fm;
		if ( type == DIGIOType.ENCODER ) {
			// Draw value
			g.setColor( Color.WHITE );
			Font oldFont = g.getFont();
			g.setFont( g.getFont().deriveFont( 22.0f ) );
			fm = g.getFontMetrics();
			String val = String.format( "%.2f", value );
			g.drawString( val, ( getWidth() + STATUS_WIDTH ) / 2 - fm.stringWidth( val ) / 2, getHeight() / 2 + fm.getAscent() / 2 );
			g.setFont( oldFont );
		}
		
		// Draw type
		g.setColor( Color.WHITE );
		fm = g.getFontMetrics();
		String t = ( "" + type.sh ).toUpperCase();
		g.drawString( t, getWidth() - fm.stringWidth( t ), getHeight() );
		
		drawSlot( g );
		drawName( g );
		if ( type == DIGIOType.ENCODER ) {
			drawStatusLight( g, OTHER );
		} else {
			drawStatusLight( g, value == 1.0 ? ON : OFF );
		}
	}
	
}
