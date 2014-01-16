package net.sf.tweety.arg.dung.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.graphs.util.GraphUtil;

/**
 * This generator generators all possible Dung argumentation theories
 * (modulo graph isomorphism). It starts with the argumentation frameworks
 * just consisting of one arguments and then increases their size.
 * 
 * @author Matthias Thimm
 */
public class IsoSafeEnumeratingDungTheoryGenerator implements DungTheoryGenerator {

	/** The number of arguments in the theory that is to be generated next.*/
	private int cntArguments;
	/** The number of attacks in the theory that is to be generated next.*/
	private int cntAttacks;
	/** The set of all Dung theories with cntArguments arguments and cntAttacks attacks.*/
	private Collection<DungTheory> currentTheories;
	/** The iterator on currentTheories. */
	private Iterator<DungTheory> iterator;
	
	/**
	 * Creates a new enumerating Dung theory generator.
	 */
	public IsoSafeEnumeratingDungTheoryGenerator(){
		this.cntArguments = 0;
		this.cntAttacks = 0;
		this.currentTheories = new HashSet<DungTheory>();
		this.iterator = this.currentTheories.iterator();
	}
	
	/** Checks whether the first theory is isomorphic to some theory
	 * in "theories".
	 * @param theory a Dung theory
	 * @param theories a collection of Dung theories.
	 * @return "true" if the first theory is isomorphic to some theory
	 * in "theories".
	 */
	private boolean isIsomorphic(DungTheory theory, Collection<DungTheory> theories){
		for(DungTheory other: theories)
			if(GraphUtil.isIsomorphic(theory, other))
				return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.util.DungTheoryGenerator#generate()
	 */
	@Override
	public DungTheory generate() {
		if(this.iterator.hasNext())
			return this.iterator.next();
		if(this.cntAttacks < this.cntArguments * this.cntArguments){
			this.cntAttacks++;
			Collection<DungTheory> newTheories = new HashSet<DungTheory>();
			for(DungTheory theory: this.currentTheories){
				for(Argument a: theory){
					for(Argument b: theory){
						if(!theory.isAttackedBy(b, a)){
							DungTheory newTheory = new DungTheory();
							newTheory.addAll(theory);
							newTheory.addAllAttacks(theory.getAttacks());
							newTheory.add(new Attack(a,b));
							if(!this.isIsomorphic(newTheory, newTheories))
								newTheories.add(newTheory);
						}
					}
				}
			}			
			this.currentTheories = newTheories;
			this.iterator = this.currentTheories.iterator();
			return this.iterator.next();
		}
		this.cntArguments++;
		this.cntAttacks = 0;
		DungTheory theory = new DungTheory();
		for(int i = 0; i < this.cntArguments; i++)
			theory.add(new Argument("A"+i));
		this.currentTheories.clear();
		this.currentTheories.add(theory);
		this.iterator = this.currentTheories.iterator();
		return this.iterator.next();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.util.DungTheoryGenerator#generate(net.sf.tweety.argumentation.dung.syntax.Argument)
	 */
	@Override
	public DungTheory generate(Argument arg) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.util.DungTheoryGenerator#setSeed(long)
	 */
	@Override
	public void setSeed(long seed) {
		throw new UnsupportedOperationException();		
	}

}
