package org.tweetyproject.logics.translators.adfrevision;

import java.util.Collection;

import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlSignature;

/**
 * Implementation of Symmetric Distance Function "Delta" as proposed in [Heyninck 2020]
 * 
 * Adapted from class "DalalDistance" for the use of Three Valued Logic
 * 
 * @author Jonas Schumacher
 *
 */
public class DalalDistanceThreeValued {

	/**
	 * 	  
	 * Based on new version of PriestWorld_adapted
	 *
	 * @param a PriestWorldAdapted
	 * @param b PriestWorldAdapted
	 * @return distance between the 2 worlds
	 */

	public double distance(PriestWorldAdapted a, PriestWorldAdapted b) {
		double n = 0;
		PlSignature sig = new PlSignature();
		sig.addAll(a.getSignature().toCollection());
		sig.addAll(b.getSignature().toCollection());
		for(Proposition p: sig){
			PriestWorldAdapted.TruthValue val_a = a.get(p);
			PriestWorldAdapted.TruthValue val_b = b.get(p);
			
			// Same value: skip
			if (val_a == val_b) {
				n += 0;
			}
			// opposite value: add +1 
			else if (val_a == PriestWorldAdapted.TruthValue.TRUE && val_b == PriestWorldAdapted.TruthValue.FALSE) {
				n += 1;
			}
			else if (val_b == PriestWorldAdapted.TruthValue.TRUE && val_a == PriestWorldAdapted.TruthValue.FALSE) {
				n += 1;
			}
			// in all other cases: add +.5
			else {
				n += 0.5;
			}
		}
		return n;
	}
	/**
	 * 
	 * @param coll multiple PriestWorlds
	 * @param b single PriestWorldAdapted
	 * @return resulting distance 
	 */
	public double distance(Collection<PriestWorldAdapted> coll, PriestWorldAdapted b) {
		double n = b.getSignature().size();
		
		for (PriestWorldAdapted a : coll) {
			n = Math.min(n, this.distance(a, b));
		}
		return n;
	}
}
