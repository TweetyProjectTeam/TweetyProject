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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisibility.sequence;

import java.util.LinkedHashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.*;
import org.tweetyproject.arg.dung.syntax.*;

/**
 * This class represents a sequence of set of arguments, which is typically generated during the process 
 * of finding extensions in a framework by serialising sets of arguments.
 * 
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisationSequence extends LinkedHashSet<Extension<DungTheory>> {

	private static final long serialVersionUID = -109538431325318647L;
	
	/**
	 * @return The extension of this sequence, which has the most arguments and which is thus seen as the final extension
	 */
	public Extension<DungTheory> getExtensionFinal() {
		Extension<DungTheory> output = null;
		for (Extension<DungTheory> extension : this) {
			if(output == null || extension.size() > output.size()) output = extension;
		}
		return output;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
            return true;

        if (!(o instanceof Set))
            return false;
        SerialisationSequence c = (SerialisationSequence) o;
        if (c.size() != size())
            return false;
        try {
        	return super.equals(c);
        } catch (ClassCastException | NullPointerException unused) {
            return false;
        }
	}
}
