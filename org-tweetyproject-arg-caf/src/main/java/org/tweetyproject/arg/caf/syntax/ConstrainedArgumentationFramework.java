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
* Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
*/

package org.tweetyproject.arg.caf.syntax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import org.tweetyproject.arg.caf.reasoner.SimpleCAFAdmissibleReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleAdmissibleReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 * This class implements a constrained abstract argumentation theory (CAF) using a propositional logic formula.
 * <br>
 * <br>See
 * <br>
 * <br>Coste-Marquis, Sylvie, Caroline Devred, and Pierre Marquis. "Constrained Argumentation Frameworks." KR 6 (2006): 112-122.
 * 
 * @author Sandra Hoffmann
 *
 */

public class ConstrainedArgumentationFramework extends DungTheory {

	private PlFormula constraint;		
	private PlParser parser = new PlParser();	
	

	/**
	 * default constructor.
	 */
	public ConstrainedArgumentationFramework() {
		super();
		this.constraint = new Tautology();
	}
	
	/**
	 * Constructor for a CAF from a graph. 
	 * 
	 * @param graph A graph representing the structure of an argumentation framework.
	 */
	public ConstrainedArgumentationFramework(Graph<Argument> graph) {
		super(graph);
		this.constraint = new Tautology();
	}
	
