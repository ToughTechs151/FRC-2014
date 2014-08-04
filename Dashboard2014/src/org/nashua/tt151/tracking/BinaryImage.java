package org.nashua.tt151.tracking;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * An image whose pixels can only either be 0 (black) or 1 (white)
 * 
 * @author Kareem El-Faramawi
 */
public class BinaryImage {
	public static final int BLACK = 0xFF000000;
	public static final int WHITE = 0xFFFFFFFF;
	
	private boolean[][] data;
	private final int WIDTH;
	private final int HEIGHT;
	
	public BinaryImage( int width, int height ) {
		WIDTH = width;
		HEIGHT = height;
		data = new boolean[HEIGHT][WIDTH];
	}
	
	public int getWidth() {
		return WIDTH;
	}
	
	public int getHeight() {
		return HEIGHT;
	}
	
	public boolean getValue( int x, int y ) {
		return data[y][x];
	}
	
	public void setValue( int x, int y, boolean value ) {
		data[y][x] = value;
	}
	
	public void setValue( int x, int y, int value ) {
		if ( value != BLACK && value != WHITE ) {
			System.err.println( "Invalid value - use BinaryImage constants" );
			return;
		}
		data[y][x] = value == WHITE;
	}
	
	public void fillBlack() {
		data = new boolean[HEIGHT][WIDTH];
	}
	
	public void fillWhite() {
		for ( int y = 0; y < HEIGHT; y++ ) {
			for ( int x = 0; x < WIDTH; x++ ) {
				data[y][x] = true;
			}
		}
	}
	
	public BufferedImage getImage() {
		BufferedImage img = new BufferedImage( WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_BINARY );
		for ( int y = 0; y < HEIGHT; y++ ) {
			for ( int x = 0; x < WIDTH; x++ ) {
				img.setRGB( x, y, data[y][x] ? WHITE : BLACK );
			}
		}
		return img;
	}
	
	public void draw( Graphics g, int drawX, int drawY ) {
		g.drawImage( getImage(), drawX, drawY, null );
	}
}