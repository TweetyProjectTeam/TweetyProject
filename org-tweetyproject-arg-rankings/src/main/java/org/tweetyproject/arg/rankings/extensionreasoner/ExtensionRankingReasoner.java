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

package org.tweetyproject.arg.rankings.extensionreasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.SetTools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.*;


/**
 * Reasoner using ranking semantics.
 *
 * @author Lars Bengel
 * @author Daniel Letkemann
 */
//TODO: rework class to use classes from "comparator" module (e.g. LatticePartialOrder) instead of weird "Comparison -> Character [<,>,=,null] Map.
public class ExtensionRankingReasoner {
    private final ExtensionRankingSemantics semantics;
    private final List<Method> baseFunctions;
    private Map<List<Extension<DungTheory>>, Character> comparisonMap;
//    public final  Extension<DungTheory> debug = new Extension<DungTheory>();
//    public final  Extension<DungTheory> debug2 = new Extension<DungTheory>();


    /**
     * Creates a reasoner for the given ordering semantics.
     * @param semantics an ordering semantics
     * @throws NoSuchMethodException if an error occurs
     */
    public ExtensionRankingReasoner(ExtensionRankingSemantics semantics) throws NoSuchMethodException {
        this.semantics = semantics;
        this.baseFunctions = getCompareMethods(this.semantics);
//        debug.add(new Argument("1"));
////        debug.add(new Argument("2"));
//        debug.add(new Argument("3"));
//        debug.add(new Argument("4"));
//        debug.add(new Argument("5"));
//        debug.add(new Argument("6"));
//        debug.add(new Argument("7"));
//
////        debug2.add(new Argument("1"));
////        debug2.add(new Argument("2"));
//        debug2.add(new Argument("3"));
//        debug2.add(new Argument("4"));
//        debug2.add(new Argument("5"));
//        debug2.add(new Argument("6"));
//        debug2.add(new Argument("7"));
    }

    /**
     * Returns the semantic of ExtensionRankingReasoner instance.
     * @return ExtensionRankingSemantics (enum) of the reasoner
     */
    public ExtensionRankingSemantics getSemantics(){
        return this.semantics;
    }


