/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.dung.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;

/**
 * Parses abstract argumentation frameworks in the logic programming
 * format which is given by the following BNF
 * (start symbol is S):<br/>
 * <br/>
 * S 	::== "" | "arg" "(" ARGUMENT ")" "\n" S | "att" "(" ARGUMENT "," ARGUMENT ")" "\n" S<br/>
 * 
 * where "ARGUMENT" represents any string (without blanks) as a terminal symbol.
 *  
 * @author Matthias Thimm
 */
public class ApxParser extends Parser<DungTheory>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Parser#parseBeliefBase(java.io.Reader)
	 */
	@Override
	public DungTheory parseBeliefBase(Reader reader) throws IOException, ParserException {
		DungTheory theory = new DungTheory();
		BufferedReader in = new BufferedReader(reader);
		String row = null;
		while ((row = in.readLine()) != null) {
			row = row.trim();
			if(row.equals("")) continue;			
			if(row.startsWith("arg")){
				row = row.substring(3).trim();
				if(!row.endsWith(".")){
					in.close();
					throw new IOException("\"arg(ARGUMENT).\" expected, found " + row);
				}
				row = row.substring(0,row.length()-1).trim();
				if(!row.startsWith("(") || !row.endsWith(")")){
					in.close();
					throw new IOException("\"arg(ARGUMENT).\" expected, found " + row);
				}
				row = row.substring(1,row.length()-1).trim();							
				theory.add(new Argument(row));
			}else if(row.startsWith("att")){
				row = row.substring(3).trim();
				if(!row.endsWith(".")){
					in.close();
					throw new IOException("\"att(ARGUMENT,ARGUMENT).\" expected, found " + row);
				}
				row = row.substring(0,row.length()-1).trim();
				if(!row.startsWith("(") || !row.endsWith(")")){
					in.close();
					throw new IOException("\"att(ARGUMENT,ARGUMENT).\" expected, found " + row);
				}
				row = row.substring(1,row.length()-1).trim();
				String attackerString = row.substring(0, row.indexOf(","));
				String attackedString = row.substring(row.indexOf(",")+1, row.length());
				Argument a1 = new Argument(attackerString);
				Argument a2 = new Argument(attackedString);				
				theory.add(new Attack(a1,a2));
			}else{
				in.close();
				throw new IOException("Argument or attack declaration expected, found " + row);
			}	
		}
		in.close();
		return theory;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Parser#parseFormula(java.io.Reader)
	 */
	@Override
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
		String row;
		StringBuilder line = new StringBuilder();
		int c;
		do{
			c = reader.read();
			if(c == -1)
				break;
			line.append((char)c);
			
		}while(true);
		reader.close();
		row = line.toString().trim();
		if(row.startsWith("arg")){
			row = row.substring(3).trim();
			if(!row.endsWith("."))
				throw new IOException("\"arg(ARGUMENT).\" expected, found " + row);			
			row = row.substring(0,row.length()-1).trim();
			if(!row.startsWith("(") || !row.endsWith(")"))
				throw new IOException("\"arg(ARGUMENT).\" expected, found " + row);			
			row = row.substring(1,row.length()-1).trim();							
			return new Argument(row);
		}else if(row.startsWith("att")){
			row = row.substring(3).trim();
			if(!row.endsWith("."))
				throw new IOException("\"att(ARGUMENT,ARGUMENT).\" expected, found " + row);			
			row = row.substring(0,row.length()-1).trim();
			if(!row.startsWith("(") || !row.endsWith(")"))
				throw new IOException("\"att(ARGUMENT,ARGUMENT).\" expected, found " + row);			
			row = row.substring(1,row.length()-1).trim();
			String attackerString = row.substring(0, row.indexOf(","));
			String attackedString = row.substring(row.indexOf(",")+1, row.length());
			Argument a1 = new Argument(attackerString);
			Argument a2 = new Argument(attackedString);				
			return new Attack(a1,a2);
		}
		throw new ParserException("Entity not recognized: " + row);
	}

}
