package org.tweetyproject.arg.dung.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.DefaultSubsetIterator;
import org.tweetyproject.commons.util.SubsetIterator;

/**
 * Contains some methods for checking realizability
 * of extension sets, see e.g. [Dunne, Dvorak, Linsbichler, Woltran: Characteristics of multiple viewpoints in abstract argumentation.
 *  Artificial Intelligence 228: 153-178, 2015]
 */
public abstract class Realizability {

	/**
	 * Returns the set of all arguments appearing in some extension.
	 * @param exts some set of extensions
	 * @return the set of all arguments appearing in some extension.
	 */
	public static Collection<Argument> args(Collection<Extension<DungTheory>> exts){
		Collection<Argument> args = new HashSet<>();
		for(Extension<DungTheory> ext: exts)
			args.addAll(ext);
		return args;
	}
	
	/**
	 * Checks whether the given pair of arguments is a pair in the extension set, i.e.
	 * whether there is an extension that contains both arguments.
	 * @param exts some set of extensions
	 * @param a some argument
	 * @param b some argument
	 * @return true, if the two arguments are a pair.
	 */
	public static boolean isPair(Collection<Extension<DungTheory>> exts, Argument a, Argument b){
		for(Extension<DungTheory> ext: exts)
			if(ext.contains(a) && ext.contains(b))
				return true;		
		return false;
	}
	
	/**
	 * Returns the set of sets of arguments {a,b}, s.t. both a and b appear
	 * in at least one extension.
	 * @param exts some set of extensions
	 * @return the set of all arguments appearing in some extension.
	 */
	public static Collection<Set<Argument>> pairs(Collection<Extension<DungTheory>> exts){
		Collection<Set<Argument>> pairs = new HashSet<>();
		List<Argument> args = new ArrayList<Argument>(Realizability.args(exts));
		Set<Argument> pair;
		for(int i = 0; i < args.size()-1; i++) {
			// {a,a} is always a pair
			pair = new HashSet<>();
			pair.add(args.get(i));
			pairs.add(pair);
			for(int j=i+1; j < args.size(); j++) {
				if(Realizability.isPair(exts, args.get(i), args.get(j))) {
					pair = new HashSet<>();
					pair.add(args.get(i));
					pair.add(args.get(j));
					pairs.add(pair);
				}
			}
		}
		pair = new HashSet<>();
		pair.add(args.get(args.size()-1));
		pairs.add(pair);
		return pairs;
	}	
	
	/**
	 * Checks whether the set is downward-closed, i.e., if it contains each subset of each
	 * contained set.
	 * @param exts some set of extensions
	 * @return true if the set is downward-closed
	 */
	public static boolean isDownwardClosed(Collection<Extension<DungTheory>> exts){
		// we do this check naively, could probably be optimised
		SubsetIterator<Argument> it;
		for(Extension<DungTheory> ext: exts) {
			it = new DefaultSubsetIterator<Argument>(new HashSet<>(ext));
			while(it.hasNext())
				if(!exts.contains(new Extension<DungTheory>(it.next())))
					return false;
		}		
		return true;
	}
	
	/**
	 * Checks whether the given set is tight, i.e., whether for all extensions ext and each
	 * argument a, if (ext with a) is not in the set, there there is an argument b in ext such that
	 * (a,b) is not a pair.
	 * @param exts some set of extensions
	 * @return true if the set is tight.
	 */
	public static boolean isTight(Collection<Extension<DungTheory>> exts){
		Collection<Argument> args = Realizability.args(exts);
		Collection<Set<Argument>> pairs = Realizability.pairs(exts);
		for(Extension<DungTheory> ext: exts) {
			for(Argument a: args) {
				if(!ext.contains(a)) {
					Extension<DungTheory> ext2 = new Extension<>();
					ext2.addAll(ext);
					ext2.add(a);
					if(!exts.contains(ext2)) {
						boolean witness_found = false;
						Set<Argument> set;
						for(Argument b: ext) {
							set = new HashSet<>();
							set.add(a);
							set.add(b);
							if(!pairs.contains(set)) {
								witness_found = true;
								break;
							}
						}
						if(!witness_found)
							return false;
					}
				}
			}
		}		
		return true;
	}
	
	/**
	 * Checks whether the given set is incomparable, i.e., whether for each pair
	 * ext1, ext2 of extensions we have that neither is a proper subset of the other.
	 * @param exts some set of extensions
	 * @return true if the set is incomparable.
	 */
	public static boolean isIncomparable(Collection<Extension<DungTheory>> exts){
		for(Extension<DungTheory> ext1: exts) {
			for(Extension<DungTheory> ext2: exts) {
				if(ext1 != ext2) {
					if(ext1.containsAll(ext2))
						return false;
				}
			}
		}		
		return true;
	}
	
	/**
	 * Checks whether the given set is adm-closed, i.e., whether for each pair
	 * ext1, ext2 of extensions we have that if all a in ext1 and b in ext2 are a pair, then (ext1 union ext2) is also
	 * an extension.
	 * @param exts some set of extensions
	 * @return true if the set is adm-closed.
	 */
	public static boolean isAdmClosed(Collection<Extension<DungTheory>> exts){
		Collection<Set<Argument>> pairs = Realizability.pairs(exts);
		for(Extension<DungTheory> ext1: exts) {
			for(Extension<DungTheory> ext2: exts) {
				if(ext1 != ext2) {
					// first check whether the union is an extension
					Extension<DungTheory> ext3 = new Extension<>();
					ext3.addAll(ext1);
					ext3.addAll(ext2);
					if(exts.contains(ext3))
						continue;
					// so there must be two arguments which are not a pair
					boolean witness_found = false;
					for(Argument a: ext1) {
						for(Argument b: ext2) {
							Set<Argument> set = new HashSet<>();
							set.add(a);
							set.add(b);
							if(!pairs.contains(set)) {
								witness_found = true;
								break;
							}
						}
						if(witness_found)
							break;
					}
					if(!witness_found)
						return false;
				}
			}
		}		
		return true;
	}
	
	/**
	 * Checks whether the given set if pref-closed, i.e., whether
	 * it is incomparable and adm-closed.
	 * @param exts a set of extensions
	 * @return true if the set is pref-closed.
	 */
	public static boolean isPrefClosed(Collection<Extension<DungTheory>> exts){
		return Realizability.isIncomparable(exts) && Realizability.isAdmClosed(exts);
	}
}