    /**
     * Returns the strongest rank of the "getModels" method.
     * @param theory a dung theory
     *@param cardinalityMode if true, extensions are ranked by cardinality of BF results rather than subset relations
     * @return the list of extensions in the best rank
     * @throws InvocationTargetException should never happen
     * @throws IllegalAccessException should never happen
     */
    public List<Extension<DungTheory>> getModel(DungTheory theory, boolean cardinalityMode) throws InvocationTargetException, IllegalAccessException{
        //return the best rank
        List<List<Extension<DungTheory>>> ranks = getModels(theory, cardinalityMode);
        return  ranks.get(ranks.size()-1);
    }
    /**
     * Computes the ordering over all subsets of theory wrt. to the ordering semantics.
     * This is done by making several comparisons (in lexicographic order) of Base Functions applied to Extensions
     * and implies a total order that is sorted and split into ranks in the end.
     * LOWER levels are ranked BETTER (Ascending order).
     * Two extensions in one rank are either equally ranked or incomparable,
     * thus an extension placed in a better rank does not necessarily mean it is better than all the extensions in the ranks above
     * @param theory a dung theory
     * @param cardinalityMode if true, extensions are ranked by cardinality of BF results rather than subset relations
     * @return a list representing the ordering of all subsets of the given graph wrt. the ordering semantics (ascending order)
     * @throws InvocationTargetException should never happen
     * @throws IllegalAccessException should never happen
     */
    public List<List<Extension<DungTheory>>> getModels(DungTheory theory, boolean cardinalityMode) throws InvocationTargetException, IllegalAccessException {
        //returns partitioned list of ranked extensions partitioned into ranks
        Set<Set<Argument>> subsets = new SetTools<Argument>().subsets(theory);

        List<Extension<DungTheory>> extensions = new LinkedList<>();
        for(Set<Argument> set : subsets){
            Extension<DungTheory> ext = new Extension<>(set);
            extensions.add(ext);
        }
        Collections.reverse(extensions);
        //puts the extension containing all arguments at the first index, root of ranks-graph

        this.comparisonMap = getComparisonSigns(extensions,theory,cardinalityMode);
        return getRanksFromList(extensions);
    }
    /**
     * cardinalityMode is set to false if using this one-parameter method
     * Computes the ordering over all subsets of theory wrt. to the ordering semantics.
     * This is done by making several comparisons (in lexicographic order) of Base Functions applied to Extensions
     * and implies a total order that is sorted and split into ranks in the end.
     * LOWER levels are ranked BETTER (Ascending order).
     * Two extensions in one rank are either equally ranked or incomparable,
     * thus an extension placed in a better rank does not necessarily mean it is better than all the extensions in the ranks above
     * @param theory a dung theory
     * @return a list representing the ordering of all subsets of the given graph wrt. the ordering semantics (ascending order)
     * @throws InvocationTargetException should never happen
     * @throws IllegalAccessException should never happen
     */
    public List<List<Extension<DungTheory>>> getModels(DungTheory theory) throws InvocationTargetException, IllegalAccessException {
        return getModels(theory,false);
    }
    
    
    //cardinal mode!!!!!!!!!!!!!!!!!!!!!!!!!!
    public List<List<Argument>> rankArguments(DungTheory theory) throws InvocationTargetException, IllegalAccessException{
		List<List<Extension<DungTheory>>> result = getModels(theory);
		Map<Argument, Boolean> argMap = new HashMap<>();
	    Collection<Argument> nodes = theory.getNodes();
	    int count = 0;
	    for(Argument arg : nodes) {
        	argMap.put(arg, false);
        }
	    List<Extension<DungTheory>> rankedExt;
	    List<List<Argument>> ranks = new ArrayList<>();
        for(int i = result.size()-1; i >= 0; i--) {
        	List<Argument> rank = new ArrayList<>();
        	rankedExt = result.get(i);
        	for(Extension<DungTheory> ext : rankedExt) {
        		 Iterator<Argument> it = ext.iterator();
        		 while( it.hasNext() ) {
        			 Argument arg = it.next();
        			 if(argMap.get(arg) == false) {
        				 argMap.put(arg, true);
        				 rank.add(arg);
        				 count++;
        			 } 
        		 }
        	}
        	if(rank.size() > 0) {
   			 ranks.add(rank); 
   		    } 
        	if( count == nodes.size()){
        		break;
        	}
        }
		return ranks;
	}
    
    

    /**
     * Returns the correct comparison sign for two extensions by using the base functions in correct order.
     * Lexicographic: if two sets are equal regarding the base function -> use next base function in the order for further comparisons.
     * @param argumentSubsets sets to get all comparisons of
     * @param theory attacks/defenses used for base function calculations
     * @param cardinalityMode if true, extensions are ranked by cardinality of BF results rather than subset relations
     * @return map of comparison signs for a list of two extensions
     * @throws InvocationTargetException should never happen
     * @throws IllegalAccessException should never happen
     */
    private Map<List<Extension<DungTheory>>,Character> getComparisonSigns(List<Extension<DungTheory>> argumentSubsets, DungTheory theory, boolean cardinalityMode) throws InvocationTargetException, IllegalAccessException {
        // < if better ranked, can also be read as an "arrow" in a directed graph ( ext1 "<"---- ext2 )
        // > if worse ranked
        // = if equal, means that both extensions have matching parents in graph terms
        // null if incomparable, no graph edge between extensions
        Map<List<Extension<DungTheory>>, Character> subsetToSignMap = new HashMap<>();
        for (int i = 0; i<argumentSubsets.size(); i++){
            Extension<DungTheory> ss1 = argumentSubsets.get(i);
            for(int j = i+1; j<argumentSubsets.size(); j++){
                Extension<DungTheory> ss2 = argumentSubsets.get(j);
                ArrayList<Extension<DungTheory>> comparison = new ArrayList<>();
                comparison.add(ss1); comparison.add(ss2);
                putCompareSignInMap(comparison, subsetToSignMap,theory, cardinalityMode);
                // ext1 (sign) ext2 is stored in the map, ext2 (sign) ext1 is not
            }
        }
        return subsetToSignMap;
    }

