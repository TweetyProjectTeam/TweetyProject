package net.sf.tweety.preferences.io;

import net.sf.tweety.preferences.PreferenceOrder;
import net.sf.tweety.preferences.Relation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

import net.sf.tweety.util.Triple;

/**
 * 
 * 
 * @author Bastian Wolf
 * @param <T>
 */
public class POWriter<T> {
	
	public void writeToFile(String filename, PreferenceOrder<T> po){
		
		PrintWriter pw = null;
		try {
		Writer fw = new FileWriter(filename);
		Writer bw = new BufferedWriter(fw);
		pw = new PrintWriter(bw);
		
		String s = "{";
		int count = 1;
		for (T e : po.getDomainElements()){
			
			if (count < po.getDomainElements().size())
				s += e.toString() + ", ";
			else
				s += e.toString();
		count++;
		}
		
		s += "}";
		
		pw.println(s);
		
		Iterator<Triple<T, T, Relation>> it = po.iterator();
		while (it.hasNext()){
			Triple<T, T, Relation> temp = it.next();
			if(temp.getThird() == Relation.LESS){
				pw.println(temp.getFirst() + " < " + temp.getSecond());
			}
			if(temp.getThird() == Relation.LESS_EQUAL){
				pw.println(temp.getFirst() + " <= " + temp.getSecond());
			}
		}
		} catch (IOException e){
			System.out.println("File could not be generated");
		} finally {
			if (pw != null)
				pw.close();
		}
	}
}
