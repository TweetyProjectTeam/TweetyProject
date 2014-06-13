/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logicprogramming.asp.solver;

import net.sf.tweety.logicprogramming.asp.syntax.*;

import java.io.*;
import java.util.*;

/**
 * container class for ELPSource instances
 * 
 * @author Thomas Vengels
 *
 */
public class SourceList extends LinkedList<ELPSource> {
	
	/**
	 * adds a program to the source list
	 * 
	 * @param elp program to add
	 */
	public void add(ELP elp) {
		ELPSource src = new ELPSource(elp);
		this.add(src);
	}
	
	/**
	 * adds a collection of facts to the source list
	 * 
	 * @param facts	facts to add
	 */
	public void add(Collection<ELPLiteral> facts) {
		ELPSource src = new ELPSource(facts);
		this.add(src);
	}
	
	/**
	 * adds a program on disc to the source list
	 * 
	 * @param fileName elp file to add
	 */
	public void add(String fileName) {
		ELPSource src = new ELPSource(fileName);
		this.add(src);
	}
	

	/**
	 * this method saves the source list as a single
	 * extended logical program.
	 * 
	 * @param filename destination file to create
	 * @throws IOException
	 */
	public void saveAs(String filename) throws IOException {
		BufferedWriter w = new BufferedWriter(new FileWriter(filename));
		
		for (ELPSource src : this) {
			if (src.getType() == ELPSource.SRC_ELP) {
				ELP e = src.resELP;
				ArrayList<ELPRule> rules = e.getRules();
				for ( ELPRule rule : rules) {
					w.write( rule.toString() );
					w.newLine();
				}
			}
			
			if (src.getType() == ELPSource.SRC_FACT) {
				src.addResource(w);
			}
			
			if (src.getType() == ELPSource.SRC_FILE) {
				BufferedReader fr = new BufferedReader( new FileReader(src.getSrcFile()) );
				String ln = fr.readLine();
				while (ln != null){
					w.write(ln);
					w.newLine();
					ln = fr.readLine();
				}  
			}
		}
		
		w.flush();
		w.close();
	}
}
