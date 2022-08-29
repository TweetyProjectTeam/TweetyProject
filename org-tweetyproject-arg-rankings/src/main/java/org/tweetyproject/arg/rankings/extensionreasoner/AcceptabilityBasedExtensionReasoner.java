package org.tweetyproject.arg.rankings.extensionreasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.comparator.GeneralComparator;

import java.util.*;

public class AcceptabilityBasedExtensionReasoner {
    /**
     * Reasoner for refining extension based semantics based on acceptability of arguments.
     * Based on 'Def. 28' from "Combining Extension-Based Semantics and Ranking-Based Semantics for Abstract Argumentation" (E. Bonzon et al, KR 2018)
     * * @author Daniel Letkemann
     */
    private AggregationFunction aggregationFunction;


    /**
     * Returns the acceptability-based extensions from any subset of extensions.
     * These are the extensions which have the most wins in pairwise comparisons with the rest.
     * An extensions wins, if it has a higher number of arguments which are strictly more acceptable than its opponents.
     *
     * @param extensions    a collection of extensions
     * @param argumentRanks argument ranks as returned by argument ranking Reasoners "getModel" method
     * @param theory        a dung theory
     * @return acceptability-based extension subset of extensions
     */
    public Collection<Extension<DungTheory>> getModels(Collection<Extension<DungTheory>> extensions, GeneralComparator<Argument, DungTheory> argumentRanks, DungTheory theory) throws Exception {
        Map<Integer, Collection<Extension<DungTheory>>> scoreToExtensionsMap = getScoreToExtensionsMap(extensions, argumentRanks, theory);
        List<Integer> scores = new LinkedList<>(scoreToExtensionsMap.keySet());
        Collections.sort(scores);
        int argmaxIndex = scores.get(scores.size() - 1);
//        Extension<DungTheory> argmax = scoreToExtensionsMap.keySet().stream().findFirst().orElseThrow();
//        for (Extension<DungTheory> ext : scoreToExtensionsMap.keySet()) {
//            if (scoreToExtensionsMap.get(ext)>scoreToExtensionsMap.get(argmax)) {
//                argmax = ext;
//            }
//        }

        return scoreToExtensionsMap.get(argmaxIndex);
    }

    /**
     * Return a map of scores to a collection of respective extensions.
     * "Inverted" output of getExtensionToScoreMap
     *
     * @param extensions    a collection of extensions
     * @param argumentRanks argument ranks as returned by argument ranking Reasoners "getModel" method
     * @param theory        a dung theory
     * @return score-to-extensions map
     */
    public Map<Integer, Collection<Extension<DungTheory>>> getScoreToExtensionsMap(Collection<Extension<DungTheory>> extensions, GeneralComparator<Argument, DungTheory> argumentRanks, DungTheory theory) {

        Map<Extension<DungTheory>, Integer> extensionToScoreMap = getExtensionToScoreMap(extensions, argumentRanks, theory);
        Map<Integer, Collection<Extension<DungTheory>>> scoreToExtensionsMap = new HashMap<>();

        for (Extension<DungTheory> ext : extensionToScoreMap.keySet()) {
            int score = extensionToScoreMap.get(ext);
            scoreToExtensionsMap.computeIfAbsent(score, k -> new HashSet<>());
            scoreToExtensionsMap.get(score).add(ext);
        }

        return scoreToExtensionsMap;
    }

    /**
     * Returns a map of extensions and their respective scores/wins.
     *
     * @param extensions    a collection of extensions
     * @param argumentRanks argument ranks as returned by argument ranking Reasoners "getModel" method
     * @param theory        a dung theory
     * @return extension-to-score map
     */
    public Map<Extension<DungTheory>, Integer> getExtensionToScoreMap(Collection<Extension<DungTheory>> extensions, GeneralComparator<Argument, DungTheory> argumentRanks, DungTheory theory) {
        Map<Pair<Extension<DungTheory>, Extension<DungTheory>>, Pair<Integer, Integer>> matchupWins = new HashMap<>();
        Map<Extension<DungTheory>, Integer> extensionToScoreMap = new HashMap<>();

        List<Extension<DungTheory>> extensionList = new LinkedList<>(extensions);
        List<Pair<Extension<DungTheory>, Extension<DungTheory>>> matchups = new LinkedList<>();

        // initialize map with all alternatives having score '0
        int n = extensionList.size();
        for (int i = 0; i < n; i++) {
//            Argument arg1 = arguments.get(i);
            Extension<DungTheory> ext1 = extensionList.get(i);
            extensionToScoreMap.put(ext1, 0);
            //initialize map of matchups with both alternatives having '0' wins
            for (int j = i + 1; j < n; j++) {
//                Argument arg2 = arguments.get(j);
                Extension<DungTheory> ext2 = extensionList.get(j);
                Pair<Extension<DungTheory>, Extension<DungTheory>> matchup = new Pair<>(ext1, ext2);
                matchupWins.put(matchup, new Pair<>(0, 0));
                matchups.add(matchup);
            }
        }
        //count individual argument matchup wins (+1 for winning, 0 if tied or loss)
        for (Pair<Extension<DungTheory>, Extension<DungTheory>> matchup : matchups) {
            Extension<DungTheory> ext1 = matchup.getFirst();
            Extension<DungTheory> ext2 = matchup.getSecond();

            int newWinsArg1 = matchupWins.get(matchup).getFirst();
            int newWinsArg2 = matchupWins.get(matchup).getSecond();
            for (Argument arg1 : ext1) {
                for (Argument arg2 : ext2) {
                    if (argumentRanks.isStrictlyMoreAcceptableThan(arg1, arg2)) {
                        ++newWinsArg1;
                    }
                    if (argumentRanks.isStrictlyMoreAcceptableThan(arg2, arg1)) {
                        ++newWinsArg2;
                    }
                }
            }


            matchupWins.put(matchup, new Pair<>(newWinsArg1, newWinsArg2));
        }
        //compare wins of matchups and increment scores in the following way:
        // if strictly more wins: +1 point to winner

        for (Pair<Extension<DungTheory>, Extension<DungTheory>> matchup : matchups) {
            Extension<DungTheory> ext1 = matchup.getFirst();
            Extension<DungTheory> ext2 = matchup.getSecond();
            int wins1 = matchupWins.get(matchup).getFirst();
            int wins2 = matchupWins.get(matchup).getSecond();
            if (wins1 > wins2) {
                extensionToScoreMap.put(ext1, extensionToScoreMap.get(ext1) + 1);
            } else if (wins1 < wins2) {
                extensionToScoreMap.put(ext2, extensionToScoreMap.get(ext2) + 1);
            }
        }
        return extensionToScoreMap;
    }

}
