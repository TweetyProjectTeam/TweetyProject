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
package net.sf.tweety.plugin.parameter;

/**
 * This is the abstract basics class for CommandParameter
 * Never leave identifier and description arguments for the constructor
 * empty, they can not be changed afterwards
 * 
 * @author Bastian Wolf
 *
 */

public abstract class CommandParameter implements Cloneable {
	
	/**
	 * The identifier string for the command parameter
	 */
	private String identifier;
	
	/**
	 * The description string for this parameter,  e.g.
	 * "-identifier <argument>, does magic with argument={simple, complex}"
	 */
	private String description;
	
	/**
	 * non-empty constructor (member variables cannot be changed after construction) 
	 * @param id the identifier string for this command parameter (e.g. aggr for aggregation)
	 * @param des the description string for this command parameter
	 */
	public CommandParameter(String id, String des) {
		this.identifier = id;
		this.description = des;
	}
	
	/**
	 * returns this parameters name (the identifier)
	 * @return this parameters name (the identifier)
	 */
	public String getIdentifier(){
		return identifier;
	}
	
	
	/**
	 * returns the description for this parameter
	 * @return the description for this parameter
	 */
	public String getDescription(){
		return description;
	}
	
	
	/**
	 * Checks, if the given String is a valid argument for this command parameter 
	 * @param s the given input string
	 * @return true if valid, false if not
	 */
	public abstract boolean isValid(String s);
	

	/**
	 * instantiate the command with the given argument string if valid
	 * @param s the given argument string
	 * @return The CommandParameter instance or null if the given string is not valid.
	 */
	public abstract CommandParameter instantiate(String s);
	
}