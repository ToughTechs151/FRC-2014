package org.nashua.tt151.tracking;

public final class LED {
	public static final LED GREEN = new LED( 0.28104576f, 0.5f, 0.21699347f, 1.0f, 0.53267974f, 1.0f );
	public static final LED BLUE = new LED( 0.26027396f, 0.6712329f, 0.6369863f, 1.0f, 0.8150685f, 1.0f ); // Change
																											// this
	private float hMin;
	private float hMax;
	private float sMin;
	private float sMax;
	private float bMin;
	private float bMax;
	
	public LED( float hMin, float hMax, float sMin, float sMax, float bMin, float bMax ) {
		this.hMin = hMin;
		this.hMax = hMax;
		this.sMin = sMin;
		this.sMax = sMax;
		this.bMin = bMin;
		this.bMax = bMax;
	}
	
	public float getHueMin() {
		return hMin;
	}
	
	public void setHueMin( float hMin ) {
		this.hMin = hMin;
	}
	
	public float getHueMax() {
		return hMax;
	}
	
	public void setHueMax( float hMax ) {
		this.hMax = hMax;
	}
	
	public float getSaturationMin() {
		return sMin;
	}
	
	public void setSaturationMin( float sMin ) {
		this.sMin = sMin;
	}
	
	public float getSaturationMax() {
		return sMax;
	}
	
	public void setSaturationMax( float sMax ) {
		this.sMax = sMax;
	}
	
	public float getBrightnessMin() {
		return bMin;
	}
	
	public void setBrightnessMin( float bMin ) {
		this.bMin = bMin;
	}
	
	public float getBrightnessMax() {
		return bMax;
	}
	
	public void setBrightnessMax( float bMax ) {
		this.bMax = bMax;
	}
}