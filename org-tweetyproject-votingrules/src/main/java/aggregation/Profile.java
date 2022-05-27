package aggregation;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.comparator.GeneralComparator;
import org.tweetyproject.comparator.NumericalPartialOrder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class Profile<A extends Formula> extends HashMap<A,Integer> {

    public Profile(){

    }
    public Profile(NumericalPartialOrder<A,DungTheory> npo, Collection<A> alternatives){

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
         for(A alt: alternatives){
             this.putIfAbsent(alt,priority);
         }
    }

    public void addUnrankedAlternatives(Collection<A> alternativesToAdd){
        int highestPrio = this.getMaxPriority()+1;
        for(A alt:alternativesToAdd){
            this.putIfAbsent(alt,highestPrio);
        }
    }

    public void addUnrankeAlternative(A alternative){
        int highestPrio = this.getMaxPriority()+1;
        this.putIfAbsent(alternative,highestPrio);
    }

    private Integer getMaxPriority(){
        int maxVal=0;
        for(A alt :this.keySet()){
            if(this.get(alt) > maxVal){
                maxVal = this.get(alt);
            }
        }
        return maxVal;
    }

}