    /**
     * "Lexicographically" calculate base functions of two extensions and compare the outcome,
     * if they are equal, recursively calculate sign for the next base function in the order of the corresponding semantic.
     * @param comparison list of TWO extensions, first index of list corresponds to extension on the "left"
     * @param map map to store comparison sign for comparison
     * @param theory attacks/defenses for base function calculations
     * @param cardinalityMode if true, extensions are ranked by cardinality of BF results rather than subset relations
     * @throws InvocationTargetException should never happen
     * @throws IllegalAccessException should never happen
     */
    private void putCompareSignInMap(ArrayList<Extension<DungTheory>> comparison,Map<List<Extension<DungTheory>>,Character> map, DungTheory theory, boolean cardinalityMode) throws InvocationTargetException, IllegalAccessException {
        putCompareSignInMap(comparison, map, theory, cardinalityMode,0);
    }private void putCompareSignInMap(ArrayList<Extension<DungTheory>> comparison, Map<List<Extension<DungTheory>>, Character> map, DungTheory theory, boolean cardinalityMode, int iteration) throws InvocationTargetException, IllegalAccessException {
        if(iteration == baseFunctions.size()){
            //no more baseFunctions:
            // if preferred or grounded semantic:
            //  compare subsets themselves

            Extension<DungTheory> cs1 = new Extension<>(comparison.get(0));
            Extension<DungTheory> cs2 = new Extension<>(comparison.get(1));
            switch(semantics) {
                case R_GR:
                    if (isStrictlySmaller(cs1, cs2,false)){
                        map.put(comparison, '<');
                    } else if (isStrictlyBigger(cs1, cs2,false)) {
                        map.put(comparison, '>');
                    }
                    else{
                        map.put(comparison,null);
                    }
                    break;
                case R_PR: // , R_CO_PR
                    if (isStrictlySmaller(cs1, cs2,false)) {
                        map.put(comparison, '>');
                    } else if (isStrictlyBigger(cs1, cs2,false)) {
                        map.put(comparison, '<');
                    }
                    else{
                        map.put(comparison,null);
                    }
                    break;

            }
        }
        else {
            Method compareMethod = baseFunctions.get(iteration);
            Extension<DungTheory> subset1 = comparison.get(0);
            Object[] parameters = new Object[2];
            parameters[0] = subset1;
            parameters[1] = theory;
            Extension<DungTheory> cs1 = (Extension<DungTheory>) compareMethod.invoke(this, parameters);
            Extension<DungTheory> subset2 = comparison.get(1);
            parameters[0] = subset2;
            Extension<DungTheory> cs2 = (Extension<DungTheory>) compareMethod.invoke(this, parameters);
            if (isStrictlySmaller(cs1, cs2,cardinalityMode)) {
                map.put(comparison, '<');
            } else if (isStrictlyBigger(cs1, cs2,cardinalityMode)) {
                map.put(comparison, '>');
            } else if (cs1.equals(cs2)) {
                //recurse
                map.put(comparison, '=');
                putCompareSignInMap(comparison, map, theory, cardinalityMode,iteration + 1);
            }
            else{
                map.put(comparison, null);
            }

        }
    }

    /**
     * Returns true if ext1 < ext2.
     * If cardinalityMode = true -> Returns true if ext1 has striclty less elements than ext2.
     * @param ext1 first extension
     * @param ext2 second extension
     * @param cardinalityMode if true, extensions are ranked by cardinality of BF results rather than subset relations
     * @return true if ext1 is strict subset of ext2, false otherwise (equal or superset)
     */
    private boolean isStrictlySmaller(Extension<DungTheory> ext1, Extension<DungTheory> ext2, boolean cardinalityMode){
        Set<Argument> set1 = new HashSet<>(ext1);
        Set<Argument> set2 = new HashSet<>(ext2);
        // set1 is a strict-subset of set2 iff the union of the compare sets equals the compare set of set1 and not set2
        if(!cardinalityMode) {
            Collection<Argument> union = new HashSet<>(set1);
            union.addAll(set2);
            return (!union.equals(set1) && union.equals(set2));
        }
        //cardinalityMode true, go by compareset sizes only
        else{
            return (set1.size() < set2.size());
        }
    }
    /**
     * Returns true if ext1 > ext2.
     * If cardinalityMode = true -> Returns true if ext1 has strictly more elements than ext2.
     * @param ext1 first extension
     * @param ext2 second extension
     * @param cardinalityMode if true, extensions are ranked by cardinality of BF results rather than subset relations
     * @return true if ext1 is strict superset of ext2, false otherwise (equal or subset)
     */
    private boolean isStrictlyBigger(Extension<DungTheory> ext1, Extension<DungTheory> ext2, boolean cardinalityMode){
        //flip set parameters
        return isStrictlySmaller(ext2,ext1,cardinalityMode);
    }


