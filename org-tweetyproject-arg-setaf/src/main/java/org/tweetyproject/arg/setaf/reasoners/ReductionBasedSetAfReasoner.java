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
package org.tweetyproject.arg.setaf.reasoners;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.setaf.syntax.SetAf;
import org.tweetyproject.arg.setaf.syntax.SetAttack;

import java.util.Collection;
import java.util.HashSet;

/**
 * Reasoner for SetAfs that reduces the SetAf to a dung theory via a polynomial reduction
 *
 * @see "Modgil and Bench-Capon (2011), 'Metalevel argumentation', J. Log. Comput."
 *
 * @author Lars Bengel
 */
public class ReductionBasedSetAfReasoner extends AbstractSetAfExtensionReasoner {

    /** the underlying semantics reasoner */
    private final AbstractExtensionReasoner reasoner;

    /**
     * Initializes new SetAf reasoner with the given dung reasoner
     * @param reasoner some dung reasoner
     */
    public ReductionBasedSetAfReasoner(AbstractExtensionReasoner reasoner) {
        this.reasoner = reasoner;
    }

    /**
     * Initializes new SetAf reasoner with the given dung semantics
     * @param semantics some semantics
     */
    public ReductionBasedSetAfReasoner(Semantics semantics) {
        this(AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics));
    }

    @Override
    public Collection<Extension<SetAf>> getModels(SetAf bbase) {
        DungTheory theory = getDungTheory(bbase);
        Collection<Extension<SetAf>> result = new HashSet<>();
        for (Extension<DungTheory> ext : reasoner.getModels(theory)) {
            Extension<SetAf> new_ext = new Extension<>();
            for (Argument arg : ext) {
                if (bbase.contains(arg)) {
                    new_ext.add(arg);
                }
            }
            result.add(new_ext);
        }
        return result;
    }

    @Override
    public Extension<SetAf> getModel(SetAf bbase) {
        return getModels(bbase).iterator().next();
    }

    /**
     * Computes a dung theory for the given SetAf
     * @param bbase some SetAf
     * @return the corresponding dung theory
     */
    public DungTheory getDungTheory(SetAf bbase) {
        DungTheory theory = new DungTheory();
        theory.addAll(bbase);
        for (SetAttack att : bbase.getAttacks()) {
            Argument arg1 = new Argument(String.format("_att(%s,%s)", att.getAttackers(), att.getAttacked()));
            theory.add(arg1);
            theory.addAttack(arg1, att.getAttacked());
            for (Argument x : att.getAttackers()) {
                Argument arg2 = new Argument(String.format("_att(%s,%s,%s)", att.getAttackers(), att.getAttacked(), x));
                theory.add(arg2);
                theory.addAttack(x, arg2);
                theory.addAttack(arg2, arg1);
            }
        }
        return theory;
    }

    @Override
    public boolean isInstalled() {
        return true;
    }
}
