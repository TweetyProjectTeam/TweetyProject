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

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.bipolar.analysis.AbstractAnalysis;
import org.tweetyproject.arg.bipolar.analysis.AnalysisResult;
import org.tweetyproject.arg.bipolar.analysis.AnalysisType;
import org.tweetyproject.arg.bipolar.io.eaf.EAFToDAFConverter;
import org.tweetyproject.arg.bipolar.syntax.EAFTheory;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.NamedPEAFTheory;
import org.tweetyproject.arg.bipolar.syntax.PEAFTheory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractExtensionAnalysis extends AbstractAnalysis implements ExtensionAnalysis {
    /**
     * The default constructor
     *
     * @param peafTheory        The PEAF Theory
     * @param extensionReasoner The extension reasoner
     * @param analysisType      The type of the analysis
     */
    public AbstractExtensionAnalysis(PEAFTheory peafTheory, AbstractExtensionReasoner extensionReasoner, AnalysisType analysisType) {
        super(peafTheory, extensionReasoner, analysisType);
    }

    @Override
    public List<Set<String>> getExtensions() {
        // Convert peaf -> eaf -> daf, then run jargsemsat
        EAFTheory eafTheory = EAFTheory.newEAFTheory(peafTheory);
        DungTheory dungTheory = EAFToDAFConverter.convert(eafTheory);
        Collection<Extension<DungTheory>> extensions = extensionReasoner.getModels(dungTheory);

        NamedPEAFTheory namedPEAFTheory = (NamedPEAFTheory) this.peafTheory;
        List<Set<String>> results = new ArrayList<Set<String>>();
        for (Extension<DungTheory> extension : extensions) {
            Set<String> extensionWithNames = new HashSet<String>();
            for (Argument argument : extension) {

                String[] argumentNames = argument.getName().split("_");

                for (String argumentName : argumentNames) {
                    BArgument eArgument = namedPEAFTheory.getArguments().get(Integer.parseInt(argumentName));
                    String name = namedPEAFTheory.getNameOfArgument(eArgument);

                    extensionWithNames.add(name);
                }

            }
            results.add(extensionWithNames);
        }

        return results;
    }

    @Override
    public AnalysisResult query(Set<BArgument> args) {
        return null;
    }
}
