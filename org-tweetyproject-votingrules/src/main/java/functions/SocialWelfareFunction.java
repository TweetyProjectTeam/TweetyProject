package functions;

import aggregation.Profile;
import org.tweetyproject.commons.Formula;

import java.util.*;

public interface SocialWelfareFunction<A extends Formula>{
//    protected abstract Map<A, Float> calculateScores(List<Profile<A>> profiles);
    /**
     *returns a ranked list of alternatives for a list of votes/ballots. The lower the index of the list, the better ranked it is.
     * @param profiles a list of votes/ballots for alternatives of type "A"
     * @return a ranking of alternativesyu
     */
   public List<List<A>> calculateRanks(List<Profile<A>> profiles);
}
