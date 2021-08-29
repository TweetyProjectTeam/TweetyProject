package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.*;

public class SimpleClInheritedReasoner{
	private static AbstractExtensionReasoner reasoner;
	
	/**
	 * constructor for direct initialization of semantics
	 * @param semantics the Dung semantics
	 */
	public SimpleClInheritedReasoner(Semantics semantics) {
		setSemantics(semantics);
		
	}
	/**
	 * empty constructor
	 */
	public SimpleClInheritedReasoner() {
		
	}
	/**
	 * calculates all claim sets for a given framework
	 * @param bbase the Dung framework to be evaluated
	 * @return the claim sets
	 */
	public Set<Set<String>> getModels(ArgumentationFramework<Argument> bbase) {
		
		Collection<Extension> ext = reasoner.getModels(bbase);
		
		Set<Set<String>> claims = new HashSet<Set<String>>();
		for(Extension e : ext) {
			Set<String> extensionClaims = new HashSet<String>();
			for(Argument a : e) {
				extensionClaims.add(((ClaimBasedTheory) bbase).getClaimMap().get(a));
			}
			claims.add(extensionClaims);
		}
		return claims;
	}

	/**
	 * calculates one claim set for a given framework
	 * @param bbase the Dung framework to be evaluated
	 * @return the claim set
	 */
	public Set<String> getModel(ArgumentationFramework<Argument> bbase) {
		Extension ext = reasoner.getModel(bbase);		
	
		Set<String> extensionClaims = new HashSet<String>();
		for(Argument a : ext) {
			extensionClaims.add(((ClaimBasedTheory) bbase).getClaimMap().get(a));
		}
			
		
		return extensionClaims;
	}
	/**
	 * manually sets the semantics
	 * @param semantics the Dung semantics
	 */
	public static void setSemantics(Semantics semantics){
		reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
		
	}



}
