package org.nashua.tt151.libraries.parsers;

import org.nashua.tt151.libraries.parsers.ProtocolParsing.*;
import org.nashua.tt151.util.MathTools;
import org.nashua.tt151.util.BitwiseTools;
import org.nashua.tt151.util.StringTools;


/**
 * A class representing a message adhering to the StringFormat specification.
 * A StringMessage object contains a Command, Key, value and arg fields, and
 * can be converted to a String.
 * @author Brennan
 * @version 1.1
 */
public class StringMessage implements Message{
    
    private Command command;
    private Key key;
    private String value;
    private String[] args;
    
    public StringMessage() {
        command = Command.Unknown;
        key = null;
        value = null;
        args = null;
    }
    
    public StringMessage(Command command, Key key) {
    	this.command = command;
    	this.key = key;
    }
    
    public StringMessage(Command command, Key key, String value) {
    	this.command = command;
    	this.key = key;
    	this.value = value;
    }
    
    public StringMessage(Command command, Key key, String value, String[] args) {
    	this.command = command;
    	this.key = key;
    	this.value = value;
    	this.args = args;
    }
    
    public Command getCommand() {
        return this.command;
    }
    public Key getKey() {
        return this.key;
    }
    public String getValue() {
        return this.value;
    }
    public String[] getArgs() {
        return this.args;
    }
    
    public void setCommand(Command command) {
        this.command = command;
    }
    public void setKey(Key key) {
        this.key = key;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public void setArgs(String[] args) {
        this.args = args;
    }
    
    public String convert() {
        String achar = "";
        if (args != null) { achar=":"; }
        return command.shorthand + ":" + key.shorthand + ":" + value + achar + StringTools.getStrings(args, ",");
    }
    
    
    
    
    
}
