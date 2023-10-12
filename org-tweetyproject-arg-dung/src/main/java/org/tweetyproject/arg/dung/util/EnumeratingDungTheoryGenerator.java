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
package org.tweetyproject.arg.dung.util;

import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This generator generators all possible Dung argumentation theories.
 * It starts with the argumentation frameworks just consisting of one
 * arguments and then increases their size.
 *
 * @author Matthias Thimm
 */
public class EnumeratingDungTheoryGenerator implements DungTheoryGenerator {

	/** The size of the currently generated theories. */
	private int currentSize = 1;
	/** used to enumerate all possible attack combinations. */
	protected BitSet attacks = null;
	/**The set of possible attacks. */
	private List<Attack> possibleAttacks;
	/** The current set of arguments. */
	protected Set<Argument> arguments;
	/** The label of all arguments created, which is followed by a number */
	private String labelArguments = "a";

	/**
	 * Creates a new enumerating Dung theory generator.
	 */
	public EnumeratingDungTheoryGenerator(){
	}

	/**
	 * @return A BitSet representing the attack pattern in the last generated framework.
	 */
	public BitSet getAttacks() {
		return (BitSet)this.attacks.clone();
	}

	/**
	 * @return Number of arguments of the last argumentation framework created by this generator, if the number was not newly set.
	 */
	public int getCurrentSize() {
		return this.currentSize;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.util.DungTheoryGenerator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.argumentation.util.DungTheoryGenerator#generate()
	 */
	@Override
	public DungTheory next() {
		if(this.attacks == null){
			this.initArgsAtcks();
		}else{
			if(this.addOneToBitSet(this.attacks, this.possibleAttacks.size())){
				this.currentSize++;
				this.initArgsAtcks();
			}
		}
		DungTheory theory = new DungTheory();
		theory.addAll(this.arguments);
		for(int i = 0; i < this.possibleAttacks.size(); i++) {
			if(this.attacks.get(i)) {
				theory.add(this.possibleAttacks.get(i));
			}
		}
		return theory;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.argumentation.util.DungTheoryGenerator#generate(org.tweetyproject.argumentation.dung.syntax.Argument)
	 */
	@Override
	public DungTheory next(Argument arg) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Sets a new pattern of attacks from which the generator continues to generate new frameworks.
	 * @param newAtks BitSet representing a pattern of attacks in a argumentation framework.
	 * @return true iff the specified attack pattern was successfully set
	 */
	public boolean setAttacks(BitSet newAtks) {
		if(this.attacks == null) {
			this.initArgsAtcks();
		}

		if(newAtks.size() != this.attacks.size()) {
			throw new IllegalArgumentException("The specified bitset needs to be of same length than the current bitset.");
		}

		this.attacks.clear();
		this.attacks.or(newAtks);
		return this.attacks.equals(newAtks);
	}

	/**
	 * Sets a new number of arguments for this generator and resets the generation process,
	 * so that the generator restarts going through all possible attacks
	 * @param newSize Number of arguments of the generator. Has to be > 0.
	 * @return true iff the number of arguments was successfully set
	 */
	public boolean setCurrentSize(int newSize) {
		if(newSize < 1) {
			throw new IllegalArgumentException("Size has to be > 0");
		}
		this.currentSize = newSize;
		this.attacks = null; // resets generator
		return this.currentSize == newSize;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.argumentation.util.DungTheoryGenerator#setSeed(long)
	 */
	@Override
	public void setSeed(long seed) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds all necessary arguments to the list
	 */
	protected void addArguments() {
		for(int i = 0; i < this.currentSize;i++) {
			this.arguments.add(new Argument(this.labelArguments+i));
		}
	}

	/**
	 * Adds "1" to the bitset.
	 * @param b a bitset
	 * @param length the length of the bitset
	 * @return "true" if the addition results in an overflow.
	 */
	protected boolean addOneToBitSet(BitSet b, int length){
		boolean carry = true;
		int idx = 0;
		while(carry && (idx < length)){
			if(!b.get(idx)){
				b.set(idx);
				carry = false;
			} else {
				b.set(idx, false);
			}
			idx++;
		}
		return carry;
	}

	/**
	 * Initializes the arguments and attacks of the first framework of the current size to generate
	 */
	protected void initArgsAtcks() {
		this.arguments = new HashSet<>();
		this.addArguments();
		this.possibleAttacks = this.generatePossibleAttacks(this.arguments);
		this.attacks = new BitSet(this.possibleAttacks.size());
	}

	/**
	 * Computes all possible attacks.
	 * @param arguments some set of arguments
	 * @return the set of all possible attacks.
	 */
	private List<Attack> generatePossibleAttacks(Set<Argument> arguments){
		List<Attack> result = new LinkedList<>();
		for(Argument a: arguments) {
			for(Argument b: arguments) {
				result.add(new Attack(a,b));
			}
		}
		return result;
	}

}
