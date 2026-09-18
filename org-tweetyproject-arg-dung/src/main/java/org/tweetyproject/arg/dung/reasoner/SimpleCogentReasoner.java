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
package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Simple reasoner for cogent semantics. Cogent extensions only need to defend against non-self-attacking arguments.
 *
 * @see "Bodanza, Gustavo A., and Fernando A. Tohmè. 'Two approaches to the problems of self-attacking arguments and general odd-length cycles of attack.' Journal of Applied Logic 7.4 (2009)"
 *
 * @author Lars Bengel
 */
public class SimpleCogentReasoner extends AbstractExtensionReasoner {
    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        Collection<Extension<DungTheory>> result = new HashSet<>();
        for (Extension<DungTheory> ext : new SimpleConflictFreeReasoner().getModels(bbase)) {
            boolean cogent = true;
            for (Argument arg : ext) {
                if (!isCogentlyDefended(bbase, ext, arg)) {
                    cogent = false;
                    continue;
                }
                if (!cogent) break;
            }
            if (cogent) {
                result.add(ext);
            }
        }
        return result;
    }

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {
        return getModels(bbase).iterator().next();
    }

    private boolean isCogentlyDefended(DungTheory theory, Extension<DungTheory> extension, Argument argument) {
        for (Argument attacker : theory.getAttackers(argument)) {
            if (theory.isAttackedBy(attacker,attacker)) continue;
            if (!theory.isAttacked(attacker, extension)) return false;
        }
        return true;
    }
}
