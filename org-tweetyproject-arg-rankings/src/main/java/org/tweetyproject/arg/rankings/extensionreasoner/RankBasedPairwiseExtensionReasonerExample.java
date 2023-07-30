package org.tweetyproject.arg.rankings.extensionreasoner;

import java.util.Collection;
import java.util.LinkedList;

import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;


/**
 * An example on how to utilize the "RankBasedPairwiseExtensionReasoner".
 * 
 * @author Benajmin Birner
 *
 */
public class RankBasedPairwiseExtensionReasonerExample {

	public static void main(String[] args) throws Exception {
		
		Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        Argument f = new Argument("f");
        Argument g = new Argument("g");
		
		
		DungTheory example = new DungTheory();
        example.add(a);
        example.add(b);
        example.add(c);
        example.add(d);
        example.add(e);
        example.add(f);
        example.add(g);
        
        
        example.addAttack(a, e);
        example.addAttack(a, d);
        example.addAttack(a, f);
        example.addAttack(c, b);
        
        example.addAttack(c, g);
        example.addAttack(f, g);
        example.addAttack(g, f);
        example.addAttack(d, c);
        
        example.addAttack(e, c);
        example.addAttack(e, d);
        example.addAttack(d, e);
        example.addAttack(b, a);
        
        
        
        RankBasedPairwiseExtensionReasoner mod = new RankBasedPairwiseExtensionReasoner(AggregationFunction.LEXIMIN, ExtensionRankingSemantics.R_PR);
        Collection<Extension<DungTheory>> prExt = new SimplePreferredReasoner().getModels(example);
        LinkedList<Extension<DungTheory>> best = mod.getModels(prExt, example, new CategorizerRanking());

        
        System.out.println("The best Extension(s):");
        
        for(Extension<DungTheory> bestE : best) {
        	System.out.println(bestE.toString());
        }
	}

}
