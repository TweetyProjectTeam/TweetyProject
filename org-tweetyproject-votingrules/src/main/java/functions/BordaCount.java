package functions;

import aggregation.Profile;
import org.tweetyproject.commons.Formula;

import java.util.*;
/**
 *
 * @author Daniel Letkemann
 * scoring based on Borda Count
 *
 */
public class BordaCount<A extends Formula> extends ScoringRuleSWF<A>{
    @Override
    public Vector<Float> getScoringVector(Integer length) {
        Vector<Float> scoringVector = new Vector<>();
        for(float i=length-1; i>=0; i--){
            scoringVector.add(i);
        }
        return scoringVector;
    }

    /**
     * Return a scoring vector of certain length, averaged on indexes where alternatives are tied.
     * This allows a free prioritization of alternatives anywhere on a vote/ballot.
     * ('Averaging' as in "Nina Narodytska and Toby Walsh, "The Computational Impact of Partial Votes on Strategic Voting" (2014)")
     * @param length length of the averaged scoring vector
     * @param profiles partitions of list of votes/ballots where: priority of elements inside list -1 = partition index
     * @return an averaged scoring vector for the list of
     */
    private Vector<Float> getAveragedScoringVector(Integer length, List<List<A>> profiles) {
        Vector<Float> scoringVector = (Vector<Float>) getScoringVector(length);
        for(int i=0; i<profiles.size(); i++){
            ArrayList<A> tiedAlternatives = (ArrayList<A>) profiles.get(i);
            //more than one alternative inside one rank means that the scoring vector needs to be averaged on the according indexes
            if(tiedAlternatives.size()>1){
                average(scoringVector,i,i+tiedAlternatives.size());
            }
        }
        return scoringVector;
    }

    /** Averages input scoring vector between startIndex (incl.) and endIndex (excl.).
     * 'Averaging' as in "Nina Narodytska and Toby Walsh, "The Computational Impact of Partial Votes on Strategic Voting" (2014)"
     * @param scoringVector scoring vector to average out (in-place)
     * @param startIndex index of starting element
     * @param endIndex index after the last element
     */
    private void average(Vector<Float> scoringVector, int startIndex, int endIndex) {
        float average = 0;
        //set arithmetic mean for indexes
        for(int i=startIndex;i<endIndex;i++) {
            average += scoringVector.get(i);
        }
        average /= (endIndex-startIndex);
        for(int i=startIndex;i<endIndex;i++){
            scoringVector.set(i,average);
        }
    }

    @Override
    public List<List<A>> calculateRanks(List<Profile<A>> profiles) {
        return null;
    }

    /**
     * Returns a map of Borda scores for each alternative
     * @param profiles list of all Profiles / Ballots / Votes
     * @return map of alternative -> Borda score
     */
    public Map<A, Float>  calculateScores(List<Profile<A>> profiles) {
        scores = new HashMap<>();
        LinkedList<A> alternatives = new LinkedList<>(profiles.get(0).keySet());


        int n = alternatives.size();
        // initialize map with '0' values
        for (A altA : alternatives) {
            scores.put(altA, 0f);
        }


        for(Profile<A> profile:profiles){
            List<List<A>> profileOrder = new ArrayList<>(n);
            for(int i=0;i<n;i++){
                List<A> empty= new ArrayList<>();
                profileOrder.add(empty);
            }
            for(A alt:alternatives){
                int priority = profile.get(alt);
                profileOrder.get(priority-1).add(alt);
            }
            Vector<Float> scoringVector = getAveragedScoringVector(n,profileOrder);
            int count = 0;
            for (List<A> altList : profileOrder) {
                for (A alt : altList) {
                    scores.put(alt, scores.get(alt) + scoringVector.get(count));
                    count++;
                }
            }
//            for(int p=1;p<=n;p++){
//                float gain = getScoringVector(n).get(p-1);
//                List<A> pList = profileOrder.get(p-1);
//                for(A alt:pList){
//                    gain = (float) (maxPriority -p)/pList.size();
//                    scores.put(alt, scores.get(alt)+gain);
//                }
//            }
        }

        return scores;
    }

}

