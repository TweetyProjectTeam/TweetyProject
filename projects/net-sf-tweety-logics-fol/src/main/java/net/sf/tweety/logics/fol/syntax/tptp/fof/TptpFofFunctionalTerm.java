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
@SuppressWarnings("unused")
public class TptpFofFunctionalTerm extends TptpFofStringTerm {

	/**
	 * the describing functor of this functional term
	 */
    private TptpFofFunctor functor;

    /**
     * the arguments of this functional term
     */
    private List<TptpFofTerm<?>> arguments;

    /**
     * target sort?
     * TODO: necessary?
     */
    private TptpFofSort target;

    public TptpFofFunctionalTerm(){
    	
    }
    
    /**
     * Standard Constructor with given value
     * @param value
     */
    public TptpFofFunctionalTerm(String name) {
    	super();
        this.functor = new TptpFofFunctor(name);
    }

    public TptpFofFunctionalTerm(TptpFofFunctor functor, List<TptpFofTerm<?>> arguments) {
    	super();
        this.functor = functor;
        this.arguments = arguments;
    }

    @Override
    public void set(String value) {
        
    	// TODO: impelement
    }

	@Override
	public String get() {
		String s = this.getName();
		if(arguments.size()>0){
			s+="(";
			for (TptpFofTerm<?> t : arguments){
				Object obj = t.get();
				if(obj instanceof String)
					s+=(String) obj + ",";
			}
			// remove the last unnecessary ","-Symbol and add closing parenthesis
			s = s.substring(0, s.length()-2) + ")";
		}
		return s;
	}
}
