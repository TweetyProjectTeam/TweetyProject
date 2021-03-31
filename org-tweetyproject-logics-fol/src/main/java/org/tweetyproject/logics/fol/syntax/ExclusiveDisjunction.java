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
package org.tweetyproject.logics.fol.syntax;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.logics.commons.LogicalSymbols;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;

/**
 * The exclusive disjunction (XOR) in first-order logic.
 * 
 * @author Anna Gessler
 */
public class ExclusiveDisjunction extends AssociativeFolFormula{
	
	/**
	 * Creates a new exclusive disjunction with the given inner formulas. 
	 * @param formulas a collection of formulas.
	 */
	public ExclusiveDisjunction(Collection<? extends RelationalFormula> formulas){
		super(formulas);
	}
	
	/**
	 * Creates a new (empty) exclusive disjunction.
	 */
	public ExclusiveDisjunction(){
		this(new HashSet<RelationalFormula>());
	}
	
	/**
	 * Creates a new exclusive disjunction with the two given formulae
	 * @param first a relational formula.
	 * @param second a relational formula.
	 */
	public ExclusiveDisjunction(RelationalFormula first, RelationalFormula second){
		this();
		this.add(first);
		this.add(second);
	}	
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.firstorderlogic.syntax.FolFormula#isDnf()
	 */
	@Override
	public boolean isDnf(){
		return false;
	}
	
	 /*
   * (non-Javadoc)
   * @see org.tweetyproject.logics.firstorderlogic.syntax.FolFormula#toNNF()
   */
  @Override
  public FolFormula toNnf() {
		if (!this.isEmpty()) {
			if (!(this.get(0) instanceof FolFormula))
				throw new IllegalStateException("Can not convert conjunctions containing non-first-order formulae to NNF.");
			FolFormula tempLeft = ((FolFormula)this.get(0)).toNnf();
			for (int i = 1; i < this.size(); i++) {
				if (!(this.get(i) instanceof FolFormula))
					throw new IllegalStateException("Can not convert conjunctions containing non-first-order formulae to NNF.");
				FolFormula tempRight = ((FolFormula)this.get(i)).toNnf();
				Conjunction left = new Conjunction(new Negation(tempLeft), tempRight);
				Conjunction right = new Conjunction(tempLeft, new Negation(tempRight));
				tempLeft = new Disjunction(left, right);
			}
			return (Disjunction) tempLeft;
		} else
			return new Disjunction();
  }
  
  /*
   * (non-Javadoc)
   * @see org.tweetyproject.logics.firstorderlogic.syntax.FolFormula#collapseAssociativeFormulas()
   */
  @Override
  public RelationalFormula collapseAssociativeFormulas() {
    if(this.isEmpty())
      return new Contradiction();
    if(this.size() == 1)
      return ((FolFormula)this.iterator().next()).collapseAssociativeFormulas();
    ExclusiveDisjunction newMe = new ExclusiveDisjunction();
    for(RelationalFormula f: this){
      if(! (f instanceof FolFormula)) throw new IllegalStateException("Can not collapse disjunctions containing non-first-order formulae.");
      RelationalFormula newF = ((FolFormula)f).collapseAssociativeFormulas();
      if(newF instanceof ExclusiveDisjunction)
        newMe.addAll((ExclusiveDisjunction) newF);
      else newMe.add(newF);
    }
    return newMe;
  }
	
	@Override
	public ExclusiveDisjunction clone() {
		return new ExclusiveDisjunction(support.copyHelper(this));
	}
	
	//-------------------------------------------------------------------------
	//	ASSOC SUPPORT BRIDGE METHODS
	//-------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	@Override
	public ExclusiveDisjunction createEmptyFormula() {
		return new ExclusiveDisjunction();
	}

	@Override
	public String getOperatorSymbol() {
		return LogicalSymbols.EXCLUSIVEDISJUNCTION();
	}

	@Override
	public String getEmptySymbol() {
		return LogicalSymbols.CONTRADICTION();
	}
}
