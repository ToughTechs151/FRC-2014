package org.nashua.tt151.device;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class AnalogDevice extends Device {
	
	public enum AnalogType {
		ACCELEROMETER( 'A', new Color( 0, 0, 255 ) ),
		GYRO( 'G', new Color( 255, 255, 0 ) ),
		POTENTIOMETER( 'P', new Color( 0, 255, 0 ) ),
		UNKNOWN( 'U', new Color( 255, 0, 0 ) );
		
		private char sh;
		private Color c;
		
		public static AnalogType getFromShorthand( char sh ) {
			for ( AnalogType a : AnalogType.values() ) {
				if ( a.sh == sh ) {
					return a;
				}
			}
			return UNKNOWN;
		}
		
		private AnalogType( char sh, Color c ) {
			this.sh = sh;
			this.c = c;
		}
	}
	
	private double value;
	private AnalogType type;
	
	public AnalogDevice( int slot, String name, double value,  AnalogType type ) {
		super( slot, name );
		this.value = value;
		this.type = type;
	}
	
	public void paintComponent( Graphics g ) {
		//Draw bg
		g.setColor( getBackground() );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		
		//Draw value
		g.setColor( Color.WHITE );
		Font oldFont = g.getFont();
		g.setFont( g.getFont().deriveFont( 22.0f ) );
		FontMetrics fm = g.getFontMetrics();
		String val = String.format( "%.2f", value );
		g.drawString( val, ( getWidth() + STATUS_WIDTH ) / 2 - fm.stringWidth( val ) / 2, getHeight() / 2 + fm.getAscent() / 2 );
		g.setFont( oldFont );
		
		//Draw type
		g.setColor( Color.WHITE );
		fm = g.getFontMetrics();
		String t = ( "" + type.sh ).toUpperCase();
		g.drawString( t, getWidth() - fm.stringWidth( t ), getHeight() );
		
		drawSlot( g );
		drawName( g );
		drawStatusLight( g, type.c );
	}
	
}
