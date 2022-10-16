package org.tweetyproject.arg.bipolar.examples;

import org.tweetyproject.arg.bipolar.analysis.AnalysisResult;
import org.tweetyproject.arg.bipolar.analysis.ExactAnalysis;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.PEAFTheory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JustificationAnalysisExample {
    public static void main(String[] s) {
        int numOfArgs = 7;
        PEAFTheory peafTheory = new PEAFTheory();

        for (int i = 0; i < numOfArgs; i++) {
            peafTheory.addArgument(i);
        }


        peafTheory.addSupport(new HashSet<BArgument>(), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(0)))), 1.0);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(2)))), 0.6);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(1)))), 0.7);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(3)))), 0.9);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(4)))), 0.3);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(3)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(5)))), 0.5);
        HashSet<BArgument> arg3And4 = new HashSet<BArgument>();
        arg3And4.add(peafTheory.getArguments().get(3));
        arg3And4.add(peafTheory.getArguments().get(4));
        peafTheory.addSupport(arg3And4, new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(6)))), 0.9);
        peafTheory.addAttack(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(5)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(2)))));
        peafTheory.addAttack(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(5)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(1)))));
        peafTheory.addAttack(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(1)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(5)))));
        peafTheory.addAttack(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(1)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(6)))));

        List<BArgument> args = peafTheory.getArguments();
//        args.get(0).setName("eta");
//        args.get(1).setName("b");
//        args.get(2).setName("d");
//        args.get(3).setName("e");
//        args.get(4).setName("f");
//        args.get(5).setName("a");
//
//
//        args.get(6).setName("c");

        Set<BArgument> query = new HashSet<>();
//        query.add(args.get(0));
        query.add(args.get(1));
        query.add(args.get(6));

        ExactAnalysis analysis = new ExactAnalysis(peafTheory, new SimplePreferredReasoner());
        AnalysisResult result = analysis.query(query);
        result.print();


    }

}
