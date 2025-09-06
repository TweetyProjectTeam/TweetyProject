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
package org.tweetyproject.arg.eaf.writer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.commons.syntax.interfaces.Atom;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.commons.syntax.StringTerm;
import org.tweetyproject.logics.fol.syntax.Conjunction;
import org.tweetyproject.logics.fol.syntax.Disjunction;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.fol.syntax.Negation;
import org.tweetyproject.logics.ml.syntax.Necessity;
import org.tweetyproject.logics.ml.syntax.Possibility;


/**
 * 
 */
public class EafCnfWriter extends AbstractEafWriter{
	/** Default */
	public EafCnfWriter(){

	}

	@Override
	public void write(EpistemicArgumentationFramework eaf, File f) throws IOException {
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		writer.println("p af " + eaf.size() + " " + eaf.getAttacks().size());
		Map<Argument,Integer> map = new HashMap<Argument,Integer>();
		int idx = 1;
		for(Argument arg: eaf)
			map.put(arg, idx++);
		for(Attack att: eaf.getAttacks())
			writer.println(map.get(att.getAttacker()) + " -" + map.get(att.getAttacked()) + " 0");
		//map arguments in constraint to arguments in cnf format
		FolFormula constraint = eaf.getConstraint();
		Set<? extends Atom> atoms = constraint.getAtoms();
		for(Atom atom: atoms) {
			String name = atom.toString();
			int numP = constraint.getPredicates().size();
			int idP = map.get(new Argument(name));
			Atom newAtom = new FolAtom(new Predicate(String.valueOf(idP)));
			for (int i = 0; i < numP; i++) {
				//change any occurence of atom in the constraint to newAtom
				constraint = replaceAtom(constraint, atom, newAtom);
			}	
		}
		writer.println("c " + constraint.toString());
		writer.close();		
	}
	
	// Helper method to replace atoms in a formula
	private FolFormula replaceAtom(FolFormula formula, Atom oldAtom, Atom newAtom) {
	    if (formula instanceof FolAtom) {
	        if (formula.equals(oldAtom)) {
	            return (FolFormula) newAtom;
	        }
	        return formula;
	    }
	    
	    if (formula instanceof Negation) {
	        Negation neg = (Negation) formula;
	        return new Negation(replaceAtom((FolFormula) neg.getFormula(), oldAtom, newAtom));
	    }
	    
	    if (formula instanceof Conjunction) {
	        Conjunction conj = (Conjunction) formula;
	        List<RelationalFormula> newFormulas = new ArrayList<>();
	        for (RelationalFormula f : conj) {
	            newFormulas.add(replaceAtom((FolFormula) f, oldAtom, newAtom));
	        }
	        return new Conjunction(newFormulas);
	    }
	    
	    if (formula instanceof Disjunction) {
	        Disjunction disj = (Disjunction) formula;
	        List<RelationalFormula> newFormulas = new ArrayList<>();
	        for (RelationalFormula f : disj) {
	            newFormulas.add(replaceAtom((FolFormula) f, oldAtom, newAtom));
	        }
	        return new Disjunction(newFormulas);
	    }
	    
	    if (formula instanceof Necessity) {
	        Necessity nec = (Necessity) formula;
	        return new Necessity(replaceAtom((FolFormula) nec.getFormula(), oldAtom, newAtom));
	    }
	    
	    if (formula instanceof Possibility) {
	        Possibility poss = (Possibility) formula;
	        return new Possibility(replaceAtom((FolFormula) poss.getFormula(), oldAtom, newAtom));
	    }
	    
	    return formula;
	}
}
