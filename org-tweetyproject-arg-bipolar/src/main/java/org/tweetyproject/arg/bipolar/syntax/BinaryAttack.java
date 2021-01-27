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
import org.tweetyproject.arg.dung.ldo.syntax.LdoNegation;
import org.tweetyproject.arg.dung.ldo.syntax.LdoRelation;
import org.tweetyproject.arg.dung.syntax.DungSignature;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.graphs.DirectedEdge;

/**
 * This class models a binary attack relation between two arguments. It comprises of two attributes of <code>Argument</code> and is used by
 * abstract argumentation theories.
 *
 * @author Lars Bengel
 *
 */
public class BinaryAttack extends DirectedEdge<BArgument> implements Attack {
    /**
     * Default constructor; initializes the two arguments used in this attack relation
     * @param attacker the supporting argument
     * @param attacked the supported argument
     */
    public BinaryAttack(BArgument attacker, BArgument attacked) {
        super(attacker, attacked);
    }

    /**
     * returns the attacked argument of this attack relation.
     * @return the attacked argument of this attack relation.
     */
    public BArgument getAttacked() {
        return this.getNodeB();
    }

    /**
     * returns the attacking argument of this attack relation.
     * @return the attacking argument of this attack relation.
     */
    public BArgument getAttacker() {
        return this.getNodeA();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String var10000 = this.getAttacker().toString();
        return "(" + var10000 + "," + this.getAttacked().toString() + ")";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (!o.getClass().equals(this.getClass())) {
            return false;
        } else if (!this.getAttacker().equals(((BinaryAttack)o).getAttacker())) {
            return false;
        } else {
            return this.getAttacked().equals(((BinaryAttack)o).getAttacked());
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this.getAttacked().hashCode() + 7 * this.getAttacker().hashCode();
    }

    @Override
    public boolean contains(Object o) {
        return this.getAttacked().equals(o) || this.getAttacker().equals(o);
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.arg.dung.syntax.DungEntity#getLdoFormula()
     */
    public LdoFormula getLdoFormula() {
        return new LdoRelation(this.getAttacker().getLdoFormula(), new LdoNegation(this.getAttacked().getLdoFormula()));
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.kr.Formula#getSignature()
     */
    public Signature getSignature() {
        DungSignature sig = new DungSignature();
        sig.add(this.getAttacked());
        sig.add(this.getAttacker());
        return sig;
    }
}
