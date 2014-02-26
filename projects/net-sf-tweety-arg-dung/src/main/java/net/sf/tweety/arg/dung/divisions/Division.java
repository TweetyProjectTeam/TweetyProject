package net.sf.tweety.arg.dung.divisions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.util.Pair;
import net.sf.tweety.util.SetTools;

/**
 * A pair <P,V> is a division of AAF G w.r.t. semantics X iff there is an X-extension E
 * such that P\subseteq E and E \cap V = {}, cf. [Hunter,Thimm,2014].
 * 
 * @author Matthias Thimm
 */
public class Division extends Pair<Extension,Extension>{

	/**
	 * Creates a new division for the given parameters.
	 * @param p some set of arguments.
	 * @param v some set of arguments
	 */
	public Division(Extension p, Extension v){
		super(p,v);
	}
	
	/**
	 * Checks whether this division is valid wrt. some of the given extensions
	 * @param exts a collection of extensions.
	 * @return "true" if this division is valid.
	 */
	public boolean isValid(Collection<Extension> exts){
		for(Extension e: exts)
			if(this.isValid(e))
				return true;
		return false;
	}
	
	/**
	 * Checks whether this division is valid wrt. the given extension.
	 * @param ext some extension
	 * @return "true" iff this division is valid.
	 */
	public boolean isValid(Extension ext){
		if(!ext.containsAll(this.getFirst()))
			return false;
		Set<Argument> tmp = new HashSet<Argument>(ext);
		tmp.retainAll(this.getSecond());
		if(tmp.size()>0)
			return false;
		return true;
	}
	
	/**
	 * Returns all divisions of all given extensions and the given aaf.
	 * @param exts a collection of extensions.
	 * @param aaf a Dung theory
	 * @return the set of divisions of all extensions returned by the given reasoner.
	 */
	public static Collection<Division> getDivisions(Collection<Extension> exts, DungTheory aaf){
		Collection<Division> result = new HashSet<Division>();
		for(Extension e: exts)
			result.addAll(Division.getDivisions(e, aaf));
		return result;
	}
	
	/**
	 * Returns all divisions of aaf that arise from the given extension.
	 * @param ext some extension 
	 * @param aaf some Dung theory
	 * @return the set of divisions of aaf that arise from the given extension.
	 */
	public static Collection<Division> getDivisions(Extension ext, DungTheory aaf){
		Collection<Division> result = new HashSet<Division>();
		Collection<Argument> remaining = new HashSet<Argument>(aaf);
		remaining.removeAll(ext);
		SetTools<Argument> setTools = new SetTools<Argument>();
		Set<Set<Argument>> subsetsExt = setTools.subsets(ext);
		Set<Set<Argument>> subsetsRem = setTools.subsets(remaining);
		for(Set<Argument> p: subsetsExt)
			for(Set<Argument> v: subsetsRem)
				result.add(new Division(new Extension(p),new Extension(v)));
		return result;		
	}
	
}
