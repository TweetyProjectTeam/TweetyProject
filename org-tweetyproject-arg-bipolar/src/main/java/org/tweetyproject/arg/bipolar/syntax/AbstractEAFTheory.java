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
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A base class for implementing PEAFTheory and EAFTheory.
 *
 * @param <S> the support link (ESupport or PSupport)
 * @author Taha Dogan Gunes
 */
public abstract class AbstractEAFTheory<S extends Support> extends AbstractBipolarFramework{

    /**
     * The root node of the EAFTheory
     */
    protected BArgument eta;
    /**
     * Arguments stored in a set
     */
    protected Set<BArgument> argumentsSet = new HashSet<BArgument>();

    /**
     * Arguments stored in a list
     */
    protected ArrayList<BArgument> arguments = new ArrayList<>();
    /**
     * Supports stored in a list
     */
    protected ArrayList<S> supports = new ArrayList<>();
    /**
     * Attacks stored in a list
     */
    protected ArrayList<Attack> attacks = new ArrayList<>();

    /**
     * Add an argument
     *
     * @param argument an EArgument objects
     */
    public void addArgument(BArgument argument) {
        if (arguments.size() == 0) {
            eta = argument;
        }
        arguments.add(argument);
        argumentsSet.add(argument);
    }


    /**
     * Add an attack
     *
     * @param attack an EAttack object
     * @return true if successful
     */
    public boolean addAttack(EAttack attack) {
        return attacks.add(attack);
    }

    /**
     * Add a support
     *
     * @param support a T-Support object
     * @return true if successful
     */
    public boolean addSupport(S support) {
        return supports.add(support);
    }

    /**
     * Creates an argument with a name
     *
     * @param name the name of the argument in string
     * @return the created EArgument object
     */
    protected BArgument createArgument(String name) {
        return new BArgument(name);
    }

    /**
     * Add an attack with sets
     *
     * @param froms arguments that originates the attack
     * @param tos   arguments that receive the attack
     */
    @SuppressWarnings("unchecked")
	public void addAttack(BipolarEntity froms, BipolarEntity tos) {
        int identifier = attacks.size();
        HashSet<BArgument> fromSet = new HashSet<BArgument>();
        HashSet<BArgument> toSet = new HashSet<BArgument>();
        if(froms instanceof BArgument)
        	fromSet.add((BArgument) froms);
        if(froms instanceof HashSet<?>)
        	fromSet = (HashSet<BArgument>) froms;
        if(tos instanceof BArgument)
        	toSet.add((BArgument) tos);
        if(tos instanceof HashSet<?>)
        	toSet = (HashSet<BArgument>) tos;
        EAttack attack = this.createAttack(Integer.toString(identifier), fromSet, toSet);
        this.addAttack(attack);
    }

    /**
     * Add an attack with sets
     *
     * @param froms arguments that originates the attack
     * @param tos   arguments that receive the attack
     */
    public void addAttack(Set<BArgument> froms, Set<BArgument> tos) {
        int identifier = attacks.size();
        EAttack attack = this.createAttack(Integer.toString(identifier), froms, tos);
        this.addAttack(attack);
    }

    /**
     * Creates an attack object (does not add to the internal abstract object)
     *
     * @param name  the name of the attack
     * @param froms the set of arguments that the attack originates from
     * @param tos   the set of arguments that the attack targets
     * @return EAttack object
     */
    protected EAttack createAttack(String name, Set<BArgument> froms, Set<BArgument> tos) {
        if (tos.contains(eta)) {
            throw new RuntimeException("Argument eta can't be attacked.");
        }
        EAttack attack = new EAttack( froms, tos);

        return attack;
    }

    /**
     * Create a set of arguments from the given indices
     *
     * @param fromIndices the indices of arguments requested
     * @return the set of arguments
     */
    protected Set<BArgument> createEmptyArgSet(int[] fromIndices) {
        Set<BArgument> froms = new HashSet<>(fromIndices.length);
        for (int fromIndex : fromIndices) {
            froms.add(arguments.get(fromIndex));
        }
        return froms;
    }

    /**
     * Create and add a new argument with identifier
     *
     * @param identifier integer identifier for the argument
     * @return the created argument
     */
    public BArgument addArgument(int identifier) {
    	BArgument argument = this.createArgument(Integer.toString(identifier));
        this.addArgument(argument);
        return argument;
    }

    /**
     * Get all arguments
     *
     * @return a set of arguments
     */
    public Set<BArgument> getArgumentsAsSet() {
        return this.argumentsSet;
    }

    /**
     * @return a list of arguments
     */
    public ArrayList<BArgument> getArguments() {
        return arguments;
    }

    /**
     * Get all supports
     *
     * @return a list of supports
     */
    public Set<Support> getSupports() {
        return new HashSet<Support>(supports);
    }
    
    /**
     * Get all supports invloving argument arg
     * @param arg an argument
     * @return a list of supports
     */
    public Set<Support> getSupports(BArgument arg) {
        HashSet<Support> result = new HashSet<Support>();
        for(Support s : this.getSupports()) {
        	if(s.contains(arg))
        		result.add(s);
        }
    	return result;
    }

    /**
     * Get all attacks
     *
     * @return a list of attacks
     */
    public Set<Attack> getAttacks() {
        return new HashSet<Attack>(attacks);
    }

    public <T> Set<Set<T>> powerSet(Set<T> originalSet) {
        Set<Set<T>> sets = new HashSet<Set<T>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<T>(list.subList(1, list.size())); 
        for (Set<T> set : powerSet(rest)) {
            Set<T> newSet = new HashSet<T>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }       
        return sets;
    } 
    /**
     * Pretty print of the EAFTheory
     */
    public String toString() {
    	String result = "";
    	result += ("-- Arguments --");
        int i = 0;
        for (BArgument argument : this.getArguments()) {
        	result += (i + ". " + argument);
            i++;
        }

        result += "\n";
        result += ("-- Supports --");
        i = 0;
        for (Support support : this.getSupports()) {
        	result += (i + ". " + support);
            i++;
        }

        result += "";
        result += ("-- Attacks --");
        i = 0;
        for (Attack attack : this.getAttacks()) {
        	result += (i + ". " + attack);
            i++;
        }

        return result += ("\n");
    }

    /**
     * Get all the number of arguments
     *
     * @return the number of arguments
     */
    public int getNumberOfArguments() {
        return arguments.size();
    }

    /**
     * Get the eta (the root) argument
     *
     * @return eta argument
     */
    public BArgument getEta() {
        return eta;
    }
}
