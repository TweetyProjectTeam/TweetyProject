package net.sf.tweety.logics.fol.syntax.tptp.fof;

import java.util.List;

/**
 * 
 * <functor> ::= <atomic_word>
 * @author Bastian Wolf
 *
 */
public class TptpFofFunctor {
	
	/**
	 * Atom name
	 */
	private TptpFofAtom name;
	
	/**
	 * list of arguments
	 */
	private List<TptpFofSort> arguments;
	
	/**
	 * arity of arguments 
	 */
	private int arity;
	
	/**
	 * 
	 * @param name
	 */
	public TptpFofFunctor(TptpFofAtom name){
		this.name = name;
		this.arity=0;
	}
	
	/**
	 * 
	 * @param name
	 * @param arguments
	 */
	public TptpFofFunctor(TptpFofAtom name, List<TptpFofSort> arguments){
		this.name = name;
		this.arguments = arguments;
		this.arity = arguments.size();
	}

	/*
	 * Getter
	 */
	public TptpFofAtom getName() {
		return name;
	}

	public List<TptpFofSort> getArguments() {
		return arguments;
	}

	public int getArity() {
		return arity;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((arguments == null) ? 0 : arguments.hashCode());
		result = prime * result + arity;
		result = prime * result
				+ ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TptpFofFunctor other = (TptpFofFunctor) obj;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		if (arity != other.arity)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		// TODO implement
		return null;
	}
}
