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
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.commons.syntax.Sort;
import org.tweetyproject.logics.commons.syntax.interfaces.Atom;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.ml.parser.MlParser;
import org.tweetyproject.logics.ml.syntax.MlBeliefSet;
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
	 * For example, a valid epistemic string is: "<>(in(c))=>[](in(a))".
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
	public EpistemicArgumentationFramework(Graph<Argument> graph, FolFormula constraint) {
		super(graph);
		setConstraint(constraint);
	}
	
	
	/**
	 * Sets a new epistemic formula as the constraint of EAF.
	 * 
	 * @param constraint A modal logic formula to be set as the new epistemic constraint.
	 * @return true when the constraint is successfully set.
	 */
	public boolean setConstraint(FolFormula constraint) {
		this.constraint = convertToDnf(constraint);
		return true;
	}
	
	
	/**
	 * Sets a new epistemic constraint by parsing a string representation of a formula.
	 * The constraint string is converted disjunctive normal form (DNF).
	 * 
	 * Note: The parser does not support negated modal operators directly. 
	 * If you need to use them, please convert as follows:
	 *   - Convert "¬M φ" to "K ¬φ".
	 *   - Convert "¬K φ" to "M ¬φ".
	 *
	 * @param constraint the constraint string to parse (e.g., "K(in(a)) ∨ K(out(a))")
	 * @return true if the constraint was successfully parsed and set; false if parsing fails
	 */

	public boolean setConstraint(String constraint) {
	    try {
	        // Get all arguments from the graph to set the signature for the parser
	        Collection<Argument> arguments = this.getNodes();
	        Set<String> validArgumentNames = new HashSet<>();

	        for (Argument arg : arguments) {
	            String argName = arg.getName();
	            validArgumentNames.add(argName);

	            Constant constant = new Constant(argName);
	            sig.add(constant);
	        }

	        // Add modal label predicates (in, out, und)
	        sig.add(new Predicate("in", 1));
	        sig.add(new Predicate("out", 1));
	        sig.add(new Predicate("und", 1));

	        parser.setSignature(sig);

	        // Parse and convert to DNF
	        this.constraint = convertToDnf((FolFormula) parser.parseFormula(constraint));
	        return true;

	    } catch (ParserException | IOException e) {
	    	System.out.println(("Failed to parse constraint string: \"" + constraint + "\". Reason: " + e.getMessage()));
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
		if(!satisfiesConstraint(createEpistemicKnowledgeBaseFromExtensions(extSet))) return false; 
	    // Check whether the given labeling is maximal
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
		if(!satisfiesConstraint(createEpistemicKnowledgeBaseFromExtensions(extSet))) return false; 
	    // Check whether the given labeling is maximal
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.ST);
		Collection<Extension<DungTheory>> extensions = reasoner.getModels(this);

		return isMaximalEpistemicLabellingSet(extSet, extensions);
	}
	
	/**
	 * Determines if the given labeling is a gr-epistemic labeling.
	 * A labeling is gr-epistemic if it is a grounded labelling and satisfies the epistemic constraint.
	 *
	 * @param lab the labeling to check
	 * @return true if the labeling is gr-epistemic, false otherwise
	 */
	public Boolean isGrEpistemicLabeling(Labeling lab){
		Extension<DungTheory> ext = (Extension<DungTheory>) lab.getArgumentsOfStatus(ArgumentStatus.IN);
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.GR);
		Collection<Extension<DungTheory>> extensions = reasoner.getModels(this);
		
	    if (!extensions.contains(ext)) return false;
		return (satisfiesConstraint(ext)); 
	}
	
	/**
	 * Determines if the given set of labelings is a gr-epistemic labeling Set.
	 * A gr-epistemic labeling set is a maximal set of gr-epistemic labelings that satisfy the constraint of the underlying AF
	 *
	 * @param labSet the set of labelings to check
	 * @return true if the set of labelings is a gr-epistemic labeling set, false otherwise
	 */
	public Boolean isGrEpistemicLabelingSet(Set<Labeling> labSet){
	    if (labSet.size() != 1) {
	        return false;
	    }
		for (Labeling lab: labSet) {
			return isGrEpistemicLabeling(lab);
		}
		//grounded extension is unique so we should not end up here
		return false;
	}
	
	/**
	 * Determines if the given labeling is a pr-epistemic labeling.
	 * A labeling is pr-epistemic if it is a preferred labelling and satisfies the epistemic constraint.
	 *
	 * @param lab the labeling to check
	 * @return true if the labeling is pr-epistemic, false otherwise
	 */
	public Boolean isPrEpistemicLabeling(Labeling lab){
		Extension<DungTheory> ext = (Extension<DungTheory>) lab.getArgumentsOfStatus(ArgumentStatus.IN);
		
		if (!this.isPreferred(ext)) return false;
		return (satisfiesConstraint(ext)); 
	}
	
	/**
	 * Determines if the given set of labelings is a pr-epistemic labeling Set.
	 * A pr-epistemic labeling set is a maximal set of pr-epistemic labelings that satisfy the constraint of the underlying AF
	 *
	 * @param labSet the set of labelings to check
	 * @return true if the set of labelings is a pr-epistemic labeling set, false otherwise
	 */
	public Boolean isPrEpistemicLabelingSet(Set<Labeling> labSet){
		Collection<Extension<DungTheory>> extSet = new HashSet();
		for (Labeling lab: labSet) {
			Extension<DungTheory> ext = (Extension<DungTheory>) lab.getArgumentsOfStatus(ArgumentStatus.IN);
			if (!this.isPreferred(ext)) return false;
			extSet.add(ext);
		}
		if(!satisfiesConstraint(createEpistemicKnowledgeBaseFromExtensions(extSet))) return false; 
	    // Check whether the given labeling is maximal
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.PR);
		Collection<Extension<DungTheory>> extensions = reasoner.getModels(this);

		return isMaximalEpistemicLabellingSet(extSet, extensions);
	}
	
	
	private Boolean isMaximalEpistemicLabellingSet(Collection<Extension<DungTheory>>extSet, Collection<Extension<DungTheory>> extensions) {
	    for (Extension<DungTheory> candidateExt : extensions) {
	        if (extSet.contains(candidateExt)) {
	            continue;
	        }
	        
	        //union of the current extensions with the candidate.
	        Collection<Extension<DungTheory>> extendedSet = new HashSet<>(extSet);
	        extendedSet.add(candidateExt);
	        
	        // If the extended set satisfies the constraint, then labSet is not maximal.
	        if (satisfiesConstraint(createEpistemicKnowledgeBaseFromExtensions(extendedSet))) {
	            return false;
	        }
	    }    
	    return true;
	}
	
	
	private MlBeliefSet createEpistemicKnowledgeBaseFromExtensions(Collection<Extension<DungTheory>> extensions) {
	    // Convert to labeling sets
	    Set<Labeling> labelings = new HashSet<>();
	    for(Extension<DungTheory> ext : extensions) {
            Labeling lab = new Labeling(this, ext);
            labelings.add(lab);
	    }
		
		return createEpistemicKnowledgeBase(labelings);
	}

	/**
	 * Returns the epistemic knowledge base consisting of propositional formulas based on 
	 * all extensions of a given semantics for this eaf.
	 *
	 *
	 * @param w the semantics used to compute the extensions
	 * @return the epistemic knowledge base derived from the extensions
	 */
	public MlBeliefSet getWEpistemicKnowledgeBase(Semantics w) {
		
		//get all Extensions
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(w);
		Collection<Extension<DungTheory>> extensions = reasoner.getModels(this);
		
	    // Convert to labeling sets
	  Collection<Labeling> labellings = new HashSet<>();
	    for(Extension<DungTheory> ext : extensions) {
            Labeling lab = new Labeling(this, ext);
            labellings.add(lab);;
	    }
		
		return createEpistemicKnowledgeBase(labellings);
	}
	
	
	private MlBeliefSet createEpistemicKnowledgeBase(Collection<Labeling> labelings) {
		MlBeliefSet beliefSet = new MlBeliefSet();
		// build Belief base from AF and labeling
		for (Labeling lab : labelings) {
				beliefSet.add(new Conjunction(createFolFormula(lab)));
		}
		return beliefSet;
	}
	
	private MlBeliefSet createEpistemicKnowledgeBase(Labeling labeling) {
		MlBeliefSet beliefSet = new MlBeliefSet();
		beliefSet.add(new Conjunction(createFolFormula(labeling)));
		return beliefSet;
	}
	
	private Collection<FolFormula> createFolFormula(Labeling lab) {
	    Collection<FolFormula> literals = new ArrayList<>();

	    for (Argument arg : this.getNodes()) {
	        String argName = arg.getName();

	        ArgumentStatus status = lab.get(arg);
	        Predicate p;
	        switch (status) {
	            case IN:
	                p = new Predicate("in", 1);
	                break;
	            case OUT:
	                p = new Predicate("out", 1);
	                break;
	            case UNDECIDED:
	                p = new Predicate("und", 1);
	                break;
	            default:
	                throw new IllegalStateException("Unknown label status: " + status);
	        }

	        Constant constArg = new Constant(argName, Sort.THING); 
	        literals.add(new FolAtom(p, constArg));
	    }

	    return literals;
	}

	/**
	 * Checks whether the given labeling satisfies the epistemic constraint of the underlying AF.
	 *
	 * @param lab the labeling to check against the epistemic constraint
	 * @return true if the extension satisfies the constraint, false otherwise
	 */
	public boolean satisfiesConstraint(Labeling lab) {
		 MlBeliefSet kb = createEpistemicKnowledgeBase(lab);
		    // If formula is a disjunction, check if any disjunct is satisfied
		    if (this.constraint instanceof Disjunction) {
		        Disjunction disj = (Disjunction) this.constraint;
		        for (RelationalFormula disjunct : disj) {
		            if (disjunct instanceof Conjunction) {
		                if (satisfiesConjunction(kb, (Conjunction) disjunct)) {
		                    return true;
		                }
		            } else { // Handle single necessity/possibility
		                if (satisfiesModalFormula(kb, disjunct)) {
		                    return true;
		                }
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
	
	/**
	 * Checks whether the given extension satisfies the epistemic constraint of the underlying AF.
	 *
	 * @param ext the extension to check against the epistemic constraint
	 * @return true if the extension satisfies the constraint, false otherwise
	 */
	public boolean satisfiesConstraint(Extension<DungTheory> ext) {
		//Translate extentension into labeling
		Labeling lab = new Labeling(this, ext);
		return satisfiesConstraint(lab);
	}
	
	/**
	 * Checks whether the given epistemic knowledge base satisfies the epistemic constraint of the underlying AF.
	 *
	 * @param beliefSet the epistemic knowledge base to check
	 * @return true if the belief set satisfies the constraint, false otherwise
	 */
	public boolean satisfiesConstraint(MlBeliefSet beliefSet) {
		return satisfiesConstraint(beliefSet, this.constraint);
	}
	
	private boolean satisfiesConstraint(MlBeliefSet beliefSet, FolFormula constraint) {
	    // If formula is a disjunction, check if any disjunct is satisfied
	    if (constraint instanceof Disjunction) {
	        Disjunction disj = (Disjunction) constraint;
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
	    else if (constraint instanceof Conjunction) {
	        return satisfiesConjunction(beliefSet, (Conjunction) constraint);
	    }
	    // Handle single necessity/possibility
	    else {
	        return satisfiesModalFormula(beliefSet, constraint);
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
	    	//handle special case of empty set
	    	if (beliefSet.isEmpty()) return true;
	    	
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
	                if (literal.equals(atom)) {
	                    return true;
	                }
	            }
	        }
	    } else if (extension instanceof FolAtom) {
	        // Handle case where the formula is a single atom, not a conjunction
	        return extension.equals(atom);
	    }
	    return false;
	}
	
	
	/**
	 * Computes all epistemic labeling sets under the given semantics.
	 *
	 * @param w the semantics used to compute the labeling sets
	 * @return a set containing all epistemic labeling sets
	 */
	public Set<Set<Labeling>> getWEpistemicLabellingSets(Semantics w) {
	    // Get all extensions
	    AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(w);
	    Collection<Extension<DungTheory>> extensions = reasoner.getModels(this);
	    

	    //if any of the args in the constraint are und(arg), a reduction of the AF is included, to calculate extension that have arg as undecided
	    Set<String> undecidedArgs = extractUndecidedArgsFromConstraint();
	    //For each und(x), reduce AF and recompute extensions
	    for (String argName : undecidedArgs) {
	        Argument arg = new Argument(argName);

	        // Build reduced AF
	        DungTheory reduced = new DungTheory(this);
	        if (reduced.contains(arg)) {
	            reduced.remove(arg);
	        }

	        // Remove all arguments that attack arg (ensuring arg will be undecided in the labelling)
	        for (Argument attacker : this.getAttackers(arg)) {
	            reduced.remove(attacker);
	        }

	        // Compute new extensions from reduced AF
	        Collection<Extension<DungTheory>> reducedExtensions = reasoner.getModels(reduced);
	        extensions.addAll(reducedExtensions);
	    }
	    // Convert to labeling sets
	    Set<Labeling> labelings = new HashSet<>();
	    for(Extension<DungTheory> ext : extensions) {
            Labeling lab = new Labeling(this, ext);
            labelings.add(lab);
	    }
	       
	    // Find maximal satisfying sets
	    Set<Set<Labeling>> maximalSets = findMaximalSatisfyingSets(labelings);
	    return maximalSets;
	}
	
	private Set<String> extractUndecidedArgsFromConstraint() {
	    Set<String> result = new HashSet<>();
	    if (this.constraint == null)
	        return result;

	    for (Atom atom : this.constraint.getAtoms()) {
	        Predicate p = atom.getPredicate();
	        if (p.getName().equals("und") && atom.getArguments().size() == 1) {
	            Term<?> term = atom.getArguments().get(0);
	            if (term instanceof Constant) {
	                result.add(((Constant) term).get());
	            }
	        }
	    }
	    return result;
	}

	private Set<Set<Labeling>> findMaximalSatisfyingSets(Set<Labeling> labelings) {
		Set<Set<Labeling>> maximalSets = new HashSet<>();
	    
	    // Try the full set first
	    if (satisfiesConstraint(createEpistemicKnowledgeBase(labelings))) {
	        maximalSets.add(labelings);
	        return maximalSets;
	    }
	    
	    // If full set doesn't satisfy, try removing one extension at a time
	    findMaximalSetsRecursive(labelings, new HashSet<>(), maximalSets, new HashSet<>(labelings));
	    
	    return maximalSets;
	}

	private void findMaximalSetsRecursive(Set<Labeling> currentSet, 
            Set<Labeling> excluded,
            Set<Set<Labeling>> maximalSets,
            Set<Labeling> remaining) {
		// Base case: if there is an already found satisfying set that is a strict superset of currentSet, stop exploring.
		if (maximalSets.stream().anyMatch(existing -> 
			existing.containsAll(currentSet) && existing.size() > currentSet.size())) {
		return;
		}
		
		// Check if current set satisfies the constraint
		if (satisfiesConstraint(createEpistemicKnowledgeBase(currentSet))) {
			// Remove any previously found set that is a proper subset of currentSet.
			maximalSets.removeIf(existing -> currentSet.containsAll(existing) && existing.size() < currentSet.size());
		
			// Add the current set only if it is no subset of any existing satisfying set.
			boolean isSubsumed = maximalSets.stream().anyMatch(existing -> 
		      existing.containsAll(currentSet) && existing.size() > currentSet.size());
			if (!isSubsumed) {
				maximalSets.add(new HashSet<>(currentSet));
			}
		return;
		}
		
		// Continue exploring by trying to remove one extension at a time
		for (Labeling ext : remaining) {
			if (!excluded.contains(ext)) {
				Set<Labeling> newSet = new HashSet<>(currentSet);
				newSet.remove(ext);
				
				Set<Labeling> newExcluded = new HashSet<>(excluded);
				newExcluded.add(ext);
				
				Set<Labeling> newRemaining = new HashSet<>(remaining);
				newRemaining.remove(ext);
				
				findMaximalSetsRecursive(newSet, newExcluded, maximalSets, newRemaining);
			}
		}
	}
	
	/**
	 * Computes if the given newConstraint is stronger than the underlying constraint of the EAF. 
	 * A constraint φ₁ is stronger than φ₂ if, whenever a set of labelings SL satisfies φ₁, it also satisfies φ₂. 
	 *
	 * @param newConstraint a string representing the constraint
	 * @return w the semantics for which to test if constraint is stronger
	 */
	public boolean isStrongerConstraint(String newConstraint, Semantics w) {
		FolFormula constraint2;
		try {
			constraint2 = convertToDnf((FolFormula) parser.parseFormula(newConstraint));
			return isStrongerConstraint(constraint2,w);
		} catch (ParserException | IOException e) {
			System.out.println(("Failed to parse constraint string: \"" + newConstraint + "\". Reason: " + e.getMessage()));
		}
		return false;
	}
	
	
	/**
	 * Computes if the given newConstraint is stronger than the underlying constraint of the EAF. 
	 * A constraint φ₁ is stronger than φ₂ if whenever a set of labelings SL satisfies φ₁, it also satisfies φ₂. 
	 *
	 * @param newConstraint a FolFormula representing the constraint
	 * @return w the semantics for which to test if constraint is stronger
	 */
	public boolean isStrongerConstraint(FolFormula newConstraint, Semantics w) {
	    // Create temporary EAF for the new constraint
	    EpistemicArgumentationFramework tempEAF1 = new EpistemicArgumentationFramework(this, newConstraint);

	    // Compute ω-epistemic labelling sets for the new constraint
	    Set<Set<Labeling>> labelSets1 = tempEAF1.getWEpistemicLabellingSets(w);

	    // Check whether each set also satisfies the original constraint
	    for (Collection<Labeling> sl : labelSets1) {
	        MlBeliefSet kb = createEpistemicKnowledgeBase(sl);
	        if (!satisfiesConstraint(kb)) {
	            return false; // φ₁ ⊭ φ₂
	        }
	    }

	    return true; // All sets satisfying φ₁ also satisfy φ₂
	}

	
	
	// Misc methods
	
	private FolFormula convertToDnf(RelationalFormula formula) {
	    if (formula instanceof Conjunction) {
	        Conjunction conj = (Conjunction) formula;
	        // Convert each conjunct to DNF
	        List<RelationalFormula> dnfConjuncts = new ArrayList<>();
	        for (RelationalFormula f : conj) {
	            dnfConjuncts.add(convertToDnf(f));
	        }
	        
	        //Distribute conjunction over disjunction
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

	    // Distribute and flatten: (a ∨ b) ∧ (c ∨ d) = (a ∧ c) ∨ (a ∧ d) ∨ (b ∧ c) ∨ (b ∧ d)
	    List<RelationalFormula> resultDisjuncts = new ArrayList<>();
	    for (RelationalFormula first : firstConjunctDisjuncts) {
	        if (restDnf instanceof Disjunction) {
	            for (RelationalFormula rest : ((Disjunction) restDnf)) {
	                // Create flattened list of conjuncts
	                List<RelationalFormula> flattenedConjuncts = new ArrayList<>();
	                
	                // Add conjuncts from first formula
	                if (first instanceof Conjunction) {
	                    flattenedConjuncts.addAll(((Conjunction) first).getFormulas());
	                } else {
	                    flattenedConjuncts.add(first);
	                }
	                
	                // Add conjuncts from rest formula
	                if (rest instanceof Conjunction) {
	                    flattenedConjuncts.addAll(((Conjunction) rest).getFormulas());
	                } else {
	                    flattenedConjuncts.add(rest);
	                }
	                
	                resultDisjuncts.add(new Conjunction(flattenedConjuncts));
	            }
	        } else {
	            List<RelationalFormula> flattenedConjuncts = new ArrayList<>();
	            
	            if (first instanceof Conjunction) {
	                flattenedConjuncts.addAll(((Conjunction) first).getFormulas());
	            } else {
	                flattenedConjuncts.add(first);
	            }
	            
	            if (restDnf instanceof Conjunction) {
	                flattenedConjuncts.addAll(((Conjunction) restDnf).getFormulas());
	            } else {
	                flattenedConjuncts.add(restDnf);
	            }
	            
	            resultDisjuncts.add(new Conjunction(flattenedConjuncts));
	        }
	    }

	    return new Disjunction(resultDisjuncts);
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

	    // (v) K(φ → ψ) transformation
	    if (formula instanceof Necessity &&
	        ((Necessity)formula).getFormula() instanceof Implication) {
	        Implication impl = (Implication)((Necessity)formula).getFormula();
	        RelationalFormula left = impl.getFormulas().getFirst();
	        RelationalFormula right = impl.getFormulas().getSecond();
	        
	        // First transform implication to ¬φ ∨ ψ
	        List<RelationalFormula> disjuncts = new ArrayList<>();
	        
	        // Create ¬φ
	        RelationalFormula negatedLeft = new Negation((FolFormula)left);
	        
	        // Create ¬φ ∨ ψ
	        disjuncts.add(negatedLeft);
	        disjuncts.add(right);
	        Disjunction transformedImpl = new Disjunction(disjuncts);
	        
	        // Now apply K to the transformed formula: K(¬φ ∨ ψ)
	        return applyEpistemicTransformations(new Necessity(transformedImpl));
	    }

	    // (vi) M(φ → ψ) transformation
	    if (formula instanceof Possibility &&
	        ((Possibility)formula).getFormula() instanceof Implication) {
	        Implication impl = (Implication)((Possibility)formula).getFormula();
	        RelationalFormula left = impl.getFormulas().getFirst();
	        RelationalFormula right = impl.getFormulas().getSecond();
	        
	        // First transform implication to ¬φ ∨ ψ
	        List<RelationalFormula> disjuncts = new ArrayList<>();
	        
	        // Create ¬φ
	        RelationalFormula negatedLeft = new Negation((FolFormula)left);
	        
	        // Create ¬φ ∨ ψ
	        disjuncts.add(negatedLeft);
	        disjuncts.add(right);
	        Disjunction transformedImpl = new Disjunction(disjuncts);
	        
	        // Now apply M to the transformed formula: M(¬φ ∨ ψ)
	        return applyEpistemicTransformations(new Possibility(transformedImpl));
	    }

	    // (vii) K(φ ↔ ψ) transformation
	    if (formula instanceof Necessity &&
	        ((Necessity)formula).getFormula() instanceof Equivalence) {
	        Equivalence equiv = (Equivalence)((Necessity)formula).getFormula();
	        RelationalFormula left = equiv.getFormulas().getFirst();
	        RelationalFormula right = equiv.getFormulas().getSecond();
	        
	        // Transform equivalence to (φ → ψ) ∧ (ψ → φ)
	        // First implication: φ → ψ
	        Implication impl1 = new Implication(left, right);
	        
	        // Second implication: ψ → φ
	        Implication impl2 = new Implication(right, left);
	        
	        // Create conjunction: (φ → ψ) ∧ (ψ → φ)
	        List<RelationalFormula> conjuncts = new ArrayList<>();
	        conjuncts.add(impl1);
	        conjuncts.add(impl2);
	        Conjunction transformedEquiv = new Conjunction(conjuncts);
	        
	        // apply K to the transformed formula: K((φ → ψ) ∧ (ψ → φ))
	        return applyEpistemicTransformations(new Necessity(transformedEquiv));
	    }

	    // (viii) M(φ ↔ ψ) transformation
	    if (formula instanceof Possibility &&
	        ((Possibility)formula).getFormula() instanceof Equivalence) {
	        Equivalence equiv = (Equivalence)((Possibility)formula).getFormula();
	        RelationalFormula left = equiv.getFormulas().getFirst();
	        RelationalFormula right = equiv.getFormulas().getSecond();
	        
	        // Transform equivalence to (¬φ ∨ ψ) ∧ (¬ψ ∨ φ)
	        
	        // First disjunction: ¬φ ∨ ψ
	        List<RelationalFormula> disjuncts1 = new ArrayList<>();
	        disjuncts1.add(new Negation((FolFormula)left));
	        disjuncts1.add(right);
	        Disjunction disj1 = new Disjunction(disjuncts1);
	        
	        // Second disjunction: ¬ψ ∨ φ
	        List<RelationalFormula> disjuncts2 = new ArrayList<>();
	        disjuncts2.add(new Negation((FolFormula)right));
	        disjuncts2.add(left);
	        Disjunction disj2 = new Disjunction(disjuncts2);
	        
	        // Create conjunction: (¬φ ∨ ψ) ∧ (¬ψ ∨ φ)
	        List<RelationalFormula> conjuncts = new ArrayList<>();
	        conjuncts.add(disj1);
	        conjuncts.add(disj2);
	        Conjunction transformedEquiv = new Conjunction(conjuncts);
	        
	        // apply M to the transformed formula: M((¬φ ∨ ψ) ∧ (¬ψ ∨ φ))
	        return applyEpistemicTransformations(new Possibility(transformedEquiv));
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
