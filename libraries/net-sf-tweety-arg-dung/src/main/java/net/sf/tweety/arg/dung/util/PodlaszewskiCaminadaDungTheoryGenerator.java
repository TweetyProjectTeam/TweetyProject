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

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;

/**
 * This generator generates abstract argumentation frameworks following the schema
 * described in "Strong Admissibility Revisited" (COMMA2014) by Martin Caminda, following
 * and idea by Mikolay Podlaszewski. These frameworks are supposedly hard for computing grounded
 * semantics.
 * 
 * @author Matthias Thimm
 *
 */
public class PodlaszewskiCaminadaDungTheoryGenerator implements DungTheoryGenerator{
	
	/**
	 * The number of components in the generated frameworks. 
	 */
	private int numComponents;
	
	/**
	 * Creates a new generator that generates frameworks of the 
	 * given numComponents >= 0. More precisely, the number of arguments in these
	 * frameworks have 4 + numComponents*4 arguments.
	 * @param numComponents the number of components
	 */
	public PodlaszewskiCaminadaDungTheoryGenerator(int numComponents){
		this.numComponents = numComponents;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.util.DungTheoryGenerator#generate()
	 */
	@Override
	public DungTheory next() {
		Argument a = new Argument("a");
		return this.next(a);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.util.DungTheoryGenerator#generate(net.sf.tweety.arg.dung.syntax.Argument)
	 */
	@Override
	public DungTheory next(Argument arg) {
		DungTheory theory = new DungTheory();
		// the initial argument
		Argument d = new Argument("d");
		theory.add(arg);
		theory.add(d);
		// add first component
		Argument b = new Argument("b1");
		Argument c = new Argument("c1");
		theory.add(b);
		theory.add(c);
		theory.add(new Attack(b,c));
		theory.add(new Attack(c,b));
		theory.add(new Attack(b,arg));
		theory.add(new Attack(c,arg));
		// add further components
		Argument bn;
		Argument cn;
		int idx = 2;
		for(int i = 0; i < this.numComponents; i++){
			bn = new Argument("b" + idx);
			cn = new Argument("c" + idx);
			idx++;
			theory.add(bn);
			theory.add(cn);
			theory.add(new Attack(bn,b));
			theory.add(new Attack(cn,c));			
			b = new Argument("b" + idx);
			c = new Argument("c" + idx);
			theory.add(b);
			theory.add(c);
			theory.add(new Attack(b,bn));
			theory.add(new Attack(b,cn));
			theory.add(new Attack(b,c));
			theory.add(new Attack(c,b));
			theory.add(new Attack(c,bn));
			theory.add(new Attack(c,cn));
			idx++;
		}		
		// add beginning of the framework
		theory.add(new Attack(d,b));
		theory.add(new Attack(d,c));
		return theory;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.util.DungTheoryGenerator#setSeed(long)
	 */
	@Override
	public void setSeed(long seed) {
		// not needed as the computation is deterministic		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.util.DungTheoryGenerator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return true;
	}
}
