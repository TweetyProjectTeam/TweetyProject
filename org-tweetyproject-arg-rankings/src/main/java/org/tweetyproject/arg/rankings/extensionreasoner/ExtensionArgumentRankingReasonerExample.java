package org.tweetyproject.arg.rankings.extensionreasoner;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.CategorizerRankingReasoner;
import org.tweetyproject.comparator.NumericalPartialOrder;


/**
 * This class illustrates the usage of the ExtensionArgumentRankingReasoner.
 * 
 * @author Benjamin Birner
 *
 */
public class ExtensionArgumentRankingReasonerExample {
	
	public static void main(String[] args) throws Exception {
		
		ArrayList<LinkedList<Extension<DungTheory>>> rankedExt = new ArrayList<>();
		LinkedList<Extension<DungTheory>> l1 = new LinkedList<>();
		LinkedList<Extension<DungTheory>> l2 = new LinkedList<>();
		LinkedList<Extension<DungTheory>> l3 = new LinkedList<>();
		LinkedList<Extension<DungTheory>> l4 = new LinkedList<>();
		
		Extension<DungTheory> e1 = new Extension<DungTheory>();
	    Extension<DungTheory> e2 = new Extension<DungTheory>();
	    Extension<DungTheory> e3 = new Extension<DungTheory>();
	    Extension<DungTheory> e4 = new Extension<DungTheory>();
	    Extension<DungTheory> e5 = new Extension<DungTheory>();
	    Extension<DungTheory> e6 = new Extension<DungTheory>();
	    Extension<DungTheory> e7 = new Extension<DungTheory>();
	    Extension<DungTheory> e8 = new Extension<DungTheory>();
	    Extension<DungTheory> e9 = new Extension<DungTheory>();
	    Extension<DungTheory> e10 = new Extension<DungTheory>();
	    
	    Argument a = new Argument("a");
	    Argument b = new Argument("b");
	    Argument c = new Argument("c");
	    Argument d = new Argument("d");
	    Argument e = new Argument("e");
	    Argument f = new Argument("f");
	    Argument g = new Argument("g");
	    Argument h = new Argument("h");
	    Argument i = new Argument("i");
	    
	    
	    e1.add(a);
	    e1.add(b);
	    e1.add(c);
	    e1.add(d);
	    e1.add(e);
	    e1.add(f);
	    e1.add(h);
	    
	    e2.add(b);
	    e2.add(c);
	    e2.add(d);
	    e2.add(e);
	    e2.add(f);
	    e2.add(h);
	    
	    e3.add(d);
	    e3.add(e);
	    e3.add(f);
	    e3.add(h);
	    
	    e4.add(d);
	    e4.add(e);
	    e4.add(h);
	    
	    l1.add(e1);
	    l1.add(e2);
	    l1.add(e3);
	    l1.add(e4);
	    
	    
	    
	    
	    
	    
	    
	    e5.add(b);
	    e5.add(g);
	    e5.add(i);
	  
	    e6.add(g);
	    e6.add(i);
	    
	    l2.add(e5);
	    l2.add(e6);
	    
	    
	   
	    e7.add(c);
	    
	    l3.add(e7);
	    
	    
	    
	    e8.add(d);
	    e8.add(e);
	    e8.add(h);
	  
	    e9.add(d);
	   
	    e10.add(e);
	    e10.add(h);
	    
	    l4.add(e8);
	    l4.add(e9);
	    l4.add(e10);
	    
	    rankedExt.add(l1);
	    rankedExt.add(l2);
	    rankedExt.add(l3);
	    rankedExt.add(l4);
	    
	    ExtensionArgumentRankingReasoner reasoner = new ExtensionArgumentRankingReasoner();
        ArrayList<LinkedList<Argument>> rankedArgs = reasoner.getRankedArguments(rankedExt);
	    
        System.out.println("Argument Ranking Test:");
	    int z = 1;
        for(LinkedList<Argument> l : rankedArgs) {
        	
        	if(l.size() > 0) {
        		System.out.print("Rank " +z+ ": ");
            	for(Argument aa : l) {
                	System.out.print(aa.toString());
                	
                }
            	z++;
            	System.out.println("");
        	}
        	
        }
        System.out.println();
        
        
        
        
        
        
        
        
        
        
        
        DungTheory example = new DungTheory();
        example.add(a);
        example.add(b);
        example.add(c);
        example.add(d);
        example.add(e);
        example.add(f);
        example.add(g);
        example.addAttack(a, d);
        example.addAttack(a, e);
        example.addAttack(a, f);
        example.addAttack(b, a);
        example.addAttack(c, b);
        example.addAttack(c, g);
        example.addAttack(d, c);
        example.addAttack(d, e);
        example.addAttack(e, c);
        example.addAttack(e, d);
        example.addAttack(f, g);
        example.addAttack(g, f);
        
        
        AcceptabilityBasedExtensionReasoner ABER = new AcceptabilityBasedExtensionReasoner();
        Collection<Extension<DungTheory>> prExtensions = new SimplePreferredReasoner().getModels(example);
        NumericalPartialOrder<Argument, DungTheory> catOrder = new CategorizerRankingReasoner().getModel(example);
        Map<Integer, Collection<Extension<DungTheory>>> ABERResult = ABER.getScoreToExtensionsMap(prExtensions, catOrder, example);
        ArrayList<LinkedList<Extension<DungTheory>>> ABERResultList = ABER.mapToArrayList(ABERResult);
        ArrayList<LinkedList<Argument>> ABERrankedArgs = reasoner.getRankedArguments(ABERResultList);
        
        System.out.println("AcceptabilityBasedExtensionReasoner Ranked Arguments:");
        z = 1;
        for(LinkedList<Argument> l : ABERrankedArgs) {
        	
        	if(l.size() > 0) {
        		System.out.print("Rank " +z+ ": ");
            	for(Argument aa : l) {
                	System.out.print(aa.toString());
                	
                }
            	z++;
            	System.out.println("");
        	}
        }
        System.out.println();
        
        
        
        
        
        
        
        
        RankBasedExtensionReasoner RBER = new RankBasedExtensionReasoner(AggregationFunction.AVG);
        Collection<Extension<DungTheory>> prExtensions2 = new SimplePreferredReasoner().getModels(example);
        NumericalPartialOrder<Argument, DungTheory> catOrder2 = new CategorizerRankingReasoner().getModel(example);
        Map<Vector<Double>, Set<Extension<DungTheory>>> RBERResult = RBER.getAggregatedVectorToExtensionMap(prExtensions2, catOrder2, example);
        ArrayList<LinkedList<Extension<DungTheory>>> RBERResultList = RBER.mapToArrayList(RBERResult);
        ArrayList<LinkedList<Argument>> RBERRrankedArgs = reasoner.getRankedArguments(RBERResultList);
        
        System.out.println("RankedBasedExtensionReasoner Ranked Arguments:");
        z = 1;
        for(LinkedList<Argument> l : RBERRrankedArgs) {
        	
        	if(l.size() > 0) {
        		System.out.print("Rank " +z+ ": ");
            	for(Argument aa : l) {
                	System.out.print(aa.toString());
                	
                }
            	z++;
            	System.out.println("");
        	}
        }
        System.out.println();
        
        
        
        
        
        
        
        
        
        
        
        Extension<DungTheory> e11 = new Extension<DungTheory>();
        Extension<DungTheory> e22 = new Extension<DungTheory>();
        
        e11.add(a);
        e11.add(b);
        
        e22.add(c);
        e22.add(d);
        
        Collection<Extension<DungTheory>> extensionList = new HashSet<>();
        extensionList.add(e11);
        extensionList.add(e22);
        
        
        CopelandBasedComparisonExtensionReasoner coba = new CopelandBasedComparisonExtensionReasoner();
        ArgumentsNonAttackedComparator argNonAtt = new ArgumentsNonAttackedComparator();
        ArrayList<LinkedList<Extension<DungTheory>>> res1 = coba.getModels(extensionList, example, argNonAtt);
        ArrayList<LinkedList<Argument>> copRankedArgs = reasoner.getRankedArguments(res1);
        
        System.out.println("CopelandBasedComparisonExtensionReasoner Ranked Arguments:");
        z = 1;
        for(LinkedList<Argument> l : copRankedArgs) {
        	
        	if(l.size() > 0) {
        		System.out.print("Rank " +z+ ": ");
            	for(Argument aa : l) {
                	System.out.print(aa.toString());
                	
                }
            	z++;
            	System.out.println("");
        	}
        }
        System.out.println();
        
        
        
        
        
        ExtensionRankingReasoner cf_reasoner = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_CF);
        List<List<Extension<DungTheory>>> result = cf_reasoner.getModels(example, true);
        ArrayList<LinkedList<Extension<DungTheory>>> resAList = cf_reasoner.ListToArrayList(result);
        ArrayList<LinkedList<Argument>> exRankedArgs = reasoner.getRankedArguments(resAList);
        
