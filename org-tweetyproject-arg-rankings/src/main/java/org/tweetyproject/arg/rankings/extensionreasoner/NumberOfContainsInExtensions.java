package org.tweetyproject.arg.rankings.extensionreasoner;

import java.util.Collection;
import java.util.Vector;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class implements the <code>SupportVectorFunction<code> Interface to provide the specific implementation in order to 
 * compute the support vector concerning the Extension <code>ext<code>. In this case, the support vector consists of the number 
 * of each <code>ext<code> argument of appearances in <code>extensions<code>.  
 *  
 * 
 * @author Benjamin Birner
 *
 */
public class NumberOfContainsInExtensions implements SupportVectorFunction {

	@Override
	public Vector<Double> getSupportVector(Extension<DungTheory> ext, Collection<Extension<DungTheory>> extensions, boolean aggregate) {
		Vector<Double> vsupp = new Vector<>();
        for (Argument arg : ext) {
            vsupp.add(getNumberOfContainsInExtensions(arg, extensions));
        }
		return vsupp;
	}
	
	
	 /**
     * Returns the number of predefined Extensions in which arg is contained.
     * (ne_semantic(arg,theory)
     *
     * @param arg an argument
     * @param extensions set of Extensions
     * @return number of Extensions that arg appears in.
     */
    private Double getNumberOfContainsInExtensions(Argument arg, Collection<Extension<DungTheory>> extensions) {
        double count = 0d;
        for (Extension<DungTheory> ext : extensions) {
            if (ext.contains(arg)) {
                count++;
            }

        }
        return count;
    }

}
