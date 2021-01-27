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
package org.tweetyproject.plugin;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.tweetyproject.plugin.parameter.CommandParameter;

/**
 * This abstract class implements the TweetyPlugin interface and provides a base
 * for plugin implementations in each project
 * 
 * @author Bastian Wolf
 * 
 */
// @PluginImplementation
public abstract class AbstractTweetyPlugin implements TweetyPlugin {
	/**
	 * valid parameters for this plugin
	 */
	protected List<CommandParameter> parameters = new ArrayList<CommandParameter>();

	/**
	 * returns the command this plugin is called with
	 */
	@Override
	public abstract String getCommand();

	/**
	 * executes the given input
	 */
	@Override
	public abstract PluginOutput execute(File[] input, CommandParameter[] params);

	/**
	 * adds new command parameter to this plugin
	 * 
	 * @param cmdParameter
	 *            the command parameter to be added
	 */
	public void addParameter(CommandParameter cmdParameter) {
		parameters.add(cmdParameter);
	}

	/**
	 * returns all possible parameters
	 */
	@Override
	public List<CommandParameter> getParameters() {
		return parameters;
	}

	/**
	 * checks, whether each command parameter given with the plugin call is
	 * valid within is this plugin
	 * 
	 * @param s the given parameter string
	 * @return a newly instantiated command parameter with the given value
	 * @throws CloneNotSupportedException if cloning is not supported
	 */
	public CommandParameter validateParameter(String s)
			throws CloneNotSupportedException {
		for (CommandParameter cmp : parameters) {
			if (cmp.isValid(s)) {
				CommandParameter cpa = cmp.instantiate(s);
				return cpa;
			}
		}
		return null;
	}
	
	/**
	 * This method handels instantiations with array-lists instead of a simple string (e.g. for multi-argument parameter)
	 * @param s the ArrayList&lt;String&gt; containing the input parameter
	 * @return the newly instantiated command parameter with the given value 
	 * @throws CloneNotSupportedException if cloning is not supported
	 */
	public ArrayList<CommandParameter> validateParameter(ArrayList<String> s)
			throws CloneNotSupportedException {
		ArrayList<CommandParameter> alcp = new ArrayList<CommandParameter>(
				s.size());
		for (String value : s) {
			CommandParameter cp = validateParameter(value);
			if(!(cp==null)){
			alcp.add(cp);
			}
		}
		return alcp;
	}

}
