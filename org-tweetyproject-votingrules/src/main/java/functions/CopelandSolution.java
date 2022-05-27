package functions;

import aggregation.Profile;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.util.Pair;

import java.util.*;

public class CopelandSolution<A extends Formula> extends CondorcetSWF<A> {
    @Override
    public List<List<A>> calculateRanks(List<Profile<A>> profiles) {
        scores = calculateScores(profiles);
        return null;
    }

    public Map<A, Float> calculateScores(List<Profile<A>> profiles) {
        Map<Pair<A, A>, Pair<Integer, Integer>> pairwiseWins = new HashMap<>();
        scores = new HashMap<A, Float>();
        LinkedList<A> alternatives = new LinkedList<>(profiles.get(0).keySet());
        LinkedList<Pair<A, A>> alternativePairs = new LinkedList<>();


        int n = alternatives.size();
        // initialize map with '0' values
        for (int i = 0; i < n; i++) {
            A altA = alternatives.get(i);
            scores.put(altA, 0f);
            for (int j = i + 1; j < n; j++) {
                A altB = alternatives.get(j);
                Pair<A, A> comp = new Pair<A, A>(altA, altB);
                Pair<Integer, Integer> compWins = new Pair<Integer, Integer>(0, 0);
                pairwiseWins.put(comp, compWins);
                alternativePairs.add(comp);
            }


        }

        for (Profile<A> p : profiles) {
            for (Pair<A, A> comp : alternativePairs) {
                A altA = comp.getFirst();
                A altB = comp.getSecond();

                int prioA = p.get(altA);
                int prioB = p.get(altB);

                int newWinsA = pairwiseWins.get(comp).getFirst();
                int newWinsB = pairwiseWins.get(comp).getSecond();
                if (prioA < prioB) {
                    ++newWinsA;
                }
                if (prioA > prioB) {
                    ++newWinsB;
                }
                Pair<Integer, Integer> newCompWins = new Pair<Integer, Integer>(newWinsA, newWinsB);
                pairwiseWins.put(comp, newCompWins);
            }
        }
//        System.out.println(pairwiseWins.toString());
        for (Pair<A, A> comp : alternativePairs) {
            A altA = comp.getFirst();
            A altB = comp.getSecond();
            int winsA = pairwiseWins.get(comp).getFirst();
            int winsB = pairwiseWins.get(comp).getSecond();
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
