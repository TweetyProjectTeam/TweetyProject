package org.tweetyproject.arg.dung.examples;

import java.util.HashMap;

import org.tweetyproject.arg.dung.reasoner.SimpleClInheritedReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleClNaiveReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleClPreferredReaonser;
import org.tweetyproject.arg.dung.reasoner.SimpleClSemistableReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleClStableReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleClStagedReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ClaimBasedTheory;

public class ClaimBasedReasonersTest {

	public static void main(String[] args) {

		ClaimBasedTheory ex = new ClaimBasedTheory();

        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");

        ex.add(a);
        ex.add(b);
        ex.add(c);
        ex.add(d);
        ex.addAttack(a, b);
        ex.addAttack(b, a);
        ex.addAttack(d, c);
        ex.addAttack(c, d);

        HashMap<Argument, String> claimMap = new HashMap<Argument, String>();
        claimMap.put(a, "c1");
        claimMap.put(b, "c2");
        claimMap.put(c, "c1");
        claimMap.put(d, "c2");
        ex.setClaimMap(claimMap);
        
        SimpleClInheritedReasoner re1 = new SimpleClInheritedReasoner(Semantics.CO);
        System.out.println("inherited complete: " + re1.getModels(ex));
        SimpleClPreferredReaonser re2 = new SimpleClPreferredReaonser();
        System.out.println("cl-preferred: " + re2.getModels(ex));
        SimpleClStableReasoner re3 = new SimpleClStableReasoner();
        System.out.println("cl-stable: " +re3.getModels(ex));
        SimpleClSemistableReasoner re4 = new SimpleClSemistableReasoner();
        System.out.println("cl-semistable: " +re4.getModels(ex));
        SimpleClStagedReasoner re5 = new SimpleClStagedReasoner();
        System.out.println("cl-staged: " +re5.getModels(ex));
        SimpleClNaiveReasoner re6 = new SimpleClNaiveReasoner();
        System.out.println("cl-naive: " +re6.getModels(ex));
        
        
        
        
	}
}
