/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.eaf.syntax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Labeling;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.ml.parser.MlParser;
import org.tweetyproject.logics.ml.reasoner.SimpleMlReasoner;
import org.tweetyproject.logics.ml.syntax.MlBeliefSet;
import org.tweetyproject.logics.ml.syntax.MlFormula;
import org.tweetyproject.logics.ml.syntax.Necessity;
import org.tweetyproject.logics.ml.syntax.Possibility;
import org.tweetyproject.logics.fol.syntax.Conjunction;
import org.tweetyproject.logics.fol.syntax.Disjunction;
import org.tweetyproject.logics.fol.syntax.Equivalence;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.Negation;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.fol.syntax.Implication;
import org.tweetyproject.logics.fol.syntax.Tautology;


/**
 * This class implements an epistemic abstract argumentation theory (EAF) using modal logic formulas.
 * <br>
 * <br>See
 * <br>
 * <br>Sakama, Chiaki, and Tran Cao Son. "Epistemic argumentation framework: Theory and computation." Journal of Artificial Intelligence Research 69 (2020): 1103-1126.
 * 
 * @author Sandra Hoffmann
 *
 */
public class EpistemicArgumentationFramework extends DungTheory{
	
	private FolFormula constraint;	
	private FolSignature sig = new FolSignature();
	private MlParser parser = new MlParser();
	

	/**
	 * default constructor.
	 */
	public EpistemicArgumentationFramework() {
		super();
		this.constraint = new Possibility(new Tautology());
	}
	
	/**
	 * Constructor for an EAF from a graph. 
	 * 
	 * @param graph A graph representing the structure of an argumentation framework.
	 */
	public EpistemicArgumentationFramework(Graph<Argument> graph) {
		super(graph);
		this.constraint = new Possibility(new Tautology());
	}
	
	/**
	 * Constructor for an EAF using a given graph and constraint.
	 *
	 * The constraint is provided as a string and parsed into an epistemic formula.
	 * For example, a valid epistemic string is: "<>(c)=>[](a)".
	 *
	 * Note: The parser does not support negated modal operators directly. 
	 * If you need to use them, please convert as follows:
	 *   - Convert "¬M φ" to "K ¬φ".
	 *   - Convert "¬K φ" to "M ¬φ".
	 *
	 * @param graph      A graph representing the structure of the argumentation framework.
	 * @param constraint A string representing the epistemic constraint.
	 */
	public EpistemicArgumentationFramework(Graph<Argument> graph, String constraint) {
		super(graph);
		setConstraint(constraint);
	}
	
	
	
	/**
	 * Constructor for an EAF from a graph and a modal logic formula as the constraint.
	 * 
	 * @param graph A graph representing the structure of arguments and their relations.
	 * @param constraint A modal logic formula representing the constraint.
	 */
	public EpistemicArgumentationFramework(Graph<Argument> graph, MlFormula constraint) {
		super(graph);
		setConstraint(constraint);
	}
	
	
	/**
	 * Sets a new epistemic formula as the constraint of EAF.
	 * 
	 * @param constraint A modal logic formula to be set as the new epistemic constraint.
	 * @return true when the constraint is successfully set.
	 */
	public boolean setConstraint(MlFormula constraint) {
		this.constraint = convertToDnf(constraint);
		return true;
	}
	
	
	/**
	 * Sets a new constraint by parsing a string representing an epistemic formula.
	 * 
	 * The constraint is provided as a string and parsed into an epistemic formula.
	 * For example, a valid epistemic string is: "<>(c)=>[](a)".
	 *
	 * Note: The parser does not support negated modal operators directly. 
	 * If you need to use them, please convert as follows:
	 *   - Convert "¬M φ" to "K ¬φ".
	 *   - Convert "¬K φ" to "M ¬φ".
	 *
	 * 
	 * @param constraint A string representing the epistemic constraint to be parsed.
	 * @return true if the constraint is successfully set, false if an error occurs during parsing.
	 */
	public boolean setConstraint(String constraint) {
		try {
			//get all arguments from graph to set signature for parser
			Collection<Argument> arguments = this.getNodes();
			for (Argument arg :arguments){
				sig.add(new Predicate(arg.getName(), 0));
			}
			parser.setSignature(sig);
			this.constraint = convertToDnf((FolFormula) parser.parseFormula(constraint));
			return true;
		} catch (ParserException | IOException e) {
			return false;
		}
	}
	
	
	/**
	 * Retrieves the current epistemic constraint of the framework.
	 * 
	 * @return The epistemic formula representing the current constraint.
	 */
	public FolFormula getConstraint() {
		return this.constraint;
	}
	
