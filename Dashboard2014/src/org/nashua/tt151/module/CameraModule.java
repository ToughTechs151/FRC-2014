package org.nashua.tt151.module;

import ipcapture.IPCapture;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import org.nashua.tt151.Dashboard;
import org.nashua.tt151.module.ConnectionModule.State;
import org.nashua.tt151.tracking.Blob;
import org.nashua.tt151.tracking.LED;
import org.nashua.tt151.tracking.Processor;
import org.nashua.tt151.util.FileIOHelper;

import processing.core.PApplet;

public class CameraModule extends JPanel {
	private static final int WIDTH = 320;
	private static final int HEIGHT = 240;
	private final BufferedImage UNAVAILABLE;
	private IPCapture cam;
	private BufferedImage raw;
	private BufferedImage display;
	private BufferedImage mask;
	private Processor p;
	private volatile LED led = LED.GREEN;
	
	public double valSendY = 0.0;
	public double valSendH = 0.0;
	
	public static final String CAM_IP = "10.1.51.11";
	
	public CameraModule() {
		// Create image for no feed
		UNAVAILABLE = new BufferedImage( WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB );
		Graphics g = UNAVAILABLE.createGraphics();
		g.setColor( Color.BLACK );
		g.fillRect( 0, 0, UNAVAILABLE.getWidth(), UNAVAILABLE.getHeight() );
		g.setColor( Color.RED );
		g.setFont( new Font( "OCR A Std", Font.PLAIN, 26 ) );
		FontMetrics fm = g.getFontMetrics();
		String msg = "CAMERA FEED";
		g.drawString( msg, UNAVAILABLE.getWidth() / 2 - fm.stringWidth( msg ) / 2, UNAVAILABLE.getHeight() / 2 - fm.getAscent() );
		msg = "UNAVAILABLE";
		g.drawString( msg, UNAVAILABLE.getWidth() / 2 - fm.stringWidth( msg ) / 2, UNAVAILABLE.getHeight() / 2 + fm.getAscent() * 2 );
		
		// Initialize module
		setPreferredSize( new Dimension( WIDTH, HEIGHT ) );
		setSize( getPreferredSize() );
		display = UNAVAILABLE;
		cam = new IPCapture( new PApplet(), "http://" + CAM_IP + "/mjpg/video.mjpg", "FRC", "FRC" );
		cam.init( WIDTH, HEIGHT, IPCapture.RGB );
		cam.start();
		new Timer().scheduleAtFixedRate( new TimerTask() {
			public void run() {
				if ( !cam.isAlive() ) {
					cam.start();
					display = UNAVAILABLE;
				} else if ( cam.isAvailable() ) {
					cam.read();
					raw = (BufferedImage) cam.getNative();
					led = Dashboard.led.getLED();
					p = new Processor( raw, led, WIDTH, HEIGHT );
					mask = p.mask.getImage();
					display = new BufferedImage( WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB );
					Graphics g = display.createGraphics();
					g.drawImage( raw, 0, 0, null );
					if ( p.staticTarget != null ) {
						drawBlobDetails( g, p.staticTarget, Color.BLUE );
						if ( p.dynamicTarget != null ) {
							drawBlobDetails( g, p.dynamicTarget, Color.RED );
						}
					}
				}
			}
		}, 1, 33 );
	}
	
	private void drawBlobDetails( Graphics g, Blob b, Color crossColor ) {
		Rectangle r = b.getBounds();
		g.setColor( new Color( 255, 255, 0, 100 ) );
		g.fillRect( r.x, r.y, r.width, r.height );
		g.setColor( crossColor );
		g.drawLine( r.x + r.width / 2, 0, r.x + r.width / 2, HEIGHT );
		g.drawLine( 0, r.y + r.height / 2, WIDTH, r.y + r.height / 2 );
	}
	
	public void paintComponent( Graphics g ) {
		g.drawImage( Dashboard.led.showMask() ? mask : display, 0, 0, null );
	}
	
	public boolean isHot() {
		return p.staticTarget != null && p.dynamicTarget != null;
	}
}
