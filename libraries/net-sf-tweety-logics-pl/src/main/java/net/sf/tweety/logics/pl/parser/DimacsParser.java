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
 *  Copyright 2016-2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.StringTokenizer;

import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.PlSignature;

/**
 * Parser a file in Dimacs format into a PlBeliefSet.
 * 
 * @author Matthias Thimm
 *
 */
public class DimacsParser extends Parser<PlBeliefSet,PlFormula> {
	
	/**
	 * The signature used for parsing (is automatically set if a file is parsed, but
	 * must be set explicitly if only a single formula is parsed). 
	 */
	private PlSignature signature = null;
	
	/**
	 * An array representation of the propositions (for index mapping) 
	 */
	private Proposition[] prop_idx = null;
	
	/**
	 * Sets the signature of this parser. Note that the natural order of the atoms
	 * in the signature (as returned by an iterator) is used for indexing variables in
	 * clauses.
	 * @param sig some signature
	 */
	public void setSignature(PlSignature sig) {
		this.signature = sig;
		this.prop_idx = new Proposition[sig.size()];
		int idx = 0;
		for(Proposition p: sig)
			this.prop_idx[idx++] = p;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Parser#parseBeliefBase(java.io.Reader)
	 */
	@Override
	public PlBeliefSet parseBeliefBase(Reader reader) throws IOException, ParserException {
		PlBeliefSet beliefSet = new PlBeliefSet();		
		String s = "";
		// read from the reader and separate formulas by "\n"
		try{
			for(int c = reader.read(); c != -1; c = reader.read()){
				if(c == 10){
					s = s.trim();					
					if(s.startsWith("c")) {
						// ignore comments					
					}else if(s.startsWith("p")) {
						// parse preamble						
						StringTokenizer tokenizer = new StringTokenizer(s," ");
						tokenizer.nextToken();
						tokenizer.nextToken();
						int numberVars = new Integer(tokenizer.nextToken());
						this.signature = new PlSignature();
						this.prop_idx = new Proposition[numberVars];
						for(Integer i = 1; i <= numberVars; i++) {
							Proposition p = new Proposition(i.toString());						
							this.signature.add(p);
							this.prop_idx[i-1] = p;
						}
					}else if(!s.equals("")) 
						// parse clause
					 	beliefSet.add(this.parseFormula(new StringReader(s)));
					s = "";
				}else{
					s += (char) c;
				}
			}		
			if(!s.equals(""))
				beliefSet.add(this.parseFormula(new StringReader(s)));
		}catch(Exception e){
			throw new ParserException(e);
		}
		return beliefSet;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Parser#parseFormula(java.io.Reader)
	 */
	@Override
	public Disjunction parseFormula(Reader reader) throws IOException, ParserException {
		if(this.signature == null)
			throw new RuntimeException("Signature not set.");
		Disjunction clause = new Disjunction();
		String s = "";
		for(int c = reader.read(); c != -1; c = reader.read()){
			if((c == '0' && s == "") || c == 10){
				return clause;
			}else if(c == ' ' || c == '\t'){
				s = s.trim();
				if(s.length()>0) {
					int idx = new Integer(s);
					if(idx < 0)
						clause.add(new Negation(this.prop_idx[(idx*-1)-1]));
					else
						clause.add(this.prop_idx[idx-1]);
				}
				s = "";
			}else {
				s += (char) c;
			}
		}
		return clause;
	}

}
