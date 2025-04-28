package org.tweetyproject.arg.dung.equivalence.kernel;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

public class LocalExpansionCompleteKernel extends EquivalenceKernel {
    @Override
    public Collection<Attack> getRedundantAttacks(DungTheory theory) {
        Collection<Attack> attacks = new HashSet<>();
        for (Argument a : theory) {
            if (!theory.isAttackedBy(a,a)) continue;
            for (Argument b : theory) {
                if (a.equals(b)) continue;
                if (theory.isAttackedBy(b, b)) {
                    attacks.add(new Attack(a, b));
                }
                for (Argument c : theory) {
                    if (c.equals(a)) continue;
                    if (theory.isAttackedBy(a, b) && theory.isAttackedBy(b, c)) {
                        attacks.add(new Attack(a, b));
                        break;
                    }
                }
            }
        }
        //System.out.println(attacks);
        return attacks;
    }
}

