/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.fol.syntax.tptp.fof;

import java.util.List;

/**
 * @author Bastian Wolf
 */
public class TptpFofPredicate {

	/**
	 * Predicate name
	 */
    private String name;

	/**
	 * list of arguments
	 */
    private List<TptpFofTerm<?>> arguments;

    /**
	 * arity of arguments 
	 */
    private int arity;

    /*
     * Constructor
     */
    public TptpFofPredicate(){

    }

    public TptpFofPredicate(String name) {
        this.name = name;
    }

    public TptpFofPredicate(String name, List<TptpFofTerm<?>> arguments) {
        this.name = name;
        this.arguments = arguments;
        this.arity = arguments.size();
    }

    /*
     * Getter
     */
    public String getName() {
        return name;
    }

    public List<TptpFofTerm<?>> getArguments() {
        return arguments;
    }

    public int getArity() {
        return arity;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TptpFofPredicate that = (TptpFofPredicate) o;

        if (arity != that.arity) return false;
        if (arguments != null ? !arguments.equals(that.arguments) : that.arguments != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }
    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        result = 31 * result + arity;
        return result;
    }
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	String s = this.name.toString();
    	s+= "(";
    	for(TptpFofTerm<?> term : this.arguments){
    		s+=term.toString()+ ",";
    	}
    	s = s.substring(0,s.length()-1) + ")";
        return s;

    }
}
