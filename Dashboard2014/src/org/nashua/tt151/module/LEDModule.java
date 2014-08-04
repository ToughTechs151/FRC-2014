package org.nashua.tt151.module;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.nashua.tt151.Dashboard;
import org.nashua.tt151.tracking.LED;

public class LEDModule extends JPanel {
	private JCheckBox dispMask;
	private JSlider hMin;
	private JSlider hMax;
	private JSlider sMin;
	private JSlider sMax;
	private JSlider bMin;
	private JSlider bMax;
	private JComboBox<String> presets;
	private final float PRECISION = 1e9f;
	
	public LEDModule() {
		setLayout( new GridLayout( 8, 2 ) );
		JPanel blankPanel = new JPanel();
		
		//Mask checkbox
		dispMask = new JCheckBox("Display Mask");
		dispMask.setSelected( false );
		add( dispMask );
		add( blankPanel );
		// Dropdown of preset values
		presets = new JComboBox<String>( new String[] { "Blue", "Green", "Custom" } );
		presets.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				switch ( (String) presets.getSelectedItem() ) {
					case "Blue":
						setSliders( false );
						setLED( LED.BLUE );
						break;
					case "Green":
						setSliders( false );
						setLED( LED.GREEN );
						break;
					case "Custom":
					default:
						setSliders( true );
						break;
				}
			}
		} );
		
		// HUE
		final JLabel hMinLbl = new JLabel( "Hue Min: " + 0.0, JLabel.CENTER );
		hMin = new JSlider( 0, (int) PRECISION, 0 );
		hMin.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				hMin.setValue( clamp( 0, hMax.getValue(), hMin.getValue() ) );
				hMinLbl.setText( "Hue Min: " + hMin.getValue() / PRECISION );
			}
		} );
		final JLabel hMaxLbl = new JLabel( "Hue Max: " + 1.0, JLabel.CENTER );
		hMax = new JSlider( 0, (int) PRECISION, (int) PRECISION );
		hMax.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				hMax.setValue( clamp( hMin.getValue(), (int) PRECISION, hMax.getValue() ) );
				hMaxLbl.setText( "Hue Max: " + hMax.getValue() / PRECISION );
			}
		} );
		
		// SATURATION
		final JLabel sMinLbl = new JLabel( "Saturation Min: " + 0.0, JLabel.CENTER );
		sMin = new JSlider( 0, (int) PRECISION, 0 );
		sMin.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				sMin.setValue( clamp( 0, sMax.getValue(), sMin.getValue() ) );
				sMinLbl.setText( "Saturation Min: " + sMin.getValue() / PRECISION );
			}
		} );
		final JLabel sMaxLbl = new JLabel( "Saturation Max: " + 1.0, JLabel.CENTER );
		sMax = new JSlider( 0, (int) PRECISION, (int) PRECISION );
		sMax.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				sMax.setValue( clamp( sMin.getValue(), (int) PRECISION, sMax.getValue() ) );
				sMaxLbl.setText( "Saturation Max: " + sMax.getValue() / PRECISION );
			}
		} );
		
		// BRIGHTNESS
		final JLabel bMinLbl = new JLabel( "Brightness Min: " + 0.0, JLabel.CENTER );
		bMin = new JSlider( 0, (int) PRECISION, 0 );
		bMin.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				bMin.setValue( clamp( 0, bMax.getValue(), bMin.getValue() ) );
				bMinLbl.setText( "Brightness Min: " + bMin.getValue() / PRECISION );
			}
		} );
		final JLabel bMaxLbl = new JLabel( "Brightness Max: " + 1.0, JLabel.CENTER );
		bMax = new JSlider( 0, (int) PRECISION, (int) PRECISION );
		bMax.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				bMax.setValue( clamp( bMin.getValue(), (int) PRECISION, bMax.getValue() ) );
				bMaxLbl.setText( "Brightness Max: " + bMax.getValue() / PRECISION );
			}
		} );
		
		JLabel LEDLbl = new JLabel( "LED:", JLabel.CENTER );
		
		// Set colors
		Color bg = Color.DARK_GRAY;
		Color fg = Color.WHITE;
		setBackground( bg );
		blankPanel.setBackground( bg );
		dispMask.setBackground( bg );
		dispMask.setForeground( fg );
		presets.setBackground( bg );
		presets.setForeground( fg );
		hMin.setBackground( bg );
		hMax.setBackground( bg );
		sMin.setBackground( bg );
		sMax.setBackground( bg );
		bMin.setBackground( bg );
		bMax.setBackground( bg );
		LEDLbl.setForeground( fg );
		hMinLbl.setForeground( fg );
		hMaxLbl.setForeground( fg );
		sMinLbl.setForeground( fg );
		sMaxLbl.setForeground( fg );
		bMinLbl.setForeground( fg );
		bMaxLbl.setForeground( fg );
		
		// Add everything
		add( LEDLbl );
		add( presets );
		add( hMinLbl );
		add( hMin );
		add( hMaxLbl );
		add( hMax );
		add( sMinLbl );
		add( sMin );
		add( sMaxLbl );
		add( sMax );
		add( bMinLbl );
		add( bMin );
		add( bMaxLbl );
		add( bMax );
		
		presets.setSelectedIndex( 0 );
	}
	
	private void setSliders( boolean enabled ) {
		hMin.setEnabled( enabled );
		hMax.setEnabled( enabled );
		sMin.setEnabled( enabled );
		sMax.setEnabled( enabled );
		bMin.setEnabled( enabled );
		bMax.setEnabled( enabled );
	}
	
	public LED getLED() {
		return new LED( hMin.getValue() / PRECISION, hMax.getValue() / PRECISION, sMin.getValue() / PRECISION, sMax.getValue() / PRECISION, bMin.getValue() / PRECISION, bMax.getValue() / PRECISION );
	}
	
	private void setLED( LED led ) {
		hMin.setValue( (int) ( led.getHueMin() * PRECISION ) );
		hMax.setValue( (int) ( led.getHueMax() * PRECISION ) );
		sMin.setValue( (int) ( led.getSaturationMin() * PRECISION ) );
		sMax.setValue( (int) ( led.getSaturationMax() * PRECISION ) );
		bMin.setValue( (int) ( led.getBrightnessMin() * PRECISION ) );
		bMax.setValue( (int) ( led.getBrightnessMax() * PRECISION ) );
	}
	
	private int clamp( int min, int max, int val ) {
		return Math.min( max, Math.max( min, val ) );
	}
	
	public boolean showMask() {
		return dispMask.isSelected();
	}
}
