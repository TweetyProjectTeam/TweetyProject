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

import java.util.*;

import net.sf.tweety.commons.*;


/**
 * This class captures the signature of a Dung theory,
 * i.e. a set of arguments.
 * 
 * @author Matthias Thimm
 */
public class DungSignature extends SetSignature<Argument>{

	/**
 	  * Creates a new (empty) Dung signature.
 	  */
	public DungSignature(){
		super();
	}

	/**
	 * Creates a new signature with the single given argument.
	 * @param argument an argument.
	 */
	public DungSignature(Argument argument){
		super(argument);
	}
	
	/**
	 * Creates a new signature with the given set of arguments.
	 * @param arguments a set of arguments.
	 */
	public DungSignature(Collection<? extends Argument> arguments){
		super(arguments);		
	}	
	
}
