package net.sf.tweety.arg.dung.parser;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.Reader;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;


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
public class ApxParser extends AbstractDungParser {

	/* (non-Javadoc)
	 * @see argc.parser.Parser#parse(java.io.File)
	 */
	@Override
	public DungTheory parse(Reader reader) throws IOException{
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

	

	
	
	
}
