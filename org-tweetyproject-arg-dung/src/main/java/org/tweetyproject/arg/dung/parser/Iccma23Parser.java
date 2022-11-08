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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
 package org.tweetyproject.arg.dung.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Parses abstract argumentation frameworks in the ICCMA 23 format 
 * (see also https://iccma2023.github.io/rules.html#input-format).
 * It is given by the following BNF (start symbol is S):<br>
 * <br>
 * S 	     ::== PROBLEM "\n" ATTACKS<br> 
 * PROBLEM   ::== "p af " INT<br>
 * ATTACKS   ::== (INT " " INT "\n")*  
 * 
 * where lines starting with "#" are comments and ignored during
 * parsing. INT is a positive natural number. The line beginning with "p af" is the
 * problem definition, it is followed by a positive integer which is the number
 * of arguments. After the problem definition there is one line for each attack.
 * The first integer in these lines describe the attacker of the attack and the
 * second the attacked.<br/>
 * <br/>
 * NOTE: this format is a slight variation of the CNF format (see CnfParser)
 * 
 * @author Matthias Thimm
 *
 */
public class Iccma23Parser extends AbstractDungParser {

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.parser.AbstractDungParser#parse(java.io.File)
	 */
	@Override
	public DungTheory parse(Reader reader) throws IOException {
		DungTheory theory = new DungTheory();
		int numArgs = -1;
		String attacker, attacked;
		BufferedReader in = new BufferedReader(reader);
		String row = null;
		while ((row = in.readLine()) != null) {
			row = row.trim();
			if(row.equals("")) continue;
			// ignore comment lines
			if(row.startsWith("#")) continue;
			// problem definition line, check validity later
			if(row.startsWith("p af")){
				numArgs = Integer.parseInt(row.substring(4).trim());				
				continue;
			}
			// line describing an attack
			attacker = row.substring(0,row.indexOf(" ")).trim();
			attacked = row.substring(row.indexOf(" ")+1).trim();
			theory.add(new Argument("a" + attacker));
			theory.add(new Argument("a" + attacked));
			theory.add(new Attack(new Argument("a" + attacker),new Argument("a" + attacked)));
		}
		in.close();
		// check validity
		if(theory.size() != numArgs)
			throw new IOException("Illegal number of arguments and/or attacks.");
		return theory;
	}	
}

