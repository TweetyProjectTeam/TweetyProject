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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.util;

import java.util.BitSet;

import org.tweetyproject.arg.dung.syntax.*;

/**
 * This class is responsible for dilating a specified original abstract argumentation framework by adding new arguments and new attacks,
 *  while conserving the original framwork.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class EnumeratingDilationGenerator extends EnumeratingDungTheoryGenerator {

	private DungTheory frameworkOriginal;
	
	public EnumeratingDilationGenerator(DungTheory frameworkOriginal) {
		this.frameworkOriginal = frameworkOriginal;
		super.setCurrentSize(frameworkOriginal.size() + 1);
	}
	
	@Override
	protected void initArgsAtcks() {
		super.initArgsAtcks();
		setExistingAttacks();
	}

	@Override
	protected void addArguments() {
		for(var arg : frameworkOriginal) {
			arguments.add(arg);
		}
		super.addArguments();
	}
	
	@Override
	protected boolean addOneToBitSet(BitSet b, int length){
		boolean result = super.addOneToBitSet(b, length);
		keepExistingNonAttacks();
		return result;
	}
	
	/**
	 * Sets the attacks, which already existed in the original framework to dilate
	 */
	private void setExistingAttacks() {
		int i = 0;
		for(var a : this.arguments) {
			for(var b : this.arguments) {
				if(frameworkOriginal.containsAttack(new Attack(a,b))) {
					this.attacks.set(i);
				}
				i++;
			}
			i++;
		}
	}
	
	/**
	 * Ensures that no new attacks are added to the original framework to dilate
	 */
	private void keepExistingNonAttacks() {
		int i = 0;
		for(var a : this.arguments) {
			for(var b : this.arguments) {
				if(frameworkOriginal.contains(a) && frameworkOriginal.contains(b) && !frameworkOriginal.containsAttack(new Attack(a,b))) {
					this.attacks.set(i, false);
				}
				i++;
			}
			i++;
		}
	}
}
