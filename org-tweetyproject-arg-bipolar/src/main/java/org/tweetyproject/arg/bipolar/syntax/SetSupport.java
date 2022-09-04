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

package org.tweetyproject.arg.bipolar.syntax;

import org.tweetyproject.arg.dung.ldo.syntax.LdoFormula;
import org.tweetyproject.arg.dung.ldo.syntax.LdoRelation;
import org.tweetyproject.arg.dung.syntax.DungSignature;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.graphs.DirectedEdge;

import java.util.Collection;

/**
 * This class models a support between a set of arguments and an argument. It comprises of a set of <code>BArgument</code> and is used by
 * bipolar abstract argumentation theories.
 *
 * @author Lars Bengel
 *
 */
public class SetSupport extends DirectedEdge<BipolarEntity> implements Support {

    /**
     * Default constructor; initializes the arguments used in this support relation
     * @param supporter the supporting set of arguments
     * @param supported the supported argument
     */
    public SetSupport(ArgumentSet supporter, ArgumentSet supported){
        super(supporter, supported);
    }

    /**
     * initializes the arguments used in this support relation
     * @param supporter a collection of arguments
     * @param supported some argument
     */
    public SetSupport(Collection<BArgument> supporter, Collection<BArgument> supported) {
        this(new ArgumentSet(supporter), new ArgumentSet(supported));
    }

    /**
     * initializes the arguments used in this support relation
     * @param supporter the supporting argument
     * @param supported the supported argument
     */
    public SetSupport(BArgument supporter, BArgument supported){
        super(new ArgumentSet(supporter), supported);
    }

    /**
     * returns the supported argument of this support relation.
     * @return the supported argument of this support relation.
     */
    public BArgument getSupported() {
        return (BArgument) this.getNodeB();
    }

    /**
     * returns the supporting set of arguments of this support relation.
     * @return the supporting set of arguments of this support relation.
     */
    public ArgumentSet getSupporter() {
        return (ArgumentSet) this.getNodeA();
    }

    /**
     * Return true if the given argument is in this support relation.
     * @param argument some argument
     * @return true if the given argument is in this support relation.
     */
    public boolean contains(BArgument argument){
        return this.getSupported().equals(argument) || this.getSupporter().contains(argument);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return "("+this.getSupporter().toString()+","+this.getSupported().toString()+")";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o){
        if(!o.getClass().equals(this.getClass())) return false;
        if(!this.getSupporter().equals(((SetSupport)o).getSupporter())) return false;
        if(!this.getSupported().equals(((SetSupport)o).getSupported())) return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode(){
        return this.getSupported().hashCode() + 11 * this.getSupporter().hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        return this.getSupported().equals(o) || this.getSupporter().contains(o);
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.arg.dung.syntax.DungEntity#getLdoFormula()
     */
    public LdoFormula getLdoFormula() {
        return new LdoRelation(this.getSupporter().getLdoFormula(), this.getSupported().getLdoFormula());
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.kr.Formula#getSignature()
     */
    public Signature getSignature(){
        DungSignature sig = new DungSignature();
        sig.add(this.getSupported());
        sig.add(this.getSupporter());
        return sig;
    }
}
