package net.sf.tweety.arg.aba.semantics;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.tweety.arg.aba.ABATheory;
import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.aba.syntax.Deduction;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

public class ABAAttack<T extends Invertable> extends Attack {

	public ABAAttack(Argument attacker, Argument attacked) {
		super(attacker, attacked);
	}
	
	public static <T extends Invertable> boolean  attacks (Deduction<T> attacker, Assumption<T> attacked) {
		return attacked instanceof Assumption
				&& attacker.getConclusion().equals(attacked.getConclusion().complement());
	}

	public static <T extends Invertable>Collection<ABAAttack<T>> allAttacks(Collection<Assumption<T>> from, Collection<Assumption<T>> to,
			ABATheory<T> abat) {
		Collection<ABAAttack<T>> result = new ArrayList<>();
		for (Deduction<T> deduction : abat.getAllDeductions(from))
			for (Assumption<T> assumption : to)
				if (deduction.getConclusion().equals(assumption.getConclusion().complement())) {
					Deduction<T> ass = new Deduction<>("");
					ass.setRule(assumption);
					result.add(new ABAAttack<>(deduction, ass));
				}
		return result;
	}
	
	public static <T extends Invertable>Collection<ABAAttack<T>> allAttacks(ABATheory<T> abat) {
		return allAttacks(abat.getAssumptions(),abat.getAssumptions(),abat);		
	}
	
	

}
