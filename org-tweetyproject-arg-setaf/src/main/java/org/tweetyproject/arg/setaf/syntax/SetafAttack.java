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

import org.tweetyproject.arg.dung.ldo.syntax.LdoFormula;
import org.tweetyproject.arg.dung.ldo.syntax.LdoNegation;
import org.tweetyproject.arg.dung.ldo.syntax.LdoRelation;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungEntity;
import org.tweetyproject.arg.dung.syntax.DungSignature;
import org.tweetyproject.commons.*;
import org.tweetyproject.graphs.HyperDirEdge;

/**
 * This class models an attack between two arguments. It comprises of two attributes of <code>Argument</code> and is mainly used by
 * abstract argumentation theories as e.g. <code>SetafTheory</code>.
 *
 * @author  Sebastian Franke
 *
 */
public class SetafAttack extends HyperDirEdge<Argument>   implements DungEntity {

	/**
	 * Default constructor; initializes the two arguments used in this attack relation
	 * @param attacker the attacking argument
	 * @param attacked the attacked argument
	 */
	public SetafAttack(Set<Argument> attackers, Argument attacked){
			super(attackers, attacked);

	}
	
	/**
	 * Default constructor; initializes the two arguments used in this attack relation
	 * @param attacker the attacking argument
	 * @param attacked the attacked argument
	 */
	public SetafAttack(Argument attacker, Argument attacked){
		super(attacker, attacked);
		
	}


	
	/**
	 * returns true if one arguments in <code>arguments</code> attacks another within this attack relation.
	 * @param arguments a list of arguments
	 * @return returns true if one arguments in <code>arguments</code> attacks another.
	 */
	public boolean isConflictFree(Collection<? extends Argument> arguments){
		Iterator<? extends Argument> it = arguments.iterator();
		while(it.hasNext()){
			Argument arg = (Argument) it.next();
			if(arg.equals(this.getAttacked())){
				Iterator<? extends Argument> it2 = arguments.iterator();

				Set<Argument> arg2 = this.getNodeA();
				if(!arguments.contains(arg2))
					return false;
						
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
	public Set<Argument> getAttackers() {
		return this.getNodeA();
	}

	
	/**
	 * Return true if the given argument is in this attack relation.
	 * @param argument some argument
	 * @return true if the given argument is in this attack relation.
	 */
	public boolean contains(Argument argument){
		return this.getAttacked().equals(argument) || this.getAttackers().contains(argument);
	}

	
	/* (non-Javadoc)
	 * @see org.tweetyproject.kr.Formula#getSignature()
	 */
	public Signature getSignature(){
		DungSignature sig = new DungSignature();
		sig.add(this.getAttacked());
		sig.add(this.getAttackers());
		return sig;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "("+ (this.getAttackers() == null? "":this.getAttackers().toString())+
						","+(this.getAttacked() == null? "":this.getAttacked().toString())+")";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(!o.getClass().equals(this.getClass())) return false;
		if(!this.getAttackers().equals(((SetafAttack)o).getAttackers())) return false;
		if(!this.getAttacked().equals(((SetafAttack)o).getAttacked())) return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		return this.getAttacked() == null? 0 : this.getAttacked().hashCode()
					+ (this.getAttackers() == null? 0 : this.getAttackers().hashCode());
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.setaf.syntax.DungEntity#getLdoFormula()
	 */
	@Override
	public LdoFormula getLdoFormula() {
		return new LdoRelation(((DungEntity) this.getAttackers()).getLdoFormula(), new LdoNegation(this.getAttacked().getLdoFormula()));
	}



}
