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

import org.tweetyproject.arg.bipolar.io.EdgeListWriter;

import java.util.Collection;
import java.util.Set;

/**
 * This class implements an abstract argumentation theory
 * in the sense of Probabilistic Evidential Argumentation Frameworks (PrEAF).
 * see Li, Hengfei. Probabilistic argumentation. 2015. PhD Thesis. Aberdeen University.
 *
 * @author Taha Dogan Gunes
 */
public class PEAFTheory extends AbstractEAFTheory<WeightedSetSupport> {

    /**
     * Default constructor; initializes an empty PEAFTheory
     */
    public PEAFTheory() {
    }

    /**
     * Optional constructor; initializes an PEAFTheory with arguments
     *
     * @param noArguments the number of arguments
     */
    public PEAFTheory(Set<BArgument> noArguments) {
            this.addAll(noArguments);

    }



    /**
     * Add an argument with an integer identifier
     *
     * @param identifier the identifier of the argument
     * @return EArgument object
     */
    public BArgument addArgument(int identifier) {
        BArgument argument = this.createArgument(Integer.toString(identifier));
        this.addArgument(argument);
        return argument;
    }





    /**
     * Add a support with sets for many to many mapping
     *
     * @param froms set with arguments that originate the support
     * @param tos   set with arguments that receive the support
     * @param cp    the result assigned to the support link (must be in range [0.0, 1.0])
     */
    public void addSupport(Set<BArgument> froms, Set<BArgument> tos, double cp) {

        WeightedSetSupport support = new WeightedSetSupport( new ArgumentSet(froms), new ArgumentSet(tos), cp);
        this.addSupport(support);
    }

    /**
     * Add attack between arguments with indices
     *
     * @param froms the index of the EArgument that originates the attack
     * @param tos   the index of the EArgument that receieves the attack
     */
    public void addAttack(BipolarEntity froms, BipolarEntity tos) {
        this.addAttack(froms, tos);
    }

    /**
     * Returns an ascii tree for debug purposes.
     * This is usually used in the unit tests.
     *
     * @return the ascii tree in string
     */
    public String getASCIITree() {
        StringBuilder builder = new StringBuilder();
        for (Support support : supports) {
            StringBuilder builder1 = EdgeListWriter.getStringBuilder((ArgumentSet) support.getSupporter(), (ArgumentSet) support.getSupported(), " -> ");
            if (builder1 != null) {
                builder.append(builder1);
                builder.append("\n");
            }
        }

        return builder.toString();
    }

    /**
     * Helper function to print the ascii tree
     */
    public void printASCIITree() {
        System.out.println(this.getASCIITree());
    }

	@Override
	public boolean isAcceptable(BArgument argument, Collection<BArgument> ext) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean add(Support support) {
		this.addSupport((WeightedSetSupport) support);
		return true;
	}

	@Override
	public boolean add(Attack attack) {
		this.attacks.add((EAttack) attack);
		return true;
	}




}
