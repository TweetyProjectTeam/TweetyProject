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

/**
 * This class models fof-entries written to tptp files.
 * 
 * General:
 * fof(<name>, <role>, <formula><annotations>)
 * Example:
 * fof(animal_martin, axiom, animal(martin)"Constant 'martin' is from Sort 'animal'")   
 * 
 * @author Bastian Wolf
 */
public class TptpFofFormulaFormat {

	/**
	 * The name of this formula entry
	 */
    private String name;

    /**
     * The role describes the role of this entry
     * Currently only "axiom" (for the knowledge base) and
     * "conjecture" (for queries) are used
     */
    private TptpFofFormulaRole role;

    /**
     * The formula of this entry
     */
    private TptpFofFormula formula;

    /**
     * Annotation describing the purpose of this entry, linking sources etc.
     * Optional.
     */
    private String annotation;

    /**
     * Base constructor for entries without initial annotation.
     * @param name the name of this entry
     * @param role the role of this formula (axiom or conjecture)
     * @param formula the formula itself
     */
    public TptpFofFormulaFormat(String name, TptpFofFormulaRole role, TptpFofFormula formula) {
        this.name = name;
        this.role = role;
        this.formula = formula;
    }

    /**
     * Extended constructor for entries with initial annotation.
     * @param name the name of this entry
     * @param role the role of this formula (axiom or conjecture)
     * @param formula the formula itself
     * @param annotation further annotations
     */
    public TptpFofFormulaFormat(String name, TptpFofFormulaRole role, TptpFofFormula formula, String annotation) {
        this.name = name;
        this.role = role;
        this.formula = formula;
        this.annotation = annotation;
    }

    /*
     * Getter
     */
    
    public String getName() {
        return name;
    }

    public TptpFofFormulaRole getRole() {
        return role;
    }

    public TptpFofFormula getFormula() {
        return formula;
    }

    public String getAnnotation() {
        return annotation;
    }

    /*
     * Setter
     */
    
    public void setName(String name) {
        this.name = name;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }


    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TptpFofFormulaFormat that = (TptpFofFormulaFormat) o;

        if (annotation != null ? !annotation.equals(that.annotation) : that.annotation != null) return false;
        if (formula != null ? !formula.equals(that.formula) : that.formula != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (role != null ? !role.equals(that.role) : that.role != null) return false;

        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (formula != null ? formula.hashCode() : 0);
        result = 31 * result + (annotation != null ? annotation.hashCode() : 0);
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String s = "fof(" + name + "," + role + "," + formula.toString();
        if (annotation !=null)
            s+= annotation;
        s+=")";
        return s;
    }

}
