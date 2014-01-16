package net.sf.tweety;

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
