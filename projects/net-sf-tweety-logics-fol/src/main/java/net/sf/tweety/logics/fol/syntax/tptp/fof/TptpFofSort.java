/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.fol.syntax.tptp.fof;

import java.util.Set;

/**
 * This class represents the sort of any
 * @author Bastian Wolf
 */
public class TptpFofSort {

	
	/**
	 * The name of this sort
	 */
    private String name;

    /**
     * Set containing all constants of this sort
     */
    private Set<TptpFofConstant> constants;
    
    /**
     * Set containing all variables of this sort
     */
    private Set<TptpFofVariable> variables;
    
    public static final TptpFofSort THING = new TptpFofSort("Thing");
    
    /**
     * Empty Constructor
     */
    public TptpFofSort(){

    }

    /**
     * 
     * @param name
     */
    public TptpFofSort(String name){
        this.name = name;
    }

    /**
     * 
     * @param name
     */
    public TptpFofSort(String name, Set<? extends TptpFofTerm<?>> argument){
        this.name = name;
        for(TptpFofTerm<?> t : argument){
            add(t);
        }
    }
    
    /**
     * 
     * @return
     */
    public TptpFofSort(String name, Set<TptpFofConstant> constants, Set<TptpFofVariable> variables){
    	this.name = name;
    	this.constants = constants;
    	this.variables = variables;
    }
    
    /*
     * Getter
     */
    public String getName() {
        return name;
    }

    /*
     * Setter
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
	 * @param t
	 * @return
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public void add(TptpFofTerm<?> t) {
		if(t instanceof TptpFofConstant){
			this.constants.add((TptpFofConstant) t);
		} else if(t instanceof TptpFofVariable)
			this.variables.add((TptpFofVariable) t);
	}



	/*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TptpFofSort that = (TptpFofSort) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }
}
