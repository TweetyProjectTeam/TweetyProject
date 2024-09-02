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
package org.tweetyproject.arg.bipolar.analysis.extensions;

import org.tweetyproject.arg.bipolar.analysis.AnalysisType;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.bipolar.syntax.NamedPEAFTheory;

/**
 * Computes the preferred extension of the given PEAF
 * <p>
 * Uses `jargsemsat` for computing extensions.
 *
 * @author Taha Dogan Gunes
 */
public class PreferredAnalysis extends AbstractExtensionAnalysis {
    /**
     * Constructor
     * @param peaf NamedPEAFTheory
     */
    public PreferredAnalysis(NamedPEAFTheory peaf) {
        super(peaf, new SimplePreferredReasoner(), AnalysisType.PREFERRED);
    }
}
