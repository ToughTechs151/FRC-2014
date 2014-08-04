package org.nashua.tt151.device;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;

import javax.swing.JPanel;

public abstract class Device extends JPanel {
	public static final int WIDTH = 100;
	public static final int HEIGHT = 50;
	protected static final int STATUS_WIDTH = WIDTH / 6;
	
	protected String name;
	protected int slot;
	
	public Device( int slot, String name ) {
		this.slot = slot;
		this.name = name;
		setPreferredSize( new Dimension( WIDTH, HEIGHT ) );
		setSize( getPreferredSize() );
		setBackground( Color.GRAY.darker().darker() );
	}
	
	public int getSlot() {
		return slot;
	}
	
	protected void drawSlot( Graphics g ) {
		g.setColor( Color.WHITE );
		FontMetrics fm = g.getFontMetrics();
		String s = "" + slot;
		g.drawString( s, WIDTH - fm.stringWidth( s ), fm.getAscent() );
	}
	
	protected void drawName( Graphics g ) {
		g.setColor( Color.WHITE );
		FontMetrics fm = g.getFontMetrics();
//		g.drawString( name, ( getWidth() + STATUS_WIDTH ) / 2 - fm.stringWidth( name ) / 2, fm.getAscent() );
		g.drawString( name, STATUS_WIDTH, fm.getAscent() );
	}
	
	protected void drawStatusLight( Graphics g, Color status ) {
		g.setColor( Color.GRAY.darker() );
		g.fillRect( 0, 0, STATUS_WIDTH, getHeight() );
		Graphics2D g2d = (Graphics2D) g;
		Paint oldPaint = g2d.getPaint();
		g2d.setPaint( new LinearGradientPaint( 0, 0, 0, getHeight(), new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { g.getColor(), status, g.getColor() } ) );
		g2d.fillRect( 0, 0, STATUS_WIDTH, getHeight() );
		g2d.setPaint( oldPaint );
	}
	
	public abstract void paintComponent( Graphics g );
}
