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
package net.sf.tweety.logics.fol.syntax.tptp.fof;

/**
 * This class implements the generic Fof-Term-class with Strings
 * @author Bastian Wolf
 */
public abstract class TptpFofStringTerm extends TptpFofTerm<String> {

	/**
	 * The name of this term
	 */
	protected String name;
	
	/**
	 * Generic empty constructor
	 */
	public TptpFofStringTerm(){
		super();
	}
	
	/**
	 * Constructor with given name
	 * @param name
	 */
	public TptpFofStringTerm(String name) {
		super(name);
	}
	
	/**
	 * 
	 * @param name
	 * @param sort
	 */
	public TptpFofStringTerm(String name, TptpFofSort sort){
		super(name, sort);
	}
	
	/**
	 * 
	 */
	@Override
	public abstract void set(String name);

	/**
	 * 
	 */
	public TptpFofSort getSort(){
		return this.sort;
	}
	
	public String getName(){
		return name;
	}
	
	public String toString(){
		return this.name;
	}
	
}
