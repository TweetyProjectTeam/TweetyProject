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
 package net.sf.tweety.arg.dung.parser;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;


/**
 * Parses abstract argumentation frameworks in the logic programming
 * format which is given by the following BNF
 * (start symbol is S):<br>
 * <br>
 * S 	::== "" | "arg" "(" ARGUMENT ")" "\n" S | "att" "(" ARGUMENT "," ARGUMENT ")" "\n" S<br>
 * 
 * where "ARGUMENT" represents any string (without blanks) as a terminal symbol.
 *  
 * @author Matthias Thimm
 */
public class ApxParser extends AbstractDungParser {

	/* (non-Javadoc)
	 * @see argc.parser.Parser#parse(java.io.File)
	 */
	@Override
	public DungTheory parse(Reader reader) throws IOException{
		DungTheory theory = new DungTheory();
		BufferedReader in = new BufferedReader(reader);
		String row = null;
		Map<String,Argument> arguments = new HashMap<String,Argument>();
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
				Argument a = new Argument(row);
				arguments.put(row, a);
				theory.add(a);
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
				theory.addAttack(arguments.get(row.substring(0, row.indexOf(","))),arguments.get(row.substring(row.indexOf(",")+1, row.length())));
			}else{
				in.close();
				throw new IOException("Argument or attack declaration expected, found " + row);
			}	
		}
		in.close();
		return theory;
	}

	

	
	
	
}
