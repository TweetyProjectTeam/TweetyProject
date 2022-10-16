package org.tweetyproject.arg.bipolar.examples;

import org.tweetyproject.arg.bipolar.inducers.LiExactPEAFInducer;
import org.tweetyproject.arg.bipolar.io.EdgeListWriter;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.PEAFTheory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public class AllPEAFInducerExample {
    public static void main(String[] args) {
        int numOfArgs = 8;


        PEAFTheory peafTheory = new PEAFTheory();

        for (int i = 0; i < numOfArgs; i++) {
            peafTheory.addArgument(i);
        }

        peafTheory.addSupport(new HashSet<BArgument>(), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(0)))), 1.0);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(1)))), 0.3);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(1)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(2)))), 0.8);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(1)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(3)))), 0.9);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(2)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(4)))), 0.85);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(3)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(5)))), 0.5);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(3)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(6)))), 0.6);
        HashSet<BArgument> myArgs = new HashSet<BArgument>();
        myArgs.add(peafTheory.getArguments().get(5)); 
        myArgs.add(peafTheory.getArguments().get(4));
        peafTheory.addSupport(myArgs, new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(7)))), 0.4);

        peafTheory.addAttack(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(5)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(4)))));
        peafTheory.addAttack(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(2)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(6)))));

        peafTheory.prettyPrint();

        EdgeListWriter.write("0.peaf", peafTheory);

        AtomicInteger i = new AtomicInteger();
        LiExactPEAFInducer inducer = new LiExactPEAFInducer(peafTheory);

        inducer.induce(ind -> {
            int n = i.getAndIncrement();
            System.out.println(ind);
            EdgeListWriter.write(n + ".eaf", ind.toNewEAFTheory());

        });

    }
}
