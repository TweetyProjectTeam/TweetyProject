package net.sf.tweety.arg.dung.syntax;

import net.sf.tweety.commons.*;
import net.sf.tweety.graphs.Node;

/**
 * This class models an argument used by Dung's abstract argumentation theory and is just described by its name.
 * @author Matthias Thimm
 *
 */
public class Argument implements Formula, Node{
	/**
	 * The name of the argument.
	 */
	private String name;

	/**
	 * Default constructor that assigns the given <source>name</source> to this argument
	 * @param name the name of the argument
	 */
	public Argument(String name){
		this.name = name;
	}

	/**
	 * returns the name of the argument
	 * @return the name of the argument
	 */
	public String getName(){
		return name;
	}

	/**
	 * sets the name of the argument
	 * @param name the name of the argument
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Formula#getSignature()
	 */
	public Signature getSignature(){
		return new DungSignature(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(!o.getClass().equals(this.getClass())) return false;
		if(!((Argument)o).getName().equals(name)) return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		return this.name.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return name;
	}
}
