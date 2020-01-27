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
import net.sf.tweety.arg.dung.syntax.DungSignature;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.graphs.DirectedEdge;

import java.util.Collection;

/**
 * This class models an attack between a set of arguments and an argument. It comprises of a set of <code>BArgument</code> and is used by
 * bipolar abstract argumentation theories.
 *
 * @author Lars Bengel
 *
 */
public class SetAttack extends DirectedEdge<BipolarEntity> implements Attack{

    /**
     * Default constructor; initializes the arguments used in this attack relation
     * @param supporter the attacking set of arguments
     * @param supported the attacked argument
     */
    public SetAttack(ArgumentSet supporter, BArgument supported){
        super(supporter, supported);
    }

    /**
     * initializes the arguments used in this attack relation
     * @param supporter a collection of attacking arguments arguments
     * @param supported the attacked argument
     */
    public SetAttack(Collection<BArgument> supporter, BArgument supported){
        this(new ArgumentSet(supporter), supported);
    }

    /**
     *  initializes the arguments used in this attack relation
     * @param supporter the attacking argument
     * @param supported the attacked argument
     */
    public SetAttack(BArgument supporter, BArgument supported){
        super(new ArgumentSet(supporter), supported);
    }

    /**
     * returns the attacked argument of this attack relation.
     * @return the attacked argument of this attack relation.
     */
    public BArgument getAttacked() {
        return (BArgument) this.getNodeB();
    }

    /**
     * returns the attacking set of arguments of this attack relation.
     * @return the attacking set of arguments of this attack relation.
     */
    public ArgumentSet getAttacker() {
        return (ArgumentSet) this.getNodeA();
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
        if(!this.getAttacker().equals(((SetAttack)o).getAttacker())) return false;
        if(!this.getAttacked().equals(((SetAttack)o).getAttacked())) return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode(){
        return this.getAttacked().hashCode() + 7 * this.getAttacker().hashCode();
    }

    @Override
    public boolean contains(Object o) {
        return this.getAttacked().equals(o) || this.getAttacker().contains(o);
    }

    @Override
    public LdoFormula getLdoFormula() {
        return new LdoRelation(this.getAttacker().getLdoFormula(), new LdoNegation(this.getAttacked().getLdoFormula()));
    }

    @Override
    public Signature getSignature() {
        DungSignature sig = new DungSignature();
        sig.add(this.getAttacked());
        sig.add(this.getAttacker());
        return sig;
    }
}
