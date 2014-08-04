package org.nashua.tt151.util;

/**
 * Math utility methods
 * @author Brian Ashworth (Original Author: Brennan Ringey)
 */
public final class MathTools {
    
    /**
     * Declared as private to force static usage
     */
    private MathTools() {
    }

    /**
     * Rounds a number up to a certain decimal place
     * @param val - Raw number
     * @param places - Decimal places
     * @return Rounded up number
     */
    public static double ceil(double val, int places) {
        return ((int) (val * pow(10, places))) / pow(10, places) + pow(10, -places);
    }

    /**
     * Rounds a number down to a certain decimal place
     * @param val - Raw number
     * @param places - Decimal places
     * @return Rounded down number
     */
    public static double floor(double val, int places) {
        return ((int) (val * pow(10, places))) / pow(10, places);
    }

    /**
     * Raise base to the power exp
     * @param base The number to use for the base
     * @param exp The exponent to use for the power
     * @return Base raised to the exp power
     */
    public static double pow(double base, int exp) {
        if (exp<0) {
            return 1.0/pow(base, -exp);
        }
        if (exp==0) {
            return 1;
        }
        double v = base;
        for (int i=1; i<exp; i++) {
            v *= base;
        }
        return v;
    }
    
    public static int numOfPlaces(double number) {
    	int count = 0;
    	
    	String n = String.valueOf(number);
    	String[] splitted = StringTools.split(n, '.');
    	
    	count = splitted[1].length();
    	
    	/*
    	double frac = number - floor(number, 0);
    	
    	while (frac - floor(frac, 0) != 0) {
    		
    		frac *= 10.0;
    		frac = round(frac, 10);
    		System.out.println(frac);
    		count++;
    	}*/
    	
    	return count;
    }
    
    /**
     * Returns the decimal (fractional) part of a double as a whole number
     * @param number A double
     * @return The double's decimal part as a whole number
     */
    public static double fpart(double number) {
    	String n = String.valueOf(number);
    	String[] splitted = StringTools.split(n, '.');
    	int parsed = Integer.parseInt(splitted[1]);
    	
    	return (double)parsed;
    }
    
    public static int ipart(double number) {
    	return (int)floor(number, 0);
    }
    
    /**
     * Round val to the closest number keeping places number of decimal places
     * @param val Original Value
     * @param places Number of decimal places to keep
     * @return Val rounded to the closest number keeping places number decimal places
     */
    public static double round(double val, int places) {
        return ((int) (val * pow(10, places))) % 10 > 4 ? ceil(val, places) : floor(val, places);
    }
}
