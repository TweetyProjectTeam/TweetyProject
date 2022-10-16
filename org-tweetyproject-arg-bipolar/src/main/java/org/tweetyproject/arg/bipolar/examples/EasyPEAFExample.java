package org.tweetyproject.arg.bipolar.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.tweetyproject.arg.bipolar.inducers.LiExactPEAFInducer;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.PEAFTheory;

public class EasyPEAFExample {
    public static void main(String[] args) {
        PEAFTheory peafTheory = new PEAFTheory();
        peafTheory.addArgument(0);
        peafTheory.addArgument(1);


        peafTheory.addSupport(new HashSet<BArgument>(), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(0)))), 1.0);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(1)))), 0.3);


        peafTheory.prettyPrint();
        LiExactPEAFInducer inducer = new LiExactPEAFInducer(peafTheory);

        inducer.induce(ind -> {
            System.out.println(ind);


        });
    }
}
