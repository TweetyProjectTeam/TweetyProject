package org.tweetyproject.arg.bipolar.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.tweetyproject.arg.bipolar.inducers.ExactPEAFInducer;
import org.tweetyproject.arg.bipolar.inducers.LiExactPEAFInducer;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.PEAFTheory;

public class ExpandMCExample {
    public static void main(String[] args) {
        PEAFTheory peafTheory = new PEAFTheory();
        for(int i = 0; i < 4; i++) {
        	peafTheory.addArgument(i);
        }

        peafTheory.addSupport(new HashSet<BArgument>(), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(0)))), 1.0);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(1)))), 0.8);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(1)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(2)))), 0.9);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(2)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(3)))), 0.9);

        peafTheory.prettyPrint();

        System.out.println("The LiExactPEAFInducer:");
        System.out.println();
        final double[] p = {0.0};
        LiExactPEAFInducer inducer = new LiExactPEAFInducer(peafTheory);
        inducer.induce(inducibleEAF -> {
            System.out.println(inducibleEAF);
            p[0] += inducibleEAF.getInducePro();
        });

        System.out.println("The result is: " + p[0]);
        p[0] = 0.0;

        System.out.println();
        System.out.println("The ExactPEAFInducer:");
        System.out.println();
        ExactPEAFInducer inducer2 = new ExactPEAFInducer(peafTheory);
        inducer2.induce(inducibleEAF -> {
            System.out.println(inducibleEAF);
            p[0] += inducibleEAF.getInducePro();
        });

        System.out.println("The result is: " + p[0]);
    }
}
