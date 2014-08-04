package org.nashua.tt151.util;

/**
 * A utility class containing methods for string handling
 * @author Brennan
 * @version 1.1
 */
public class StringTools {
    
    private StringTools() {}
    
    //Helper method used for counting the occurances of a character in a String
    public static int count(String msg, char sep) {
        int c = 0;
        for (int i=0;i<msg.length();i++) {
            if (msg.charAt(i)==sep) {
                c++;
            }
        }
        return c;
    }
    
    //DEPRECATED
    //Helper method used for splitting a String based on a character
    /*TODO Make it prettier
    public static String[] split(String msg, char sep) {
        String[] parts = new String[count(msg, sep)+1];
        int index = -1;
        parts[0] = msg.substring(0, msg.indexOf(sep, index+1));
        for (int i=1;i<parts.length;i++) {
            int findex = msg.indexOf(sep, index+1);
            if (findex==-1) {
                parts[i] = msg.substring(index+1);
                break;
            } else {
                index=msg.indexOf(sep, findex+1);
                if (index==-1) {
                    parts[i] = msg.substring(findex+1);
                } else {
                    parts[i] = msg.substring(findex+1, index);
                }
            }
        }
        return parts;
    }*/
    
    
    public static String[] split(String msg, char sep) {
        int repeat = count(msg, sep);
        int from = 0, to = 0;
        String[] splitted = new String[repeat+1];
        
        int counter = 0;
        while (counter != repeat) {
            to = msg.indexOf(sep, from);
            splitted[counter] = msg.substring(from, to);
            from = to+1;
            counter++;
        }
        splitted[counter] = msg.substring(from);
        
        
        return splitted;
    }
    
    public static String getStrings(String[] s, String sep) {
    	String a = "";
    	if (s != null) {
	    	for (int i = 0; i != s.length; i++) {
	    		a += s[i];
	    		if (i != s.length-1) {
	    			a += sep;
	    		}
	    	}
    	}
    	return a;
    }
    
}
