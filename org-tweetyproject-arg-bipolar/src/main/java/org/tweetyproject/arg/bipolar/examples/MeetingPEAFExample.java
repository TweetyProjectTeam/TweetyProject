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
        HashSet<BArgument> arg0 = new HashSet<BArgument>();
        arg0.add(peafTheory.getArguments().get(0));
        HashSet<BArgument> arg1 = new HashSet<BArgument>();
        arg1.add(peafTheory.getArguments().get(1));
        HashSet<BArgument> arg2 = new HashSet<BArgument>();
        arg2.add(peafTheory.getArguments().get(2));
        HashSet<BArgument> arg3 = new HashSet<BArgument>();
        arg3.add(peafTheory.getArguments().get(3));
        HashSet<BArgument> arg4 = new HashSet<BArgument>();
        arg4.add(peafTheory.getArguments().get(4));
        HashSet<BArgument> arg5 = new HashSet<BArgument>();
        arg5.add(peafTheory.getArguments().get(5));
        HashSet<BArgument> arg6 = new HashSet<BArgument>();
        arg6.add(peafTheory.getArguments().get(6));
        
        peafTheory.addSupport(new HashSet<BArgument>(), arg0 , 1.0);
        peafTheory.addSupport(arg0, arg2, 1.0);
        peafTheory.addSupport(arg0, arg1, 1.0);
        peafTheory.addSupport(arg0, arg3, 1.0);
        peafTheory.addSupport(arg0, arg4, 1.0);
        peafTheory.addSupport(arg3, arg5, 1.0);
//        peafTheory.addSupport(new int[]{3, 4}, new int[]{6}, 0.9);

        peafTheory.addAttack(arg5, arg2);
        peafTheory.addAttack(arg5, arg1);
        peafTheory.addAttack(arg1, arg5);
        peafTheory.addAttack(arg1, arg6);


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
