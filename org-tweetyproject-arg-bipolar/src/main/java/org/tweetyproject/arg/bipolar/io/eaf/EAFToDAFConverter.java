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
        bfsTraverse(dungTheory, startingPoint, discovered, mapping, eafTheory);

        // Once the traversal is finished
        System.out.println(mapping);

        for (BArgument from : eafTheory.getArguments()) {
            for (org.tweetyproject.arg.bipolar.syntax.Attack attack : eafTheory.getAttacks()) {
            	if(attack.contains(from)) {
	                Set<BArgument> edgeArguments = new HashSet<BArgument>();
	
	                findEdgeNodes(attack.getAttacked(), edgeArguments, eafTheory);
	
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

    private static void findEdgeNodes(BipolarEntity bipolarEntity, Set<BArgument> edgeArguments, EAFTheory eafTheory) {
        Map<BArgument, Set<Support>> supportsToArgs = new HashMap<BArgument, Set<Support>>();

        for(Support s : eafTheory.getSupports()) {
        	for(BArgument a : eafTheory.getArguments()) {
        		if(s.contains(a)) {
        			if(supportsToArgs.get(a) != null) {
        				supportsToArgs.get(a).add(s);
        				
        			}
        			else {
        				supportsToArgs.put(a, new HashSet<Support>(new ArrayList<Support>(Arrays.asList(s))));
        			}
        		}
        	}
        	
        }
    	for (BArgument child : bipolarEntity) {
            if (supportsToArgs.get(child).size() == 0) {
                edgeArguments.add(child);
            } else {
                edgeArguments.add(child);
                for (Support support : supportsToArgs.get(child)) {
                    findEdgeNodes(support.getSupported(), edgeArguments, eafTheory);
                }
            }
        }
    }

    private static void bfsTraverse(DungTheory dungTheory, BArgument startingPoint, Set<BArgument> discovered, Map<BArgument, Set<Argument>> mapping, EAFTheory eafTheory) {
        Map<BArgument, Set<Support>> supportsToArgs = new HashMap<BArgument, Set<Support>>();
        Map<BArgument, Set<org.tweetyproject.arg.bipolar.syntax.Attack>> attacksToArgs = new HashMap<BArgument, Set<org.tweetyproject.arg.bipolar.syntax.Attack>>();
        Map<BArgument, Set<org.tweetyproject.arg.bipolar.syntax.Attack>> incomingAttacksToArgs = new HashMap<BArgument, Set<org.tweetyproject.arg.bipolar.syntax.Attack>>();
        
    	for(BArgument a : eafTheory.getArguments()) {
    		for(Support s : eafTheory.getSupports()) {
        		if(s.contains(a)) {
        			if(supportsToArgs.get(a) != null) {
        				supportsToArgs.get(a).add(s);
        				
        			}
        			else {
        				supportsToArgs.put(a, new HashSet<Support>(new ArrayList<Support>(Arrays.asList(s))));
        			}
        		}
        	}
    		for(org.tweetyproject.arg.bipolar.syntax.Attack s : eafTheory.getAttacks()) {
        		if(s.contains(a)) {
        			if(attacksToArgs.get(a) != null) {
        				attacksToArgs.get(a).add( s);
        				
        			}
        			else {
        				attacksToArgs.put(a, new HashSet<org.tweetyproject.arg.bipolar.syntax.Attack>(new ArrayList<org.tweetyproject.arg.bipolar.syntax.Attack>(Arrays.asList(s))));
        			}
        		}
        		if(s.getAttacked().contains(a)) {
        			if(incomingAttacksToArgs.get(a) != null) {
        				incomingAttacksToArgs.get(a).add( s);
        				
        			}
        			else {
        				incomingAttacksToArgs.put(a, new HashSet<org.tweetyproject.arg.bipolar.syntax.Attack>(new ArrayList<org.tweetyproject.arg.bipolar.syntax.Attack>(Arrays.asList(s))));
        			}
        		}
        	}
        	
        }
    	Queue<Pair<BArgument, List<BArgument>>> queue = new LinkedList<>();

        if (discovered.contains(startingPoint)) {
            return;
        }
        discovered.add(startingPoint);
        queue.add(new Pair<>(startingPoint, new ArrayList<BArgument>(Arrays.asList(startingPoint))));
        

        while (!queue.isEmpty()) {
            Pair<BArgument, List<BArgument>> pair = queue.poll();
            BArgument v = pair.getFirst();

            System.out.println("h1: " + pair.getSecond());
            if (supportsToArgs.get(v).size() > 0) {
                for (Support support : supportsToArgs.get(v)) {
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

            if (attacksToArgs.get(v).size() > 0) {
//                System.out.println("h2: " + pair.getSecond());
                addDAFArgument(dungTheory, mapping, pair);
            }

            if (incomingAttacksToArgs.get(v).size() > 0) {
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
