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
package net.sf.tweety.arg.dung.util;

import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;

/**
 * This generator generators all possible Dung argumentation theories.
 * It starts with the argumentation frameworks just consisting of one
 * arguments and then increases their size.
 * 
 * @author Matthias Thimm
 */
public class EnumeratingDungTheoryGenerator implements DungTheoryGenerator {

	/** The size of the currently generated theories. */
	private int currentSize;
	/** used to enumerate all possible attack combinations. */
	private BitSet attacks = null;	
	/**The set of possible attacks. */
	private List<Attack> possibleAttacks;
	/** The current set of arguments. */
	private Set<Argument> arguments;
	
	/**
	 * Creates a new enumerating Dung theory generator.
	 */
	public EnumeratingDungTheoryGenerator(){		
	}
	
	/**
	 * Adds "1" to the bitset.	 
	 * @param b a bitset
	 * @param length the length of the bitset
	 * @return "true" if the addition results in an overflow.
	 */
	private boolean addOneToBitSet(BitSet b, int length){
		boolean carry = true;
		int idx = 0;
		while(carry && idx < length){			
			if(!b.get(idx)){
				b.set(idx);
				carry = false;
			}
			else 
				b.set(idx, false);
			idx++;
		}
		return carry;
	}
	
	/**
	 * Computes all possible attacks.
	 * @param arguments some set of arguments
	 * @return the set of all possible attacks.
	 */
	private List<Attack> generatePossibleAttacks(Set<Argument> arguments){
		List<Attack> result = new LinkedList<Attack>();
		for(Argument a: arguments)
			for(Argument b: arguments)
				result.add(new Attack(a,b));
		return result;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.util.DungTheoryGenerator#generate()
	 */
	@Override
	public DungTheory next() {
		if(this.attacks == null){
			this.currentSize = 1;
			this.arguments = new HashSet<Argument>();
			arguments.add(new Argument("A1"));
			this.possibleAttacks = this.generatePossibleAttacks(arguments);
			this.attacks = new BitSet(this.possibleAttacks.size());
		}else{
			if(this.addOneToBitSet(this.attacks, this.possibleAttacks.size())){
				this.currentSize++;
				this.arguments = new HashSet<Argument>();
				for(int i = 0; i < this.currentSize;i++)
					arguments.add(new Argument("A"+i));				
				this.possibleAttacks = this.generatePossibleAttacks(this.arguments);
				this.attacks = new BitSet(this.possibleAttacks.size());				
			}
		}
		DungTheory theory = new DungTheory();
		theory.addAll(this.arguments);
		for(int i = 0; i < this.possibleAttacks.size(); i++)
			if(this.attacks.get(i))
				theory.add(this.possibleAttacks.get(i));
		return theory;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.util.DungTheoryGenerator#generate(net.sf.tweety.argumentation.dung.syntax.Argument)
	 */
	@Override
	public DungTheory next(Argument arg) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.util.DungTheoryGenerator#setSeed(long)
	 */
	@Override
	public void setSeed(long seed) {
		throw new UnsupportedOperationException();		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.util.DungTheoryGenerator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return true;
	}

}
