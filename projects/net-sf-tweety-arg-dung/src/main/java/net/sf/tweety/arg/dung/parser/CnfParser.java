package net.sf.tweety.arg.dung.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;

/**
 * Parses abstract argumentation frameworks in the CNF/Dimacs Format used for the
 * SAT solver competition (see also http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html).
 * It is given by the following BNF (start symbol is S):<br/>
 * <br/>
 * S 	     ::== COMMENTS "\n" PROBLEM "\n" ATTACKS<br/>
 * COMMENTS  ::== ("c" COMMENT "\n")*<br/>
 * PROBLEM   ::== "p af " INT INT</br>
 * ATTACKS   ::== (INT " -" INT " 0\n")*  
 * 
 * where "COMMENT" represents any string, and INT is a positive natural number. Lines beginning
 * with "c" are comment lines and are ignored by the parser. The line beginning with "p af" is the
 * problem definition, it is followed by two positive integers where the first is the number
 * of arguments and the second is the number of attacks in the given framework. After the problem
 * definition there is one line for each attack. The first integer in these lines describe the attacker
 * of the attack and the second the attacked (the attacked is also prefixed by a minus sign "-"). Each
 * line of an attack ends with "0" and a line break.  
 * 
 * @author Matthias Thimm
 *
 */
public class CnfParser extends AbstractDungParser {

	/* (non-Javadoc)
	 * @see net.sf.probo.parser.Parser#parse(java.io.File)
	 */
	@SuppressWarnings("resource")
	@Override
	public DungTheory parse(Reader reader) throws IOException {
		DungTheory theory = new DungTheory();
		int numArgs = -1;
		int numAtt = -1;
		String attacker, attacked;
		BufferedReader in = new BufferedReader(reader);
		String row = null;
		while ((row = in.readLine()) != null) {
			row = row.trim();
			if(row.equals("")) continue;
			// ignore comment lines
			if(row.startsWith("c")) continue;
			// problem definition line, check validity later
			if(row.startsWith("p af")){
				row = row.substring(4).trim();
				numArgs = new Integer(row.substring(0,row.indexOf(" ")).trim());
				numAtt = new Integer(row.substring(row.indexOf(" ")+1).trim());
				continue;
			}
			// line describing an attack
			if(!row.endsWith("0"))
				throw new IOException("Line is not terminated by '0'");
			attacker = row.substring(0,row.indexOf(" ")).trim();
			attacked = row.substring(row.indexOf("-")+1, row.lastIndexOf("0")).trim();
			theory.add(new Argument("a" + attacker));
			theory.add(new Argument("a" + attacked));
			theory.add(new Attack(new Argument("a" + attacker),new Argument("a" + attacked)));
		}
		in.close();
		// check validity
		if(theory.size() != numArgs || theory.getAttacks().size() != numAtt)
			throw new IOException("Illegal number of arguments and/or attacks.");
		return theory;
	}	
}

