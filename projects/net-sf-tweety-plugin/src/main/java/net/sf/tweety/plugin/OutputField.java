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
package net.sf.tweety.plugin;

/**
 * This class models single fields used for the PluginOutput
 * Each field contains its description and its value, e.g.:
 * 
 * Query:
 * a + b || !a + !b
 * 
 * where "Query:" is the description and "a + b || !a + !b" is the value.
 * 
 * @author Bastian Wolf
 */

public class OutputField {
	
	// this fields value description
	private String description;
	//the field value
	private String value;
	
	public OutputField() {
		value = "";
	}
	
	public OutputField(String val){
		value = val;
	}
	
	public OutputField(String description, String val){
		this.description = description;
		value = val;
	}
	
	public String merge(){
		String s = "";
		s += description + ":\n";
		s += value;
		return s;
	}
}
