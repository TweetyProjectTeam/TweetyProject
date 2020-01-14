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
package net.sf.tweety.arg.bipolar.syntax;

import net.sf.tweety.arg.dung.ldo.syntax.LdoFormula;
import net.sf.tweety.arg.dung.ldo.syntax.LdoNegation;
import net.sf.tweety.arg.dung.ldo.syntax.LdoRelation;
import net.sf.tweety.arg.dung.syntax.*;
import net.sf.tweety.commons.*;
import net.sf.tweety.graphs.DirectedEdge;

/**
 * This class models a support between two arguments. It comprises of two attributes of <code>Argument</code> and is used by
 * bipolar abstract argumentation theories.
 *
 * @author Lars Bengel
 *
 */
public class Support extends DirectedEdge<Argument> implements DungEntity{

    /**
     * Default constructor; initializes the two arguments used in this support relation
     * @param supporter the supporting argument
     * @param supported the supported argument
     */
    public Support(Argument supporter, Argument supported){
        super(supporter,supported);
    }

    /**
     * returns the supported argument of this support relation.
     * @return the supported argument of this support relation.
     */
    public Argument getSupported() {
        return this.getNodeB();
    }

    /**
     * returns the supporting argument of this support relation.
     * @return the supporting argument of this support relation.
     */
    public Argument getSupporter() {
        return this.getNodeA();
    }

    /**
     * Return true if the given argument is in this support relation.
     * @param argument some argument
     * @return true if the given argument is in this support relation.
     */
    public boolean contains(Argument argument){
        return this.getSupported().equals(argument) || this.getSupporter().equals(argument);
    }


    /* (non-Javadoc)
     * @see net.sf.tweety.kr.Formula#getSignature()
     */
    public Signature getSignature(){
        DungSignature sig = new DungSignature();
        sig.add(this.getSupported());
        sig.add(this.getSupporter());
        return sig;
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
        if(!this.getSupporter().equals(((Support)o).getSupporter())) return false;
        if(!this.getSupported().equals(((Support)o).getSupported())) return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode(){
        return this.getSupported().hashCode() + 7 * this.getSupporter().hashCode();
    }

    /* (non-Javadoc)
     * @see net.sf.tweety.arg.dung.syntax.DungEntity#getLdoFormula()
     */
    @Override
    public LdoFormula getLdoFormula() {
        return new LdoRelation(this.getSupporter().getLdoFormula(), new LdoNegation(this.getSupported().getLdoFormula()));
    }

}
