package org.tweetyproject.arg.bipolar.io.eaf;


import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.bipolar.syntax.EAFTheory;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.BipolarEntity;
import org.tweetyproject.arg.bipolar.syntax.EAttack;
import org.tweetyproject.arg.bipolar.syntax.Support;
import org.tweetyproject.commons.util.Pair;

import java.util.*;

public class EAFToDAFConverter {

    public static DungTheory convert(EAFTheory eafTheory) {
        DungTheory dungTheory = new DungTheory();
        // Keeping track of which arguments are discovered
        Set<BArgument> discovered = new HashSet<BArgument>();
        // Stores which EArgument corresponds to which dafArguments (one-to-many)
        Map<BArgument, Set<Argument>> mapping = new HashMap<BArgument, Set<Argument>>();

        BArgument startingPoint = eafTheory.getEta();
        bfsTraverse(dungTheory, startingPoint, discovered, mapping);

        // Once the traversal is finished
        System.out.println(mapping);

        for (BArgument from : eafTheory.getArguments()) {
            for (org.tweetyproject.arg.bipolar.syntax.Attack attack : eafTheory.getAttacks()) {
            	if(attack.contains(from)) {
	                Set<BArgument> edgeArguments = new HashSet<BArgument>();
	
	                findEdgeNodes(attack.getAttacked(), edgeArguments);
	
	                Set<Argument> dafAttackers = mapping.get(from);
	
	
	                System.out.println("Edge arguments: " + edgeArguments);
	                if (dafAttackers == null) {
	                    System.err.println("dafAttackers is null: " + from);
	                }
	
	                if (edgeArguments.isEmpty()) {
	                    System.err.println("dafAttackeds is null: " + edgeArguments);
	                }
	
	                if (edgeArguments.isEmpty() || dafAttackers == null) {
	                    return null;
	                }
	
	
	                for (Argument dafAttacker : dafAttackers) {
	                    for (BArgument attacked : edgeArguments) {
	                        if (mapping.get(attacked) == null) {
	                            continue;
	                        }
	                        for (Argument argument : mapping.get(attacked)) {
	                            dungTheory.addAttack(dafAttacker, argument);
	                        }
	                    }
	                }
            	}
            }
        }

        System.out.println(dungTheory.prettyPrint());
        return dungTheory;
    }

    private static void findEdgeNodes(BipolarEntity bipolarEntity, Set<BArgument> edgeArguments) {
        Map<BArgument, Set<Support>> supportsToArgs = new HashMap<BArgument, Set<Support>>();
        for(Support s : )
    	for (BArgument child : bipolarEntity) {
            if (child.getSupports().size() == 0) {
                edgeArguments.add(child);
            } else {
                edgeArguments.add(child);
                for (Support support : child.getSupports()) {
                    findEdgeNodes(support.getSupported(), edgeArguments);
                }
            }
        }
    }

    private static void bfsTraverse(DungTheory dungTheory, BArgument startingPoint, Set<BArgument> discovered, Map<BArgument, Set<Argument>> mapping) {
        Queue<Pair<BArgument, List<BArgument>>> queue = new LinkedList<>();

        if (discovered.contains(startingPoint)) {
            return;
        }
        discovered.add(startingPoint);
        queue.add(new Pair<>(startingPoint, new ArrayList(startingPoint)));
        

        while (!queue.isEmpty()) {
            Pair<BArgument, List<BArgument>> pair = queue.poll();
            BArgument v = pair.getFirst();

            System.out.println("h1: " + pair.getSecond());
            if (v.getSupports().size() > 0) {
                for (Support support : v.getSupports()) {
                    for (BArgument to : support.getSupported()) {
//  FIXME: since the support structure is a tree perhaps we can avoid this
//                        if (!discovered.contains(to)) {
//                            discovered.add(to);
                        List<BArgument> newList = new ArrayList<BArgument>();
                        newList.addAll(pair.getSecond());
                        newList.add(to);
                        queue.add(new Pair<>(to, newList));
//                        }
                    }
                }
            } else {
                addDAFArgument(dungTheory, mapping, pair);
            }

            if (v.getAttacks().size() > 0) {
//                System.out.println("h2: " + pair.getSecond());
                addDAFArgument(dungTheory, mapping, pair);
            }

            if (v.getIncomingAttacks().size() > 0) {
                addDAFArgument(dungTheory, mapping, pair);
            }


        }

    }

    private static void addDAFArgument(DungTheory dungTheory, Map<BArgument, Set<Argument>> mapping, Pair<BArgument, List<BArgument>> pair) {
        List<String> args = new ArrayList<String>();
        for (BArgument eArgument : pair.getSecond()) {
            args.add(eArgument.getName());
        }

        Argument dafArgument = new Argument(String.join("_", args));
        dungTheory.add(dafArgument);

        BArgument eArgument = pair.getSecond().get(pair.getSecond().size() - 1);
        if (!mapping.containsKey(eArgument)) {
            mapping.put(eArgument, new HashSet());
        }
        mapping.get(eArgument).add(dafArgument);


    }


}
