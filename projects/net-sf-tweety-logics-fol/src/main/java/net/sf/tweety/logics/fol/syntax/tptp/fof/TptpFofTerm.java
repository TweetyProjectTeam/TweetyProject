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
 * Term class. (Useful?)
 * @author Bastian Wolf
 *
 * @param <T>
 */
public abstract class TptpFofTerm<T> {
	
	/**
	 * The generic value of this term
	 */
	protected T value;
	
	/**
	 * The sort of this term
	 */
	protected TptpFofSort sort;
	
	
	
	public TptpFofTerm(){
		
	}
	
	/**
	 * Generic constructor with value and fixed standard sort
	 * @param value the value of this term
	 */
	public TptpFofTerm(T value){
		this(value, TptpFofSort.THING);
	}
	
	/**
	 * Constructor with given value and sort
	 * @param value the value of this term
	 * @param sort the sort of this term
	 */
	public TptpFofTerm(T value, TptpFofSort sort){
		this.sort = sort;
		set(value);
	}
	
	/*
	 * Setter
	 */
	public void set(T value){
		this.value = value;
	}
	
	/*
	 * Getter
	 */
	public abstract T get();
	
	public abstract TptpFofSort getSort();
		
	
}
