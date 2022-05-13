package functions;

import aggregation.Profile;

import java.util.*;

public class CopelandSolution<A> extends CondorcetSWF<A> {
    @Override
    public List<List<A>> calculateRanks(List<Profile<A>> profiles) {
        scores = calculateScores(profiles);
        return null;
    }
    public Map<A, Float> calculateScores(List<Profile<A>> profiles) {
        Map<ArrayList<A>,ArrayList<Integer>> wins = new HashMap<>();
        scores = new HashMap<A, Float>();
        LinkedList<A> alternatives = new LinkedList<>(profiles.get(0).keySet());


        int n = alternatives.size();
        // initialize map with '0' values
        for (int i=0;i<n;i++) {
            A altA = alternatives.get(i);
            scores.put(altA,0f);
            for (int j=i+1;j<n;j++) {
                A altB = alternatives.get(j);
                ArrayList<A> comp = new ArrayList<>(2);
                comp.add(altA); comp.add(altB);
                ArrayList<Integer> compWins = new ArrayList<>(2);
                compWins.add(0); compWins.add(0);
                wins.put(comp,compWins);
            }


        }

        for(Profile<A> p: profiles) {
            for (int i=0;i<n;i++) {
                A altA = alternatives.get(i);
                for (int j=i+1;j<n;j++) {
                    A altB = alternatives.get(j);
                    ArrayList<A> comp = new ArrayList<>(2);
                    comp.add(altA); comp.add(altB);
                    int prioA = p.get(altA);
                    int prioB = p.get(altB);
                    if (prioA < prioB) {
                        int newWinsA = wins.get(comp).get(0) + 1;
                        int newWinsB = wins.get(comp).get(1);
                        ArrayList<Integer> newCompWins = new ArrayList<>(2);
                        newCompWins.add(newWinsA); newCompWins.add(newWinsB);
                        wins.put(comp, newCompWins);
                    }
                    if (prioA > prioB) {
                        int newWinsA = wins.get(comp).get(0);
                        int newWinsB = wins.get(comp).get(1)+1;
                        ArrayList<Integer> newCompWins = new ArrayList<>(2);
                        newCompWins.add(newWinsA); newCompWins.add(newWinsB);
                        wins.put(comp, newCompWins);
                    }
                }
            }
        }
        System.out.println(wins.toString());
        for (int i=0;i<n;i++) {
            A altA = alternatives.get(i);
            for (int j=i+1;j<n;j++) {
                A altB = alternatives.get(j);
                ArrayList<A> comp = new ArrayList<>(2);
                comp.add(altA); comp.add(altB);
                int winsA = wins.get(comp).get(0);
                int winsB = wins.get(comp).get(1);
                if (winsA>winsB){
                    scores.put(altA,scores.get(altA)+1f);
                }
                else if(winsA==winsB){
                    scores.put(altA,scores.get(altA)+1/2f);
                    scores.put(altB,scores.get(altB)+1/2f);
                }
                else{
                    scores.put(altB,scores.get(altB)+1f);
                }
            }
        }

        return scores;
    }


}
