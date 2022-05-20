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
package org.tweetyproject.arg.dung.syntax;

import org.tweetyproject.arg.dung.ldo.syntax.LdoArgument;
import org.tweetyproject.arg.dung.ldo.syntax.LdoFormula;
import org.tweetyproject.commons.*;
import org.tweetyproject.graphs.Node;

/**
 * This class models an argument used by Dung's abstract argumentation theory and is just described by its name.
 * @author Matthias Thimm
 *
 */
public class Argument implements DungEntity, Node, Comparable<Argument>{
	/**
	 * The name of the argument.
	 */
	private String name;

	/**
	 * Default constructor that assigns the given <code>name</code> to this argument
	 * @param name the name of the argument
	 */
	public Argument(String name){
		this.name = name;
	}

	/**
	 * returns the name of the argument
	 * @return the name of the argument
	 */
	public String getName(){
		return name;
	}

	/**
	 * sets the name of the argument
	 * @param name the name of the argument
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.kr.Formula#getSignature()
	 */
	public Signature getSignature(){
		return new DungSignature(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(!o.getClass().equals(this.getClass())) return false;
		if(!((Argument)o).getName().equals(getName())) return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		return this.name.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return name;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.syntax.DungEntity#getLdoFormula()
	 */
	@Override
	public LdoFormula getLdoFormula() {
		return getLdoArgument();
	}
	
	/**
	 * 
	 * @return getLdoArgument
	 */
	public LdoArgument getLdoArgument(){
		return new LdoArgument(this.name);
	}

	@Override
	public int compareTo(Argument o) {
		return this.name.compareTo(o.name);
	}
}
