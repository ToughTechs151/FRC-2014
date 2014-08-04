package org.nashua.tt151.libraries.parsers;

import org.nashua.tt151.libraries.parsers.ProtocolParsing.Command;
import org.nashua.tt151.libraries.parsers.ProtocolParsing.Key;

public interface Message {
	public String convert();
	
	public void setCommand( Command command );
	
	public void setKey( Key key );
	
	public void setValue( String value );
	
	public void setArgs( String[] args );
	
	public Command getCommand();
	
	public Key getKey();
	
	public String getValue();
	
	public String[] getArgs();
}
