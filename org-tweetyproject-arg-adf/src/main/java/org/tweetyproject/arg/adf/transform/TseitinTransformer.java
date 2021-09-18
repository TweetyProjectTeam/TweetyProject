package org.tweetyproject.arg.adf.transform;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ConjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ContradictionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.DisjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.EquivalenceAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ExclusiveDisjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ImplicationAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.NegationAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.TautologyAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.util.CacheMap;
import org.tweetyproject.arg.adf.util.Pair;

public final class TseitinTransformer implements Collector<Literal, Clause>, Transformer<Pair<Literal, Collection<Clause>>> {

	private final boolean optimize;
		
	private final int rootPolarity;
	
	private final Function<Argument, Literal> mapping;
	
	private final Literal TRUE = Literal.create("T");
	
	private final Literal FALSE = TRUE.neg();

	private TseitinTransformer(Function<Argument, Literal> mapping, boolean optimize, int rootPolarity) {
		this.mapping = Objects.requireNonNull(mapping);
		this.optimize = optimize;
		this.rootPolarity = rootPolarity;
	}
	
	public static TseitinTransformer ofPositivePolarity(boolean optimize) {
		return ofPositivePolarity(new CacheMap<>(arg -> Literal.create(arg.getName())), optimize);
	}
	
	public static TseitinTransformer ofNegativePolarity(boolean optimize) {
		return ofNegativePolarity(new CacheMap<>(arg -> Literal.create(arg.getName())), optimize);
	}
	
	public static TseitinTransformer ofPositivePolarity(Function<Argument, Literal> mapping, boolean optimize) {
		return new TseitinTransformer(mapping, optimize, 1);
	}
	
	public static TseitinTransformer ofNegativePolarity(Function<Argument, Literal> mapping, boolean optimize) {
		return new TseitinTransformer(mapping, optimize, -1);
	}

	@Override
	public Literal collect(AcceptanceCondition acc, Consumer<Clause> clauses) {
		clauses.accept(Clause.of(TRUE)); // fix truth value
		return define(acc, clauses);
	}

	@Override
	public Pair<Literal, Collection<Clause>> transform(AcceptanceCondition acc) {
		List<Clause> clauses = new LinkedList<>();
		Literal name = collect(acc, clauses);
		return Pair.of(name, clauses);
	}

	private void defineConjunction(Literal name, Literal left, Literal right, Consumer<Clause> clauses, int polarity) {
		if (polarity >= 0 || !optimize) {
			clauses.accept(Clause.of(left, name.neg()));
			clauses.accept(Clause.of(right, name.neg()));
		}
		if (polarity <= 0 || !optimize) {
			clauses.accept(Clause.of(left.neg(), right.neg(), name));
		}
	}

	private void defineDisjunction(Literal name, Literal left, Literal right, Consumer<Clause> clauses, int polarity) {
		if (polarity >= 0 || !optimize) {
			clauses.accept(Clause.of(left, right, name.neg()));
		}
		if (polarity <= 0 || !optimize) {
			clauses.accept(Clause.of(left.neg(), name));
			clauses.accept(Clause.of(right.neg(), name));
		}
	}
	
	private void defineImplication(Literal name, Literal left, Literal right, Consumer<Clause> clauses, int polarity) {
		if (polarity >= 0 || !optimize) {
			clauses.accept(Clause.of(name.neg(), left.neg(), right));
		}
		if (polarity <= 0 || !optimize) {
			clauses.accept(Clause.of(name, left));
			clauses.accept(Clause.of(name, right.neg()));
		}
	}
	
	private void defineEquivalence(Literal name, Literal left, Literal right, Consumer<Clause> clauses, int polarity) {
		if (polarity >= 0 || !optimize) {
			clauses.accept(Clause.of(name.neg(), left, right.neg()));
			clauses.accept(Clause.of(name.neg(), left.neg(), right));
		}
		if (polarity <= 0 || !optimize) {
			clauses.accept(Clause.of(left, right, name));
			clauses.accept(Clause.of(left.neg(), right.neg(), name));
		}
	}
	
