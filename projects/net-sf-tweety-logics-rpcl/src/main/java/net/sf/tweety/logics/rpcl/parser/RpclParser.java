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
package net.sf.tweety.logics.rpcl.parser;

import java.io.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.fol.parser.*;
import net.sf.tweety.logics.fol.syntax.*;
import net.sf.tweety.logics.rpcl.*;
import net.sf.tweety.logics.rpcl.syntax.*;
import net.sf.tweety.math.probability.*;


/**
 * This class implements a parser for relational probabilistic conditional logic. The BNF for a conditional
 * knowledge base is given by (starting symbol is KB)
 * <br>
 * <br> KB 			::== SORTSDEC PREDDECS (CONDITIONAL "\n")*
 * <br> SORTSDEC    ::== ( SORTNAME "=" "{" (CONSTANTNAME ("," CONSTANTNAME)*)? "}" "\n" )*
 * <br> PREDDECS	::== ( "type" "(" PREDICATENAME "(" (SORTNAME ("," SORTNAME)*)? ")" ")" "\n" )*
 * <br> CONDITIONAL ::== "(" FORMULA ")" "[" PROB "]" | "(" FORMULA "|" FORMULA ")" "[" PROB "]" 
 * <br> FORMULA     ::== ATOM | "(" FORMULA ")" | FORMULA "&&" FORMULA | FORMULA "||" FORMULA | "!" FORMULA | "+" | "-"
 * <br> ATOM		::== PREDICATENAME "(" (TERM ("," TERM)*)? ")"
 * <br> TERM		::== VARIABLENAME | CONSTANTNAME 
 * <br> 
 * <br> where SORTNAME, PREDICATENAME, CONSTANTNAME, and VARIABLENAME are sequences of
 * <br> symbols from {a,...,z,A,...,Z,0,...,9} with a letter at the beginning and PROB is a float in [0,1].
 * 
 *  @author Matthias Thimm
 */
public class RpclParser extends Parser<RpclBeliefSet> {

	/** For parsing FOL fragments. */
	private FolParser folParser;
	
	/**
	  Creates a new RPCL Parser
	 */
	public RpclParser(){
		folParser = new FolParser();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Parser#parseBeliefBase(java.io.Reader)
	 */
	@Override
	public RpclBeliefSet parseBeliefBase(Reader reader) throws IOException, ParserException {
		RpclBeliefSet beliefSet = new RpclBeliefSet();
		String s = "";
		// for keeping track of the section of the file
		// 0 means sorts declaration
		// 1 means functor/predicate declaration
		// 2 means conditional section
		int section = 0; 
		// read from the reader and separate formulas by "\n"
		try{
			for(int c = reader.read(); c != -1; c = reader.read()){
				if(c == 10){
					if(!s.equals("")){
						if(s.trim().startsWith("type")) section = 1;
						else if(section == 1) section = 2;
						
						if(section == 2)
							beliefSet.add(this.parseFormula(new StringReader(s)));
						else if(section == 1)
							this.folParser.parseTypeDeclaration(s,this.folParser.getSignature());
						else this.folParser.parseSortDeclaration(s,this.folParser.getSignature());
					}
					s = "";
				}else{
					s += (char) c;
				}
			}		
			if(!s.equals("")){
				if(s.trim().startsWith("type")) section = 1;
				else if(section == 1) section = 2;
				
				if(section == 2)
					beliefSet.add(this.parseFormula(new StringReader(s)));
				else if(section == 1)
					this.folParser.parseTypeDeclaration(s,this.folParser.getSignature());
				else this.folParser.parseSortDeclaration(s,this.folParser.getSignature());
			}
		}catch(Exception e){
			throw new ParserException(e);
		}
		return beliefSet;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.parser.FolParser#parseFormula(java.io.Reader)
	 */
	@Override
	public RelationalProbabilisticConditional parseFormula(Reader reader) throws IOException, ParserException {
		// read into string
		String s = "";
		try{
			for(int c = reader.read(); c != -1; c = reader.read())
				s += (char) c;
		}catch(Exception e){
			throw new ParserException(e);
		}
		// check probability 
		if(!s.contains("[")) throw new ParserException("Missing '[' in conditional definition.");
		if(!s.contains("]")) throw new ParserException("Missing ']' in conditional definition.");
		String probString = s.substring(s.lastIndexOf("[")+1,s.lastIndexOf("]"));
		Probability prob;
		try{
			prob = new Probability(Double.parseDouble(probString));
		}catch(Exception e){
			throw new ParserException("Could not parse probability '" + probString + "'.");
		}
		if(!s.contains("(") || !s.contains(")")) 
			throw new ParserException("Conditionals must be enclosed by parantheses.");
		String condString = s.substring(1,s.lastIndexOf(")"));
		//check for a single "|" (note, that "||" denotes disjunction)
		int idx = 0;		
		while(idx != -1){
			idx = condString.indexOf("|", idx);
			if(condString.charAt(idx+1) != '|')
				break;			
			idx += 2;
		}		
		FolParser parser = new FolParser();
		parser.setSignature(this.getSignature());
		if(idx == -1){
			RelationalProbabilisticConditional r = new RelationalProbabilisticConditional((FolFormula)parser.parseFormula(condString.substring(0, condString.length())),prob);
			this.folParser.setSignature(parser.getSignature());
			return r;
		}
		// check whether variables have the correct sort wrt. the scope of the whole conditional		
		RelationalProbabilisticConditional cond = new RelationalProbabilisticConditional((FolFormula)parser.parseFormula(condString.substring(idx+1, condString.length())),(FolFormula)parser.parseFormula(condString.substring(0, idx)),prob);
		this.folParser.setSignature(parser.getSignature());
		parser.parseFormula(condString.substring(idx+1, condString.length()) + " && " + condString.substring(0, idx));
		return cond;
	}
	
	/**
	 * Returns the signature of this parser.
	 * @return the signature of this parser.
	 */
	public FolSignature getSignature(){
		return this.folParser.getSignature();
	}
}
