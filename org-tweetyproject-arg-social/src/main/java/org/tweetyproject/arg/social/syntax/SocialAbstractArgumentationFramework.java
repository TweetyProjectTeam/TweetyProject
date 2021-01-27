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
package org.tweetyproject.arg.social.syntax;

import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.Graph;

/**
 * This class implements a social abstract argumentation framework from
 * [Joao Leite, Joao Martins. Social Abstract Argumentation. IJCAI 2011]
 * 
 * @author Matthias Thimm
 *
 */
public class SocialAbstractArgumentationFramework extends DungTheory{

	/** The number of positive votes of arguments */
	private Map<Argument,Integer> pos_votes;
	/** The number of negative votes of arguments */
	private Map<Argument,Integer> neg_votes;
	
	/**
	 * Creates a new empty social abstract argumentation framework
	 */
	public SocialAbstractArgumentationFramework(){
		super();
		this.pos_votes = new HashMap<Argument,Integer>();
		this.neg_votes = new HashMap<Argument,Integer>();
	}
	
	/**
	 * Creates a new social abstract argumentation framework from the given graph.
	 * @param graph some graph
	 */
	public SocialAbstractArgumentationFramework(Graph<Argument> graph){
		super(graph);
		this.pos_votes = new HashMap<Argument,Integer>();
		this.neg_votes = new HashMap<Argument,Integer>();
		for(Argument arg: this){
			this.pos_votes.put(arg, 0);
			this.neg_votes.put(arg, 0);
		}
	}
	
	/**
	 * Returns the number of positive votes of the given argument
	 * @param arg some argument
	 * @return the number of positive votes of the given argument
	 */
	public int getPositive(Argument arg){
		return this.pos_votes.get(arg);
	}
	
	/**
	 * Returns the number of negative votes of the given argument
	 * @param arg some argument
	 * @return the number of negative votes of the given argument
	 */
	public int getNegative(Argument arg){
		return this.neg_votes.get(arg);
	}
	
	/**
	 * Adds the given number of positive votes to the
	 * given argument
	 * @param arg some argument
	 * @param number some number
	 */
	public void voteUp(Argument arg, int number){
		this.pos_votes.put(arg, this.pos_votes.get(arg) + number);
	}
	
	/**
	 * Adds a positive vote to the given argument.
	 * @param arg some argument
	 */
	public void voteUp(Argument arg){
		this.voteUp(arg, 1);
	}
	
	/**
	 * Adds the given number of negative votes to the
	 * given argument
	 * @param arg some argument
	 * @param number some number
	 */
	public void voteDown(Argument arg, int number){
		this.neg_votes.put(arg, this.neg_votes.get(arg) + number);
	}
	
	/**
	 * Adds a negative vote to the given argument.
	 * @param arg some argument
	 */
	public void voteDown(Argument arg){
		this.voteDown(arg, 1);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.DungTheory#remove(org.tweetyproject.arg.dung.syntax.Argument)
	 */
	@Override
	public boolean remove(Argument a){
		boolean result = super.remove(a);
		this.pos_votes.remove(a);
		this.neg_votes.remove(a);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.BeliefSet#add(org.tweetyproject.commons.Formula)
	 */
	@Override
	public boolean add(Argument a){
		boolean result = super.add(a);
		if(result){
			this.pos_votes.put(a, 0);
			this.neg_votes.put(a, 0);
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.DungTheory#add(org.tweetyproject.arg.dung.DungTheory)
	 */
	@Override
	public boolean add(DungTheory theory){
		boolean result = super.add(theory);
		if(theory instanceof SocialAbstractArgumentationFramework){
			for(Argument a: theory){
				this.pos_votes.put(a, ((SocialAbstractArgumentationFramework)theory).getPositive(a));
				this.neg_votes.put(a, ((SocialAbstractArgumentationFramework)theory).getNegative(a));
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){		
		String s = "<{";
		boolean isFirst = true;
		for(Argument arg: this){
			if(isFirst)
				isFirst = false;
			else s += ",";
			s += arg + "(+" + this.pos_votes.get(arg)+ "-" +this.neg_votes.get(arg) + ")";
		}
		s += "},";
		s += super.getAttacks();
		s += ">";
		return s;
	}
}