	private void defineExclusiveDisjunction(Literal name, Literal left, Literal right, Consumer<Clause> clauses, int polarity) {
		if (polarity >= 0 || !optimize) {
			clauses.accept(Clause.of(name.neg(), left, right));
			clauses.accept(Clause.of(name.neg(), left.neg(), right.neg()));
		}
		if (polarity <= 0 || !optimize) {
			clauses.accept(Clause.of(name, left.neg(), right));
			clauses.accept(Clause.of(name, left, right.neg()));
		}
	}
	
	private void defineNegation(Literal name, Literal child, Consumer<Clause> clauses, int polarity) {
		if (polarity >= 0 || !optimize) {
			clauses.accept(Clause.of(name.neg(), child.neg()));
		}
		if (polarity <= 0 || !optimize) {
			clauses.accept(Clause.of(name, child));
		}
	}
	
	private Literal define(AcceptanceCondition acc, Consumer<Clause> clauses) {
		Literal name = createRootName(acc);
		define(name, acc, clauses, rootPolarity);
		return name;
	}

	/**
	 * Works as a replacement to the Visitor-Pattern approach, since it is expected
	 * to be faster. The goal is to replace it with pattern matching, once it is available in a future java release.
	 * 
	 * @param name
	 * @param acc
	 * @param clauses
	 * @param polarity
	 */
	private void define(Literal name, AcceptanceCondition acc, Consumer<Clause> clauses, int polarity) {
		if (acc instanceof BinaryAcceptanceCondition) {
			BinaryAcceptanceCondition bin = (BinaryAcceptanceCondition) acc;
			Literal left = createName(bin.getLeft());
			Literal right = createName(bin.getRight());
			if (acc instanceof ConjunctionAcceptanceCondition) {
				defineConjunction(name, left, right, clauses, polarity);
				define(left, bin.getLeft(), clauses, polarity);
				define(right, bin.getRight(), clauses, polarity);
			} else if (acc instanceof DisjunctionAcceptanceCondition) {
				defineDisjunction(name, left, right, clauses, polarity);
				define(left, bin.getLeft(), clauses, polarity);
				define(right, bin.getRight(), clauses, polarity);
			} else if (acc instanceof ImplicationAcceptanceCondition) {
				defineImplication(name, left, right, clauses, polarity);
				define(left, bin.getLeft(), clauses, -polarity);
				define(right, bin.getRight(), clauses, polarity);
			} else if (acc instanceof EquivalenceAcceptanceCondition) {
				defineEquivalence(name, left, right, clauses, polarity);
				define(left, bin.getLeft(), clauses, polarity);
				define(right, bin.getRight(), clauses, polarity);
			} else if (acc instanceof ExclusiveDisjunctionAcceptanceCondition) {
				defineExclusiveDisjunction(name, left, right, clauses, polarity);
				define(left, bin.getLeft(), clauses, polarity);
				define(right, bin.getRight(), clauses, polarity);
			} 
		} else if (acc instanceof NegationAcceptanceCondition) {
			Literal child = createName(((NegationAcceptanceCondition) acc).getChild());
			defineNegation(name, child, clauses, polarity);
			define(child, ((NegationAcceptanceCondition) acc).getChild(), clauses, -polarity);
		}
	}
		
	private Literal createName(AcceptanceCondition acc) {
		if (acc instanceof Argument) {
			return mapping.apply((Argument)acc);
		} else if (acc instanceof TautologyAcceptanceCondition) {
			return TRUE;
		} else if (acc instanceof ContradictionAcceptanceCondition) {
			return FALSE;
		} else {
			return Literal.createTransient();
		}
	}
	
	private Literal createRootName(AcceptanceCondition acc) {
		if (acc instanceof Argument) {
			return mapping.apply((Argument)acc);
		} else if (acc instanceof TautologyAcceptanceCondition) {
			return TRUE;
		} else if (acc instanceof ContradictionAcceptanceCondition) {
			return FALSE;
		} else {
			return Literal.create();
		}
	}

}
