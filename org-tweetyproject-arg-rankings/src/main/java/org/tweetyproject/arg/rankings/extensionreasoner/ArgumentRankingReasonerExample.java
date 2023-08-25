package org.tweetyproject.arg.rankings.extensionreasoner;

import java.util.ArrayList;
import java.util.LinkedList;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

public class ArgumentRankingReasonerExample {
	
	public static void main(String[] args) {
		
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
	    
	    ArgumentRankingReasoner reasoner = new ArgumentRankingReasoner();
        ArrayList<LinkedList<Argument>> rankedArgs = reasoner.getRankedArguments(rankedExt);
	    
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
		
	}
	
	
}
