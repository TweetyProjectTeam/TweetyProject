package examples;

import aggregation.Profile;
import functions.BordaCount;
import functions.CopelandSolution;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.syntax.IncompleteTheory;
import org.tweetyproject.arg.rankings.reasoner.CategorizerRankingReasoner;
import org.tweetyproject.comparator.NumericalPartialOrder;


import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PreferenceAggregationExample {
    public static void main(String[] args){
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");

//        Profile<Argument> r1 = new Profile<>();
//        Profile<Argument> r2 = new Profile<>();
//        Profile<Argument> r3 = new Profile<>();
//        Profile<Argument> r4 = new Profile<>();
//        r1.put(b,1); r1.put(d,1); r1.put(c,2); r1.put(a,3);
//        r2.put(a,1); r2.put(d,1); r2.put(b,2); r2.put(c,3);
//        r3.put(b,1); r3.put(d,2); r3.put(c,3); r3.put(a,4);
//        r4.put(a,1); r4.put(d,2); r4.put(b,3); r4.put(c,4);
//
//        List<Profile<Argument>> completions = new ArrayList<>();
//        completions.add(r1); completions.add(r2); completions.add(r3); completions.add(r4);

        LinkedList<Argument> alternatives = new LinkedList<>();
        alternatives.add(a);
        alternatives.add(b);
        alternatives.add(c);
        alternatives.add(d);

        IncompleteTheory iTheory = new IncompleteTheory();
        iTheory.addPossibleArgument(a);
        iTheory.addDefiniteArgument(b);
        iTheory.addDefiniteArgument(c);
        iTheory.addDefiniteArgument(d);

        iTheory.addDefiniteAttack(a,b);
        iTheory.addDefiniteAttack(b,c);
        iTheory.addPossibleAttack(c,d);
        iTheory.addDefiniteAttack(d,c);

        Collection<DungTheory> completions = iTheory.getAllCompletions();
        System.out.println("Completions:" + completions.toString());

        CategorizerRankingReasoner cat = new CategorizerRankingReasoner();


        List<Profile<Argument>> profiles = new ArrayList<>();
        for(DungTheory comp: completions){
            NumericalPartialOrder<Argument,DungTheory> order = cat.getModel(comp);
            profiles.add(new Profile<Argument>(order,alternatives));
        }

        CopelandSolution<Argument> copeland = new CopelandSolution<>();
        Map<Argument,Float> copelandScoreMap = copeland.calculateScores(profiles);
        System.out.println(copelandScoreMap.toString());

        BordaCount<Argument> borda = new BordaCount<>();
        Map<Argument,Float> bordaScoreMap = borda.calculateScores(profiles);
        System.out.println(bordaScoreMap.toString());




    }

}
