package org.tweetyproject.arg.dung.equivalence;

import org.tweetyproject.arg.dung.equivalence.kernel.EquivalenceKernel;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

public class LocalExpansionEquivalence implements Equivalence<DungTheory> {

    private final Semantics semantics;

    public LocalExpansionEquivalence(Semantics semantics) {
        this.semantics = semantics;
    }

    @Override
    public boolean isEquivalent(DungTheory theory1, DungTheory theory2) {
        switch (semantics) {
            case ADM,PR -> {
                return new StrongEquivalence(Semantics.ADM).isEquivalent(theory1, theory2);
            }
            case GR -> {
                if (!new HashSet<>(theory1).equals(new HashSet<>(theory2))) {
                    System.out.println("ARGS");
                    return false;
                }
                if (!theory1.faf(new Extension<>()).equals(theory2.faf(new Extension<>()))) {
                    System.out.println("FAF");
                    return false;
                }

                Collection<Argument> S = theory1.faf(new Extension<>());

                if (S.size() == 1) {
                    Argument a = S.iterator().next();
                    if (isSelfLoopPathological(theory1.getReduct(a)) && isSelfLoopPathological(theory2.getReduct(a))) {
                        System.out.println("SL_PAT");
                        return true;
                    }

                    if (!EquivalenceKernel.GROUNDED.getKernel(theory1.getReduct(a)).equals(EquivalenceKernel.GROUNDED.getKernel(theory2.getReduct(a)))) {
                        System.out.println("RED_GR_KERN");
                        return false;
                    }
                    Argument b1 = getBPathological(theory1);
                    Argument b2 = getBPathological(theory2);
                    if (b1 == null || !b1.equals(b2)) {
                        System.out.println(b1.toString() + b2.toString());
                        return false;
                    }
                    for (Argument d : theory1.getReduct(a).getAttackers(b1)) {
                        if (!theory2.getReduct(a).isAttackedBy(b1,d)) {
                            System.out.println("D_ATT_1");
                            return false;
                        }
                    }
                    for (Argument d : theory2.getReduct(a).getAttackers(b1)) {
                        if (!theory1.getReduct(a).isAttackedBy(b1,d)) {
                            System.out.println("D_ATT_2");
                            return false;
                        }
                    }
                } else {
                    for (Argument a : S) {
                        if (!EquivalenceKernel.GROUNDED.getKernel(theory1.getReduct(a)).equals(EquivalenceKernel.GROUNDED.getKernel(theory2.getReduct(a)))) {
                            System.out.println("GR_KERN");
                            return false;
                        }
                    }
                }
                return true;
            }
            case CO -> {
                //if (EquivalenceKernel.LE_COMPLETE.getKernel(theory1).equals(EquivalenceKernel.LE_COMPLETE.getKernel(theory2))) {
                //    Argument b = getBSaturated(theory1);
                //    Argument b2 = getBSaturated(theory2);
                //    if (b != null && b.equals(b2)) return true;
                //}
                if (!EquivalenceKernel.ADMISSIBLE.getKernel(theory1).equals(EquivalenceKernel.ADMISSIBLE.getKernel(theory2))) return false;
                if (!theory1.faf(new Extension<>()).equals(theory2.faf(new Extension<>()))) return false;
                Argument b = getBSaturated(theory1);
                if (b == null) return false;
                if (!b.equals(getBSaturated(theory2))) return false;
                return true;
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

    private boolean isBSaturated(DungTheory theory, Argument b) {
        if (theory.isAttackedBy(b,b)) return false;
        if (theory.getAttackers(b).isEmpty()) return false;
        for (Argument a : theory.getAttackers(b)) {
            if (!theory.isAttackedBy(a,a)) return false;
        }
        for (Argument a : theory.getAttackers(b)) {
            for (Argument d : theory) {
                if (theory.isAttackedBy(d,d)) continue;
                if (!theory.isAttackedBy(a,d)) return false;
            }

        }
        return true;
    }

    private Argument getBSaturated(DungTheory theory) {
        for (Argument b : theory) {
            if (isBSaturated(theory, b)) {
                return b;
            }
        }
        return null;
    }
}
