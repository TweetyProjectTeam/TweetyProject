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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.extended.syntax;

import org.tweetyproject.arg.dung.ldo.syntax.LdoFormula;
import org.tweetyproject.arg.dung.ldo.syntax.LdoNegation;
import org.tweetyproject.arg.dung.ldo.syntax.LdoRelation;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungEntity;
import org.tweetyproject.arg.dung.syntax.DungSignature;
import org.tweetyproject.commons.Signature;

/**
 * Represents an Extended Attack, i.e., an attack from some argument to another (extended) attack or argument
 *
 * @author Lars Bengel
 */
public class ExtendedAttack implements DungEntity {

    private final Argument nodeA;
    private final DungEntity nodeB;

    public ExtendedAttack(Argument attacker, DungEntity attacked) {
        this.nodeA = attacker;
        if (attacked instanceof Argument) {
            this.nodeB = attacked;
        } else if (attacked instanceof Attack) {
            this.nodeB = attacked;
        } else if (attacked instanceof ExtendedAttack) {
            this.nodeB = attacked;
        } else throw new IllegalArgumentException("Unsupported Attack Target Type: " + attacked);
    }

    /**
     * Initializes an extended attack from an argument to an extended attack
     * @param attacker some argument
     * @param attacked some extended attack
     */
    public ExtendedAttack(Argument attacker, ExtendedAttack attacked) {
        this.nodeA = attacker;
        this.nodeB = attacked;
    }

    /**
     * Initializes an extended attack from an argument to an attack
     * @param attacker some argument
     * @param attacked some attack
     */
    public ExtendedAttack(Argument attacker, Attack attacked) {
        this.nodeA = attacker;
        this.nodeB = attacked;
    }

    /**
     * Initializes an extended attack from an argument to an argument
     * @param attacker some argument
     * @param attacked some argument
     */
    public ExtendedAttack(Argument attacker, Argument attacked) {
        this.nodeA = attacker;
        this.nodeB = attacked;
    }

    /**
     * get first node of the edge
     * @return the first node
     */
    public Argument getNodeA() {
        return nodeA;
    }

    /**
     * get target of edge
     * @return edge target
     */
    public DungEntity getNodeB() {
        return nodeB;
    }

    /**
     * returns the attacked element of this attack relation.
     * @return the attacked element of this attack relation.
     */
    public DungEntity getAttacked() {
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
     * @return "true" if the given argument is in this attack relation.
     */
    public boolean contains(Argument argument){
        return this.getAttacked().equals(argument) || this.getAttacker().equals(argument);
    }

    /**
     * Determines whether the given attack is contained in this attack
     * @param attack some attack
     * @return "true" if the given attack is contained in this attack
     */
    public boolean contains(Attack attack){
        return this.getAttacked().equals(attack);
    }

    /**
     * Determines whether the given attack is contained in this attack
     * @param attack some extended attack
     * @return "true" if the given attack is contained in this attack
     */
    public boolean contains(ExtendedAttack attack){
        return this.getAttacked().equals(attack);
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.kr.Formula#getSignature()
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
        if(!this.getAttacker().equals(((ExtendedAttack)o).getAttacker())) return false;
        if(!this.getAttacked().equals(((ExtendedAttack)o).getAttacked())) return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode(){
        return this.getAttacked().hashCode() + 7 * this.getAttacker().hashCode();
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.arg.dung.syntax.DungEntity#getLdoFormula()
     */
    @Override
    public LdoFormula getLdoFormula() {
        return new LdoRelation(this.getAttacker().getLdoFormula(), new LdoNegation(this.getAttacked().getLdoFormula()));
    }
}
