/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
package net.sf.tweety.logicprogramming.asp.syntax;

import java.io.*;
import java.util.*;

import net.sf.tweety.logicprogramming.asp.parser.ELPParser;

/**
 * this class models an extended logic program. it
 * consists of a set of rules (and fact and constraints).
 * 
 * @author Thomas Vengels
 *
 */
public class ELP {
	
	ArrayList<ELPRule>	rules = new ArrayList<ELPRule>();;
		
	
	public ELP() {
	}
	
	/**
	 * this method adds a rule to the program
	 * represented by this object.
	 * 
	 * @param r	rule to add
	 */
	public void add(ELPRule r) {
		rules.add(r);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for(ELPRule r : rules) {
			sb.append(r.toString()+"\n");
		}
		
		return sb.toString();
	}
	
	/**
	 * number of rules in elp
	 * @return
	 */
	public int nRules() {
		return rules.size();
	}
	
	/**
	 * get rule i
	 * @param i
	 * @return	rule with name (index) i
	 */
	public ELPRule getRule(int i) {
		return rules.get(i);
	}
	
	/**
	 * returns all elp rules in this program
	 *  
	 * @return	arraylist of elp rules
	 */
	public ArrayList<ELPRule> getRules() {
		return this.rules;
	}
	
	
	/**
	 * return all literals that are facts
	 * @return
	 */
	public List<ELPLiteral> getFacts() {
		ArrayList<ELPLiteral> ret = new ArrayList<ELPLiteral>();
		for (ELPRule r : rules) {
			if (!r.isFact())
				continue;
			
			int iI = r.nHead();
			for (int i = 0; i < iI; i++) {
				ret.add(r.getHead(i));
			}
		}
		return ret;
	}
	
	/**
	 * store elp on disc
	 * 
	 * @param file	output filename (like foo.elp or bar.txt)
	 */
	public void saveAs(String filename) {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(filename));
			for (ELPRule r : rules) {
				w.write(r.toString());
				w.newLine();
			}
			w.flush();
			w.close();
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * loads an elp from a file ressource
	 * 
	 * @param filename file to read elp from
	 */
	public static ELP loadFrom(String filename) {
		ELP ret = new ELP();
		
		try {
			
			return ELPParser.parse(filename);
			
		} catch (Exception e) {
			System.out.println("parser error while parsing "+filename+"!");
			e.printStackTrace();
		}
		
		return ret;
	}
	
	
	
	/**
	 * this methods adds all rules from the given
	 * program to this program
	 * @param program2	program to add
	 */
	public void add(ELP program2) {
		if (program2 == null)
			return;
		
		this.rules.addAll(program2.getRules());
	}
}
