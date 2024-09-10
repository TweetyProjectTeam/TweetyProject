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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
 * This class implements a constrained abstract argumentation theory (WAF) using a propositional logic formula.
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
	 * default constructor 
	 */
	public ConstrainedArgumentationFramework() {
		super();
		this.constraint = new Tautology();
	}
	
	/**
	 * default constructor 
	 */
	public ConstrainedArgumentationFramework(Graph<Argument> graph) {
		super(graph);
		this.constraint = new Tautology();
	}
	
	/**
	 * default constructor 
	 */
	public ConstrainedArgumentationFramework(Graph<Argument> graph, String constraint) {
		super(graph);
		try {
			this.constraint = parser.parseFormula(constraint);
		} catch (ParserException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ConstrainedArgumentationFramework(String constraint) {
		super();
		try {
			this.constraint = parser.parseFormula(constraint);
		} catch (ParserException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ConstrainedArgumentationFramework(Graph<Argument> graph, PlFormula constraint) {
		super(graph);
		this.constraint = constraint;
	}
	
	public boolean setConstraint(PlFormula constraint) {
		this.constraint = constraint;
		return true;
	}
	
	public boolean setConstraint(String constraint) {
		try {
			this.constraint = parser.parseFormula(constraint);
			return true;
		} catch (ParserException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public PlFormula getConstraint() {
		return constraint;
	}
	
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
		//System.out.println(beliefSet.toString());
		
		
		//check if constraint satifies beliefbase
		SimplePlReasoner reasoner = new SimplePlReasoner();
		return reasoner.query(beliefSet, constraint);

	}
	
	
	public boolean isCAdmissibleSet(Extension<DungTheory> ext) {
		return super.isAdmissible(ext) && isCompletion(ext);
	}
	
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

	
	public boolean isStableCExtension(Extension<DungTheory> ext) {
		return isCAdmissibleSet(ext) && isStable(ext);
	}
	

	
	/**
	 * The characteristic function of a constrained argumentation framework: F_CAF(S) = {A | A is acceptable w.r.t. S and S ∪ {a} satisfies C}.
	 * This function takes an extension (a set of arguments) and returns a new extension based on whether each argument is 
	 * acceptable with respect to the given extension, and the extension with the new argument satisfies the constraint C.
	 * 
	 * @param extension an extension (a set of arguments) for which the characteristic function will be applied.
	 * @return a new extension containing arguments that are acceptable with respect to the given extension 
	 *         and where S ∪ {a} satisfies C, or null if any invalid condition is found.
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
	                
	                //Ensure that fcaf is monotone
	                if (possExt.containsAll(newExtension)) {
	                    // Add the current argument to the new extension
	                    newExtension.add(argument);
	                } else {
	                    // fcaf is not monotone
	                    return null;
	                }
	            }
	        }
	    }
	    // Return the newly constructed extension
	    return newExtension;
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
