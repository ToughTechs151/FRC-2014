package org.nashua.tt151.device;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class PWMDevice extends Device {
	private static final Color NEUTRAL = new Color( 255, 255, 0 );
	private static final Color FORWARD = new Color( 0, 255, 0 );
	private static final Color BACKWARD = new Color( 255, 0, 0 );
	
	public enum PWMType {
		JAGUAR( 'J' ),
		VICTOR( 'V' ),
		TALON( 'T' ),
		SERVO( 'S' ),
		UNKNOWN( 'U' );
		
		private char sh;
		
		public static PWMType getFromShorthand( char sh ) {
			for ( PWMType p : PWMType.values() ) {
				if ( p.sh == sh ) {
					return p;
				}
			}
			return UNKNOWN;
		}
		
		private PWMType( char sh ) {
			this.sh = sh;
		}
	}
	
	private String name;
	private PWMType type;
	private double value;
	
	public PWMDevice( int slot, String name, double value, PWMType type ) {
		super( slot, name );
		this.value = value;
		this.type = type;
	}
	
	public void paintComponent( Graphics g ) {
		// Draw bg
		g.setColor( getBackground() );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		
		// Draw value
		g.setColor( Color.WHITE );
		Font oldFont = g.getFont();
		g.setFont( g.getFont().deriveFont( 22.0f ) );
		FontMetrics fm = g.getFontMetrics();
		String val = String.format( "%.2f", value );
		g.drawString( val, ( getWidth() + STATUS_WIDTH ) / 2 - fm.stringWidth( val ) / 2, getHeight() / 2 + fm.getAscent() / 2 );
		g.setFont( oldFont );
		
		// Draw type
		g.setColor( Color.WHITE );
		fm = g.getFontMetrics();
		String t = ( "" + type.sh ).toUpperCase();
		g.drawString( t, getWidth() - fm.stringWidth( t ), getHeight() );
		
		drawSlot( g );
		drawName( g );
		if ( type == PWMType.SERVO ) {
			drawStatusLight( g, NEUTRAL );
		} else {
			drawStatusLight( g, value == 0 ? NEUTRAL : ( value > 0 ? FORWARD : BACKWARD ) );
		}
	}
}
