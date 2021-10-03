package org.tweetyproject.arg.dung.syntax;

public class ClaimArgument extends Argument{

	public ClaimArgument(String name, Claim claim) {
		super(name);
		this.claim = claim;
	}

	Claim claim;
	
	public Claim getClaim() {
		return this.claim;
	}
	public void setClaim(Claim c) {
		this.claim = c;
	}
	public String toString() {
		return this.getClaim().toString();
	}
	
}
