package net.sf.tweety.arg.dung.syntax;

import java.util.*;

import net.sf.tweety.*;
import net.sf.tweety.graphs.DirectedEdge;

/**
 * This class models an attack between two arguments. It comprises of two attributes of <source>Argument</source> and is mainly used by
 * abstract argumentation theories as e.g. <source>DungTheory</source>.
 *
 * @author Matthias Thimm
 *
 */
public class Attack extends DirectedEdge<Argument> implements Formula {

	/**
	 * Default constructor; initializes the two arguments used in this attack relation
	 * @param attacker the attacking argument
	 * @param attacked the attacked argument
	 */
	public Attack(Argument attacker, Argument attacked){
		super(attacker,attacked);
	}

	/**
	 * returns true if one arguments in <source>arguments</source> attacks another within this attack relation.
	 * @param arguments a list of arguments
	 * @return returns true if one arguments in <source>arguments</source> attacks another.
	 */
	public boolean isConflictFree(Collection<? extends Argument> arguments){
		Iterator<? extends Argument> it = arguments.iterator();
		while(it.hasNext()){
			Argument arg = (Argument) it.next();
			if(arg.equals(this.getAttacker())){
				Iterator<? extends Argument> it2 = arguments.iterator();
				while(it2.hasNext()){
					Argument arg2 = (Argument) it2.next();
					if(arg2.equals(this.getAttacked()))
						return false;
				}
			}
		}
		return true;
	}

	/**
	 * returns the attacked argument of this attack relation.
	 * @return the attacked argument of this attack relation.
	 */
	public Argument getAttacked() {
		return this.getNodeB();
	}

	/**
	 * returns the attacking argument of this attack relation.
	 * @return the attacking argument of this attack relation.
	 */
	public Argument getAttacker() {
		return this.getNodeA();
	}

	
	/**
	 * Return true if the given argument is in this attack relation.
	 * @param argument some argument
	 * @return true if the given argument is in this attack relation.
	 */
	public boolean contains(Argument argument){
		return this.getAttacked().equals(argument) || this.getAttacker().equals(argument);
	}

	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Formula#getSignature()
	 */
	public Signature getSignature(){
		DungSignature sig = new DungSignature();
		sig.add(this.getAttacked());
		sig.add(this.getAttacker());
		return sig;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "("+this.getAttacker().toString()+","+this.getAttacked().toString()+")";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(!o.getClass().equals(this.getClass())) return false;
		if(!this.getAttacker().equals(((Attack)o).getAttacker())) return false;
		if(!this.getAttacked().equals(((Attack)o).getAttacked())) return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		return this.getAttacked().hashCode() + 7 * this.getAttacker().hashCode();
	}

}
