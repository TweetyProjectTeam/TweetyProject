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
package net.sf.tweety.agents;

/**
 * An executable is an action an agent performs within an environment.
 * 
 * @author Matthias Thimm
 */
public interface Executable {

	/**
	 * This constant represents the default null operation.
	 */
	public static final Executable NO_OPERATION = new Executable(){
		public String toString(){ return "NO_OPERATION";}
		@Override
		public boolean isNoOperation() {
			return true;
		}		
	};
	
	/**
	 * Indicates whether this operation can be regarded
	 * as no operation at all.
	 * @return "true" if this operation is a noop.
	 */
	public boolean isNoOperation();
}
