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

import java.util.Iterator;

import org.tweetyproject.arg.dung.ldo.syntax.LdoFormula;
import org.tweetyproject.arg.dung.ldo.syntax.LdoRelation;
import org.tweetyproject.arg.dung.syntax.DungSignature;
import org.tweetyproject.commons.*;
import org.tweetyproject.graphs.DirectedEdge;

/**
 * This class models a support between two arguments. It comprises of two attributes of <code>Argument</code> and is used by
 * bipolar abstract argumentation theories.
 *
 * @author Lars Bengel
 *
 */
public class BinarySupport extends DirectedEdge<BArgument> implements Support{

    /**
     * Default constructor; initializes the two arguments used in this support relation
     * @param supporter the supporting argument
     * @param supported the supported argument
     */
    public BinarySupport(BArgument supporter, BArgument supported){
        super(supporter,supported);
    }

    /**
     * returns the supported argument of this support relation.
     * @return the supported argument of this support relation.
     */
    public BArgument getSupported() {
        return this.getNodeB();
    }

    /**
     * returns the supporting argument of this support relation.
     * @return the supporting argument of this support relation.
     */
    public BArgument getSupporter() {
        return this.getNodeA();
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
        if(!this.getSupporter().equals(((BinarySupport)o).getSupporter())) return false;
        if(!this.getSupported().equals(((BinarySupport)o).getSupported())) return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode(){
        return this.getSupported().hashCode() + 11 * this.getSupporter().hashCode();
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.arg.dung.syntax.DungEntity#getLdoFormula()
     */
    public LdoFormula getLdoFormula() {
        return new LdoRelation(this.getSupporter().getLdoFormula(), this.getSupported().getLdoFormula());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        return this.getSupported().equals(o) || this.getSupporter().equals(o);
    }

	@Override
	public Iterator<BArgument> iterator() {
		return null;
	}
}
