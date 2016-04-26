package net.sf.tweety.arg.aspic.syntax;

public class Premise {
	
	private final String identifier;
	private final boolean axiom;
	
	public Premise(String identifier, boolean axiom) {
		super();
		this.identifier = identifier;
		this.axiom = axiom;
	}

	public String getIdentifier() {
		return identifier;
	}

	public boolean isAxiom() {
		return axiom;
	}
	
}
