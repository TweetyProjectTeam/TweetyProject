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

package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.ExtensionRankingSemantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.SetTools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * Reasoner for ordering semantics
 *
 * @author Lars Bengel
 * @author Daniel Letkemann
 */
public class ExtensionRankingReasoner {
    private final ExtensionRankingSemantics semantics;
    private final List<Method> baseFunctions;
    private Map<List<Extension<DungTheory>>, Character> comparisonMap;
//    public final  Extension debug = new Extension();
//    public final  Extension debug2 = new Extension();


    /**
     * create a reasoner for the given ordering semantics
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
     *get the semantic of OrderingSemanticsReasoner instance
     * @return OrderingSemantics enum that was used for creating the reasoner
     */
    public ExtensionRankingSemantics getSemantics(){
        return this.semantics;
    }


    /**
     * returns the lowest rank of the "getModels" method
     * @param theory a dung theory
     * @return the list of extensions in the best rank
     * @throws InvocationTargetException should never happen
     * @throws IllegalAccessException should never happen
     */
    public List<Extension<DungTheory>> getModel(DungTheory theory) throws InvocationTargetException, IllegalAccessException{
        //return the best rank
        List<List<Extension<DungTheory>>> ranks = getModels(theory);
        return  ranks.get(ranks.size()-1);
    }/**
     * compute the ordering over all subsets of theory wrt. to the ordering semantics
     * this is done by making several comparisons (in lexicographic order) of Base Functions applied to Extensions
     * this implies a total order that is sorted and split into ranks in the end.
     * LOWER levels are ranked BETTER (Ascending order)
     * !!!two extensions in one rank are either equally ranked or incomparable.
     * !!!thus an extension placed in a better rank does not necessarily mean it is better than all the extensions in the rank below
     * @param theory a dung theory
     * @return a list representing the ordering of all subsets of the given graph wrt. the ordering semantics (ascending order)
     * @throws InvocationTargetException should never happen
     * @throws IllegalAccessException should never happen
     */
    public List<List<Extension<DungTheory>>> getModels(DungTheory theory) throws InvocationTargetException, IllegalAccessException {
        //returns partitioned list of ranked extensions partitioned into ranks
        Set<Set<Argument>> subsets = new SetTools<Argument>().subsets(theory);

        List<Extension<DungTheory>> extensions = new LinkedList<>();
        for(Set<Argument> set : subsets){
            Extension<DungTheory> ext = new Extension<DungTheory>(set);
            extensions.add(ext);
        }
        Collections.reverse(extensions);
        //puts the extension containing all arguments at the first index, root of ranks-graph

        this.comparisonMap = getComparisonSigns(extensions,theory);
        return getRanksFromList(extensions);
    }

