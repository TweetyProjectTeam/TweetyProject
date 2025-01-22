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
package org.tweetyproject.preferences.io;

import org.tweetyproject.commons.util.Triple;
import org.tweetyproject.preferences.PreferenceOrder;
import org.tweetyproject.preferences.Relation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

/**
 *
 * POWriter class
 * @author Bastian Wolf
 * @param <T> generic preference order type
 */
public class POWriter<T> {

	/**
	 * Write to file
	 * @param filename the filename
	 * @param po preference order
	 */
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

    /** Default Constructor */
    public POWriter(){}
}
