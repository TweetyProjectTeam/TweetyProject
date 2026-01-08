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

import org.tweetyproject.arg.dung.equivalence.kernel.EquivalenceKernel;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.SetTools;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class defines local (expansion) equivalence for {@link DungTheory argumentation frameworks} wrt. some {@link Semantics semantics},
 * i.e., two AFs F and G are local expansion equivalent iff they possess the same set of
 * {@link org.tweetyproject.arg.dung.semantics.Extension extensions} wrt. the {@link Semantics semantics} under every local expansion
 *
 * @see "Emilia Oikarinen and Stefan Woltran. 'Characterizing strong equivalence for argumentation frameworks.' Artificial intelligence 175.14-15 (2011): 1985-2009."
 *
 * @author Lars Bengel
 */
public class LocalExpansionEquivalence implements Equivalence<DungTheory> {

    private final Semantics semantics;

    /**
     * Initializes a new instance of this equivalence notion
     * @param semantics some semantics
     */
    public LocalExpansionEquivalence(Semantics semantics) {
        this.semantics = semantics;
    }

    @Override
    public boolean isEquivalent(DungTheory theory1, DungTheory theory2) {
        switch (semantics) {
            case ADM,PR,ID,UC,SST,EA -> {
                return new StrongEquivalence(Semantics.ADM).isEquivalent(theory1, theory2);
            }
            case GR,SAD -> {
                if (!new HashSet<>(theory1).equals(new HashSet<>(theory2))) {
                    return false;
                }
                if (!theory1.faf(new Extension<>()).equals(theory2.faf(new Extension<>()))) {
                    return false;
                }

                Collection<Argument> S = theory1.faf(new Extension<>());

                if (S.size() == 1) {
                    Argument a = S.iterator().next();
                    if (isSelfLoopPathological(theory1.getReduct(a)) && isSelfLoopPathological(theory2.getReduct(a))) {
                        return true;
                    }

                    if (!EquivalenceKernel.GROUNDED.getKernel(theory1.getReduct(a)).equals(EquivalenceKernel.GROUNDED.getKernel(theory2.getReduct(a)))) {
                        return false;
                    }
                    Argument b1 = getBPathological(theory1);
                    Argument b2 = getBPathological(theory2);
                    if (b1 == null || !b1.equals(b2)) {
                        return false;
                    }
                    for (Argument d : theory1.getReduct(a).getAttackers(b1)) {
                        if (!theory2.getReduct(a).isAttackedBy(b1,d)) {
                            return false;
                        }
                    }
                    for (Argument d : theory2.getReduct(a).getAttackers(b1)) {
                        if (!theory1.getReduct(a).isAttackedBy(b1,d)) {
                            return false;
                        }
                    }
                } else {
                    for (Argument a : S) {
                        if (!EquivalenceKernel.GROUNDED.getKernel(theory1.getReduct(a)).equals(EquivalenceKernel.GROUNDED.getKernel(theory2.getReduct(a)))) {
                            return false;
                        }
                    }
                }
                return true;
            }
            case CO -> {
                return isLocalExpansionEquivalent(theory1, theory2, semantics);
            } case ST -> {
                if (EquivalenceKernel.STABLE.getKernel(theory1).equals(EquivalenceKernel.STABLE.getKernel(theory2))) {
                    return true;
                }
                if (new SimpleStableReasoner().getModels(theory1).isEmpty() && new SimpleStableReasoner().getModels(theory2).isEmpty()) {
                    Collection<Argument> arguments = new HashSet<>(theory1);
                    arguments.addAll(theory2);
                    for (Argument a : arguments) {
                        if (theory1.contains(a) && theory2.contains(a)) continue;
                        if (!theory1.isAttackedBy(a, a) || !theory2.isAttackedBy(a, a)) {
                            boolean notB = false;
                            for (Argument b : arguments) {
                                if (b.equals(a)) continue;
                                if (!theory1.isAttackedBy(b, a) || !theory2.isAttackedBy(b, a)) {
                                    if (theory1.isAttackedBy(b, b) && theory2.isAttackedBy(b, b)) {
                                        notB = true;
                                        break;
                                    }
                                }
                            }
                            if (notB) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
            default -> throw new IllegalArgumentException("Unsupported Semantics");
        }
    }

    @Override
    public boolean isEquivalent(Collection<DungTheory> theories) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getName() {
        return "Local Expansion Equivalence";
    }

    private boolean isSelfLoopPathological(DungTheory theory) {
        for (Argument arg : theory) {
            if (!theory.isAttackedBy(arg, arg)) {
                return false;
            }
        }
        return true;
    }

    private boolean isBPathological(DungTheory theory, Argument b) {
        if (theory.isAttackedBy(b,b)) return false;
        for (Argument arg : theory) {
            if (arg.equals(b)) continue;
            if (!theory.isAttackedBy(arg,arg)) {
                return false;
            }
        }
        return true;
    }

    private Argument getBPathological(DungTheory theory) {
        for (Argument b : theory) {
            if (isBPathological(theory, b)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Naively checks whether two AFs are local equivalent by comparing the extensions of every local expansion
     *
     * @param theory1 some argumentation framework
     * @param theory2 some argumentation framework
     * @param semantics some semantics
     * @return 'true' iff both AFs are local equivalent under the given semantics
     */
    public static boolean isLocalExpansionEquivalent(DungTheory theory1, DungTheory theory2, Semantics semantics) {
        if (!new HashSet<>(theory1.getNodes()).equals(new HashSet<>(theory2.getNodes()))) {
            return false;
        }
        AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
        Set<Attack> attacks = new HashSet<>();
        for (Argument a: theory1) {
            for (Argument b: theory1) {
                attacks.add(new Attack(a,b));
            }
        }
        for (Set<Attack> subset: new SetTools<Attack>().subsets(attacks)) {
            DungTheory th1 = theory1.clone();
            th1.addAllAttacks(subset);
            DungTheory th2 = theory2.clone();
            th2.addAllAttacks(subset);
            if (!reasoner.getModels(th1).equals(reasoner.getModels(th2))) {
                //System.out.println(subset);
                return false;
            }
        }
        return true;
    }
}
