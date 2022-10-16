package org.tweetyproject.arg.bipolar.examples;

import org.tweetyproject.arg.bipolar.io.EdgeListWriter;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.InducibleEAF;
import org.tweetyproject.arg.bipolar.syntax.PEAFTheory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class SomePEAFInducerExample {
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
        HashSet<BArgument> arg5And4 = new HashSet<BArgument>();
        arg5And4.add(peafTheory.getArguments().get(5));
        arg5And4.add(peafTheory.getArguments().get(4));
        peafTheory.addSupport(arg5And4, new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(7)))), 0.4);

        peafTheory.addAttack(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(5)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(4)))));
        peafTheory.addAttack(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(2)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(6)))));

        System.out.println(peafTheory.toString());

        EdgeListWriter.write("peaf.networkx", peafTheory);

        AtomicInteger i = new AtomicInteger();


        Consumer<InducibleEAF> consumer = new Consumer<InducibleEAF>() {
            @Override
            public void accept(InducibleEAF inducibleEAF) {
                int n = i.getAndIncrement();
                System.out.println(inducibleEAF);
            }
        };


    }
}
