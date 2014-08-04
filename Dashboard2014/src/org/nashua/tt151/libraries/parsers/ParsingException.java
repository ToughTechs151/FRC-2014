package org.nashua.tt151.libraries.parsers;

public class ParsingException extends RuntimeException {
	public static String[] TYPES = new String[] { "ERR-STD", "MSG-INVAL", "MSG-EMPTY", "MSG-NULL", "VAL-INVAL", "ARG-RANGE", "ARG-NAN", "ARG-NULL", "ARG-INVAL" };
	private int error;
	private String type;
	private String reason;
	
	public ParsingException() {
		this.error = 0;
		this.reason = "";
		this.type = "";
	}
	
	public ParsingException( int error, String reason ) {
		this.error = error;
		if ( this.error >= ParsingException.TYPES.length ) {
			this.error = 0;
		}
		this.type = ParsingException.TYPES[error];
		this.reason = reason;
	}
	
	public ParsingException( int error, String type, String reason ) {
		this.error = error;
		this.type = type;
		this.reason = reason;
	}
	
	public int getError() {
		return this.error;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getReason() {
		return this.reason;
	}
	
	public void setError( int error ) {
		this.error = error;
	}
	
	public void setType( String type ) {
		this.type = type;
	}
	
	public void setReason( String reason ) {
		this.reason = reason;
	}
	
	public String toString() {
		return "[ParsingException]" + "[" + this.error + "] " + this.type + ": " + this.reason;
	}
	
}