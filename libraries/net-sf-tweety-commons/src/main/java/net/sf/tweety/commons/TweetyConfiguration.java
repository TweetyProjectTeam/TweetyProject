/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.commons;

/**
 * This interface contains some configuration options for
 * Tweety.
 * 
 * @author Matthias Thimm
 */
public interface TweetyConfiguration {

	/**
	 * The possible log levels.
	 * 
	 * @author Matthias Thimm
	 */
	public enum LogLevel{
	    TRACE (5, "trace"),
	    DEBUG (4, "debug"),
	    INFO (3, "info"),
	    WARN (2, "warn"),
	    ERROR (1, "error"),
	    FATAL (0, "fatal");
	    
	    /** The log level as integer */
	    private final int levelAsInt;  
	    /** The log level as string */
	    private final String levelAsString;
	    
	    /** Creates a new LogLevel */
	    LogLevel(int levelAsInt, String levelAsString) {
	        this.levelAsInt = levelAsInt;
	        this.levelAsString = levelAsString;
	    }
	    
	    /** Returns the log level as integer */
	    public int levelAsInt(){ return this.levelAsInt; }
	    
	    /** Returns the log level as string */
	    public String levelAsString(){ return this.levelAsString; }
	    
	    /**
	     * Returns the log level described by the given string.
	     * @param s a string.
	     */
	    public static LogLevel getLogLevel(String s){
	    	for(LogLevel l: LogLevel.values()){
	    		if(l.levelAsString.equals(s.toLowerCase()))
	    			return l;
	    		try{
	    			if(l.levelAsInt == new Integer(s))
	    				return l;
	    		}catch(Exception e){}
	    	}
	    	throw new IllegalArgumentException("The given string does not represent a log level.");	
	    }
	}
}