    /**
     * Takes a list of extensions, sorts them by use of a map and splits them into individual ranks.
     * @param extensions list of extensions to sort split into ranks
     * @return a list of ranks containing extensions
     */
    public List<List<Extension<DungTheory>>> getRanksFromList(List<Extension<DungTheory>> extensions){
        //put extensions in correct topological order list and partition it into respective ranks
        extensions = rankWithQueue(extensions);
        List<List<Extension<DungTheory>>> ranks = new ArrayList<>();
        List<Extension<DungTheory>> rank = new ArrayList<>();

        for(Extension<DungTheory> e: extensions){
            Character rankSign = getRankSign(rank,e);
            if(rankSign != null && rankSign == '>'){
                ranks.add(new ArrayList<>(rank));
                rank.clear();
            }
            rank.add(e);
        }
        ranks.add(new ArrayList<>(rank));
        return ranks;
    }

    /**
     * Sorts a list of extensions topologically.
     * Implementation is following Kahn's Algorithm, with map entries being interpreted as edges in a graph.
     * @param extensions list of extensions to sort
     * @return topologically sorted list of extensions
     */

    private List<Extension<DungTheory>> rankWithQueue(List<Extension<DungTheory>> extensions) {
        //implementation is done by following Kahn's algorithm so topologically sort extensions based on their rankings
        //ranking arrows ("<", ">") between two extensions can be interpreted as directed edges ("<--", "-->")

        //root of graph: extensions that have no parent/ are ranked worst .
        List<Extension<DungTheory>> queue = new ArrayList<>();
        List<Extension<DungTheory>> root = getRoot(extensions);
        for(Extension<DungTheory> r : root){
            queue.add(extensions.remove(extensions.indexOf(r)));
        }

        List<Extension<DungTheory>> result = new ArrayList<>();

        while (queue.size() > 0) {
            Extension<DungTheory> currentQueueElem = queue.remove(0);

            result.add(currentQueueElem);
            List<Extension<DungTheory>> queueElemChildren = getChildrenByRank(currentQueueElem);
            for (Extension<DungTheory> child : queueElemChildren) {

                if (!hasOtherParent(child, result)) {
                    queue.add(child);
                }
            }
        }
        return result;
    }

    /**
     * Returns the root elements for topological sorting. These are all subsets of arguments that have no less plausible rank in comparison.
     * @param extensions list of all subsets
     * @return list of least plausible arguments
     */
    private List<Extension<DungTheory>> getRoot(List<Extension<DungTheory>> extensions) {
        List<Extension<DungTheory>> root = new ArrayList<>();
        for(Extension<DungTheory> possibleRootElem : extensions) {
            if(!hasParent(possibleRootElem)){
                root.add(possibleRootElem);
            }
        }
        return root;
    }

    /**
     * Returns all children of an extension, or rather all extensions that are ranked better than the input extension.
     * @param node an extension
     * @return all extensions ranked better than node
     */
    private List<Extension<DungTheory>> getChildrenByRank(Extension<DungTheory> node){
        //get all children of input extension
        List<Extension<DungTheory>> children = new ArrayList<>();
        Set<List<Extension<DungTheory>>> comparisons = comparisonMap.keySet();
        for(List<Extension<DungTheory>> comparison : comparisons){
            if(comparison.contains(node)){
                int index = comparison.indexOf(node);
                Character sign = comparisonMap.get(comparison);
                if(index == 0 && sign != null && sign == '>'){
                    children.add(comparison.get(1));
                }
                else if(index == 1 && sign != null && sign == '<'){
                    children.add(comparison.get(0));
                }

            }
        }
        return children;
    }

