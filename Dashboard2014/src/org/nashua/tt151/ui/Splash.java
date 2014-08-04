package org.nashua.tt151.ui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.nashua.tt151.util.FileIOHelper;

public class Splash {
	/**
	 * Displays a splash of the TT151 logo then closes itself
	 */
	public static void display() {
		JFrame splash = new JFrame();
		splash.setUndecorated( true );
		splash.setResizable( false );
		// Load GIF
		ImageIcon i = new ImageIcon( FileIOHelper.loadResourceAsURL( "ttsplash.gif" ) );
		JLabel logo = new JLabel( i );
		splash.add( logo );
		splash.setSize( i.getIconWidth(), i.getIconHeight() );
		// Center Splash on screen
		splash.setLocationRelativeTo( null );
		// Display Splash
		splash.setVisible( true );
		try {
			Thread.sleep( 1600 ); // 50ms/frame, 32 frames
		} catch ( InterruptedException e ) {}
		// Close Splash
		splash.setVisible( false );
		splash.dispose();
	}
}