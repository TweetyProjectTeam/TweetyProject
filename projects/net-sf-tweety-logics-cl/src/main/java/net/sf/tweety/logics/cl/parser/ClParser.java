package net.sf.tweety.logics.cl.parser;

import java.io.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.cl.*;
import net.sf.tweety.logics.cl.syntax.*;
import net.sf.tweety.logics.pl.parser.*;
import net.sf.tweety.logics.pl.syntax.*;


/**
 * This class implements a parser for conditional logic. The BNF for a conditional
 * knowledge base is given by (starting symbol is KB)
 * <br>
 * <br> KB 			::== CONDITIONAL ( "\n" CONDITIONAL )*
 * <br> CONDITIONAL ::== "(" FORMULA ")" | "(" FORMULA "|" FORMULA ")" 
 * <br>
 * <br>FORMULA is a propositional formula (@see net.sf.tweety.kr.l.parser.PlParser).
 * 
 *  @author Matthias Thimm
 */
public class ClParser extends Parser<ClBeliefSet> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Parser#parseBeliefBase(java.io.Reader)
	 */
	@Override
	public ClBeliefSet parseBeliefBase(Reader reader) throws IOException, ParserException {
		ClBeliefSet beliefSet = new ClBeliefSet();
		String s = "";
		// read from the reader and separate formulas by "\n"
		try{
			for(int c = reader.read(); c != -1; c = reader.read()){
				if(c == 10){
					if(!s.equals("")) beliefSet.add(this.parseFormula(new StringReader(s)));
					s = "";
				}else{
					s += (char) c;
				}
			}		
			if(!s.equals("")) beliefSet.add(this.parseFormula(new StringReader(s)));
		}catch(Exception e){
			throw new ParserException(e);
		}
		return beliefSet;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Parser#parseFormula(java.io.Reader)
	 */
	@Override
	public Conditional parseFormula(Reader reader) throws IOException, ParserException {
		// read into string
		String s = "";
		try{
			for(int c = reader.read(); c != -1; c = reader.read())
				s += (char) c;
		}catch(Exception e){
			throw new ParserException(e);
		}
		if(!(s.startsWith("(") && s.endsWith(")")))
			throw new ParserException("Conditionals must be enclosed by parantheses.");
		//check for a single "|" (note, that "||" denotes disjunction)
		int idx = 0;		
		while(idx != -1){
			idx = s.indexOf("|", idx);
			if(s.charAt(idx+1) != '|')
				break;			
		}
		PlParser plParser = new PlParser();
		if(idx == -1)
			return new Conditional((PropositionalFormula)plParser.parseFormula(s.substring(1, s.length()-1)));
		return new Conditional((PropositionalFormula)plParser.parseFormula(s.substring(idx+1, s.length()-1)),(PropositionalFormula)plParser.parseFormula(s.substring(1, idx)));		
	}

}
