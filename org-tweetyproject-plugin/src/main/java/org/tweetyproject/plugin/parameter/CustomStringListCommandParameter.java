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
 * This class extends the string list command parameter with functionality
 * to define valid strings with regular expressions.
 * @see org.tweetyproject.plugin.parameter.StringListCommandParameter
 * 
 * @author Bastian Wolf
 *
 */

public class CustomStringListCommandParameter extends
		StringListCommandParameter {
	
	/**
	 * this parameters given value;
	 */
	private String[] value;

	/**
	 * the (regular expression) validation criteria a given input string needs to fulfill
	 * 	e.g.:
	 *	"(/?([a-zA-Z0-9])(/[a-zA-Z0-9_\\-\\.])*(.[a-z]+))"
	 */
	private String criteria;
	
	/**
	 * constructor for the custom string list parameter with id and description 
	 * @param id the identification string of this parameter
	 * @param des the description of this parameter
	 */
	public CustomStringListCommandParameter(String id, String des) {
		super(id, des);	
	}
	
	/**
	 * constructor for the custom string list parameter with id, description and criteria
	 * @param id the identification string of this parameter
	 * @param des the description of this parameter
	 * @param criteria the validation criteria for this parameter
	 */
	public CustomStringListCommandParameter(String id, String des, String criteria){
		super(id, des);
	}
	
	// Getter
	
	public String[] getValue(){
		return value;
	}

	public String getCriteria(){
		return criteria;
	}
	
	// Setter
	
	public void setValue(String[] value){
		this.value = value;
	}
	
	public void setCriteria(String criteria){
		this.criteria = criteria;
	}
	
	// Methods
	/**
	 * valid iff string matches criteria
	 */
	public boolean isValid(String s){
		return s.matches(criteria);
	}
	
	/**
	 * instantiate single string
	 * @param s the given string to instantiate
	 * @return parameter
	 */
	public CommandParameter instantiate(String s){
		if(isValid(s)){
			String[] in = {s};
			StringListCommandParameter newParam = (StringListCommandParameter) this.clone();
			newParam.setValue(in);
			return newParam;
		}
		throw new IllegalArgumentException("No valid instantiation parameter: " + s);
	}
	
	/**
	 * instantiates a list of arguments if valid 
	 * @param s the given strings array to instantiate
	 * @return instantiated parameter if input is valid, 
	 */
	public CommandParameter instantiate(String[] s){
		
		ArrayList<String> als = new ArrayList<String>();
		for(int i = 0; i < s.length; i++){
			if(isValid(s[i])){
				als.add(s[i]);
			}
			// remove comment?
			// throw new IllegalArgumentException("No valid instantiation parameter: " + s[i]);
		}
		StringListCommandParameter newParam = (StringListCommandParameter) this.clone();
		newParam.setValue((String[]) als.toArray());
		return newParam;
	}
	
	/**
	 * method to clone this object for instantiation
	 */
	@Override
	public Object clone() {
		return new CustomStringListCommandParameter(this.getIdentifier(),
				this.getDescription(), this.getCriteria());

	}
}
