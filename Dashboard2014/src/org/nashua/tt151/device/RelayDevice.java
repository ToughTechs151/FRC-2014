package org.nashua.tt151.device;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class RelayDevice extends Device {
	public enum Direction {
		BACKWARD( '-' ),
		FORWARD( '+' ),
		BOTH( 'B' );
		
		private char sh;
		
		public static Direction getFromShorthand( char sh ) {
			for ( Direction d : Direction.values() ) {
				if ( d.sh == sh ) {
					return d;
				}
			}
			return BOTH;
		}
		
		private Direction( char sh ) {
			this.sh = sh;
		}
	}
	
	public enum Value {
		BACKWARD( '-', new Color( 255, 0, 0 ) ),
		FORWARD( '+', new Color( 0, 255, 0 ) ),
		OFF( '0', new Color( 255, 255, 0 ) );
		
		private char sh;
		private Color c;
		
		public static Value getFromShorthand( char sh ) {
			for ( Value v : Value.values() ) {
				if ( v.sh == sh ) {
					return v;
				}
			}
			return OFF;
		}
		
		private Value( char sh, Color c ) {
			this.sh = sh;
			this.c = c;
		}
	}
	
	private Direction dir;
	private Value value;
	
	public RelayDevice( int slot, String name, Direction dir, Value value ) {
		super( slot, name );
		this.dir = dir;
		this.value = value;
	}
	
	public void paintComponent( Graphics g ) {
		// Draw bg
		g.setColor( getBackground() );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		
		// Draw value
		g.setColor( Color.WHITE );
		Font oldFont = g.getFont();
		g.setFont( g.getFont().deriveFont( 14.0f ) );
		FontMetrics fm = g.getFontMetrics();
		String val = value.toString();
		g.drawString( val, ( getWidth() + STATUS_WIDTH ) / 2 - fm.stringWidth( val ) / 2, getHeight() / 2 + fm.getAscent() / 2 );
		g.setFont( oldFont );
		
		// Draw type
		g.setColor( Color.WHITE );
		fm = g.getFontMetrics();
		String t = ( "" + dir.sh ).toUpperCase();
		g.drawString( t, getWidth() - fm.stringWidth( t ), getHeight() );
		
		drawSlot( g );
		drawName( g );
		drawStatusLight( g, value.c );
	}
	
}
