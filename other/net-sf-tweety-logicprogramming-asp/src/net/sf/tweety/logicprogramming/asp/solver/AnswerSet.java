/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
package net.sf.tweety.logicprogramming.asp.solver;

import java.util.*;

import net.sf.tweety.logicprogramming.asp.syntax.*;


/**
 * a class representing an answer set. 
 * 
 * @author Thomas Vengels
 *
 */
public class AnswerSet extends HashSet<ELPLiteral> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AnswerSet() {
		super();
		weight = 0;
		level = 0;
	}	
	
	public AnswerSet(Set<ELPLiteral> lits, int weight, int level) {
		this.addAll(lits);
		this.weight = weight;
		this.level = level;
	}
	
	public final int				weight;
	public final int				level;
}