    /**
     * true if there is any other parent that is not contained in the ignoreList.
     * false if it has no parents or all parents are contained in the ignorelist.
     * @param node an extension
     * @param ignoreList list of all extensions that are already removed from the queue and placed into the result in Kahn's algorithm
     * @return true if node has no further parents outside ignoreList
     */
    private boolean hasOtherParent(Extension<DungTheory> node, List<Extension<DungTheory>> ignoreList){
        //go through all extensions and check if it is a parent of node and not on ignoreList
        //if one such parent is found, return true
        Set<List<Extension<DungTheory>>> comparisons = comparisonMap.keySet();
        for(List<Extension<DungTheory>> comparison : comparisons){
            if(comparison.contains(node)){
                int index = comparison.indexOf(node);
                Character sign = comparisonMap.get(comparison);
                if(index == 0 && sign != null && sign == '<' && !ignoreList.contains(comparison.get(1))){
                    return true;
                }
                else if(index == 1 && sign != null && sign == '>' && !ignoreList.contains(comparison.get(0))){
                    return true;
                }

            }
        }
        return false;

    }

    /**
     * true if there is any parent to the input node.
     * false if it has no parent.
     * @param node an extension
     * @return true if node has a parent
     */
    private boolean hasParent(Extension<DungTheory> node){
        return hasOtherParent(node, new ArrayList<>());
    }



    /**
     * Returns the "strongest" sign for all extensions inside a rank compared to an extension,
     * i.e. if node is a child to any extension inside the rank, the whole rank is a parent to that extension.
     * If for all extensions in the rank it is either incomparable or equal, then it should be inside that rank later.
     *
     * This is used to partition the final order result into individual ranks.
     * @param compareToRank rank to compare node to
     * @param node an extension
     * @return the relationship of the rank to the node
     */
    private Character getRankSign(List<Extension<DungTheory>>compareToRank,Extension<DungTheory> node) {
        //compare an extension to a single rank
        //if there is at least one parent in rank, extension is "child of whole rank
        char bestSign = 'x';
        for (Extension<DungTheory> ext : compareToRank) {
            Character sign = getSign(ext, node);
            Character signRev = getSign(node, ext);
            if ((sign != null && sign == '>') || (signRev != null && signRev == '<')) {
                bestSign = '>';
                break;
            } else if ((sign != null && sign == '=') || (signRev != null && signRev == '=')) {
                bestSign = '=';
            }
        }
        return bestSign != 'x' ?bestSign :null;
    }


    /**
     * Returns the ordering sign between two extensions.
     * May require two calls, as signs are stored in <strong> one </strong> direction only.
     * @param ext1 extension on the left side
     * @param ext2 extension on the right side
     * @return the current sign between two extensions
     */
    private Character getSign(Extension<DungTheory> ext1, Extension<DungTheory> ext2){             
        List<Extension<DungTheory>> comparison = new ArrayList<>();
        comparison.add(ext1);
        comparison.add(ext2);
        return comparisonMap.get(comparison);
    }

    /**
     * Returns the corresponding base function methods for each ordering semantics in their lexicographic order.
     * @param semantics an ordering semantic
     * @return the corresponding method
     * @throws NoSuchMethodException should never happen
     */
    private LinkedList<Method> getCompareMethods(ExtensionRankingSemantics semantics) throws IllegalArgumentException, NoSuchMethodException {

        //add base functions in lexicographic order depending on semantic
        LinkedList<Method> methods = new LinkedList<>();


        switch (semantics) {
            case R_CF -> methods.add(ExtensionRankingReasoner.class.getMethod("getConflicts", Extension.class, DungTheory.class));
            case R_AD, R_PR -> {
                methods.add(ExtensionRankingReasoner.class.getMethod("getConflicts", Extension.class, DungTheory.class));
                methods.add(ExtensionRankingReasoner.class.getMethod("getUndefended", Extension.class, DungTheory.class));
            }
            case R_CO, R_GR-> { // , R_CO_PR   !!!!!!!!!!!!!!!
                methods.add(ExtensionRankingReasoner.class.getMethod("getConflicts", Extension.class, DungTheory.class));
                methods.add(ExtensionRankingReasoner.class.getMethod("getUndefended", Extension.class, DungTheory.class));
                methods.add(ExtensionRankingReasoner.class.getMethod("getDefendedNotIn", Extension.class, DungTheory.class));
            }
            case R_SST -> {
                methods.add(ExtensionRankingReasoner.class.getMethod("getConflicts", Extension.class, DungTheory.class));
                methods.add(ExtensionRankingReasoner.class.getMethod("getUndefended", Extension.class, DungTheory.class));
                methods.add(ExtensionRankingReasoner.class.getMethod("getDefendedNotIn", Extension.class, DungTheory.class));
                methods.add(ExtensionRankingReasoner.class.getMethod("getUnattacked", Extension.class, DungTheory.class));
            }
            default -> throw new IllegalArgumentException("Unknown semantics.");
        }
        return methods;

    }

