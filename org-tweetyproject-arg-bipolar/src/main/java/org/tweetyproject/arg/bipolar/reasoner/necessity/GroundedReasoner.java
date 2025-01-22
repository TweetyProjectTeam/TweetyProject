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
package org.tweetyproject.arg.bipolar.reasoner.necessity;

import org.tweetyproject.arg.bipolar.syntax.*;

import java.util.Collection;
import java.util.HashSet;

/**
 * the grounded extension of bbase is the least fixed point of the characteristic function.
 *
 * @author Lars Bengel
 *
 */
public class GroundedReasoner {
	/**
	 *
	 * Return models
	 * @param bbase argumentation framework
	 * @return models
	 */
    public Collection<ArgumentSet> getModels(NecessityArgumentationFramework bbase) {
        Collection<ArgumentSet> extensions = new HashSet<>();
        extensions.add(this.getModel(bbase));
        return extensions;
    }

	/**
	 *
	 * Return model
	 * @param bbase argumentation framework
	 * @return model
	 */
    public ArgumentSet getModel(NecessityArgumentationFramework bbase) {
        ArgumentSet ext = new ArgumentSet();
        int size;
        do{
            size = ext.size();
            ext = bbase.faf(ext);
        }while(size!=ext.size());
        return ext;
    }

    /** Default Constructor */
    public GroundedReasoner(){}
}