	/**
	 * Determines if the given labeling is a co-epistemic labeling.
	 * A labeling is co-epistemic if it is a complete labelling and satisfies the epistemic constraint.
	 *
	 * @param lab the labeling to check
	 * @return true if the labeling is co-epistemic, false otherwise
	 */
	public Boolean isCoEpistemicLabeling(Labeling lab){
		Extension<DungTheory> ext = (Extension<DungTheory>) lab.getArgumentsOfStatus(ArgumentStatus.IN);
		
		if (!this.isComplete(ext)) return false;
		return (satisfiesConstraint(ext)); 
	}
	
	/**
	 * Determines if the given set of labelings is a co-epistemic labeling Set.
	 * A co-epistemic labeling set is a maximal set of co-epistemic labelings that satisfy the constraint of the underlying AF
	 *
	 * @param labSet the set of labelings to check
	 * @return true if the set of labelings is a co-epistemic labeling set, false otherwise
	 */
	public Boolean isCoEpistemicLabelingSet(Set<Labeling> labSet){
		Collection<Extension<DungTheory>> extSet = new HashSet();
		for (Labeling lab: labSet) {
			Extension<DungTheory> ext = (Extension<DungTheory>) lab.getArgumentsOfStatus(ArgumentStatus.IN);
			if (!this.isComplete(ext)) return false;
			extSet.add(ext);
		}
		if(!satisfiesConstraint(createEpistemicKnowledgeBase(extSet))) return false; 
	    // Try to extend the set by one additional complete extension.
	    // Assume getCompleteExtensions() returns all complete extensions of this framework.
		
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.CO);
		Collection<Extension<DungTheory>> extensions = reasoner.getModels(this);
		return isMaximalEpistemicLabellingSet(extSet, extensions);
	}
	
	/**
	 * Determines if the given labeling is a st-epistemic labeling.
	 * A labeling is st-epistemic if it is a stable labelling and satisfies the epistemic constraint.
	 *
	 * @param lab the labeling to check
	 * @return true if the labeling is st-epistemic, false otherwise
	 */
	public Boolean isStEpistemicLabeling(Labeling lab){
		Extension<DungTheory> ext = (Extension<DungTheory>) lab.getArgumentsOfStatus(ArgumentStatus.IN);
		
		if (!this.isStable(ext)) return false;
		return (satisfiesConstraint(ext)); 
	}
	
	/**
	 * Determines if the given set of labelings is a st-epistemic labeling Set.
	 * A st-epistemic labeling set is a maximal set of st-epistemic labelings that satisfy the constraint of the underlying AF
	 *
	 * @param labSet the set of labelings to check
	 * @return true if the set of labelings is a st-epistemic labeling set, false otherwise
	 */
	public Boolean isStEpistemicLabelingSet(Set<Labeling> labSet){
		Collection<Extension<DungTheory>> extSet = new HashSet();
		for (Labeling lab: labSet) {
			Extension<DungTheory> ext = (Extension<DungTheory>) lab.getArgumentsOfStatus(ArgumentStatus.IN);
			if (!this.isStable(ext)) return false;
			extSet.add(ext);
		}
		if(!satisfiesConstraint(createEpistemicKnowledgeBase(extSet))) return false; 
	    // Try to extend the set by one additional complete extension.
	    // Assume getCompleteExtensions() returns all complete extensions of this framework.
		
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.ST);
		Collection<Extension<DungTheory>> extensions = reasoner.getModels(this);

		return isMaximalEpistemicLabellingSet(extSet, extensions);
	}
	
	
	private Boolean isMaximalEpistemicLabellingSet(Collection<Extension<DungTheory>>extSet, Collection<Extension<DungTheory>> extensions) {
	    for (Extension<DungTheory> candidateExt : extensions) {
	        // Skip if the candidate is already in our set.
	        if (extSet.contains(candidateExt)) {
	            continue;
	        }
	        
	        // Form the union of the current extensions with the candidate.
	        Set<Extension<DungTheory>> extendedSet = new HashSet<>(extSet);
	        extendedSet.add(candidateExt);
	        
	        // If the extended set satisfies the constraint, then labSet is not maximal.
	        if (satisfiesConstraint(createEpistemicKnowledgeBase(extendedSet))) {
	            return false;
	        }
	    }    
	    return true;
	}
	public Boolean isSatisfying(Semantics w) {
		MlBeliefSet mbs = getWEpistemicKnowledgeBase(w);
		SimpleMlReasoner mlReasoner = new SimpleMlReasoner();
		
		return mlReasoner.query(mbs, constraint); 
	}
	
	
	public MlBeliefSet getWEpistemicKnowledgeBase(Semantics w) {
		
		//get all complete Extensions
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(w);
		Collection<Extension<DungTheory>> extensions = reasoner.getModels(this);
		return createEpistemicKnowledgeBase(extensions);
	}
	
	private MlBeliefSet createEpistemicKnowledgeBase(Collection<Extension<DungTheory>> extensions) {
		MlBeliefSet beliefSet = new MlBeliefSet();
		// build Belief base from AF and ext, all Args in ext are set to true, all other args set to false
		for (Extension<DungTheory>ext : extensions) {
				beliefSet.add(new Conjunction(createFolFormula(ext)));
		}
		return beliefSet;
	}
	
	private MlBeliefSet createEpistemicKnowledgeBase(Extension<DungTheory> extension) {
		MlBeliefSet beliefSet = new MlBeliefSet();
		// build Belief base from AF and ext, all Args in ext are set to true, all other args set to false
		beliefSet.add(new Conjunction(createFolFormula(extension)));
		return beliefSet;
	}
	
	private Collection<FolFormula> createFolFormula(Extension<DungTheory> ext) {
		Collection<FolFormula> literals = new ArrayList<>();
		Iterator<Argument> it = this.iterator();
		while(it.hasNext()) {
			Argument arg = it.next();
			if (ext.contains(arg)){
				 literals.add(new FolAtom(new Predicate(arg.getName())));				
			} else {
				literals.add(new Negation(new FolAtom(new Predicate(arg.getName()))));				
			}
		}	
		return literals;
	}
	
	private Collection<FolFormula> createFolFormula(Labeling lab) {
		Extension<DungTheory> ext = (Extension<DungTheory>) lab.getArgumentsOfStatus(ArgumentStatus.IN);
		return createFolFormula(ext);
	}
	
	public boolean satisfiesConstraint(Labeling lab) {
		Extension<DungTheory> ext = (Extension<DungTheory>) lab.getArgumentsOfStatus(ArgumentStatus.IN);
		return satisfiesConstraint(ext);
	}
	
	public boolean satisfiesConstraint(Extension<DungTheory> ext) {
		MlBeliefSet kb = createEpistemicKnowledgeBase(ext);
	    // If formula is a disjunction, check if any disjunct is satisfied
	    if (this.constraint instanceof Disjunction) {
	        Disjunction disj = (Disjunction) this.constraint;
	        for (RelationalFormula disjunct : disj) {
	            if (satisfiesConjunction(kb, (Conjunction) disjunct)) {
	                return true;
	            }
	        }
	        return false;
	    }
	    // If formula is a single conjunction
	    else if (this.constraint instanceof Conjunction) {
	        return satisfiesConjunction(kb, (Conjunction) this.constraint);
	    }
	    // Handle single necessity/possibility
	    else {
	        return satisfiesModalFormula(kb, this.constraint);
	    }
	}
	
	
	public boolean satisfiesConstraint(MlBeliefSet beliefSet) {
	    // If formula is a disjunction, check if any disjunct is satisfied
	    if (this.constraint instanceof Disjunction) {
	        Disjunction disj = (Disjunction) this.constraint;
	        for (RelationalFormula disjunct : disj) {
	            if (disjunct instanceof Conjunction) {
	                if (satisfiesConjunction(beliefSet, (Conjunction) disjunct)) {
	                    return true;
	                }
	            } else {
	                // Handle non-conjunction disjuncts (like necessity formulas)
	                if (satisfiesModalFormula(beliefSet, disjunct)) {
	                    return true;
	                }
	            }
	        }
	        return false;
	    }
	    // If formula is a single conjunction
	    else if (this.constraint instanceof Conjunction) {
	        return satisfiesConjunction(beliefSet, (Conjunction) this.constraint);
	    }
	    // Handle single necessity/possibility
	    else {
	        return satisfiesModalFormula(beliefSet, this.constraint);
	    }
	}

	private boolean satisfiesConjunction(MlBeliefSet beliefSet, Conjunction conjunction) {
	    // For each conjunct in the conjunction
	    for (RelationalFormula conjunct : conjunction) {
	        if (!satisfiesModalFormula(beliefSet, conjunct)) {
	            return false;
	        }
	    }
	    return true;
	}

	private boolean satisfiesModalFormula(MlBeliefSet beliefSet, RelationalFormula formula) {
	    if (formula instanceof Necessity) {
	        // For []φ (necessity), φ must be true in all extensions
	        Necessity nec = (Necessity) formula;
	        FolFormula innerFormula = (FolFormula) nec.getFormula();
	        
	        for (RelationalFormula extension : beliefSet) {
	            if (!satisfiesClassicalFormula((FolFormula) extension, innerFormula)) {
	                return false;
	            }
	        }
	        return true;
	    }
	    else if (formula instanceof Possibility) {
	        // For <>φ (possibility), φ must be true in at least one extension
	        Possibility poss = (Possibility) formula;
	        FolFormula innerFormula = (FolFormula) poss.getFormula();
	        
	        for (RelationalFormula extension : beliefSet) {
	            if (satisfiesClassicalFormula((FolFormula) extension, innerFormula)) {
	                return true;
	            }
	        }
	        return false;
	    }
	    else if (formula instanceof Negation) {
	        // Handle negation according to Definition 1(iv)
	        Negation neg = (Negation) formula;
	        return !satisfiesModalFormula(beliefSet, neg.getFormula());
	    }
	    throw new IllegalArgumentException("Unsupported formula type: " + formula.getClass());
	}

	private boolean satisfiesClassicalFormula(FolFormula extension, FolFormula formula) {
	    if (formula instanceof FolAtom) {
	        // Check if atom is in the extension
	        return containsAtom(extension, (FolAtom) formula);
	    }
	    else if (formula instanceof Conjunction) {
	        // For conjunction, all parts must be satisfied
	        Conjunction conj = (Conjunction) formula;
	        for (RelationalFormula conjunct : conj) {
	            if (!satisfiesClassicalFormula(extension, (FolFormula) conjunct)) {
	                return false;
	            }
	        }
	        return true;
	    }
	    else if (formula instanceof Disjunction) {
	        // For disjunction, at least one part must be satisfied
	        Disjunction disj = (Disjunction) formula;
	        for (RelationalFormula disjunct : disj) {
	            if (satisfiesClassicalFormula(extension, (FolFormula) disjunct)) {
	                return true;
	            }
	        }
	        return false;
	    }
	    else if (formula instanceof Negation) {
	        // For negation, the formula must not be satisfied
	        Negation neg = (Negation) formula;
	        return !satisfiesClassicalFormula(extension, (FolFormula) neg.getFormula());
	    }
	    throw new IllegalArgumentException("Unsupported classical formula type: " + formula.getClass());
	}

	private boolean containsAtom(FolFormula extension, FolAtom atom) {
	    if (extension instanceof Conjunction) {
	        Conjunction conj = (Conjunction) extension;
	        for (RelationalFormula literal : conj) {
	            if (literal instanceof FolAtom) {
	                if (((FolAtom) literal).getPredicate().getName().equals(
	                    atom.getPredicate().getName())) {
	                    return true;
	                }
	            }
	        }
	    }
	    return false;
	}
	

	public Set<Collection<Labeling>> getWEpistemicLabellingSets(Semantics w) {
	    // Get all extensions
	    AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(w);
	    Collection<Extension<DungTheory>> extensions = reasoner.getModels(this);
	    
	    // Find maximal satisfying sets
	    Set<Set<Extension<DungTheory>>> maximalSets = findMaximalSatisfyingSets(new HashSet<>(extensions));
	    
	    // Convert to labeling sets
	    Set<Collection<Labeling>> labellingSets = new HashSet<>();
	    for(Set<Extension<DungTheory>> extensionSet : maximalSets) {
	        Set<Labeling> labSet = new HashSet<>();
	        for(Extension<DungTheory> ext : extensionSet) {
	            Labeling lab = new Labeling(this, ext);
	            labSet.add(lab);
	        }
	        labellingSets.add(labSet);
	    }
	    
	    return labellingSets;
	}

	private Set<Set<Extension<DungTheory>>> findMaximalSatisfyingSets(Set<Extension<DungTheory>> extensions) {
	    Set<Set<Extension<DungTheory>>> maximalSets = new HashSet<>();
	    
	    // Try the full set first
	    if (satisfiesConstraint(createEpistemicKnowledgeBase(extensions))) {
	        maximalSets.add(extensions);
	        return maximalSets;
	    }
	    
	    // If full set doesn't satisfy, try removing one extension at a time
	    findMaximalSetsRecursive(extensions, new HashSet<>(), maximalSets, new HashSet<>(extensions));
	    
	    return maximalSets;
	}

	private void findMaximalSetsRecursive(Set<Extension<DungTheory>> currentSet, 
            Set<Extension<DungTheory>> excluded,
            Set<Set<Extension<DungTheory>>> maximalSets,
            Set<Extension<DungTheory>> remaining) {
	// Base case: if there is an already found satisfying set that is a strict superset of currentSet, stop exploring.
	if (maximalSets.stream().anyMatch(existing -> 
	existing.containsAll(currentSet) && existing.size() > currentSet.size())) {
	return;
	}
	
	// Check if current set satisfies the constraint
	if (satisfiesConstraint(createEpistemicKnowledgeBase(currentSet))) {
	// Remove any previously found set that is a proper subset of currentSet.
	// (Note: We remove only those that are both smaller and are contained in currentSet.)
	maximalSets.removeIf(existing -> currentSet.containsAll(existing) && existing.size() < currentSet.size());
	
	// Add the current set only if it isn’t a subset of any existing satisfying set.
	boolean isSubsumed = maximalSets.stream().anyMatch(existing -> 
	      existing.containsAll(currentSet) && existing.size() > currentSet.size());
	if (!isSubsumed) {
		maximalSets.add(new HashSet<>(currentSet));
	}
	return;
	}
	
	// Continue exploring by trying to remove one extension at a time
	for (Extension<DungTheory> ext : remaining) {
		if (!excluded.contains(ext)) {
		Set<Extension<DungTheory>> newSet = new HashSet<>(currentSet);
		newSet.remove(ext);
		
		Set<Extension<DungTheory>> newExcluded = new HashSet<>(excluded);
		newExcluded.add(ext);
		
		Set<Extension<DungTheory>> newRemaining = new HashSet<>(remaining);
		newRemaining.remove(ext);
		
		findMaximalSetsRecursive(newSet, newExcluded, maximalSets, newRemaining);
		}
	}
	}

	
	
	// Misc methods
	
	private FolFormula convertToDnf(RelationalFormula formula) {
	    // First handle the initial transformations as before
	    if (formula instanceof Conjunction) {
	        Conjunction conj = (Conjunction) formula;
	        // Convert each conjunct to DNF first
	        List<RelationalFormula> dnfConjuncts = new ArrayList<>();
	        for (RelationalFormula f : conj) {
	            dnfConjuncts.add(convertToDnf(f));
	        }
	        
	        // Now distribute conjunction over disjunction
	        return distributeConjunctionOverDisjunction(dnfConjuncts);
	    }

	    if (formula instanceof Disjunction) {
	        Disjunction disj = (Disjunction) formula;
	        List<RelationalFormula> dnfDisjuncts = new ArrayList<>();
	        for (RelationalFormula f : disj) {
	            // Convert each disjunct to DNF and add all resulting disjuncts
	            FolFormula dnfForm = convertToDnf(f);
	            if (dnfForm instanceof Disjunction) {
	                dnfDisjuncts.addAll(((Disjunction) dnfForm).getFormulas());
	            } else {
	                dnfDisjuncts.add(dnfForm);
	            }
	        }
	        return new Disjunction(dnfDisjuncts);
	    }
	 // Handle base cases
        if (formula instanceof FolAtom || 
            formula instanceof Negation ||
            formula instanceof Possibility ||
            formula instanceof Necessity) {
            return (FolFormula) applyEpistemicTransformations(formula);
        }
        
        // Handle Implication
        if (formula instanceof Implication) {
            Implication impl = (Implication) formula;
            // Convert A => B to ¬A ∨ B
            RelationalFormula left = impl.getFormulas().getFirst();
            RelationalFormula right = impl.getFormulas().getSecond();
            
            // Create ¬A
            RelationalFormula negatedLeft;
            if (left instanceof Possibility) {
                Possibility poss = (Possibility) left;
                negatedLeft = new Negation(poss);
            } else if (left instanceof Necessity) {
                Necessity nec = (Necessity) left;
                negatedLeft = new Negation(nec);
            } else {
                negatedLeft = new Negation((FolFormula)left);
            }
            
            // Create ¬A ∨ B
            List<RelationalFormula> disjuncts = new ArrayList<>();
            disjuncts.add(applyEpistemicTransformations(negatedLeft));
            disjuncts.add(applyEpistemicTransformations(right));
            return (FolFormula) new Disjunction(disjuncts);
        }
        
        // Handle Equivalence
        if (formula instanceof Equivalence) {
            Equivalence equiv = (Equivalence) formula;
            // Convert A <=> B to (A => B) ∧ (B => A)
            RelationalFormula left = equiv.getFormulas().getFirst();
            RelationalFormula right = equiv.getFormulas().getSecond();
            
            // Create first implication A => B
            Implication impl1 = new Implication(left, right);
            
            // Create second implication B => A
            Implication impl2 = new Implication(right, left);
            
            // Combine with conjunction and convert recursively
            List<RelationalFormula> conjuncts = new ArrayList<>();
            conjuncts.add(convertToDnf(impl1));
            conjuncts.add(convertToDnf(impl2));
            return (FolFormula) new Conjunction(conjuncts);
        }
        return (FolFormula) formula;
	}

	private FolFormula distributeConjunctionOverDisjunction(List<RelationalFormula> conjuncts) {
	    // Base case: if only one conjunct, return it
	    if (conjuncts.size() == 1) {
	        return (FolFormula) conjuncts.get(0);
	    }

	    // Get all disjuncts from first conjunct
	    List<RelationalFormula> firstConjunctDisjuncts = new ArrayList<>();
	    if (conjuncts.get(0) instanceof Disjunction) {
	        firstConjunctDisjuncts.addAll(((Disjunction) conjuncts.get(0)).getFormulas());
	    } else {
	        firstConjunctDisjuncts.add(conjuncts.get(0));
	    }

	    // Recursively handle rest of conjuncts
	    FolFormula restDnf = distributeConjunctionOverDisjunction(
	        conjuncts.subList(1, conjuncts.size()));

	    // Distribute: (a ∨ b) ∧ (c ∨ d) = (a ∧ c) ∨ (a ∧ d) ∨ (b ∧ c) ∨ (b ∧ d)
	    List<RelationalFormula> resultDisjuncts = new ArrayList<>();
	    for (RelationalFormula first : firstConjunctDisjuncts) {
	        if (restDnf instanceof Disjunction) {
	            for (RelationalFormula rest : ((Disjunction) restDnf)) {
	                resultDisjuncts.add(new Conjunction(Arrays.asList(first, rest)));
	            }
	        } else {
	            resultDisjuncts.add(new Conjunction(Arrays.asList(first, restDnf)));
	        }
	    }

	    return new Disjunction(resultDisjuncts);
	}
	
	
	private Set<Set<Extension<DungTheory>>> generatePowerSet(Collection<Extension<DungTheory>> extensions) {
	    Set<Set<Extension<DungTheory>>> powerSet = new HashSet<>();
	    powerSet.add(new HashSet<>()); // empty set

	    for (Extension<DungTheory> ext : extensions) {
	        Set<Set<Extension<DungTheory>>> newSets = new HashSet<>();
	        for (Set<Extension<DungTheory>> set : powerSet) {
	            Set<Extension<DungTheory>> newSet = new HashSet<>(set);
	            newSet.add(ext);
	            newSets.add(newSet);
	        }
	        powerSet.addAll(newSets);
	    }
	    return powerSet;
	}
	
	
	 private static RelationalFormula applyEpistemicTransformations(RelationalFormula formula) {
	        // Proposition 1 transformations
	        
	        // (i) ¬M φ ↔ K ¬φ
	        if (formula instanceof Negation && 
	            ((Negation)formula).getFormula() instanceof Possibility) {
	            Possibility poss = (Possibility)((Negation)formula).getFormula();
	            return new Necessity(new Negation((FolFormula)poss.getFormula()));
	        }
	        
	        // (ii) ¬K φ ↔ M ¬φ
	        if (formula instanceof Negation && 
	            ((Negation)formula).getFormula() instanceof Necessity) {
	            Necessity nec = (Necessity)((Negation)formula).getFormula();
	            return new Possibility(new Negation((FolFormula)nec.getFormula()));
	        }
	        
	        // (iii) M(φ ∨ ψ) ↔ M φ ∨ M ψ
	        if (formula instanceof Possibility && 
	            ((Possibility)formula).getFormula() instanceof Disjunction) {
	            Disjunction disj = (Disjunction)((Possibility)formula).getFormula();
	            List<RelationalFormula> newDisjuncts = new ArrayList<>();
	            for (RelationalFormula f : disj) {
	                newDisjuncts.add(new Possibility(f));
	            }
	            return new Disjunction(newDisjuncts);
	        }
	        
	        // (iv) K(φ ∧ ψ) ↔ K φ ∧ K ψ
	        if (formula instanceof Necessity && 
	            ((Necessity)formula).getFormula() instanceof Conjunction) {
	            Conjunction conj = (Conjunction)((Necessity)formula).getFormula();
	            List<RelationalFormula> newConjuncts = new ArrayList<>();
	            for (RelationalFormula f : conj) {
	                newConjuncts.add(new Necessity(f));
	            }
	            return new Conjunction(newConjuncts);
	        }
	        
	        return formula;
	    }

	
		/** Pretty print of the theory.
		 * @return the pretty print of the theory.
		 */
		public String prettyPrint(){
			String output = new String();
			Iterator<Argument> it = this.iterator();
			while(it.hasNext())
				output += "argument("+it.next().toString()+").\n";
			output += "\n";
			Iterator<Attack> it2 = this.getAttacks().iterator();
			while(it2.hasNext())
				output += "attack"+it2.next().toString()+".\n";
			
			output += "\nconstraint: " + this.constraint.toString();
			return output;
		}
		
		
	    /**
	     * Generates a string representation of the framework.
	     *
	     * @return The string representation of the framework.
	     */
	    @Override
	    public String toString() {
	        return "(" + super.toString() + "," + this.constraint + ")";
	    }

}