    /**
     * gets the correct comparison sign for two extensions by using the base functions in correct order
     * (lexicographic) if two sets are equal reg. base function -> use next one in the base function order
     * !!! if ext1 (sign) ext2 is stored in the map, then ext2 (sign) ext1 is not
     * @param argumentSubsets sets to get all comparisons of
     * @param theory attacks/defenses used for base function calculations
     * @return map of comparison signs for a list of two extensions
     * @throws InvocationTargetException should never happen
     * @throws IllegalAccessException should never happen
     */
    private Map<List<Extension<DungTheory>>,Character> getComparisonSigns(List<Extension<DungTheory>> argumentSubsets, DungTheory theory) throws InvocationTargetException, IllegalAccessException {
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
                putCompareSignInMap(comparison, subsetToSignMap,theory);
            }
        }
        return subsetToSignMap;
    }

    /**
     * "iteratively" calculate base functions of two extensions and compare the outcome,
     * if they are equal, recursively calculate sign for the next base function for the corresponding semantic
     * @param comparison list of TWO extensions, first index of list corresponds to extension on the "left"
     * @param map map to store comparison sign for comparison
     * @param theory attacks/defenses for base function calculations
     * @throws InvocationTargetException should never happen
     * @throws IllegalAccessException should never happen
     */
    private void putCompareSignInMap(ArrayList<Extension<DungTheory>> comparison,Map<List<Extension<DungTheory>>,Character> map, DungTheory theory) throws InvocationTargetException, IllegalAccessException {
        putCompareSignInMap(comparison, map, theory, 0);
    }private void putCompareSignInMap(ArrayList<Extension<DungTheory>> comparison, Map<List<Extension<DungTheory>>, Character> map, DungTheory theory, int iteration) throws InvocationTargetException, IllegalAccessException {
        if(iteration == baseFunctions.size()){
            //no more baseFunctions:
            // if preferred or grounded semantic:
            //  compare subsets themselves

            Extension<DungTheory> cs1 = new Extension<DungTheory>(comparison.get(0));
            Extension<DungTheory> cs2 = new Extension<DungTheory>(comparison.get(1));
            switch(semantics) {
                case R_GR:
                    if (isStrictSubsetOf(cs1, cs2)){
                        map.put(comparison, '<');
                    } else if (isStrictSupersetOf(cs1, cs2)) {
                        map.put(comparison, '>');
                    }
                    else{
                        map.put(comparison,null);
                    }
                    break;
                case R_PR:
                    if (isStrictSubsetOf(cs1, cs2)) {
                        map.put(comparison, '>');
                    } else if (isStrictSupersetOf(cs1, cs2)) {
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
            if (isStrictSubsetOf(cs1, cs2)) {
                map.put(comparison, '<');
            } else if (isStrictSupersetOf(cs1, cs2)) {
                map.put(comparison, '>');
            } else if (cs1.equals(cs2)) {
                //recurse
                map.put(comparison, '=');
                putCompareSignInMap(comparison, map, theory, iteration + 1);
            }
            else{
                map.put(comparison, null);
            }

        }
    }

    /**
     * true if ext1 < ext2
     * @param ext1 first extension
     * @param ext2 second extension
     * @return true if ext1 is strict subset of ext2, false otherwise (equal or superset)
     */
    private boolean isStrictSubsetOf(Extension<DungTheory> ext1, Extension<DungTheory> ext2){
        Set<Argument> subset = new HashSet<>(ext1);
        Set<Argument> superset = new HashSet<>(ext2);
        // subset is a strict-subset of superset iff the union of the compare sets equals the compare set of subset and not superset
        Collection<Argument> union = new HashSet<>(subset);
        union.addAll(superset);
        return(!union.equals(subset) && union.equals(superset));
    }
    /**
     * true if ext1 > ext2
     * @param ext1 first extension
     * @param ext2 second extension
     * @return true if ext1 is strict superset of ext2, false otherwise (equal or subset)
     */
    private boolean isStrictSupersetOf(Extension<DungTheory> ext1, Extension<DungTheory> ext2){
        return isStrictSubsetOf(ext2,ext1);
    }


    /**
     * takes a list of extensions, sorts them by use of a map and splits them into individual ranks
     * @param extensions list of extensions to sort split into ranks
     * @return a list of ranks containing extensions
     */
    private List<List<Extension<DungTheory>>> getRanksFromList(List<Extension<DungTheory>> extensions){
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
     * sorts a list of extensions topologically.
     * Adjusted Kahn Algorithm
     * Map entries interpreted as Graph edges
     * @param extensions list of extensions to sort
     * @return topologically sorted list of extensions
     */

    private List<Extension<DungTheory>> rankWithQueue(List<Extension<DungTheory>> extensions) {

        //implementation is done by following Kahn's algorithm so topologically sort extensions based on their rankings
        //ranking arrows ("<", ">") between two extensions can be interpreted as directed edges ("<--", "-->")

        //root of graph: extension with all arguments, ALWAYS ranked worst
        //make sure it is the root element.
        List<Extension<DungTheory>> queue = new ArrayList<>();
        Extension<DungTheory> root = extensions.get(0);
        for (Extension<DungTheory> e:extensions){
            if(e.size()>root.size()){
                root = e;
            }
        }
        queue.add(extensions.remove(extensions.indexOf(root)));

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
     * returns all children of node
     * simply all extensions that are ranked better than node
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
     * true if there is any other parent that is not on the ignoreList
     * false if all parents are in the ignorelist
     * @param node an extension
     * @param result list of all extensions that are already removed from the queue and placed into the result in Kahn's algorithm
     * @return true if node has no further parents outside ignoreList
     */
    private boolean hasOtherParent(Extension<DungTheory> node, List<Extension<DungTheory>> result){
        //go through all extensions and check if it is a parent of node
        //if one such parent is found, return true
        Set<List<Extension<DungTheory>>> comparisons = comparisonMap.keySet();
        for(List<Extension<DungTheory>> comparison : comparisons){
            if(comparison.contains(node)){
                int index = comparison.indexOf(node);
                Character sign = comparisonMap.get(comparison);
                if(index == 0 && sign != null && sign == '<' && !result.contains(comparison.get(1))){
                    return true;
                }
                else if(index == 1 && sign != null && sign == '>' && !result.contains(comparison.get(0))){
                    return true;
                }

            }
        }
        return false;

    }

    /**
     * returns the "strongest" sign for all extensions inside a rank compared to  node
     * i.e. if node is a child to any extension inside the rank, the whole rank is a parent to node
     * if for all extensions in the rank it is either incomparable or equal, then it should be inside that rank later
     *
     * used to partition the final order result into individual ranks
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
     * get the ordering sign between two extensions
     * !!! may require two calls, as signs are stored in one direction only
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
     * return the corresponding base function methods for each ordering semantics in their lexicographic order
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
            case R_CO, R_GR-> {
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
     * computes the set of conflicts occurring inside ext
     * @param ext an extension
     * @param theory a dung theory
     * @return set of conflict in ext
     */
    public Extension<DungTheory> getConflicts(Extension<DungTheory> ext, DungTheory theory) {
        Extension<DungTheory> conflicts = new Extension<DungTheory>();
        for (Attack att: theory.getAttacks()) {
            if (ext.contains(att.getAttacker()) && ext.contains(att.getAttacked())) {
                conflicts.add(new Argument(att.toString()));
            }
        }
        return conflicts;
    }

    /**
     * computes the set of arguments in ext, which are not defended by ext against outside attackers
     * @param ext an extension
     * @param theory a dung theory
     * @return set of arguments in ext which are not defended by ext
     */
    public Extension<DungTheory> getUndefended(Extension<DungTheory> ext, DungTheory theory) {
        Extension<DungTheory> undefended = new Extension<DungTheory>();
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
     * computes the set of arguments outside of ext, which are not attacked by ext
     * @param ext an extension
     * @param theory a dung theory
     * @return set of arguments in theory \ ext which are not attacked by ext
     */
    public Extension<DungTheory> getUnattacked(Extension<DungTheory> ext, DungTheory theory) {
        Extension<DungTheory> unattacked = new Extension<DungTheory>();
        for (Argument arg: theory) {
            if (!ext.contains(arg) && !theory.isAttacked(arg, ext)) {
                unattacked.add(arg);
            }
        }
        return unattacked;
    }

    /**
     * computes the set of arguments outside of ext, which are defended by ext
     * @param ext an extension
     * @param theory a dung theory
     * @return set of arguments in theory \ ext which are defended by ext
     */
    public Extension<DungTheory> getDefendedNotIn(Extension<DungTheory> ext, DungTheory theory) {

        //calculate attackers of extension
        Extension<DungTheory> extMinus = new Extension<DungTheory>();
        for(Argument arg:ext){
            extMinus.addAll(theory.getAttackers(arg));
        }


        //calculate fafStar recursively
        Extension<DungTheory> fafStar = new Extension<DungTheory>(ext);
        boolean hasChanged = true;
        while(hasChanged){

            //calculate faf of fafStar
            Extension<DungTheory> fafFafStar = theory.faf(fafStar);
            //execute formula
            Extension<DungTheory> newFafStar= new Extension<DungTheory>(fafStar);
            newFafStar.addAll(fafFafStar);
            newFafStar.removeAll(extMinus);
            //check if further iterations necessary
            if(newFafStar.equals(fafStar)) {
                hasChanged = false;
            }
            else{
                fafStar = new Extension<DungTheory>(newFafStar);
            }
        }
        //finalize
        Extension<DungTheory> defendedNotIn = fafStar;
        defendedNotIn.removeAll(ext);
        return defendedNotIn;


    }
    /**
     * this method always returns true because the solver is native
     */
	public boolean isInstalled() {
		return true;
	}
}
