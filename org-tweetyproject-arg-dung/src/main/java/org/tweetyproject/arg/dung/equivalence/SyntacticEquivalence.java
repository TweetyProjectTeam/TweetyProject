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
 *  Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.equivalence;

import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;

/**
 * This class defines 'syntactic' equivalence of {@link DungTheory Argumentation Frameworks},
 * i.e., it checks whether two AFs have exactly the same arguments and attacks.
 *
 * @author Julian Sander, Lars Bengel
 */
public class SyntacticEquivalence implements Equivalence<DungTheory> {
    @Override
    public boolean isEquivalent(DungTheory theory1, DungTheory theory2) {
        if (!theory1.getNodes().equals(theory2.getNodes())) return false;
        return theory1.getAttacks().equals(theory2.getAttacks());
    }

    @Override
    public boolean isEquivalent(Collection<DungTheory> theories) {
        DungTheory first = theories.iterator().next();

        for(DungTheory theory : theories) {
            if (!isEquivalent(theory, first)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return "Syntactic Equivalence";
    }
}