        System.out.println("ExtensionRankingReasoner Ranked Arguments:");
        z = 1;
        for(LinkedList<Argument> l : exRankedArgs) {
        	
        	if(l.size() > 0) {
        		System.out.print("Rank " +z+ ": ");
            	for(Argument aa : l) {
                	System.out.print(aa.toString());
                	
                }
            	z++;
            	System.out.println("");
        	}
        }
        System.out.println();
        
        
        
        
        
        
        
        
        RankBasedPairwiseExtensionReasoner mod = new RankBasedPairwiseExtensionReasoner(AggregationFunction.LEXIMIN, ExtensionRankingSemantics.R_PR);
        Collection<Extension<DungTheory>> prExt = new SimplePreferredReasoner().getModels(example);
        ArrayList<LinkedList<Extension<DungTheory>>> ranks = mod.getModels(prExt, example, new CategorizerRanking());
        
        ArrayList<LinkedList<Argument>> pairRankedArgs = reasoner.getRankedArguments(ranks);
        
        System.out.println("RankBasedPairwiseExtensionReasoner Ranked Arguments:");
        z = 1;
        for(LinkedList<Argument> l : pairRankedArgs) {
        	
        	if(l.size() > 0) {
        		System.out.print("Rank " +z+ ": ");
            	for(Argument aa : l) {
                	System.out.print(aa.toString());
                	
                }
            	z++;
            	System.out.println("");
        	}
        }
        System.out.println();
        
        
        
        
        
        Collection<Extension<DungTheory>> prExtensions3 = new SimplePreferredReasoner().getModels(example);
        NumberOfContainsInExtensions nocie = new NumberOfContainsInExtensions();
        OrderBasedExtensionReasoner OBER = new OrderBasedExtensionReasoner(AggregationFunction.SUM);
        HashMap<Vector<Double>, Set<Extension<DungTheory>>> map = OBER.getRankedExtensions(prExtensions3, nocie);
        ArrayList<LinkedList<Extension<DungTheory>>> OBERResultList = OBER.mapToArrayList(map);
        ArrayList<LinkedList<Argument>> OBERRrankedArgs = reasoner.getRankedArguments(OBERResultList);
        
        
        System.out.println("OrderBasedExtensionReasoner Ranked Arguments:");
        z = 1;
        for(LinkedList<Argument> l : OBERRrankedArgs) {
        	
        	if(l.size() > 0) {
        		System.out.print("Rank " +z+ ": ");
            	for(Argument aa : l) {
                	System.out.print(aa.toString());
                	
                }
            	z++;
            	System.out.println("");
        	}
        }
        System.out.println();
        
        
	}
	
	
}
