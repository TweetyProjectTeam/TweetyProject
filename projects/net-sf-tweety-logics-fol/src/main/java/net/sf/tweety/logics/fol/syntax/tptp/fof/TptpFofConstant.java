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
 * A constant consists of an unique identifier name and a sort.
 * @author Bastian Wolf
 */
public class TptpFofConstant extends TptpFofStringTerm {

    /**
     * Default Constructor with standard sort
     * @param value the identifier name
     */
    public TptpFofConstant(String value){
        this(value, TptpFofSort.THING);
    }

    /**
     * Constructor
     * @param value the identifier name
     * @param sort the sort of this constant
     */
    public TptpFofConstant(String value, TptpFofSort sort) {
        super(value, sort);
    }


	@Override
	public void set(String value) {
        if(value == null || value.length() == 0){
            throw new IllegalArgumentException();
        }
        this.value = value.toLowerCase();
	}

	@Override
	public String get() {
		
		return this.value;
	}

    
}
