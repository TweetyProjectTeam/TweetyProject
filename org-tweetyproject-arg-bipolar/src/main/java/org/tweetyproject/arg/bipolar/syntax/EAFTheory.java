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



import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class implements an abstract argumentation theory in the sense of Evidential Argumentation Frameworks (EAF).
 * see Li, Hengfei. Probabilistic argumentation. 2015. PhD Thesis. Aberdeen University.
 *
 * @author Taha Dogan Gunes
 */
public class EAFTheory extends AbstractEAFTheory<Support> {

    /**
     * Default constructor; initializes an empty EAFTheory
     */
    public EAFTheory() {
    }

    /**
     * Optional constructor; initializes an EAFTheory with arguments
     *
     * @param noArguments the number of arguments
     */
    public EAFTheory(Set<BArgument> noArguments) {
        for (BArgument i : noArguments) {
            this.add(i);
        }
    }

    /**
     * Creates a new EAFTheory from an PEAFTheory (the probabilities are eliminated)
     *
     * @param peafTheory PEAFTheory object
     * @return
     */
    public static EAFTheory newEAFTheory(PEAFTheory peafTheory) {
        EAFTheory eafTheory = new EAFTheory();
        for (BArgument argument : peafTheory.getArguments()) {
            eafTheory.addArgument(argument);
        }

        for (Attack attack : peafTheory.getAttacks()) {
            eafTheory.addAttack(attack.getAttacker(), attack.getAttacked());
        }

        for (Support support : peafTheory.getSupports()) {
            eafTheory.addSupport((HashSet<BArgument>)support.getSupporter(), (HashSet<BArgument>)support.getSupported());
        }

        return eafTheory;
    }


    /**
     * Creates an arguments with a name
     *
     * @param name the name of the argument
     * @return EArgument object
     */
    protected BArgument createArgument(String name) {
        return new BArgument(name);
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
     * Add a support with index arrays (froms and tos)
     *
     * @param froms integer array with argument, represents indices of arguments
     * @param tos   integer array with argument, represents indices of arguments
     */
    public void addSupport(HashSet<BArgument> froms, HashSet<BArgument> tos) {

        Support support = new SetSupport( froms, tos);
        this.addSupport(support);
    }

    /**
     * Gets arguments of the EAF
     *
     * @return list of arguments inside the EAF (insert ordered)
     */
    public ArrayList<BArgument> getArguments() {
        return arguments;
    }

    /**
     * Gets supports of the EAF
     *
     * @return list of supports inside the EAF (insert ordered)
     */
    public Set<Support> getSupports() {
        return new HashSet<Support>(supports);
    }

    /**
     * Gets attacks of the EAF
     *
     * @return list of attacks inside the EAF (insert ordered)
     */
    public Set<Attack> getAttacks() {
        return new HashSet<Attack>(attacks);
    }

    /**
     * This method converts this EAF to a DAF with using Algorithm 1 from
     * Oren et. al. 2010 "Moving Between Argumentation Frameworks"
     *
     * @return DungTheory object
     */
    public DungTheory convertToDAFNaively() {
        // This method is using Algorithm 1 from Oren et. al. 2010 "Moving Between Argumentation Frameworks"
        Set<Set<BArgument>> dungArguments = new HashSet<>(); // Line 1
        Set<Pair<Set<BArgument>, Set<BArgument>>> dungAttacks = new HashSet<>(); // Line 2

        // Line 3 - 7
        for (Set<BArgument> A :  this.powerSet(this.getArgumentsAsSet())) {
            boolean isSelfSupporting = checkIsSelfSupporting(A);

            if (A.size() > 0 && isSelfSupporting) {
                dungArguments.add(A);
            }

        }
        // Line 8 - 12
        for (Attack attack : attacks) { // Line 8
            BipolarEntity X = attack.getAttacker();
            for (BArgument x : X) { // Line 8
                for (Attack xAttack : this.getAttacks()) { // Line 8
                    for (BArgument a : xAttack.getAttacked()) { // Line 8
                        for (Set<BArgument> D : dungArguments) { // Line 9
                            if (D.contains(X)) { // Line 9: check X is subset of D
                                for (Set<BArgument> A : dungArguments) { // Line 9
                                    if (A.contains(a)) { // Line 9: check if a is in A, when A is in DARGS
                                        dungAttacks.add(new Pair<>(D, A));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Map<String, Argument> mapping = new HashMap<>();
        DungTheory dungTheory = new DungTheory();
        for (Set<BArgument> dungArgument : dungArguments) {
            String nameOfArgument = dungArgument.stream()
                    .map(BArgument::getName)
                    .sorted()
                    .collect(Collectors.joining("_"));
            Argument argument = new Argument(nameOfArgument);
            mapping.put(nameOfArgument, argument);
            dungTheory.add(argument);
        }

        for (Pair<Set<BArgument>, Set<BArgument>> dungAttack : dungAttacks) {
            String nameOfFrom = dungAttack.getFirst().stream()
                    .map(BArgument::getName)
                    .sorted()
                    .collect(Collectors.joining("_"));
            Argument from = mapping.get(nameOfFrom);

            String nameOfTo = dungAttack.getSecond().stream()
                    .map(BArgument::getName)
                    .sorted()
                    .collect(Collectors.joining("_"));
            Argument to = mapping.get(nameOfTo);

            dungTheory.addAttack(from, to);
        }
        return dungTheory;
    }
    


    /**
     * Checks if a set of arguments whether they are self-supporting or not
     * This is used for conversion from EAF to DAF.
     * A set of arguments A is self-supporting if and only if for all a in A, A e-supports a
     *
     * @param A a set of arguments
     * @return true if the set of arguments are self-supporting or not.
     */
    public boolean checkIsSelfSupporting(Set<BArgument> A) {
        // Check if the subset is self-supporting (Line 4, if A is self-supporting)
        boolean isSelfSupporting = true;
        for (BArgument a : A) { // for all a in A
            if (a == eta) {
                continue;
            }
            Set<BArgument> A_copy = new HashSet<>(A);

            // Definition 8 (Auxiliary Notions for EAFs)
            // A set of arguments S is self-supporting iff for all x \in S, S e-supports x

            // Assume that there exists a T that is subset of A.
            // if for all elements of T, (each of T is t) support a, then a has evidential support from A \ {a}
            A_copy.remove(a);

            boolean supports = false;

            for (Set<BArgument> T : this.powerSet(A_copy)) {
                for (BArgument x : T) {
                    if (a.isSupportedBy(this, (BipolarEntity)x)) {
                        supports = true;
                    }
                }
                if (supports) {
                    break;
                }
            }

            isSelfSupporting = isSelfSupporting && supports;
        }
        return isSelfSupporting;
    }

    /**
     * Pretty print of the EAFTheory
     */
    public String toString() {
    	String res = "";
        res+=("-- Arguments --");
        int i = 0;
        for (BArgument argument : this.getArguments()) {
        	res+=(i + ". " + argument);
            i++;
        }

        res+="\n";
        res+=("-- Supports --");
        i = 0;
        for (Support support : this.getSupports()) {
        	res+=(i + ". " + support);
            i++;
        }

        res+="\n";
        res+=("-- Attacks --");
        i = 0;
        for (Attack attack : this.getAttacks()) {
        	res+=(i + ". " + attack);
            i++;
        }
        return res;
    }

	@Override
	public boolean isAcceptable(BArgument argument, Collection<BArgument> ext) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean add(Support support) {
		this.addSupport(support);
		return true;
	}

	@Override
	public boolean add(Attack attack) {
		this.addAttack((EAttack) attack);
		return true;
	}
}
