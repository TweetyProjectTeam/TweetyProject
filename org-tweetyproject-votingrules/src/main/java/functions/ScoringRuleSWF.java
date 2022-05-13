package functions;

import aggregation.Profile;

import java.util.List;
import java.util.Map;

public abstract class ScoringRuleSWF<A> implements SocialWelfareFunction<A>{
    protected A altType;
    protected Map<A, Float> scores;
    protected List<List<A>> ranks;
//    protected abstract Map<A, Float> calculateScores(List<Profile<A>> profiles);

    public abstract List<Float> getScoringVector(Integer length);
    /**
     *returns a ranked list of alternatives for a list of votes/ballots. The lower the index of the list, the better ranked it is.
     * @param profiles a list of votes/ballots for alternatives of type "A"
     * @return a ranking of alternatives
     */
   public abstract List<List<A>> calculateRanks(List<Profile<A>> profiles);
}
