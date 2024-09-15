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
package org.tweetyproject.plugin.parameter;

import java.util.ArrayList;

/**
 * This class expands the CommandParameter with a selection-array containing
 * strings the parameter supports
 *
 * @author Bastian Wolf
 *
 */

public class SelectionCommandParameter extends CommandParameter {

	/**
	 * all possible values for this parameter's argument
	 */
	private String[] selections;

	/**
	 * the value each instantiated needs, has to be in selections
	 */
	private String value;

	/**
	 * Creates a new parameter.
	 * @param id some string
	 * @param des some string
	 */
	public SelectionCommandParameter(String id, String des) {
		super(id, des);
	}

	/**
	 * Creates a new parameter.
	 * @param id some string
	 * @param des some string
	 * @param selections some array of string
	 */
	public SelectionCommandParameter(String id, String des, String[] selections) {
		super(id, des);
		setSelections(selections);
	}

	/**
	 * returns each possible selection argument
	 *
	 * @return each possible selection argument
	 */
	public String[] getSelections() {
		return selections;
	}

	/**
	 * sets new selection parameter
	 *
	 * @param selections an array of strings
	 */
	public void setSelections(String[] selections) {
		this.selections = selections;
	}

	/**
	 * returns the given instantiation argument value for this parameter
	 *
	 * @return the given instantiation argument value for this parameter
	 */
	public String getValue() {
		return value;
	}

	/**
	 * sets the instantiated parameter argument value, value has to be one of
	 * the options contained in selections
	 *
	 * @param value
	 *            the value given as argument value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * checks whether a cli input parameter argument is valid for the called
	 * command parameter
	 */
	@Override
	public boolean isValid(String s) {
		for (int i = 0; i < selections.length; i++) {
			if (selections[i].equalsIgnoreCase(s))
				return true;
		}
		return false;
	}

	/**
	 * instantiates a new parameter iff the given value ist valid for this
	 * command parameter
	 */
	@Override
	public CommandParameter instantiate(String s){
		if (this.isValid(s)) {
			SelectionCommandParameter newParameter = (SelectionCommandParameter) this
					.clone();
			newParameter.setValue(s);
			return newParameter;
		}
		return null;
	}

	/**
	 * Instantiates a list of `CommandParameter` objects based on the provided list of strings.
	 *
	 * <p>
	 * This method processes a list of string values, validates each one, and creates a corresponding
	 * `SelectionCommandParameter` for each valid string. The `SelectionCommandParameter` is created
	 * by cloning the current object and setting its value to the corresponding string from the input list.
	 * The created parameters are collected into an `ArrayList` and returned.
	 * </p>
	 *
	 * <p>
	 * The method modifies the input list of strings by removing each processed and valid string.
	 * </p>
	 *
	 * @param s an `ArrayList` of `String` objects representing the input values to be converted into command parameters.
	 * @return an `ArrayList` of `CommandParameter` objects, each instantiated from a valid string in the input list.
	*/
	public ArrayList<CommandParameter> instantiate(ArrayList<String> s) {
		ArrayList<CommandParameter> alcp = new ArrayList<CommandParameter>();
		for (int i = 0; i < s.size(); i++) {
			if (this.isValid(s.get(i))) {
				SelectionCommandParameter newParameter = (SelectionCommandParameter) this
						.clone();
				newParameter.setValue(s.remove(0).toString());
				alcp.add(newParameter);
			}
		}
		return alcp;
	}

	@Override
	public Object clone() {
		return new SelectionCommandParameter(this.getIdentifier(),
				this.getDescription(), this.getSelections());

	}

}