	/**
	 * Constructor for a CAF from a given graph and constraint.
	 * The constraint is parsed from a string into a propositional formula.
	 * 
	 * @param graph A graph representing the structure of an argumentation framework.
	 * @param constraint A string representing the propositional constraint.
	 */
	public ConstrainedArgumentationFramework(Graph<Argument> graph, String constraint) {
		super(graph);
		try {
			this.constraint = parser.parseFormula(constraint);
		} catch (ParserException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Constructor for a CAF from a graph and a propositional formula as the constraint.
	 * 
	 * @param graph A graph representing the structure of arguments and their relations.
	 * @param constraint A propositional formula representing the constraint.
	 */
	public ConstrainedArgumentationFramework(Graph<Argument> graph, PlFormula constraint) {
		super(graph);
		this.constraint = constraint;
	}
	
	
	/**
	 * Sets a new propositional formula as the constraint of CAF.
	 * 
	 * @param constraint A propositional formula to be set as the new constraint.
	 * @return true when the constraint is successfully set.
	 */
	public boolean setConstraint(PlFormula constraint) {
		this.constraint = constraint;
		return true;
	}
	
	
	/**
	 * Sets a new constraint by parsing a string representing a propositional formula.
	 * 
	 * @param constraint A string representing the propositional constraint to be parsed.
	 * @return true if the constraint is successfully set, false if an error occurs during parsing.
	 */
	public boolean setConstraint(String constraint) {
		try {
			this.constraint = parser.parseFormula(constraint);
			return true;
		} catch (ParserException | IOException e) {
			return false;
		}
	}
	
	
	/**
	 * Retrieves the current propositional constraint of the framework.
	 * 
	 * @return The propositional formula representing the current constraint.
	 */
	public PlFormula getConstraint() {
		return this.constraint;
	}
	
	
	/**
	 * Checks whether the given extension forms a completion based on the argumentation framework and its constraint.
	 * 
	 * @param ext The extension (set of arguments) to be checked for completion.
	 * @return true if the extension satisfies the framework's constraint, false otherwise.
	 */
	public boolean isCompletion(Extension<DungTheory> ext) {
		// build Belief base from AF and ext, all Args in ext are set to true, all other args set to false
		PlBeliefSet beliefSet = new PlBeliefSet();
		Collection<PlFormula> literals = new ArrayList<>();
		Iterator<Argument> it = this.iterator();
		while(it.hasNext()) {
			Argument arg = it.next();
			if (ext.contains(arg)){
				 literals.add(new Proposition(arg.getName()));				
			} else {
				literals.add(new Negation(new Proposition(arg.getName())));				
			}
		}		
		beliefSet.add(new Conjunction(literals));
		
		//check if constraint satifies beliefbase
		SimplePlReasoner reasoner = new SimplePlReasoner();
		return reasoner.query(beliefSet, constraint);

	}
	
	/**
	 * Checks whether the given extension is C-admissible, meaning that it is both admissible according to 
	 * the standard Dung theory and satisfies the current constraint.
	 * 
	 * @param ext The extension to be checked for C-admissibility.
	 * @return true if the extension is C-admissible, false otherwise.
	 */
	public boolean isCAdmissibleSet(Extension<DungTheory> ext) {
		return super.isAdmissible(ext) && isCompletion(ext);
	}
	
	/**
	 * Checks whether the framework is consistent, i.e., whether there exists at least one C-admissible extension.
	 * 
	 * @return true if a C-admissible extension exists, false otherwise.
	 */
	public boolean isConsistent() {
		//get all admissible Sets
		SimpleAdmissibleReasoner amdReasoner = new SimpleAdmissibleReasoner();
		Collection<Extension<DungTheory>> admSets = amdReasoner.getModels(this);
		//check if one of them is C-admissible
		for(Extension<DungTheory> ext : admSets) {
			if(isCAdmissibleSet(ext)) return true;
		}
		return false;
	}
	
	/**
	 * Checks whether the given extension is a preferred C-extension, meaning it is a set-maximal C-admissible extension
	 * 
	 * @param ext The extension (set of arguments) to be checked for being a preferred C-extension.
	 * @return true if the extension is a preferred C-extension, false otherwise.
	 */
	public boolean isPreferredCExtension(Extension<DungTheory> ext) {
		//check if ext is C-Admissible
		if(!isCAdmissibleSet(ext)) return false;
		
		//Check whether adding ext is a maximal admissible set by subsequently adding remaining arguments and checking whether the resulting set is C-Admissible
		Extension<DungTheory> remaining = new Extension<>();
		Iterator<Argument> it = this.iterator();
		while(it.hasNext()) {
			remaining.add(it.next());
		}
		remaining.removeAll(ext);
		for(Argument arg:remaining) {
	        Extension<DungTheory> newExt = new Extension<>();
	        newExt.addAll(ext); 
			newExt.add(arg);
			if(isCAdmissibleSet(newExt)) return false;
		}
		return true;	
	}

	/**
	 * Checks whether the given extension is a stable C-extension, meaning it is C-admissible 
	 * and attacks every argument not included in the extension.
	 * 
	 * @param ext The extension (set of arguments) to be checked for being a stable C-extension.
	 * @return true if the extension is a stable C-extension, false otherwise.
	 */
	public boolean isStableCExtension(Extension<DungTheory> ext) {
		return isCAdmissibleSet(ext) && isStable(ext);
	}
	

	
	/**
	 * The characteristic function of a constrained argumentation framework: F_CAF(S) = {A | A is acceptable w.r.t. S and S ∪ {a} satisfies C}.
	 * 
	 * @param extension an extension (a set of arguments) for which the characteristic function will be applied.
	 * @return a new extension containing arguments that are acceptable with respect to the given extension 
	 *         and where S ∪ {a} satisfies C, or null if F_CAF(S) is not a monotone function.
	 */
	public Extension<DungTheory> fcaf(Extension<DungTheory> extension){
	    Extension<DungTheory> newExtension = new Extension<>();
	    Iterator<Argument> it = this.iterator();

	    while (it.hasNext()) {
	        Argument argument = it.next();
	        Extension<DungTheory> possExt = new Extension<>();
	        
	        possExt.addAll(extension); 
	        possExt.add(argument);
	        
	        // Check if the current argument is acceptable w.r.t the given extension
	        if (this.isAcceptable(argument, extension)) {
	            // Check if the possible extension satisfies the completion criteria (C)
	            if (isCompletion(possExt)) {
	            	newExtension.add(argument);
	            }
	        }
	    }
	    return newExtension;
	}
	
	
	/**
	 * Checks whether the characteristic function of the given CAF is monotonic with respect to a collection of admissible extensions.
	 * If the characteristic function is monotone and the set of admissible Extension of the CAF has a least element, then it has a C-grounded extension.
	 * 
	 * @return true if fcaf is monotonic over the admissible extensions, false otherwise
	 */
    public boolean hasMonotoneFcafA() {
    	SimpleCAFAdmissibleReasoner admReas = new SimpleCAFAdmissibleReasoner();
    	Collection<Extension<DungTheory>> cAdmSets = admReas.getModels(this);
        cAdmSets = new TreeSet<>(cAdmSets);

        for (Extension<DungTheory> extA : cAdmSets) {
            Extension<DungTheory> fcafA = this.fcaf(extA);
            for (Extension<DungTheory> extB : cAdmSets) {
                if (!extA.equals(extB)) {
                    // Check if extA is a subset of extB
                    if (extA.containsAll(extB)) {
                        Extension<DungTheory> fcafB = this.fcaf(extB);                       
                        // Check monotonicity: fcaf(extA) must be a subset of fcaf(extB)
                        if (!fcafA.containsAll(fcafB)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

	
	// Misc methods

	
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
