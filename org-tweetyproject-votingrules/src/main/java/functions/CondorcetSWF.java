package functions;

import aggregation.Profile;
import org.tweetyproject.commons.Formula;

import java.util.*;
/**
 *
 * @author Daniel Letkemann
 * abstract implementation of social welfare functions based on so called "Condorcet method" voting systems
 *
 */
public abstract class CondorcetSWF<A extends Formula> implements SocialWelfareFunction<A>{
    protected Map<A, Float> scores;
    protected List<List<A>> ranks;
//    protected abstract Map<A, Float> calculateScores(List<Profile<A>> profiles);

    /**
     *Returns a ranked list of alternatives for a list of profiles/votes/ballots. The lower the index of the list, the better ranked it is.
     * @param profiles a list of votes/ballots for alternatives of type "A"
     * @return a ranking of alternatives
     */
   public abstract List<List<A>> calculateRanks(List<Profile<A>> profiles);

    /**
     * Returns the Condorcet Winner from the input list of profiles/votes/ballots. Returns null if no Condorcet Winner is present.
     * @param profiles a list of votes/ballots for alternatives of type "A"
     * @return Condorcet Winner
     */
   public abstract A getCondorcetWinner(List<Profile<A>> profiles);
}
