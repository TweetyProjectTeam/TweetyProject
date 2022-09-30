package org.tweetyproject.arg.bipolar.examples;

import org.tweetyproject.arg.bipolar.inducers.ExactPEAFInducer;
import org.tweetyproject.arg.bipolar.inducers.LiExactPEAFInducer;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.BipolarEntity;
import org.tweetyproject.arg.bipolar.syntax.PEAFTheory;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MeetingPEAFExample {
    static double total = 0;

    public static void main(String[] s) {
        int numOfArgs = 7;


        PEAFTheory peafTheory = new PEAFTheory();

        for (int i = 0; i < numOfArgs; i++) {
            peafTheory.addArgument(i);
        }

        peafTheory.addSupport((BipolarEntity) new HashSet<BArgument>(), (BipolarEntity) peafTheory.getArguments().get(0), 1.0);
        peafTheory.addSupport(new int[]{0}, new int[]{2}, 1.0);
        peafTheory.addSupport(new int[]{0}, new int[]{1}, 1.0);
        peafTheory.addSupport(new int[]{0}, new int[]{3}, 1.0);
        peafTheory.addSupport(new int[]{0}, new int[]{4}, 1.0);
        peafTheory.addSupport(new int[]{3}, new int[]{5}, 1.0);
//        peafTheory.addSupport(new int[]{3, 4}, new int[]{6}, 0.9);

        peafTheory.addAttack(new int[]{5}, new int[]{2});
        peafTheory.addAttack(new int[]{5}, new int[]{1});
        peafTheory.addAttack(new int[]{1}, new int[]{5});
        peafTheory.addAttack(new int[]{1}, new int[]{6});


        List<BArgument> args = peafTheory.getArguments();
        args.get(0).setName("eta");
        args.get(1).setName("b");
        args.get(2).setName("d");
        args.get(3).setName("e");
        args.get(4).setName("f");
        args.get(5).setName("a");
        args.get(6).setName("c");
        peafTheory.prettyPrint();
//        EdgeListWriter.write("/Users/tdgunes/Projects/DrawPrEAF/input/0.peaf", peafTheory);

        System.out.println("LiExactPEAFInducer: ");
        AtomicInteger i = new AtomicInteger();
        LiExactPEAFInducer inducer = new LiExactPEAFInducer(peafTheory);
        total = 0;
        inducer.induce(ind -> {
            int n = i.getAndIncrement();
            System.out.println(n + ". " + ind);
            String probability = String.format("%.04f", ind.getInducePro());
            probability = probability.replace(".", "_");
//            EdgeListWriter.write("/Users/tdgunes/Projects/DrawPrEAF/input/" + n + "" + "_" + result + ".eaf", ind.toNewEAFTheory());
            total += ind.getInducePro();
        });
        System.out.println("Total result: " + total);

        System.out.println();
        System.out.println("The ExactPEAFInducer:");
        System.out.println();
        AtomicInteger i1 = new AtomicInteger();
        ExactPEAFInducer inducer2 = new ExactPEAFInducer(peafTheory);
        total = 0;
        inducer2.induce(ind -> {
            int n = i1.getAndIncrement();
            System.out.println(n + ". " + ind);
            String probability = String.format("%.04f", ind.getInducePro());
            probability = probability.replace(".", "_");
//            EdgeListWriter.write("/Users/tdgunes/Projects/DrawPrEAF/input/" + n + "" + "_" + result + ".eaf", ind.toNewEAFTheory());
            total += ind.getInducePro();
        });
        System.out.println("Total result: " + total);

    }
}
