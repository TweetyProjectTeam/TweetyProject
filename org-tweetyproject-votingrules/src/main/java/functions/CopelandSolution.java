package functions;

import aggregation.Profile;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.util.Pair;

import java.util.*;

/**
 *
 * @author Daniel Letkemann
 * scoring based on Copeland's method (pairwise matchups)
 *
 */
public class CopelandSolution<A extends Formula> extends CondorcetSWF<A> {
    @Override
    public List<List<A>> calculateRanks(List<Profile<A>> profiles) {
        scores = calculateScores(profiles);
        return null;
    }

    @Override
    public A getCondorcetWinner(List<Profile<A>> profiles) {
        if(profiles.isEmpty()){
            return null;
        }
        scores = calculateScores(profiles);
        List<Float> vals = new ArrayList<Float>(scores.values());
        Collections.sort(vals);
        Collections.reverse(vals);
        float maxVal = vals.get(0);
        float nextVal = vals.get(1);
        //no winner is present
        if(!(maxVal > nextVal)){
            return null;
        }
        else{
            Set<A> alternatives =  profiles.get(0).keySet();
            for(A alt: alternatives) {
                if (scores.get(alt) == maxVal) {
                    return alt;
                }
            }
        }
        //this should never be reached
        return null;
    }

    /**
     * get a map of Copeland scores for each alternative
     * @param profiles list of all Profiles / Ballots / Votes
     * @return map of alternative -> Copeland score
     */
    public Map<A, Float> calculateScores(List<Profile<A>> profiles) {
        Map<Pair<A, A>, Pair<Integer, Integer>> matchupWins = new HashMap<>();
        scores = new HashMap<>();
        LinkedList<A> alternatives = new LinkedList<>(profiles.get(0).keySet());
        LinkedList<Pair<A, A>> matchups = new LinkedList<>();

        int n = alternatives.size();
        // initialize map with all alternatives having score '0
        for (int i = 0; i < n; i++) {
            A altA = alternatives.get(i);
            scores.put(altA, 0f);
            //initialize map of matchups with both alternatives having '0' wins
            for (int j = i + 1; j < n; j++) {
                A altB = alternatives.get(j);
                Pair<A, A> matchup = new Pair<>(altA, altB);
                Pair<Integer, Integer> initMatchup = new Pair<>(0, 0);
                matchupWins.put(matchup, initMatchup);
                matchups.add(matchup);
            }


        }
        //count individual matchup wins (+1 for winning, 0 if tied or loss)
        //lower priority score = win ("1<2" -> "1 wins", "2 loses")
        for (Profile<A> p : profiles) {
            for (Pair<A, A> matchup : matchups) {
                A altA = matchup.getFirst();
                A altB = matchup.getSecond();

                int prioA = p.get(altA);
                int prioB = p.get(altB);

                int newWinsA = matchupWins.get(matchup).getFirst();
                int newWinsB = matchupWins.get(matchup).getSecond();
                if (prioA < prioB) {
                    ++newWinsA;
                }
                if (prioA > prioB) {
                    ++newWinsB;
                }
                Pair<Integer, Integer> newMatchupWins = new Pair<>(newWinsA, newWinsB);
                matchupWins.put(matchup, newMatchupWins);
            }
        }
        //compare wins of matchups and increment scores in the following way:
        // if strictly more wins: +1 point to winner
        // if equal amount of wins: 1/2 point to both
        for (Pair<A, A> matchup : matchups) {
            A altA = matchup.getFirst();
            A altB = matchup.getSecond();
            int winsA = matchupWins.get(matchup).getFirst();
            int winsB = matchupWins.get(matchup).getSecond();
            if (winsA > winsB) {
                scores.put(altA, scores.get(altA) + 1f);
            } else if (winsA == winsB) {
                scores.put(altA, scores.get(altA) + 1 / 2f);
                scores.put(altB, scores.get(altB) + 1 / 2f);
            } else {
                scores.put(altB, scores.get(altB) + 1f);
            }
        }

        return scores;
    }


}
