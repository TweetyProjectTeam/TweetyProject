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
package org.tweetyproject.preferences;

import java.util.HashSet;

import org.tweetyproject.commons.util.Triple;
import org.tweetyproject.math.opt.solver.Solver;
import org.tweetyproject.math.opt.solver.LpSolve;

public class PreferencesIntegerBugExample {

	public static void main(String[] args) {
		LpSolve.setBinary("/home/bwolf/Applikationen/lpsolve/source/lp_solve_5.5/lp_solve/bin/ux64/lp_solve");
		Solver.setDefaultLinearSolver(new LpSolve());
/*		
		HashSet<Triple<String, String, Relation>> relations = new HashSet<Triple<String, String, Relation>>();
		relations.add(new Triple<String, String, Relation>("a", "b", Relation.LESS));
		relations.add(new Triple<String, String, Relation>("a", "c", Relation.LESS));
		relations.add(new Triple<String, String, Relation>("a", "d", Relation.LESS));
		relations.add(new Triple<String, String, Relation>("a", "e", Relation.LESS));
		relations.add(new Triple<String, String, Relation>("b", "c", Relation.LESS));
		relations.add(new Triple<String, String, Relation>("b", "d", Relation.LESS));
		relations.add(new Triple<String, String, Relation>("b", "e", Relation.LESS));
		relations.add(new Triple<String, String, Relation>("c", "d", Relation.LESS));
		relations.add(new Triple<String, String, Relation>("c", "e", Relation.LESS));
		relations.add(new Triple<String, String, Relation>("d", "e", Relation.LESS));	

		PreferenceOrder<String> po = new PreferenceOrder<String>(relations);
*/		
	
///*	
	HashSet<Triple<Integer, Integer, Relation>> relations = new HashSet<Triple<Integer, Integer, Relation>>();
	relations.add(new Triple<Integer, Integer, Relation>(1, 2, Relation.LESS));
	relations.add(new Triple<Integer, Integer, Relation>(1, 3, Relation.LESS));
	relations.add(new Triple<Integer, Integer, Relation>(1, 4, Relation.LESS));
	relations.add(new Triple<Integer, Integer, Relation>(2, 3, Relation.LESS));
	relations.add(new Triple<Integer, Integer, Relation>(2, 4, Relation.LESS));
	relations.add(new Triple<Integer, Integer, Relation>(3, 4, Relation.LESS));
	
	PreferenceOrder<Integer> po = new PreferenceOrder<Integer>(relations);
//*/
	
/*
	HashSet<Triple<Integer, Integer, Relation>> relations = new HashSet<Triple<Integer, Integer, Relation>>();
	relations.add(new Triple<Integer, Integer, Relation>(100, 200, Relation.LESS));
	relations.add(new Triple<Integer, Integer, Relation>(100, 300, Relation.LESS));
	relations.add(new Triple<Integer, Integer, Relation>(100, 400, Relation.LESS));
	relations.add(new Triple<Integer, Integer, Relation>(200, 300, Relation.LESS));
	relations.add(new Triple<Integer, Integer, Relation>(200, 400, Relation.LESS));
	relations.add(new Triple<Integer, Integer, Relation>(300, 400, Relation.LESS));

	PreferenceOrder<Integer> po = new PreferenceOrder<Integer>(relations);
*/
	
	System.out.println(po);
	System.out.println(po.getLevelingFunction());
	System.out.println(po.getLevelingFunction().getRankingFunction());
	
	}
}
