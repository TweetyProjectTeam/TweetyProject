/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.weighted.writer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.weighted.syntax.WeightedArgumentationFramework;

/**
 *  * Writes an weighted abstract argumentation framework into a file of the
 * APX format including the weight value of each attack. Weights on attacks are specified by the suffix :- followed by the weight value. 
 * 
 * @author Sandra Hoffmann
 *
 */
public class WeightedApxWriter {

	/**
	 * Writes the weighted argumentation framework to a file.
	 *
	 * @param waf The WeightedArgumentationFramework to write.
	 * @param f   The File where the framework should be written.
	 * @throws IOException If an I/O error occurs while writing to the file.
	 */
	@SuppressWarnings("rawtypes")
	public void write(WeightedArgumentationFramework waf, File f) throws IOException {
	    write(waf, f, -1, false);
	}
	
	/**
	 * Writes the weighted argumentation framework to a file.
	 *
	 * @param waf The WeightedArgumentationFramework to write.
	 * @param f   The File where the framework should be written.
	 * @param precision The precision for formatting floating-point numbers. Use -1 to retain the original value.
	 * @throws IOException If an I/O error occurs while writing to the file.
	 */
	@SuppressWarnings("rawtypes")
	public void write(WeightedArgumentationFramework waf, File f, int precision) throws IOException {
	    write(waf, f, precision, false);
	}
	
	/**
	 * Writes the weighted argumentation framework to a file.
	 *
	 * @param waf The WeightedArgumentationFramework to write.
	 * @param f   The File where the framework should be written.
	 * @param convertToNumericWeight Convert non numeric values to their assigned double values.
	 * @throws IOException If an I/O error occurs while writing to the file.
	 */
	@SuppressWarnings("rawtypes")
	public void write(WeightedArgumentationFramework waf, File f, Boolean convertToNumericWeight) throws IOException {
	    write(waf, f, -1, convertToNumericWeight);
	}

	/**
	 * Writes the weighted argumentation framework to a file with the specified precision.
	 *
	 * @param waf       The WeightedArgumentationFramework to write.
	 * @param f         The File where the framework should be written.
	 * @param precision The precision for formatting floating-point numbers. Use -1 to retain the original value.
	 * @param convertToNumericWeight Convert non numeric values to their assigned double values.
	 * @throws IOException If an I/O error occurs while writing to the file.
	 */
	@SuppressWarnings("rawtypes")
	public void write(WeightedArgumentationFramework waf, File f, int precision, Boolean convertToNumericWeight) throws IOException {
	    PrintWriter writer = new PrintWriter(f, "UTF-8");
	    Object weight;
	    for (Object a : waf)
	        writer.println("arg(" + a + ").");

	    for (Attack att : waf.getAttacks()) {
	        String formattedWeight;
	        if (isFloatingPoint(waf.getWeight(att)) && precision != -1) {
	            // Check if it's a floating-point number and apply precision formatting
	        	weight = waf.getWeight(att);
                // Round the floating-point number to the specified precision
                double roundedValue = Math.round((double)weight * Math.pow(10, precision)) / Math.pow(10, precision);
                formattedWeight = String.valueOf(roundedValue);
	        } else {
	            // Use the original value if precision is -1 or if it's not a floating-point number
	        	// Use numeric value if convertToNumericWeight is true
	        	if (convertToNumericWeight) {
	        		formattedWeight = String.valueOf(waf.getNumericWeight(att));
	        	} else {
		            formattedWeight = String.valueOf(waf.getWeight(att));
	        	}
	        	
	        }
	        
	        writer.println("att(" + att.getAttacker().getName() + "," + att.getAttacked().getName() + "):-" + formattedWeight + ".");
	    }

	    writer.close();
	}

	/**
	 * Checks if the given object is a floating-point number (float or double).
	 *
	 * @param object The object to check.
	 * @return True if the object is a floating-point number, false otherwise.
	 */
	private boolean isFloatingPoint(Object object) {
	    return object instanceof Float || object instanceof Double;
	}

}
