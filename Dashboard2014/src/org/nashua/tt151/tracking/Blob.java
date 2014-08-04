package org.nashua.tt151.tracking;

import java.awt.Rectangle;

public class Blob implements Comparable<Blob> {
	private int x1;
	private int x2;
	private int y1;
	private int y2;
	private int area;
	private double aspectRatio;
	
	public Blob( int x1, int x2, int y1, int y2, int area ) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.area = area;
		aspectRatio = (double) ( x2 - x1 ) / (double) ( y2 - y1 );
	}
	
	public int getArea() {
		return area;
	}
	
	public Rectangle getBounds() {
		return new Rectangle( x1, y1, x2 - x1, y2 - y1 );
	}
	
	public double getAspectRatio() {
		return aspectRatio;
	}
	
	public boolean equals( Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null || getClass() != obj.getClass() ) {
			return false;
		}
		Blob other = (Blob) obj;
		return area == other.area && x1 == other.x1 && x2 == other.x2 && y1 == other.y1 && y2 == other.y2;
	}
	
	public int compareTo( Blob o ) {
		return Integer.compare( area, o.area );
	}
}
