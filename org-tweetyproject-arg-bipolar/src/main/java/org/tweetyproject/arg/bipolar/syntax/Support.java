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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.bipolar.syntax;

import org.tweetyproject.arg.dung.ldo.syntax.LdoFormula;
import org.tweetyproject.arg.dung.ldo.syntax.LdoRelation;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungSignature;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.graphs.DirectedEdge;

/**
 * This class models a support between two arguments. It comprises two attributes of <code>Argument</code> and is used by
 * bipolar abstract argumentation theories.
 *
 * @author Lars Bengel
 *
 */
public class Support extends DirectedEdge<Argument> {

    /**
     * Default constructor; initializes the two arguments used in this support relation
     * @param supporter the supporting argument
     * @param supported the supported argument
     */
    public Support(Argument supporter, Argument supported){
        super(supporter, supported);
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
        return this.getSupported().hashCode() + 11 * this.getSupporter().hashCode();
    }


    /**
     * Determine whether the object is contained  in this support
     * @param o some object
     * @return "true" if o is contained
     */
    public boolean contains(Object o) {
        return this.getSupported().equals(o) || this.getSupporter().equals(o);
    }

    /**
     * Value for distinguishing between different interpretations of the support relation
     */
    public enum Type {
        /** default */
        DEFAULT,
        /** only mediated attacks are considered */
        SIMPLE_DEDUCTIVE,
        /** supported and mediated attacks are considered */
        DEDUCTIVE,
        /** inverse of deductive */
        NECESSITY,
        /** inverse of simple deductive */
        SIMPLE_NECESSITY,
        /** arguments must be supported by evidence*/
        EVIDENTIAL;

        public static Type getType(String type) {
            switch (type) {
                case "none", "default" -> {
                    return DEFAULT;
                } case "ded_simple" -> {
                    return SIMPLE_DEDUCTIVE;
                } case "ded" -> {
                    return DEDUCTIVE;
                } case "nec_simple" -> {
                    return SIMPLE_NECESSITY;
                } case "nec" -> {
                    return NECESSITY;
                } default -> throw new IllegalArgumentException("unknown support type " + type);
            }
        }
    }
}
