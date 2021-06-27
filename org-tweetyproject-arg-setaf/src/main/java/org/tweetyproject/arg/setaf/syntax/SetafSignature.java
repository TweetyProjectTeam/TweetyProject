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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.setaf.syntax;

import java.util.*;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.*;


/**
 * This class captures the signature of a Setaf theory,
 * i.e. a set of arguments.
 * 
 * @author  Sebastian Franke
 */
public class SetafSignature extends SingleSetSignature<Argument>{

	/**
 	  * Creates a new (empty) setaf signature.
 	  */
	public SetafSignature(){
		super();
	}

	/**
	 * Creates a new signature with the single given argument.
	 * @param argument an argument.
	 */
	public SetafSignature(Argument argument){
		this();
		this.add(argument);
	}
	
	/**
	 * Creates a new signature with the given set of arguments.
	 * @param arguments a set of arguments.
	 */
	public SetafSignature(Collection<? extends Argument> arguments){
		this();
		this.addAll(arguments);
	}

	@Override
	public void add(Object obj) {
		if (obj instanceof Argument)
			formulas.add((Argument) obj);
		else
			throw new IllegalArgumentException("Illegal type " + obj.getClass());
	
	}	
	
	@Override
	public SetafSignature clone() {
		return new SetafSignature(this.formulas);
	}
	
}
