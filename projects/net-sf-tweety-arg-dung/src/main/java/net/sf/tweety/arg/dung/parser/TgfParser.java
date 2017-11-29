package net.sf.tweety.arg.dung.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;

/**
 * Parses abstract argumentation frameworks given in the 
 * trivial graph format which is given by the following BNF
 * (start symbol is S):<br/>
 * <br/>
 * S 			::== ARGUMENTS "#" "\n" ATTACKS	<br/>
 * ARGUMENTS	::== "" | ARGUMENT "\n" ARGUMENTS<br/>
 * ATTACKS		::== "" | ATTACK "\n" ATTACKS<br/>
 * ATTACK		::== ARGUMENT ARGUMENT<br/>
 * 
 * where "ARGUMENT" represents any string (without blanks) as a terminal symbol.
 * 
 * @author Matthias Thimm
 */
public class TgfParser extends AbstractDungParser{	
	
	/* (non-Javadoc)
	 * @see argc.parser.Parser#parse(java.io.File)
	 */
	@Override
	public DungTheory parse(Reader reader) throws IOException {
		DungTheory theory = new DungTheory();
		BufferedReader in = new BufferedReader(reader);
		String row = null;
		boolean argumentSection = true;
		Map<String,Argument> arguments = new HashMap<String,Argument>();
		while ((row = in.readLine()) != null) {
			if(row.trim().equals("")) continue;
			if(row.trim().equals("#")){
				argumentSection = false;
				continue;
			}
			if(argumentSection) {
				Argument a = new Argument(row.trim());
				arguments.put(a.getName(), a);
				theory.add(a);
			}				
			else{
				theory.addAttack(arguments.get(row.substring(0, row.indexOf(" ")).trim()),arguments.get(row.substring(row.indexOf(" ")+1,row.length()).trim()));
			}
		}
		in.close();
		return theory;
	}	
}
