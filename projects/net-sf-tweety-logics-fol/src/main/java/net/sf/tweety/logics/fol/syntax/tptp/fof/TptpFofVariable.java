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
package net.sf.tweety.logics.fol.syntax.tptp.fof;

/**
 * Variables are used in quantified formulas
 * Each Variable must begin with an upper case letter followed by any amount (>=0) of lower case letters 
 * @author Bastian Wolf
 */
public class TptpFofVariable extends TptpFofStringTerm {

    /**
     * The variable value must begin with an upper case character followed by optional lower case characters
     */
    private String value;

    /**
     * construct new variable with given (valid) value
     * @param value the value given
     */
    public TptpFofVariable(String value) {
    	super(value);
    	if(value instanceof String)
    	if(value.matches("[A-Z][a-z]*")){
        	this.value = value;
    	}
        else if (value.length()>1){
        	this.value = (String) value.substring(0, 0).toUpperCase() + value.substring(1, value.length()-1);
        }
        	else {
        	this.value = (String) value.toUpperCase();	
        	}
    }

   /*
    * Getter
    */
	public String getvalue() {
		return value;
	}


	/**
	 * Simply just returns the value.
	 */
	@Override
	public String toString() {
		return value;
	}

	@Override
	public void set(String value) {
		if(value instanceof String && value.length() > 0)
			if(value.matches("[A-Z][a-z]*")) {
				this.value = value;
			}
			else if (value.length()>1){
				this.value = (String) value.substring(0, 0).toUpperCase() + value.substring(1, value.length()-1);
			}
			else {
				this.value = (String) value.toUpperCase();
			}

	}

	@Override
	public String get() {
		
		return this.value;
	}

}
