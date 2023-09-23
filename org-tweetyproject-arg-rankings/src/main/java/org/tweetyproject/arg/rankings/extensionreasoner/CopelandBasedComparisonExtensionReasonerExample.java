package org.tweetyproject.arg.rankings.extensionreasoner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;



/**
 * An example on how to utilize the "CopelandBasedComparisonExtensionReasoner".
 * This example refers to example 2 in the paper "On Supported Inference and Extension Selection in
 * Abstract Argumentation Frameworks – long version–".
 * @author Benajmin Birner
 *
 */
public class CopelandBasedComparisonExtensionReasonerExample {

	public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
	
	
		DungTheory dung = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        
        dung.add(a);
        dung.add(b);
        dung.add(c);
        dung.add(d);
        
        dung.addAttack(a, c);
        dung.addAttack(c, a);
        dung.addAttack(a, d);
        dung.addAttack(b, c);
        dung.addAttack(d, b);
        
        Extension<DungTheory> e1 = new Extension<DungTheory>();
        Extension<DungTheory> e2 = new Extension<DungTheory>();
        
        e1.add(a);
        e1.add(b);
        
        e2.add(c);
        e2.add(d);

       
        Collection<Extension<DungTheory>> extensionList = new HashSet<>();
        extensionList.add(e1);
        extensionList.add(e2);
        
        StronglyDefendedComparator strDef = new StronglyDefendedComparator();
        ArgumentsNonAttackedComparator argNonAtt = new ArgumentsNonAttackedComparator();
        SubsetDeleteAttacksComparator subDelCo = new SubsetDeleteAttacksComparator(ExtensionRankingSemantics.R_PR, false);
        DeleteAttacksComparator DelCo = new DeleteAttacksComparator(ExtensionRankingSemantics.R_PR, false);
        
        CopelandBasedComparisonExtensionReasoner coba = new CopelandBasedComparisonExtensionReasoner();
        
        ArrayList<LinkedList<Extension<DungTheory>>> res0 = coba.getModels(extensionList, dung, strDef);
        ArrayList<LinkedList<Extension<DungTheory>>> res1 = coba.getModels(extensionList, dung, argNonAtt);
        ArrayList<LinkedList<Extension<DungTheory>>> res2 = coba.getModels(extensionList, dung, subDelCo);
        ArrayList<LinkedList<Extension<DungTheory>>> res3 = coba.getModels(extensionList, dung, DelCo);
        


    
        
        
     
        
        
        System.out.println("results Strongly Defended:");
        int rank = 1;
        for( LinkedList<Extension<DungTheory>> rankList : res0) {
        	if(rankList.size() > 0) {
        		System.out.print("Rank " + rank + ": ");
            	for(Extension<DungTheory> ex : rankList ) {
            		System.out.print(ex.toString());
            	}
            	rank++;
            	System.out.println("");
        	}
        }
        System.out.println("");	
        
        System.out.println("results Arguments Non Attacked:");
        rank = 1;
        for( LinkedList<Extension<DungTheory>> rankList : res1) {
        	if(rankList.size() > 0) {
        		System.out.print("Rank " + rank + ": ");
            	for(Extension<DungTheory> ex : rankList ) {
            		System.out.print(ex.toString());
            	}
            	rank++;
            	System.out.println("");
        	}
        }
        System.out.println("");
        
        System.out.println("results Subset Delete Attacks:");
        rank = 1;
        for( LinkedList<Extension<DungTheory>> rankList : res2) {
        	if(rankList.size() > 0) {
        		System.out.print("Rank " + rank + ": ");
            	for(Extension<DungTheory> ex : rankList ) {
            		System.out.print(ex.toString());
            	}
            	rank++;
            	System.out.println("");
        	}
        }
        System.out.println("");
        
        System.out.println("results Delete Attacks:");
        rank = 1;
        for( LinkedList<Extension<DungTheory>> rankList : res3) {
        	if(rankList.size() > 0) {
        		System.out.print("Rank " + rank + ": ");
            	for(Extension<DungTheory> ex : rankList ) {
            		System.out.print(ex.toString());
            	}
            	rank++;
            	System.out.println("");
        	}
        }
        
	}
}
