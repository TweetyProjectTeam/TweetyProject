package functions;

import aggregation.Profile;

import java.util.*;

public class BordaCount<A> extends ScoringRuleSWF<A>{
    @Override
    public List<Float> getScoringVector(Integer length) {
        ArrayList<Float> scoringVector = new ArrayList<>();
        for(float i=length-1; i>=0; i--){
            scoringVector.add(i);
        }
        return scoringVector;
    }

    public List<Float> getAveragedScoringVector(Integer length,List<List<A>> profiles) {
        ArrayList<Float> scoringVector = (ArrayList<Float>) getScoringVector(length);
        for(int i=0; i<profiles.size(); i++){
            ArrayList<A> tiedAlternatives = (ArrayList<A>) profiles.get(i);
            if(tiedAlternatives.size()>1){
                average(scoringVector,i,i+tiedAlternatives.size());
            }
        }
        return scoringVector;
    }

    private void average(ArrayList<Float> scoringVector, int startIndex, int endIndex) {
        float average = 0;
        for(int i=startIndex;i<endIndex;i++){
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

    public Map<A, Float>  calculateScores(List<Profile<A>> profiles) {
        scores = new HashMap<A, Float>();
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
            List<Float> scoringVector = getAveragedScoringVector(n,profileOrder);
            int count = 0;
            for(int i=0;i<profileOrder.size();i++){
                List<A> altList = profileOrder.get(i);
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

