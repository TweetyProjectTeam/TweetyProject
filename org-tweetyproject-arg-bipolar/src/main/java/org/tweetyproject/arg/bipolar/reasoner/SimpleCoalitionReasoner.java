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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.bipolar.reasoner;

import org.tweetyproject.arg.bipolar.syntax.BipolarArgumentationFramework;
import org.tweetyproject.arg.bipolar.syntax.CoalitionArgument;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Reasoner for bipolar argumentation frameworks based on the coalition approach.
 * A coalition is defined as a maximal set of arguments that is conflict-free and connected via the support relation.
 *
 * @see "Cayrol, C. and M.-C. Lagasquie-Schiex. 'Coalitions of arguments: a tool for handling bipolar
 * argumentation frameworks'. International Journal of Intelligent Systems, 25(1):83–109, 2010."
 *
 * @author Lars Bengel
 */
public class SimpleCoalitionReasoner extends AbstractBipolarExtensionReasoner {
    /** The underlying reasoner */
    private final AbstractExtensionReasoner reasoner;

    /**
     * Initializes a new coalition reasoner for the given semantics
     * @param semantics some semantics
     */
    public SimpleCoalitionReasoner(Semantics semantics) {
        this.reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
    }

    @Override
    public Collection<Extension<BipolarArgumentationFramework>> getModels(BipolarArgumentationFramework bbase) {
        Collection<Extension<BipolarArgumentationFramework>> result = new HashSet<>();
        DungTheory coalitionGraph = bbase.getCoalitionGraph();
        Collection<Extension<DungTheory>> extensions = reasoner.getModels(coalitionGraph);
        for (Extension<DungTheory> ext: extensions) {
            Extension<BipolarArgumentationFramework> extension = new Extension<>();
            for (Argument arg: ext) {
                extension.addAll(((CoalitionArgument) arg).getArguments());
            }
            result.add(extension);
        }
        return result;
    }

    @Override
    public Extension<BipolarArgumentationFramework> getModel(BipolarArgumentationFramework bbase) {
        return getModels(bbase).iterator().next();
    }
}
