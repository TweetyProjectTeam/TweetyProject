/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.fol.syntax;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;

/**
 * The classical disjunction of first-order logic.
 * @author Matthias Thimm
 */
public class Disjunction extends AssociativeFOLFormula{
	
	/**
	 * Creates a new disjunction with the given inner formulas. 
	 * @param formulas a collection of formulas.
	 */
	public Disjunction(Collection<? extends RelationalFormula> formulas){
		super(formulas);
	}
	
	/**
	 * Creates a new (empty) disjunction.
	 */
	public Disjunction(){
		this(new HashSet<RelationalFormula>());
	}
	
	/**
	 * Creates a new disjunction with the two given formulae
	 * @param first a relational formula.
	 * @param second a relational formula.
	 */
	public Disjunction(RelationalFormula first, RelationalFormula second){
		this();
		this.add(first);
		this.add(second);
	}	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#isDnf()
	 */
	@Override
	public boolean isDnf(){
		for(RelationalFormula f: this)
			if(!((FolFormula)f).isDnf() && !(f instanceof Disjunction)) return false;
		return true;
	}
	
		
	 /*
   * (non-Javadoc)
   * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#toNNF()
   */
  @Override
  public FolFormula toNnf() {
    Disjunction d = new Disjunction();
    for(RelationalFormula p : this) {
      if(p instanceof FolFormula)
        d.add( ((FolFormula) p).toNnf() );
      else
        throw new IllegalStateException("Can not convert conjunctions containing non-first-order formulae to NNF.");
    }
    return d;
  }
  
  /*
   * (non-Javadoc)
   * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#collapseAssociativeFormulas()
   */
  @Override
  public RelationalFormula collapseAssociativeFormulas() {
    if(this.isEmpty())
      return new Contradiction();
    if(this.size() == 1)
      return ((FolFormula)this.iterator().next()).collapseAssociativeFormulas();
    Disjunction newMe = new Disjunction();
    for(RelationalFormula f: this){
      if(! (f instanceof FolFormula)) throw new IllegalStateException("Can not collapse disjunctions containing non-first-order formulae.");
      RelationalFormula newF = ((FolFormula)f).collapseAssociativeFormulas();
      if(newF instanceof Disjunction)
        newMe.addAll((Disjunction) newF);
      else newMe.add(newF);
    }
    return newMe;
  }
	
	@Override
	public Disjunction clone() {
		return new Disjunction(support.copyHelper(this));
	}
	
	//-------------------------------------------------------------------------
	//	ASSOC SUPPORT BRIDGE METHODS
	//-------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	@Override
	public Disjunction createEmptyFormula() {
		return new Disjunction();
	}

	@Override
	public String getOperatorSymbol() {
		return LogicalSymbols.DISJUNCTION();
	}

	@Override
	public String getEmptySymbol() {
		return LogicalSymbols.CONTRADICTION();
	}
}
