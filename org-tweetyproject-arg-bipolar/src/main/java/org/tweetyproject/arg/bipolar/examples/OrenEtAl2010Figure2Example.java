package org.tweetyproject.arg.bipolar.examples;

import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.EAFTheory;

public class OrenEtAl2010Figure2Example {
    public static void main(String[] args) {
        EAFTheory eafTheory = new EAFTheory();
        for(int i = 0; i< 4 ; i++) {
        	eafTheory.addArgument(i);
        }


        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(1)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(2)))));
        eafTheory.addAttack(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(2)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(1)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(1)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(3)))));

        DungTheory dungTheory = eafTheory.convertToDAFNaively();

        System.out.println("EAF Theory Pretty Print:");
        eafTheory.prettyPrint();

        System.out.println("DAF Theory Pretty Print:");
        System.out.println(dungTheory.prettyPrint());

        System.out.println("SimplePreferredReasoner:");
        SimplePreferredReasoner reasoner1 = new SimplePreferredReasoner();
        for (Extension model : reasoner1.getModels(dungTheory)) {
            System.out.println(model);
        }
    }
}
