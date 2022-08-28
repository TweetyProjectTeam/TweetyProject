package org.tweetyproject.arg.rankings.extensionreasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.comparator.GeneralComparator;

import java.util.*;

public class AcceptabilityBasedExtensionReasoner {
    private AggregationFunction aggregationFunction;


    /**
     * Returns the rank-based extensions, which are a subset of the 'extensions' parameter.
     * Result depends on the input rankMap and the chosen Aggregation Function.
     * Integers in the map represent the rank in ASCENDING order, with 0 being best.
     * Aggregation with MIN/MAX of the empty Extension is interpreted as +Infinity/0 respectively.
     *
     * @param extensions    a collection of extensions
     * @param argumentRanks argument ranks as returned by argument ranking Reasoners "getModel" method
     * @param theory        a dung theory
     * @return order-based extension subset of Extensions for specified aggregation function
     * @throws Exception invalid argumentation Function
     */
    public Extension<DungTheory> getModels(Collection<Extension<DungTheory>> extensions, GeneralComparator<Argument, DungTheory> argumentRanks, DungTheory theory) throws Exception {
        Map<Extension<DungTheory>, Integer> extensionToScoreMap = getScoreMap(extensions, argumentRanks, theory);
        Extension<DungTheory> argmax = extensionToScoreMap.keySet().stream().findFirst().orElseThrow();
        for (Extension<DungTheory> ext : extensionToScoreMap.keySet()) {
            if (extensionToScoreMap.get(ext)>extensionToScoreMap.get(argmax)) {
                argmax = ext;
            }
        }

        return argmax;
    }

    /**
     * Return a map of arguments to their respective rank as integers. Lower value means better rank.
     *
     * @param argumentRanks rgument ranks as returned by argument ranking Reasoners "getModel" method
     * @param theory        a dung theory
     * @return argument to rank map
     */
    public Map<Extension<DungTheory>, Integer> getScoreMap(Collection<Extension<DungTheory>> extensions, GeneralComparator<Argument, DungTheory> argumentRanks, DungTheory theory) {
        Map<Pair<Extension<DungTheory>,Extension<DungTheory>>,Pair<Integer,Integer>> matchupWins = new HashMap<>();
        Map<Extension<DungTheory>, Integer> scoreMap = new HashMap<>();

        List<Argument> arguments = new LinkedList<>();
        arguments.addAll(theory.clone().getNodes());
        List<Extension<DungTheory>> extensionList = new LinkedList<>();
        extensionList.addAll(extensions);
        List<Pair<Extension<DungTheory>,Extension<DungTheory>>> matchups = new LinkedList<>();

        // initialize map with all alternatives having score '0
        int n = extensionList.size();
        for(int i=0; i < n; i++){
//            Argument arg1 = arguments.get(i);
            Extension<DungTheory> ext1 = extensionList.get(i);
            scoreMap.put(ext1, 0);
            //initialize map of matchups with both alternatives having '0' wins
            for (int j = i + 1; j < n; j++) {
//                Argument arg2 = arguments.get(j);
                Extension<DungTheory> ext2 = extensionList.get(j);
                Pair<Extension<DungTheory>,Extension<DungTheory>> matchup = new Pair<>(ext1,ext2);
                matchupWins.put(matchup, new Pair<>(0,0));
                matchups.add(matchup);
            }
        }
        //count individual argument matchup wins (+1 for winning, 0 if tied or loss)
        for (Pair<Extension<DungTheory>, Extension<DungTheory>> matchup : matchups) {
            Extension<DungTheory> ext1 = matchup.getFirst();
            Extension<DungTheory> ext2 = matchup.getSecond();

            int newWinsArg1 = matchupWins.get(matchup).getFirst();
            int newWinsArg2 = matchupWins.get(matchup).getSecond();
            for(Argument arg1:ext1){
                for(Argument arg2: ext2){
                    if (argumentRanks.isStrictlyMoreAcceptableThan(arg1,arg2)) {
                        ++newWinsArg1;
                    }
                    if (argumentRanks.isStrictlyMoreAcceptableThan(arg2,arg1)) {
                        ++newWinsArg2;
                    }
                }
            }


            matchupWins.put(matchup, new Pair<>(newWinsArg1,newWinsArg2));
        }
        //compare wins of matchups and increment scores in the following way:
        // if strictly more wins: +1 point to winner

        for (Pair<Extension<DungTheory>, Extension<DungTheory>> matchup : matchups) {
            Extension<DungTheory> ext1 = matchup.getFirst();
            Extension<DungTheory> ext2 = matchup.getSecond();
            int wins1 = matchupWins.get(matchup).getFirst();
            int wins2 = matchupWins.get(matchup).getSecond();
            if (wins1 > wins2) {
                scoreMap.put(ext1, scoreMap.get(ext1) + 1);
            }
            else if (wins1 < wins2) {
                scoreMap.put(ext2, scoreMap.get(ext2) + 1);
            }
        }

        return scoreMap;
    }

}