    /**
     * Computes the set of conflicts occurring inside ext.
     * @param ext an extension
     * @param theory a dung theory
     * @return set of conflict in ext
     */
    public Extension<DungTheory> getConflicts(Extension<DungTheory> ext, DungTheory theory) {
        Extension<DungTheory> conflicts = new Extension<>();
        for (Attack att: theory.getAttacks()) {
            if (ext.contains(att.getAttacker()) && ext.contains(att.getAttacked())) {
                conflicts.add(new Argument(att.toString()));
            }
        }
        return conflicts;
    }

    /**
     * Computes the set of arguments in ext, which are not defended by ext against outside attackers.
     * @param ext an extension
     * @param theory a dung theory
     * @return set of arguments in ext which are not defended by ext
     */
    public Extension<DungTheory> getUndefended(Extension<DungTheory> ext, DungTheory theory) {
        Extension<DungTheory> undefended = new Extension<>();
        for (Argument arg: ext) {
            for (Argument attacker: theory.getAttackers(arg)) {
                if (!ext.contains(attacker) && !theory.isAttacked(attacker, ext)) {
                    undefended.add(arg);
                    break;
                }
            }
        }
        return undefended;
    }

    /**
     * Computes the set of arguments outside of ext, which are not attacked by ext.
     * @param ext an extension
     * @param theory a dung theory
     * @return set of arguments in theory \ ext which are not attacked by ext
     */
    public Extension<DungTheory> getUnattacked(Extension<DungTheory> ext, DungTheory theory) {
        Extension<DungTheory> unattacked = new Extension<>();
        for (Argument arg: theory) {
            if (!ext.contains(arg) && !theory.isAttacked(arg, ext)) {
                unattacked.add(arg);
            }
        }
        return unattacked;
    }

    /**
     * Computes the set of arguments outside of ext, which are defended by ext.
     * @param ext an extension
     * @param theory a dung theory
     * @return set of arguments in theory \ ext which are defended by ext
     */
    public Extension<DungTheory> getDefendedNotIn(Extension<DungTheory> ext, DungTheory theory) {

        //calculate attackers of extension
        Extension<DungTheory> extMinus = new Extension<>();
        for(Argument arg:ext){
            extMinus.addAll(theory.getAttackers(arg));
        }


        //calculate fafStar recursively
        Extension<DungTheory> fafStar = new Extension<>(ext);
        boolean hasChanged = true;
        while(hasChanged){

            //calculate faf of fafStar
            Extension<DungTheory> fafFafStar = theory.faf(fafStar);
            //execute formula
            Extension<DungTheory> newFafStar= new Extension<>(fafStar);
            newFafStar.addAll(fafFafStar);
            newFafStar.removeAll(extMinus);
            //check if further iterations necessary
            if(newFafStar.equals(fafStar)) {
                hasChanged = false;
            }
            else{
                fafStar = new Extension<>(newFafStar);
            }
        }
        //finalize
        Extension<DungTheory> defendedNotIn = fafStar;
        defendedNotIn.removeAll(ext);
        return defendedNotIn;


    }
    /**
     * This method always returns true because the solver is native
     */
	public boolean isInstalled() {
		return true;
	}
}