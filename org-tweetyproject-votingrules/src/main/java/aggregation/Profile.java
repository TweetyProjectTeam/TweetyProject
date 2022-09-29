package aggregation;

import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.comparator.NumericalPartialOrder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Simple implementation of a Profile or voting ballots.
 * @author Daniel Letkemann
 * @param <A> currently only really supported for type "Argument"
 */
public class Profile<A extends Formula> extends HashMap<A,Integer> {

    public Profile(){

    }

    /**
     * Creates a Profile from a ranking order over alternatives for any collection of alternatives.
     * @param npo a NumericalPartialOrder over a DungTheory
     * @param alternatives a Collection of alternatives
     */
    public Profile(NumericalPartialOrder<A,DungTheory> npo, Collection<A> alternatives){
    //get best alternatives from the full collection, map them to the currently best priority.
        int priority = 1;
         Set<A> orderedAlternatives = npo.keySet();
         while(!orderedAlternatives.isEmpty()) {
             Collection<A> bestAlternatives = npo.getMaximallyAcceptedArguments(orderedAlternatives);
             for (A bA : bestAlternatives) {
                 this.put(bA, priority);
             }
             ++priority;
             orderedAlternatives.removeAll(bestAlternatives);
         }
         // alternatives that are not voted for receive the worst priority
         for(A alt: alternatives){
             this.putIfAbsent(alt,priority);
         }
    }

//    public void addUnrankedAlternatives(Collection<A> alternativesToAdd){
//        int highestPrio = this.getMaxPriority()+1;
//        for(A alt:alternativesToAdd){
//            this.putIfAbsent(alt,highestPrio);
//        }
//    }
//
//    public void addUnrankedAlternative(A alternative){
//        int highestPrio = this.getMaxPriority()+1;
//        this.putIfAbsent(alternative,highestPrio);
//    }
//
//    private Integer getMaxPriority(){
//        int maxVal=0;
//        for(A alt :this.keySet()){
//            if(this.get(alt) > maxVal){
//                maxVal = this.get(alt);
//            }
//        }
//        return maxVal;
//    }
}
