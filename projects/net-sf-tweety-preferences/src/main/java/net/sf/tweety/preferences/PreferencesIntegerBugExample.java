package net.sf.tweety.preferences;

import java.util.HashSet;

import net.sf.tweety.math.opt.solver.LpSolve;
import net.sf.tweety.util.Triple;

public class PreferencesIntegerBugExample {

	public static void main(String[] args) {
		LpSolve.setBinary("/home/bwolf/Applikationen/lpsolve/source/lp_solve_5.5/lp_solve/bin/ux64/lp_solve");
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
