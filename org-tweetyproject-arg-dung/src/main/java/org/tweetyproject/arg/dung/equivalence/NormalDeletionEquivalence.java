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
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.SetTools;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * This class defines normal deletion equivalence for {@link DungTheory argumentation frameworks} wrt. some {@link Semantics semantics},
 *  i.e., two AFs F and G are normal deletion equivalent iff they possess the same set of
 *  {@link org.tweetyproject.arg.dung.semantics.Extension extensions} wrt. the {@link Semantics semantics} under every normal deletion.
 *  A normal deletion deletes a set of arguments together with all their corresponding attacks.
 *
 * @see "Ringo Baumann. 'Context-free and context-sensitive kernels: Update and deletion equivalence in abstract argumentation' ECAI14 (2014) pp. 63–68"
 *
 * @author Lars Bengel
 */
public class NormalDeletionEquivalence implements Equivalence<DungTheory> {
    /** the semantics of this equivalence instance **/
    private final Semantics semantics;

    /**
     * Initializes a new instance of this equivalence wrt. the given semantics
     * @param semantics some semantics
     */
    public NormalDeletionEquivalence(Semantics semantics) {
        this.semantics = semantics;
    }

    @Override
    public boolean isEquivalent(DungTheory obj1, DungTheory obj2) {
        switch (semantics) {
            case CO,GR,SAD -> {
                if (!loop(obj1, obj2)) return false;
                if (!coAtt(obj1, obj2)) return false;
                Collection<Argument> shared = new SetTools<Argument>().getIntersection(new HashSet<>(obj1), new HashSet<>(obj2));
                EquivalenceKernel kernel = EquivalenceKernel.getStrongExpansionEquivalenceKernelForSemantics(semantics);
                return kernel.getKernel((DungTheory) obj1.getRestriction(shared)).equals(kernel.getKernel((DungTheory) obj2.getRestriction(shared)));
            } case ADM,PR,UC,ID -> {
                if (!loop(obj1, obj2)) return false;
                if (!admAtt(obj1, obj2)) return false;
                Collection<Argument> shared = new SetTools<Argument>().getIntersection(new HashSet<>(obj1), new HashSet<>(obj2));
                EquivalenceKernel kernel = EquivalenceKernel.getStrongExpansionEquivalenceKernelForSemantics(semantics);
                return kernel.getKernel((DungTheory) obj1.getRestriction(shared)).equals(kernel.getKernel((DungTheory) obj2.getRestriction(shared)));
            } case ST -> {
                return new StrongEquivalence(Semantics.ST).isEquivalent(obj1, obj2);
            } case SST,EA -> {
                return isNormalDeletionEquivalent(obj1,obj2,semantics);
            } default -> throw new IllegalArgumentException("unsupported semantics");
        }
    }

    @Override
    public boolean isEquivalent(Collection<DungTheory> objects) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String getName() {
        return "Normal Deletion Equivalence";
    }

    /**
     * Naively checks whether two AFs are normal deletion equivalence by comparing the extensions of every normal deletion
     *
     * @param theory1 some argumentation framework
     * @param theory2 some argumentation framework
     * @param semantics some semantics
     * @return 'true' iff both AFs are normal deletion equivalent under the given semantics
     */
    public static boolean isNormalDeletionEquivalent(DungTheory theory1, DungTheory theory2, Semantics semantics) {
        AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
        Set<Argument> arguments = new HashSet<>(theory1);
        arguments.addAll(theory2);
        for (Set<Argument> subset: new SetTools<Argument>().subsets(arguments)) {
            DungTheory th1 = theory1.clone();
            th1.removeAll(subset);
            DungTheory th2 = theory2.clone();
            th2.removeAll(subset);
            if (!reasoner.getModels(th1).equals(reasoner.getModels(th2))) {
                //System.out.println(subset);
                //System.out.println(th1);
                //System.out.println(th2);
                return false;
            }
        }
        return true;
    }

    private boolean loop(DungTheory theory1, DungTheory theory2) {
        Collection<Argument> symDif = new SetTools<Argument>().symmetricDifference(theory1, theory2);
        DungTheory combi = theory1.clone();
        combi.add(theory2);
        return symDif.equals(getSelfLoops((DungTheory) combi.getRestriction(symDif)));
    }

    private boolean coAtt(DungTheory theory1, DungTheory theory2) {
        Collection<Argument> args1 = new HashSet<>(theory1);
        args1.removeAll(theory2);
        Collection<Argument> args2 = new HashSet<>(theory2);
        args2.removeAll(theory1);

        for (Argument b: args1) {
            for (Argument a: getNonSelfLoops((DungTheory) theory1.getRestriction(new SetTools<Argument>().getIntersection(new HashSet<>(theory1), new HashSet<>(theory2))))) {
                if (theory1.isAttackedBy(a,b)) {
                    return false;
                }
            }
        }
        for (Argument b: args2) {
            for (Argument a: getNonSelfLoops((DungTheory) theory2.getRestriction(new SetTools<Argument>().getIntersection(new HashSet<>(theory1), new HashSet<>(theory2))))) {
                if (theory2.isAttackedBy(a,b)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean admAtt(DungTheory theory1, DungTheory theory2) {
        Collection<Argument> args1 = new HashSet<>(theory1);
        args1.removeAll(theory2);
        Collection<Argument> args2 = new HashSet<>(theory2);
        args2.removeAll(theory1);

        for (Argument b: args1) {
            for (Argument a: getNonSelfLoops((DungTheory) theory1.getRestriction(new SetTools<Argument>().getIntersection(new HashSet<>(theory1), new HashSet<>(theory2))))) {
                if (theory1.isAttackedBy(a,b) && !theory1.isAttackedBy(b,a)) {
                    return false;
                }
            }
        }
        for (Argument b: args2) {
            for (Argument a: getNonSelfLoops((DungTheory) theory2.getRestriction(new SetTools<Argument>().getIntersection(new HashSet<>(theory1), new HashSet<>(theory2))))) {
                if (theory2.isAttackedBy(a,b) && !theory2.isAttackedBy(b,a)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Collection<Argument> getSelfLoops(DungTheory theory) {
        Collection<Argument> result = new HashSet<>();
        for (Argument arg : theory) {
            if (theory.isAttackedBy(arg,arg)) {
                result.add(arg);
            }
        }
        return result;
    }

    private Collection<Argument> getNonSelfLoops(DungTheory theory) {
        Collection<Argument> result = new HashSet<>();
        for (Argument arg : theory) {
            if (!theory.isAttackedBy(arg,arg)) {
                result.add(arg);
            }
        }
        return result;
    }
}
