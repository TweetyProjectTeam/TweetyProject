package org.tweetyproject.arg.bipolar.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.tweetyproject.arg.bipolar.io.EdgeListWriter;
import org.tweetyproject.arg.bipolar.syntax.*;

public class EAFExample {

    public static void main(String[] _args) {
        int numOfArgs = 8;


        EAFTheory eafTheory = new EAFTheory();

        for (int i = 0; i < numOfArgs; i++) {
            eafTheory.addArgument(i);
        }

        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(1)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(2)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(2)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(3)))));
        HashSet<BArgument>args1 = new HashSet<BArgument>();
        args1.add(eafTheory.getArguments().get(1));
        args1.add(eafTheory.getArguments().get(3));
        eafTheory.addSupport(args1, new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(3)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(5)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(6)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(6)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(7)))));
        HashSet<BArgument>args2 = new HashSet<BArgument>();
        args2.add(eafTheory.getArguments().get(5));
        args2.add(eafTheory.getArguments().get(7));
        eafTheory.addSupport(args2, new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(4)))));

        eafTheory.addAttack(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(5)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(4)))));
        eafTheory.addAttack(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(2)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(6)))));

        System.out.println(eafTheory.toString());

        EdgeListWriter.write("eaf.txt", eafTheory);
    }
}
