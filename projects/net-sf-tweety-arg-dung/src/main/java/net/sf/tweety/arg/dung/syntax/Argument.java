/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.arg.dung.syntax;

import net.sf.tweety.commons.*;
import net.sf.tweety.graphs.Node;

/**
 * This class models an argument used by Dung's abstract argumentation theory and is just described by its name.
 * @author Matthias Thimm
 *
 */
public class Argument implements Formula, Node{
	/**
	 * The name of the argument.
	 */
	private String name;

	/**
	 * Default constructor that assigns the given <source>name</source> to this argument
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
	 * @see net.sf.tweety.kr.Formula#getSignature()
	 */
	public Signature getSignature(){
		return new DungSignature(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(!o.getClass().equals(this.getClass())) return false;
		if(!((Argument)o).getName().equals(name)) return false;
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
}
