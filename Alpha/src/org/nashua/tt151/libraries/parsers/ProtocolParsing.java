package org.nashua.tt151.libraries.parsers;

import org.nashua.tt151.util.BitwiseTools;
import org.nashua.tt151.util.MathTools;
import org.nashua.tt151.util.StringTools;

/**
 * This class contains classes and enums for parsing messages transmitted from
 * the laptop/robot. Use the static class Parsing for parsing messages.
 * A message is a string containing the command, key, value and args.
 * For more information on the structure of the message view the wiki.
 * 
 * @author Brennan Ringey
 * @version 1.5
 */
public class ProtocolParsing {
	
	private ProtocolParsing() {}
	
	/**
	 * A class representing a pseudo-enum for the avaiable commands
	 */
	public static class Command {
		
		public char shorthand;
		public byte bitsum;
		
		private Command( char c, byte b ) {
			this.shorthand = c;
			this.bitsum = b;
		}
		
		public static final ProtocolParsing.Command Query = new ProtocolParsing.Command( 'Q', (byte) 1 );
		public static final ProtocolParsing.Command Reply = new ProtocolParsing.Command( 'R', (byte) 2 );
		public static final ProtocolParsing.Command Send = new ProtocolParsing.Command( 'S', (byte) 3 );
		public static final ProtocolParsing.Command Unknown = new ProtocolParsing.Command( 'U', (byte) 0 );
		public static final ProtocolParsing.Command[] ALL = { Query, Reply, Send, Unknown };
		
	}
	
	/**
	 * A class representing a pseudo-enum for the available keys
	 */
	public static class Key {
		
		public String shorthand;
		public byte bitsum;
		
		private Key( String c, byte l ) {
			this.shorthand = c;
			this.bitsum = l;
		}
		
		public static final ProtocolParsing.Key CamTurnAngle = new ProtocolParsing.Key( "CA", (byte) 28 );
		public static final ProtocolParsing.Key Status = new ProtocolParsing.Key( "ST", (byte) 4 );
		public static final ProtocolParsing.Key AnalogValue = new ProtocolParsing.Key( "AV", (byte) 8 );
		public static final ProtocolParsing.Key TurnSpeed = new ProtocolParsing.Key( "TS", (byte) 16 );
		public static final ProtocolParsing.Key PWMValue = new ProtocolParsing.Key( "PV", (byte) 12 );
		public static final ProtocolParsing.Key RelayValue = new ProtocolParsing.Key( "RV", (byte) 24 );
		public static final ProtocolParsing.Key TargetHot = new ProtocolParsing.Key( "TH", (byte) 20 );
		public static final ProtocolParsing.Key DigitalIO = new ProtocolParsing.Key( "DI", (byte) 0 );
		public static final ProtocolParsing.Key[] ALL = { CamTurnAngle, Status, AnalogValue, TurnSpeed, PWMValue, RelayValue, TargetHot, DigitalIO };
		
	}
	
	public static class Value {
		public char shorthand;
		public byte bitsum;
		public int length;
		
		private Value( char c, byte b, int l ) {
			shorthand = c;
			bitsum = b;
			length = l;
		}
		
		public static final ProtocolParsing.Value BYTE = new ProtocolParsing.Value( 'b', (byte) 0, 1 );
		public static final ProtocolParsing.Value SHORT = new ProtocolParsing.Value( 's', (byte) 32, 2 );
		public static final ProtocolParsing.Value DOUBLE_BYTE = new ProtocolParsing.Value( 'd', (byte) 64, 2 );
		public static final ProtocolParsing.Value DOUBLE_SHORT = new ProtocolParsing.Value( 'e', (byte) 96, 4 );
		public static final ProtocolParsing.Value[] ALL = { BYTE, SHORT, DOUBLE_BYTE, DOUBLE_SHORT };
	}
	
	/**
	 * This class is responsible for parsing messages sent from the laptop to the
	 * robot and vice versa. For more information on the protocol visit the wiki.
	 * 
	 * @author Brennan Ringey
	 * @version 3.5
	 */
	public static final class StringParser {
		
		// Prevent instantiation
		private StringParser() {}
		
		public static StringMessage parse( String msg ) {
			StringMessage sm = new StringMessage();
			
			sm.setCommand( getCommand( msg ) );
			sm.setKey( getKey( msg ) );
			sm.setValue( getValue( msg ) );
			sm.setArgs( getArgs( msg ) );
			
			return sm;
		}
		
		/**
		 * Tests if the message is a Query (aka Request). A Query message
		 * begins with the character Q and is not case-sensitive.
		 * 
		 * @param msg The message to test
		 * @return True if the message is a query, false otherwise
		 */
		public static boolean isQuery( String msg ) {
			return ( msg.charAt( 0 ) == 'Q' || msg.charAt( 0 ) == 'q' );
		}
		
