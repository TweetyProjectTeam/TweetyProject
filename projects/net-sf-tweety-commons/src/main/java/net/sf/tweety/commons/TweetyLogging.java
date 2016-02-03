/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.commons;

import java.util.*;

import org.apache.log4j.*;

/**
 * This class handles the global logging configuration.
 * 
 * @author Matthias Thimm
 */
public abstract class TweetyLogging {

	/** The log level (The possible values are described
	 *  by <code>TweetyConfiguration.LogLevel</code>, default
	 *  is <code>TweetyConfiguration.LogLevel.INFO</code>) */
	public static TweetyConfiguration.LogLevel logLevel = TweetyConfiguration.LogLevel.INFO;
	
	/** The file used for logging (if this parameter is not set,
	 *  logging is performed on the standard output) */
	public static String logFile = null;
	
	/**
	 * Initialize the logging system.
	 */
	public static void initLogging(){
		// TODO customize the following
		Properties properties = new Properties();
		properties.put("log4j.rootLogger", logLevel.toString() + ",mainlogger");
		properties.put("log4j.appender.mainlogger.layout","org.apache.log4j.PatternLayout");
		properties.put("log4j.appender.mainlogger.layout.ConversionPattern","%5p [%t] %C{1}.%M%n      %m%n");

		if(logFile != null){
			properties.put("log4j.appender.mainlogger","org.apache.log4j.RollingFileAppender");
			properties.put("log4j.appender.mainlogger.File",logFile);
		}else
			properties.put("log4j.appender.mainlogger","org.apache.log4j.ConsoleAppender");
		PropertyConfigurator.configure(properties);
	}
}
