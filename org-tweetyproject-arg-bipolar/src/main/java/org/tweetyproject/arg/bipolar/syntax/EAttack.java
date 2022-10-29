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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.bipolar.syntax;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.tweetyproject.arg.dung.ldo.syntax.LdoFormula;
import org.tweetyproject.commons.Signature;

/**
 * Evidential attack used for PEAF and EAF
 *
 * @author Taha Dogan Gunes
 */
public class EAttack implements Attack{



    /**
     * The arguments that originate the attack
     */
    protected final ArgumentSet froms;

    /**
     * The arguments that receive the attack
     */
    protected final ArgumentSet tos;


    /**
     * The default constructor that creates EAttack object
     *
     * @param name  the name of the attack
     * @param froms the arguments that originate this attack
     * @param tos   the arguments that receive this attack
     */
    public EAttack(Set<BArgument> froms, Set<BArgument> tos) {

        this.froms = new ArgumentSet(froms);
        this.tos = new ArgumentSet(tos);
    }



    /**
     * Get the arguments that originate this attack
     *
     * @return a set of arguments
     */
    @Override
    public BipolarEntity getAttacker() {
        return froms;
    }

    /**
     * Get the arguments that receive this attack
     *
     * @return a set of arguments
     */
    @Override
    public ArgumentSet getAttacked() {
        return tos;
    }

    /**
     * Returns the attack object in string format for debug purposes
     *
     * @return verbose format of the attack in string
     */
    @Override
    public String toString() {
        return "EAtt{froms=" + froms +
                ", tos=" + tos +
                '}';
    }

    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EAttack attack = (EAttack) o;
        return Objects.equals(froms, attack.froms) && Objects.equals(tos, attack.tos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(froms, tos);
    }

	@Override
	public boolean contains(Object o) {
		if(this.getAttacked().equals( o) || this.getAttacker().equals(o))
			return true;
		return false;
	}

	@Override
	public LdoFormula getLdoFormula() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Iterator<BArgument> iterator() {
		return null;
	}


}