		public static boolean isQuery( StringMessage msg ) {
			return ( msg.getCommand().shorthand == 'Q' );
		}
		
		/**
		 * Test if the message is a Reply. A Reply message begins with the
		 * character R and is not case-sensitive.
		 * 
		 * @param msg The message to test
		 * @return True if the message is a Reply, false otherwise
		 */
		public static boolean isReply( String msg ) {
			return ( msg.charAt( 0 ) == 'R' || msg.charAt( 0 ) == 'r' );
		}
		
		public static boolean isReply( StringMessage msg ) {
			return ( msg.getCommand().shorthand == 'R' );
		}
		
		/**
		 * Test if the message is a Send. A Send message begins with the
		 * character S and is not case-sensitive.
		 * 
		 * @param msg The message to test
		 * @return True if the message is a Send, false otherwise
		 */
		public static boolean isSend( String msg ) {
			return ( msg.charAt( 0 ) == 'S' || msg.charAt( 0 ) == 's' );
		}
		
		public static boolean isSend( StringMessage msg ) {
			return ( msg.getCommand().shorthand == 'S' );
		}
		
		/**
		 * An alternative to the above methods, this method gets the command from
		 * the message header rather than testing it.
		 * 
		 * @param msg The message to get the command from
		 * @return An enumerator from Commands, unknown is returned if no other commands match
		 */
		public static ProtocolParsing.Command getCommand( String msg ) {
			char c = Character.toUpperCase( msg.charAt( 0 ) );
			ProtocolParsing.Command command = ProtocolParsing.Command.Unknown;
			
			for ( int i = 0; i != ProtocolParsing.Command.ALL.length; i++ ) {
				if ( c == ProtocolParsing.Command.ALL[i].shorthand ) {
					command = ProtocolParsing.Command.ALL[i];
				}
			}
			
			return command;
		}
		
		/**
		 * Identifies the key in the message. All of the possible keys are located on
		 * the wiki and are represented by the enum Keys. If no possible key was
		 * identified, null is returned
		 * 
		 * @param msg The message containing the key to retrieve
		 * @return An enumerator from Keys representing the key in the message
		 */
		public static ProtocolParsing.Key getKey( String msg ) {
			String k = separateByColon( msg )[1];
			ProtocolParsing.Key key = null;
			
			for ( int i = 0; i != ProtocolParsing.Key.ALL.length; i++ ) {
				if ( k.equals( ProtocolParsing.Key.ALL[i].shorthand ) ) {
					key = ProtocolParsing.Key.ALL[i];
				}
			}
			
			return key;
		}
		
		/**
		 * Gets the value from the message. The value varies based on the key. If a
		 * value was not identified, null is returned
		 * 
		 * @param msg The message to get the value from
		 * @return A string containing the value.
		 */
		public static String getValue( String msg ) {
			String[] a = separateByColon( msg );
			try {
				return a[2];
			} catch ( IndexOutOfBoundsException ex ) {
				return null;
			}
		}
		
		/**
		 * Gets the additional args (if any) from the message. Arguments are
		 * separated by commas, after the final colon
		 * 
		 * @param msg The message containing args (if any)
		 * @return An array of strings, representing the arguments
		 */
		public static String[] getArgs( String msg ) {
			String args = "";
			try {
				args = StringTools.split( msg, ':' )[3];
			} catch ( IndexOutOfBoundsException ex ) {
				System.out.println( ex.toString() );
			}
			String[] rargs = null;
			
			if ( !args.equals( "" ) ) {
				rargs = StringTools.split( args, ',' );
			}
			
			return rargs;
		}
		
		/**
		 * Create a string to be transmitted over TCP. Query does not require a value
		 * or arguments (Leave value null). Arguments are NOT required.
		 * 
		 * @param command A enumerator from Commands representing the command (Must not be Commands.Unknown)
		 * @param key An enumerator from Keys representing the key (Must not be null!)
		 * @param value A string representing the value (Leave empty string if Query)
		 * @param args A string of args to be added
		 * @return A String containing the message to be sent
		 */
		public static String createMessage( ProtocolParsing.Command command, ProtocolParsing.Key key, String value, String[] args ) {
			String packet = null;
			
			if ( command == null || key == null ) {
				return null;
			}
			
			if ( command != ProtocolParsing.Command.Unknown && key != null ) {
				packet = command.shorthand + ":" + key.shorthand + ":" + value;
				if ( args != null ) {
					if ( args.length > 0 ) {
						packet += ":";
						for ( int i = 0; i != args.length; i++ ) {
							packet += args[i];
							if ( i != args.length - 1 ) {
								packet += ",";
							}
						}
					}
				}
			}
			
			return packet;
		}
		
