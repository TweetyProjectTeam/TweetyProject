package org.tweetyproject.arg.rankings.extensionreasoner;

import org.tweetyproject.arg.dung.reasoner.*;
import org.tweetyproject.arg.rankings.extensionreasoner.AggregationFunction;
import org.tweetyproject.arg.dung.syntax.Argument;

import org.tweetyproject.arg.rankings.extensionreasoner.RankBasedExtensionReasoner;
import org.tweetyproject.arg.rankings.reasoner.CategorizerRankingReasoner;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RankBasedExtensionReasonerExample {
    public static void main(String[] args) throws Exception {

        DungTheory example = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        Argument f = new Argument("f");
        Argument g = new Argument("g");
        example.add(a);
        example.add(b);
        example.add(c);
        example.add(d);
        example.add(e);
        example.add(f);
        example.add(g);
        example.addAttack(a,d);
        example.addAttack(a,e);
        example.addAttack(a,f);
        example.addAttack(b,a);
        example.addAttack(c,b);
        example.addAttack(c,g);
        example.addAttack(d,c);
        example.addAttack(d,e);
        example.addAttack(e,c);
        example.addAttack(e,d);
        example.addAttack(f,g);
        example.addAttack(g,f);

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

        RankBasedExtensionReasoner RBER = new RankBasedExtensionReasoner(AggregationFunction.AVG);
        Map<Argument,Integer> argumentRankMap = getRankMapFromCategorizerRanking(new CategorizerRankingReasoner().getModel(example));
        System.out.println("RBE_pr,avg:" + RBER.getModels(new SimplePreferredReasoner().getModels(example),argumentRankMap));
        System.out.println("RBE_pr,avg aggregation result:" + RBER.getAggregatedVectorToExtensionMap(new SimplePreferredReasoner().getModels(example),argumentRankMap));
        RBER.setAggregationFunction(AggregationFunction.LEXIMIN);
        System.out.println("RBE_pr,leximin:" + RBER.getModels( new SimplePreferredReasoner().getModels(example),argumentRankMap));
        System.out.println("RBE_pr,leximin aggregation result:" + RBER.getAggregatedVectorToExtensionMap(new SimplePreferredReasoner().getModels(example),argumentRankMap));

//        Set<Set<Argument>> subsets = new SetTools<Argument>().subsets(figure1);
//        for(Set<Argument> set : subsets){
//            Extension<DungTheory> ext = new Extension<>(set);
//            extensions.add(ext);
//        }
//        OrderBasedExtensionReasoner OBER = new OrderBasedExtensionReasoner(AggregationFunction.SUM);
//        System.out.println("OBE_pr,sum:" + OBER.getModels(prExtensions));
//        OBER.setAggregationFunction(AggregationFunction.MAX);
//        System.out.println("OBE_pr,max:" + OBER.getModels(prExtensions));
//        OBER.setAggregationFunction(AggregationFunction.MIN);
//        System.out.println("OBE_pr,min:" + OBER.getModels(prExtensions));
//        OBER.setAggregationFunction(AggregationFunction.LEXIMAX);
//        System.out.println("OBE_pr,leximax:" + OBER.getModels(prExtensions));
//        OBER.setAggregationFunction(AggregationFunction.LEXIMIN);
//        System.out.println("OBE_pr,leximin:" + OBER.getModels(prExtensions));
//
//        System.out.println();
//
//        Collection<Extension<DungTheory>> adExtensions = AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.ADMISSIBLE_SEMANTICS).getModels(example);
//        OBER.setAggregationFunction(AggregationFunction.SUM);
//
//        System.out.println("OBE_ad,sum:" + OBER.getModels(adExtensions));
//        OBER.setAggregationFunction(AggregationFunction.MAX);
//        System.out.println("OBE_ad,max:" + OBER.getModels(adExtensions));
//        OBER.setAggregationFunction(AggregationFunction.MIN);
//        System.out.println("OBE_ad,min:" + OBER.getModels(adExtensions));
//        OBER.setAggregationFunction(AggregationFunction.LEXIMAX);
//        System.out.println("OBE_ad,leximax:" + OBER.getModels(adExtensions));
//        OBER.setAggregationFunction(AggregationFunction.LEXIMIN);
//        System.out.println("OBE_ad,leximin:" + OBER.getModels(adExtensions));
//
//
//
    }

    public static Map<Argument,Integer> getRankMapFromCategorizerRanking(Map<Argument,Double> catRanking){
        Map<Argument,Integer> rankMap = new HashMap<>();

        Collection<Double> values = catRanking.values();
        Collection<Argument> arguments = catRanking.keySet();

        Collection<Argument> maxArgs = new HashSet<>();
        int rank = 0;
        while(!arguments.isEmpty()){
            double max = 0;
            for(Argument arg: arguments){
                if(catRanking.get(arg)>max){
                    max = catRanking.get(arg);
                    maxArgs.clear();
                    maxArgs.add(arg);
                }
                else if(catRanking.get(arg) == max){
                    maxArgs.add(arg);
                }
            }
            for(Argument maxArg: maxArgs){
                rankMap.put(maxArg,rank);
                arguments.remove(maxArg);
            }
            ++rank;


        }
        return rankMap;
    }

}
