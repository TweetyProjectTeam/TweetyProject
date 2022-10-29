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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class inherits PEAFTheory to store additional information regarding the arguments inserted
 *
 * @author Taha Dogan Gunes
 */
public class NamedPEAFTheory extends PEAFTheory {

    /**
     * Internal map for getting names of the arguments given their reference
     */
    private final Map<BArgument, String> namesMap = new HashMap<BArgument, String>();
    /**
     * Internal map for getting arguments given the AIF identifier
     */
    private final Map<String, BArgument> reverseAIFMap = new HashMap<String, BArgument>();
    private final Map<BArgument, String> aifMap = new HashMap<BArgument, String>();

    /**
     * Helper function to give the names of a set of arguments
     * Static such that it can work with PEAFTheory nodes (EArgument, EAttack and PSupport)
     *
     * @param names the map that has arguments as keys and the names of arguments as string
     * @param args  the arguments that are queried
     * @return the set of names
     */
    public static Set<String> giveNames(Map<BArgument, String> names, Set<BArgument> args) {
        Set<String> argumentNames = new HashSet<String>();
        for (BArgument arg : args) {
            argumentNames.add(names.get(arg));
        }
        return argumentNames;
    }

    /**
     * Returns the argument's name give its identifier
     *
     * @param identifier the identifier of the argument
     * @return the argument's name
     */
    public String getArgumentNameFromIdentifier(String identifier) {
        return namesMap.get(getArgumentByIdentifier(identifier));
    }

    /**
     * Returns the EArgument object given its identifier
     *
     * @param identifier the identifier of the argument
     * @return corresponding EArgument object
     */
    public BArgument getArgumentByIdentifier(String identifier) {
        return reverseAIFMap.get(identifier);
    }

    /**
     * Returns the name of the argument given EArgument object's reference
     *
     * @param argument EArgument reference
     * @return the name in string
     */
    public String getNameOfArgument(BArgument argument) {
        return namesMap.get(argument);
    }

    /**
     * Add argument with names
     *
     * @param identifier        PEAF identifier as an integer value (index of the argument for efficiency reasons)
     * @param name              The given name of the argument
     * @param aifNodeIdentifier The aif node identifier
     * @return EArgument object given
     */
    public BArgument addArgument(int identifier, String name, String aifNodeIdentifier) {
        BArgument argument = super.addArgument(identifier);
        namesMap.put(argument, name);
        reverseAIFMap.put(aifNodeIdentifier, argument);
        aifMap.put(argument, aifNodeIdentifier);
        return argument;
    }

    /**
     * Print the NamedPEAFTheory for debugging purposes
     */
    public String toString() {
    	String res = "";
    	res += ("NamedPEAF:");
    	res += ("-- Arguments --");
        int i = 0;
        for (BArgument argument : this.getArguments()) {
        	res += (i + ". " + namesMap.get(argument));
            i++;
        }

        res += "\n";
        res += ("-- Supports --");
        i = 0;
        for (Support support : this.getSupports()) {
        	res += (i + ". " + support.toString());
            i++;
        }

        res += "\n";
        res += ("-- Attacks --");
        i = 0;
        for (Attack attack : this.getAttacks()) {
        	res += (i + ". " + attack.toString());
            i++;
        }

        res += ("\n");
        return res;
    }

    /**
     * prints string reprsentation without names
     */
    public void prettyPrintWithoutNames() {
        super.prettyPrint();
    }

    /**
     * 
     * @param args aruments
     * @return copy of thory without args
     */
    public NamedPEAFTheory createCopyWithoutArguments(Set<BArgument> args) {
        NamedPEAFTheory newPEAFTheory = new NamedPEAFTheory();

        int noArgs = 0;

//        peafTheory.addArgument(noArgs, argument.getName(), argument.getIdentifier());
        for (BArgument argument : this.arguments) {
            if (!args.contains(argument)) {
                newPEAFTheory.addArgument(noArgs++, namesMap.get(argument), this.aifMap.get(argument));
            }
        }
        newPEAFTheory.addSupport(new HashSet<BArgument>(), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(newPEAFTheory.getArguments().get(0)))), 1.0);

        for (Attack attack : this.attacks) {
            Set<BArgument> froms = this.filter(attack.getAttacker(), args);
            Set<BArgument> tos = this.filter(attack.getAttacked(), args);

            if (froms.isEmpty() || tos.isEmpty()) {
                continue;
            }

            Set<BArgument> newFroms = this.convert(froms, newPEAFTheory);
            Set<BArgument> newTos = this.convert(tos, newPEAFTheory);

            newPEAFTheory.addAttack(newFroms, newTos);
        }

        for (Support support : this.supports) {
            Set<BArgument> froms = this.filter(support.getSupporter(), args);
            Set<BArgument> tos = this.filter(support.getSupported(), args);

            if (froms.isEmpty() || tos.isEmpty()) {
                continue;
            }

            Set<BArgument> newFroms = this.convert(froms, newPEAFTheory);
            Set<BArgument> newTos = this.convert(tos, newPEAFTheory);

            newPEAFTheory.addSupport(newFroms, newTos, support.getConditionalProbability());
        }


        return newPEAFTheory;
    }

    /**
     * 
     * @param originalArgs original arguments
     * @param newPEAFTheory new peaf theory
     * @return set of arguments
     */
    private Set<BArgument> convert(Set<BArgument> originalArgs, NamedPEAFTheory newPEAFTheory) {
    	Set<BArgument> conversions = new HashSet<BArgument>();

        for (BArgument originalArg : originalArgs) {
            String aifIdentifier = this.aifMap.get(originalArg);
            BArgument newArg = newPEAFTheory.getArgumentByIdentifier(aifIdentifier);
            conversions.add(newArg);
        }

        return conversions;
    }

    /**
     * 
     * @param originalArgs original arguments
     * @param filter arguments for filtering
     * @return a set of arguments
     */
    private Set<BArgument> filter(BipolarEntity originalArgs, Set<BArgument> filter) {
    	Set<BArgument> filteredSet = new HashSet<BArgument>();

        for (BArgument arg : originalArgs) {
            if (!filter.contains(arg)) {
                filteredSet.add(arg);
            }
        }

        return filteredSet;
    }

    /**
     * 
     * @param e an argument
     * @return string reprentation of argument
     */
    public String getIdentifier(BArgument e) {
        return this.aifMap.get(e);
    }
}