		// public static String toFastFormat(Message msg) {
		
		// }
		
		// Helper method used by parsing methods
		private static String[] separateByColon( String msg ) {
			return StringTools.split( msg, ':' );
		}
		
	}
	
	/**
	 * This class parses FastFormat messages transmitted to and from robot/laptop.
	 * The FastFormat uses 1 byte for the header and the rest being a value/args.
	 * For more information on FastFormat go to http://goo.gl/S8fwa
	 * 
	 * @author Brennan Ringey
	 * @version 1.1
	 */
	public static class FastParser {
		
		public static final String NULL_MESSAGE = "NULL";
		
		private FastParser() {}
		
		public static boolean hasArgs( String msg ) {
			
			int size = 1;
			ProtocolParsing.Value val = getValueType( msg );
			
			if ( val == ProtocolParsing.Value.BYTE ) {
				size++;
			}
			if ( val == ProtocolParsing.Value.SHORT ) {
				size += 2;
			}
			if ( val == ProtocolParsing.Value.DOUBLE_BYTE ) {
				size += 2;
			}
			if ( val == ProtocolParsing.Value.DOUBLE_SHORT ) {
				size += 4;
			}
			
			return ( size < msg.length() );
			
			// int header = (int)msg.charAt(0);
			// header = BitwiseTools.negatebits(header, 0, 6);
			// return (header == 128);
		}
		
		public static boolean isSigned( String msg ) {
			int header = (int) msg.charAt( 0 );
			header = BitwiseTools.negatebits( header, 0, 6 );
			return ( header == 128 );
		}
		
		public static ProtocolParsing.Command getCommand( String msg ) {
			ProtocolParsing.Command command = ProtocolParsing.Command.Unknown;
			int header = (int) msg.charAt( 0 );
			// Set all bits after the first two to zero
			int cheader = BitwiseTools.negatebits( header, 2, 7 );
			
			// Iterate through all commands
			for ( int i = 0; i != ProtocolParsing.Command.ALL.length; i++ ) {
				ProtocolParsing.Command c = ProtocolParsing.Command.ALL[i];
				
				if ( cheader == c.bitsum ) {
					command = c;
					break;
				}
			}
			
			return command;
		}
		
		public static ProtocolParsing.Key getKey( String msg ) {
			ProtocolParsing.Key key = null;
			int header = (int) msg.charAt( 0 );
			int kheader = BitwiseTools.negatebits( header, 0, 1 );
			kheader = BitwiseTools.negatebits( kheader, 5, 7 );
			
			for ( int i = 0; i != ProtocolParsing.Key.ALL.length; i++ ) {
				ProtocolParsing.Key k = ProtocolParsing.Key.ALL[i];
				if ( kheader == k.bitsum ) {
					key = k;
					break;
				}
			}
			return key;
		}
		
		public static ProtocolParsing.Value getValueType( String msg ) {
			ProtocolParsing.Value val = null;
			int header = (int) msg.charAt( 0 );
			int vheader = BitwiseTools.negatebits( header, 0, 4 );
			vheader = BitwiseTools.negatebit( vheader, 7 );
			
			for ( int i = 0; i != ProtocolParsing.Value.ALL.length; i++ ) {
				ProtocolParsing.Value v = ProtocolParsing.Value.ALL[i];
				if ( vheader == v.bitsum ) {
					val = v;
					break;
				}
			}
			
			return val;
		}
		
		public static String getValue( String msg ) throws ParsingException {
			String value = "";
			ProtocolParsing.Value val = getValueType( msg );
			
			if ( val == ProtocolParsing.Value.BYTE ) {
				int num = -1;
				try {
					num = msg.charAt( 1 );
				} catch ( IndexOutOfBoundsException e ) {
					throw new ParsingException( 4, "Byte parsing failed" );
				}
				if ( num < 0 || num > 255 ) {
					throw new ParsingException( 4, "Byte parsing failed: value out of range" );
				}
				if ( isSigned( msg ) ) {
					num *= -1;
				}
				value = String.valueOf( num );
			}
			if ( val == ProtocolParsing.Value.SHORT ) {
				char[] s = msg.substring( 1 ).toCharArray();
				System.out.println( (int) s[0] + " | " + (int) s[1] );
				int num = BitwiseTools.makeShort( (int) s[0], (int) s[1] );
				if ( isSigned( msg ) ) {
					num *= -1;
				}
				value = String.valueOf( num );
				
			}
			if ( val == ProtocolParsing.Value.DOUBLE_BYTE ) {
				int wholepart = (int) msg.charAt( 1 );
				int fracpart = (int) msg.charAt( 2 );
				String neg = "";
				if ( isSigned( msg ) ) {
					neg = "-";
				}
				value = neg + wholepart + "." + fracpart;
			}
			if ( val == ProtocolParsing.Value.DOUBLE_SHORT ) {
				char[] wholeshort = msg.substring( 1, 3 ).toCharArray();
				char[] fracshort = msg.substring( 3, 5 ).toCharArray();
				int wholepart = BitwiseTools.makeShort( (int) wholeshort[0], (int) wholeshort[1] );
				int fracpart = BitwiseTools.makeShort( (int) fracshort[0], (int) fracshort[1] );
				String neg = "";
				if ( isSigned( msg ) ) {
					neg = "-";
				}
				value = neg + wholepart + "." + fracpart;
			}
			
			return value;
		}
		
