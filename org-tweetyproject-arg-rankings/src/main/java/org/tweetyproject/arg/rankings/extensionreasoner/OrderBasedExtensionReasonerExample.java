package org.tweetyproject.arg.rankings.extensionreasoner;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.rankings.extensionreasoner.OrderBasedExtensionReasoner;
import org.tweetyproject.arg.rankings.extensionreasoner.AggregationFunction;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;

public class OrderBasedExtensionReasonerExample {
    public static void main(String[] args) throws Exception {

        DungTheory example3 = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        Argument f = new Argument("f");
        Argument g = new Argument("g");
        Argument h = new Argument("h");
        example3.add(a);
        example3.add(b);
        example3.add(c);
        example3.add(d);
        example3.add(e);
        example3.add(f);
        example3.add(g);
        example3.add(h);
        example3.addAttack(a,b);
        example3.addAttack(b,a);
        example3.addAttack(e,f);
        example3.addAttack(f,e);
        example3.addAttack(b,g);
        example3.addAttack(f,g);
        example3.addAttack(g,h);
        example3.addAttack(h,d);
        example3.addAttack(d,c);
        example3.addAttack(c,d);

        DungTheory figure1 = new DungTheory();
        Argument a1 = new Argument("1");
        Argument a2 = new Argument("2");
        Argument a3 = new Argument("3");
        Argument a4 = new Argument("4");
        Argument a5 = new Argument("5");
        Argument a6 = new Argument("6");
        Argument a7 = new Argument("7");
        figure1.add(a1);
        figure1.add(a2);
        figure1.add(a3);
        figure1.add(a4);
        figure1.add(a5);
        figure1.add(a6);
        figure1.add(a7);
        figure1.addAttack(a1,a2);
        figure1.addAttack(a2,a3);
        figure1.addAttack(a3,a4);
        figure1.addAttack(a3,a5);
        figure1.addAttack(a3,a6);
        figure1.addAttack(a4,a3);
        figure1.addAttack(a4,a6);
        figure1.addAttack(a5,a5);
        figure1.addAttack(a6,a7);
        figure1.addAttack(a7,a6);


        Collection<Extension<DungTheory>> prExtensions = new SimplePreferredReasoner().getModels(example3);
//        Set<Set<Argument>> subsets = new SetTools<Argument>().subsets(figure1);
//        for(Set<Argument> set : subsets){
//            Extension<DungTheory> ext = new Extension<>(set);
//            extensions.add(ext);
//        }
        OrderBasedExtensionReasoner OBER = new OrderBasedExtensionReasoner(AggregationFunction.SUM);
        System.out.println("OBE_pr,sum:" + OBER.getModels(prExtensions));
        OBER.setAggregationFunction(AggregationFunction.MAX);
        System.out.println("OBE_pr,max:" + OBER.getModels(prExtensions));
        OBER.setAggregationFunction(AggregationFunction.MIN);
        System.out.println("OBE_pr,min:" + OBER.getModels(prExtensions));
        OBER.setAggregationFunction(AggregationFunction.LEXIMAX);
        System.out.println("OBE_pr,leximax:" + OBER.getModels(prExtensions));
        OBER.setAggregationFunction(AggregationFunction.LEXIMIN);
        System.out.println("OBE_pr,leximin:" + OBER.getModels(prExtensions));

        System.out.println();

        Collection<Extension<DungTheory>> adExtensions = AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.ADMISSIBLE_SEMANTICS).getModels(example3);
        OBER.setAggregationFunction(AggregationFunction.SUM);

        System.out.println("OBE_ad,sum:" + OBER.getModels(adExtensions));
        OBER.setAggregationFunction(AggregationFunction.MAX);
        System.out.println("OBE_ad,max:" + OBER.getModels(adExtensions));
        OBER.setAggregationFunction(AggregationFunction.MIN);
        System.out.println("OBE_ad,min:" + OBER.getModels(adExtensions));
        OBER.setAggregationFunction(AggregationFunction.LEXIMAX);
        System.out.println("OBE_ad,leximax:" + OBER.getModels(adExtensions));
        OBER.setAggregationFunction(AggregationFunction.LEXIMIN);
        System.out.println("OBE_ad,leximin:" + OBER.getModels(adExtensions));



    }

}
