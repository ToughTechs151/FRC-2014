
package org.nashua.tt151.util;

/**
 * A class containing static methods for bitwise/binary calculations
 * @author Brennan
 * @version 1.2
 */
public class BitwiseTools {
    
    /**
     * Sets a bit in a byte to zero regardless of what it is.
     * 00000111 (7) negate bit 0 = 00000110 (6)
     * @param num The byte to negate a bit
     * @param bit The zero based index of the bit (0 to 7)
     * @return A byte with the bit removed
     */
    public static int negatebit(int num, int bit) {
        int power = (int)MathTools.pow(2.0, bit);
        if ((num & power) == power) {
            num -= power;
        }
        
        return num;
    }
    
    /**
     * for-loop wrapper for negatebit.
     * @param num
     * @param bitstart
     * @param bitend
     * @return 
     */
    public static int negatebits(int num, int bitstart, int bitend) {
       
        for (int i = bitstart; i != bitend + 1; i++) {
            num = negatebit(num, i);
        }
        return num;
    }
    
    /**
     * Method creates a short from two unsigned bytes
     * @param one An integer representing the first byte of the short (0-255)
     * @param two An integer representing the second byte of the short (0-255)
     * @return An integer representing an unsigned short (0-65535)
     */
    public static int makeShort(int one, int two) {
        int s = 0;
        
        for (int i = 0; i != 8; i++) {
            int pow = (int)MathTools.pow(2.0, i);
            int pow2 = (int)MathTools.pow(2.0, i+8);
            if ((one & pow) == pow) { s += pow; }
            if ((two & pow) == pow) { s += pow2; }
            
        }
        return s;
    }
    
    /**
     * Returns an array of characters representing a short
     * @param sh A short to convert to char array
     * @return An array of two characters representing the short
     */
    public static String toCharShort(int sh) {
    	int c1 = 0, c2 = 0;
    	
    	for (int i = 0; i != 8; i++) {
            int pow = (int)MathTools.pow(2.0, i);
            int pow2 = (int)MathTools.pow(2.0, i+8);
            if ((sh & pow) == pow) { c1 += pow; }
            if ((sh & pow2) == pow2) { c2 += pow; }
    	}   
    	return "" + (char)c1 + (char)c2;
    }
    
    public static String toShortDouble(double d) {
    	String s = "";
    	
    	String whole = toCharShort((int)MathTools.floor(d, 0));
    	
    	double frac = d - Math.floor(d);
    	do {
    		frac  *= 10.0;
    		frac = MathTools.round(frac, 5);
    	} while (frac - MathTools.floor(frac, 0) != 0);
    	
    	String fraction = toCharShort((int)frac);
    	
    	s = whole + fraction;
    	
    	return s;
    }
    
    public static String toByteDouble(double d) {
    	
    	String whole = "" + (char)MathTools.floor(d, 0);
    	double frac = d - MathTools.floor(d, 0);
    	do {
    		frac *= 10.0;
    		frac = MathTools.round(frac, 5);
    	} while (frac - MathTools.floor(frac, 0) != 0);
    	
    	String fraction = "" + (char)((int)frac);
    	
    	return whole + fraction;
    }
    
   /**
     * Returns an int representing an unsigned byte. -128 is 255, -127 is 254, etc
     * @param num A byte to unsign
     * @return An int representing the byte unsigned
     */
    public static int unsign(byte num) {
        int s;
        if (num < 0) {
            s = ((num * -1) + 127);
        } else {
            s = num;
        }
        return s;
    }
    public static int unsign(short num) {
        int s;
        if (num < 0) {
            s = ((num * -1) + 32767);
        } else {
            s = num;
        }
        return s;
    }
    
    public static byte signbyte(int num) {
        byte b = 0;
        if (num >= 0 && num <= 255) {
            if (num > 127) {
                b = (byte)((num - 127) * -1);
            }
            else {
                b = (byte)num;
            }
        }
        return b;
    }
    
    public static short signshort(int num) {
        short s = 0;
        if (num >= 0 && num <= 65535) {
            if (num > 32767) {
                s = (short)((num - 32767) * -1);
            } else {
                s = (short)num;
            }
        }
        return s;
    }
}
