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
