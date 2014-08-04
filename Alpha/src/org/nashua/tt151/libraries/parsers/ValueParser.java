package org.nashua.tt151.libraries.parsers;

/**
 * A class containing methods for converting doubles and ints to adhere to the
 * protocol. The class can convert doubles and integers to message-format and
 * contains wrappers for safely parsing strings
 * 
 * @author Brennan
 * @version 1.1
 */
public final class ValueParser {
	
	// Prevent Instantiation
	private ValueParser() {}
	
	/**
	 * Clamps an integer and converts it to a string
	 * 
	 * @param num Integer to convert
	 * @param min Minimum the number can be
	 * @param max Maximum the number can be
	 * @return A string representing the number
	 */
	public static String Convert( int num, int min, int max ) {
		// Clamp
		if ( num > max ) {
			num = max;
		}
		if ( num < min ) {
			num = min;
		}
		return Integer.toString( num );
	}
	
	/**
	 * Clamps a double, truncates it and converts it to a string
	 * 
	 * @param num Double to convert
	 * @param min Minimum to clamp
	 * @param max Maximum to clamp
	 * @param places Number of decimal places (rounded)
	 * @return
	 */
	public static String Convert( double num, double min, double max, int places ) {
		// Clamping
		if ( num > max ) {
			num = max;
		}
		if ( num < min ) {
			num = min;
		}
		// Truncating
		// BigDecimal bd = new BigDecimal(num);
		// bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
		// return bd.doubleValue() + "";
		return "" + num;
	}
	
	/**
	 * Safely parses a string to an integer.
	 * 
	 * @param val String representing an integer
	 * @return An integer representing the string, -1 is returned if the string cannot be parsed
	 */
	public static int ParseInt( String val ) {
		int num;
		
		try {
			num = Integer.parseInt( val );
		} catch ( NumberFormatException ex ) {
			System.out.println( ex.toString() );
			num = -1;
		}
		
		return num;
	}
	
	/**
	 * Safely parses a string to a double.
	 * 
	 * @param val String to parse
	 * @return A double best represented by the string, Double.NaN is returned upon error
	 */
	public static double ParseDouble( String val ) {
		double d;
		
		try {
			d = Double.parseDouble( val );
		} catch ( NumberFormatException ex ) {
			System.out.println( ex.toString() );
			d = Double.NaN;
		}
		
		return d;
	}
	
}
