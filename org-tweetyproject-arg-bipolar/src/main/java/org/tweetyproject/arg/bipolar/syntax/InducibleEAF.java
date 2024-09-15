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


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This class is an intermediate representation of EAFs that are induced from a PEAF.
 * This is used for making analysis and storing intermediate values while making probabilistic justification
 * of some arguments.
 *
 * @author Taha Dogan Gunes
 */
public class InducibleEAF {
    /**
     * A subset of arguments from PEAF
     */
    public final Set<BArgument> arguments;
    /**
     * A subset of supports from PEAF
     */
    public final Set<Support> supports;
    /**
     * A subset of attacks from PEAF
     */
    public final Set<EAttack> attacks;
    /**
     * A subset of arguments from PEAF that can be used to create a new EAF (connected to InducibleEAF)
     */
    public final Set<BArgument> newArguments;
    /**
     * pInside
     */
    public final double pInside;
    /**
     * inducePro
     */
    public final double inducePro;

    /**
     * Constructor
    * @param arguments arguments
     * @param supports supports
     * @param attacks attacks
     * @param newArguments new arguments
     * @param pInside pInside
     * @param inducePro induce pro
     */
    public InducibleEAF(Set<BArgument> arguments,
                        Set<Support> supports,
                        Set<EAttack> attacks,
                        Set<BArgument> newArguments,
                        double pInside, double inducePro) {

        this.arguments = arguments;
        this.supports = supports;
        this.attacks = attacks;
        this.newArguments = newArguments;
        this.pInside = pInside;
        this.inducePro = inducePro;
    }

    /**
     *
     * @return the arguments
     */
    public Set<BArgument> getArguments() {
        return arguments;
    }

    /**
     *
     * @return the supports
     */
    public Set<Support> getSupports() {
        return supports;
    }

    /**
     *
     * @return new arguments
     */
    public Set<BArgument> getNewArguments() {
        return newArguments;
    }

    /**
     *
     * @return pINside
     */
    public double getpInside() {
        return pInside;
    }

    /**
     *
     * @return InducePro
     */
    public double getInducePro() {
        return Math.exp(inducePro);
    }

    /**
     *
     * @return a new EAFTheory
     */
    public EAFTheory toNewEAFTheory() {
        EAFTheory eafTheory = new EAFTheory();
        Set<BArgument> arguments = new HashSet<BArgument>();

        arguments.addAll(this.getArguments());

        for (Support support : supports) {
            eafTheory.addSupport(support);
        }

        for (EAttack attack : attacks) {
            eafTheory.addAttack(attack);
        }

        List<BArgument> argsSorted = new ArrayList<BArgument>();
        argsSorted.addAll(arguments);
        argsSorted.sort(Comparator.comparing(BArgument::getName));
        System.out.println(this.getArguments());
        for (BArgument argument : arguments) {
            eafTheory.addArgument(argument);
        }

        return eafTheory;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("InducibleEAF{");
        builder.append("arguments=[");
        int i = 0;

        List<BArgument> sortedArgs = new ArrayList<BArgument>();
        sortedArgs.addAll(this.arguments);
        sortedArgs.sort(Comparator.comparing(BArgument::getName));

        for (BipolarEntity argument : sortedArgs) {
            builder.append(argument.toString());
            if (i != arguments.size() - 1) {
                builder.append(",");
            }
            i++;
        }
        builder.append("], supports=[");
        i = 0;

        List<Support> sortedSupports = new ArrayList<Support>();
        sortedSupports.addAll(this.supports);


        for (Support support : sortedSupports) {
            builder.append(support.toString());
            if (i != supports.size() - 1) {
                builder.append(",");
            }
            i++;
        }

        List<EAttack> sortedAttacks = new ArrayList<EAttack>();
        sortedAttacks.addAll(this.attacks);

        builder.append("], attacks=[");
        i = 0;
        for (EAttack attack : sortedAttacks) {
            builder.append(attack.toString());
            if (i != attacks.size() - 1) {
                builder.append(",");
            }
            i++;
        }
        builder.append("] induce result=");
        builder.append(Math.exp(inducePro));
        builder.append("}");


        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InducibleEAF that = (InducibleEAF) o;
        return arguments.containsAll(that.arguments)
                && supports.containsAll(that.supports)
                && attacks.containsAll(that.attacks)
                && that.arguments.containsAll(arguments)
                && that.supports.containsAll(supports)
                && that.attacks.containsAll(attacks);
    }

    @Override
    public int hashCode() {
        List<String> all = new ArrayList<String>();
        for(BArgument arg : this.arguments) {
        	Integer i = arg.hashCode();
        	all.add(i.toString());
        }
        for(EAttack att : this.attacks) {
        	Integer i = att.hashCode();
        	all.add(i.toString());
        }
        for(Support sup : this.supports) {
        	Integer i = sup.hashCode();
        	all.add(i.toString());
        }

        return Objects.hash(all.toArray());
    }

    /**
     * adds atack links
     */
    public void addAttackLinks() {
        this.attacks.clear();
        Set<BArgument> args = new HashSet<BArgument>();
        args.addAll(this.getArguments());

        for (BipolarEntity arg : args) {
            for (Attack attack : this.attacks) {
            	if(attack.contains(arg)) {
	                BipolarEntity froms = attack.getAttacker();
	                BipolarEntity tos = attack.getAttacked();

	                if (args.containsAll((HashSet<BArgument>) froms) && args.contains(tos)) {
	                    this.attacks.add((EAttack) attack);
	                }
            	}
            }
        }
    }
}
