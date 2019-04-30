package net.sf.tweety.arg.adf.syntax;

import java.util.Collection;

import net.sf.tweety.commons.SingleSetSignature;

public class AbstractDialecticalFrameworkSignature extends SingleSetSignature<Argument>{

	public AbstractDialecticalFrameworkSignature() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AbstractDialecticalFrameworkSignature(Argument f) {
		super();
		this.add(f);
	}

	public AbstractDialecticalFrameworkSignature(Collection<? extends Argument> formulas) {
		super();
		this.addAll(formulas);
	}

	@Override
	public void add(Object obj) {
		if (obj instanceof Argument)
			formulas.add((Argument) obj);
		else
			throw new IllegalArgumentException("Unknown type " + obj.getClass());
	}
	
}
