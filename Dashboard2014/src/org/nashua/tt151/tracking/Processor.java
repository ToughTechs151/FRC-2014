package org.nashua.tt151.tracking;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import org.nashua.tt151.Dashboard;
import org.nashua.tt151.module.ConnectionModule.State;

public class Processor {
	public BinaryImage mask;
	public ArrayList<Blob> targets = new ArrayList<Blob>();
	public Blob dynamicTarget;
	public Blob staticTarget;
	
	private final double DYNAMIC_MIN_ASPECT_RATIO = 2.25;
	private final double DYNAMIC_MAX_ASPECT_RATIO = 7.75;
	private final int SIDE_TOLERANCE = 40;
	private final double STATIC_MIN_ASPECT_RATIO = 0.05;
	private final double STATIC_MAX_ASPECT_RATIO = 0.75;
	
	final int WIDTH;
	final int HEIGHT;
	
	/**
	 * Filters a raw input image from a camera using an HSB filter according to the parameters contained in
	 * the LED object. Once the image is filtered, it is searched for Blobs of a minimum area.
	 * 
	 * @param raw Input image to be filtered
	 * @param led LED containing HSB parameters
	 * @param width Width of image
	 * @param height Height of image
	 */
	public Processor( BufferedImage raw, LED led, int width, int height ) {
		this.WIDTH = width;
		this.HEIGHT = height;
		mask = filterHSB( raw, led );
		targets = detectBlobs( mask, 100 );
		
		dynamicTarget = null;
		staticTarget = null;
		
		if ( targets.size() > 0 ) {
			Collections.sort( targets );
			Collections.reverse( targets );
			
			for ( Blob b : targets ) {
				if ( b.getAspectRatio() >= STATIC_MIN_ASPECT_RATIO && b.getAspectRatio() <= STATIC_MAX_ASPECT_RATIO ) {
					staticTarget = b;
					break;
				}
			}
			
//			if ( Dashboard.state.getState() == State.AUTONOMOUS && targets.size() > 1 ) {
				for ( Blob b : targets ) {
					if ( b.getAspectRatio() >= DYNAMIC_MIN_ASPECT_RATIO && b.getAspectRatio() <= DYNAMIC_MAX_ASPECT_RATIO ) {
						dynamicTarget = b;
						break;
					}
				}
//			}
			
			if ( dynamicTarget != null && staticTarget != null && !dynamicTarget.equals( staticTarget ) ) {
				Rectangle d = dynamicTarget.getBounds();
				Rectangle s = staticTarget.getBounds();
				boolean isTarget = false;
				if ( Math.abs( d.y + d.height - s.y ) < 20 ) {
					// Right target
					if ( d.x > s.x + s.width && Math.abs( d.x - s.x + s.width ) < SIDE_TOLERANCE ) {
						isTarget = true;
					}
					// Left target
					if ( d.x + d.width < s.x && Math.abs( d.x + d.width - s.x ) < SIDE_TOLERANCE ) {
						isTarget = true;
					}
				}
				dynamicTarget = isTarget ? dynamicTarget : null;
			}
			
			if ( staticTarget != null ) {
				Rectangle s = staticTarget.getBounds();
			}
		} else {
			dynamicTarget = null;
			staticTarget = null;
		}
	}
	
	/**
	 * Checks if a given value is in the given inclusive range
	 * 
	 * @param min Minimum value of range
	 * @param max Maximum value of range
	 * @param val Value to test
	 * @return If the value is inside the range
	 */
	private boolean inRange( float min, float max, float val ) {
		return min <= val && val <= max;
	}
	
	/**
	 * Filters an image with an HSB threshold. All pixels outside the given range are set to black. All inside
	 * are set to white.
	 * 
	 * @param in Raw image to filter
	 * @param led LED containing HSB parameters
	 * @return Filtered BinaryImage
	 */
	public BinaryImage filterHSB( final BufferedImage in, final LED led ) {
		BinaryImage out = new BinaryImage( in.getWidth(), in.getHeight() );
		for ( int y = 0; y < in.getHeight(); y++ ) {
			for ( int x = 0; x < in.getWidth(); x++ ) {
				int rgb = in.getRGB( x, y );
				float[] hsb = Color.RGBtoHSB( ( rgb >> 16 ) & 0xFF, ( rgb >> 8 ) & 0xFF, rgb & 0xFF, null );
				boolean h = inRange( led.getHueMin(), led.getHueMax(), hsb[0] );
				boolean s = inRange( led.getSaturationMin(), led.getSaturationMax(), hsb[1] );
				boolean b = inRange( led.getBrightnessMin(), led.getBrightnessMax(), hsb[2] );
				if ( h && s && b ) {
					out.setValue( x, y, true );
				}
			}
		}
		return out;
	}
	
	/**
	 * Searches through a binary image to find blobs with an area greater than the given value. A Blob is a
	 * cluster of connected pixels.
	 * 
	 * @param in Binary image to search
	 * @param minArea Minimum area of a blob
	 * @return An ArrayList of all blobs found in the image
	 */
	private ArrayList<Blob> detectBlobs( final BinaryImage in, int minArea ) {
		ArrayList<Blob> blobs = new ArrayList<Blob>();
		final int width = in.getWidth();
		final int height = in.getHeight();
		boolean[][] checked = new boolean[height][width];
		for ( int y = 0; y < height; y++ ) {
			for ( int x = 0; x < width; x++ ) {
				// If the pixel is white and hasn't already been checked
				if ( in.getValue( x, y ) && !checked[y][x] ) {
					// Perform a Breadth First Search to find all connected white pixels
					ArrayList<Point> open = new ArrayList<Point>();
					ArrayList<Point> blob = new ArrayList<Point>();
					open.add( new Point( x, y ) );
					while ( !open.isEmpty() ) {
						Point current = open.get( 0 );
						open.remove( 0 );
						checked[current.y][current.x] = true;
						blob.add( current );
						ArrayList<Point> neighbors = new ArrayList<Point>();
						if ( current.x > 0 && in.getValue( current.x - 1, current.y ) ) { // Left
							neighbors.add( new Point( current.x - 1, current.y ) );
						}
						if ( current.x < width - 1 && in.getValue( current.x + 1, current.y ) ) { // Right
							neighbors.add( new Point( current.x + 1, current.y ) );
						}
						if ( current.y > 0 && in.getValue( current.x, current.y - 1 ) ) { // Up
							neighbors.add( new Point( current.x, current.y - 1 ) );
						}
						if ( current.y < height - 1 && in.getValue( current.x, current.y + 1 ) ) { // Down
							neighbors.add( new Point( current.x, current.y + 1 ) );
						}
						
						for ( Point neighbor : neighbors ) {
							if ( checked[neighbor.y][neighbor.x] || open.contains( neighbor ) ) {
								continue;
							}
							open.add( neighbor );
						}
					}
					
					// Only continues if the blob is bigger than the given area
					// Finds min/max values of blob
					if ( blob.size() >= minArea ) {
						int minX = width;
						int maxX = 0;
						int minY = height;
						int maxY = 0;
						for ( Point p : blob ) {
							minX = Math.min( minX, p.x );
							maxX = Math.max( maxX, p.x );
							minY = Math.min( minY, p.y );
							maxY = Math.max( maxY, p.y );
						}
						blobs.add( new Blob( minX, maxX, minY, maxY, blob.size() ) );
					}
				}
				checked[y][x] = true;
			}
		}
		return blobs;
	}
}