		public static String[] getArgs( String msg ) {
			Value v = getValueType( msg );
			
			String s = msg.substring( v.length );
			return StringTools.split( s, ',' );
			
		}
		
		public static String createMessage( ProtocolParsing.Command command, ProtocolParsing.Key key, ProtocolParsing.Value val, String value, String[] args ) throws ParsingException {
			
			int header = 0;
			String vsect = "", asect = "";
			
			if ( command == null ) {
				throw new ParsingException( 7, "Command Arguement was set to null" );
			}
			if ( key == null ) {
				throw new ParsingException( 7, "Key Arguement was set to null" );
			}
			if ( val == null ) {
				throw new ParsingException( 7, "Value Arguement was set to null" );
			}
			if ( value == null ) {
				throw new ParsingException( 7, "String value Arguement was set to null" );
			}
			
			header += command.bitsum;
			header += key.bitsum;
			header += val.bitsum;
			
			if ( val == ProtocolParsing.Value.BYTE ) {
				int num = 0;
				try {
					num = Integer.parseInt( value );
				} catch ( NumberFormatException ne ) {
					throw new ParsingException( 6, "'" + value + "' is not a valid integer" );
				} finally {
					if ( num >= -255 && num <= 255 ) {
						if ( num < 0 ) {
							header += 128;
						}
						num = Math.abs( num );
						vsect += (char) num;
						
					} else {
						throw new ParsingException( 5, "the value was out of range (-255 to 255)" );
					}
				}
			}
			if ( val == ProtocolParsing.Value.SHORT ) {
				int num = 0;
				try {
					num = Integer.parseInt( value );
				} catch ( NumberFormatException ne ) {
					throw new ParsingException( 6, "'" + value + "' is not a valid integer" );
				} finally {
					if ( num >= -65535 && num <= 65535 ) {
						if ( num < 0 ) {
							header += 128;
						}
						num = Math.abs( num );
						vsect = BitwiseTools.toCharShort( num );
					} else {
						throw new ParsingException( 5, "the value was out of range (-65535 to 65535)" );
					}
				}
				
			}
			if ( val == ProtocolParsing.Value.DOUBLE_BYTE || val == ProtocolParsing.Value.DOUBLE_SHORT ) {
				double d = 0.0;
				try {
					d = Double.parseDouble( value );
				} catch ( NumberFormatException ex ) {
					throw new ParsingException( 6, "'" + value + "' is not a valid double" );
				} finally {
					if ( d < 0.0 ) {
						header += 128;
						d = Math.abs( d );
					}
					
					ParsingException error = new ParsingException( 0, "No reason" );
					boolean success = false;
					
					if ( MathTools.numOfPlaces( d ) <= 3 && MathTools.fpart( d ) <= 255 && MathTools.ipart( d ) <= 255 && val == ProtocolParsing.Value.DOUBLE_BYTE ) {
						vsect = BitwiseTools.toByteDouble( d );
						success = true;
					} else {
						if ( val == ProtocolParsing.Value.DOUBLE_BYTE ) {
							error = new ParsingException( 5, "the value was out of range (-255.255 to 255.255)" );
						}
					}
					
					if ( MathTools.numOfPlaces( d ) <= 5 && MathTools.fpart( d ) <= 65535 && MathTools.ipart( d ) <= 65535 && val == ProtocolParsing.Value.DOUBLE_SHORT ) {
						vsect = BitwiseTools.toShortDouble( d );
						success = true;
					} else {
						if ( val == ProtocolParsing.Value.DOUBLE_SHORT ) {
							error = new ParsingException( 5, "the value was out of range (-65535.65535 to 65535.65535)" );
						}
					}
					if ( !success ) {
						throw error;
					}
				}
			}
			
			if ( args != null ) {
				for ( int i = 0; i != args.length; i++ ) {
					asect += args[i];
					if ( i != args.length - 1 ) {
						asect += ",";
					}
				}
			}
			
			return (char) header + vsect + asect;
		}
	}
	
}
