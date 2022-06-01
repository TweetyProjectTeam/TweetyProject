package functions;

import aggregation.Profile;
import org.tweetyproject.commons.Formula;

import java.util.List;
import java.util.Map;
/**
 *
 * @author Daniel Letkemann
 * abstract implementation of social welfare functions based on "Scoring-rule-based" voting systems
 *
 */
public abstract class ScoringRuleSWF<A extends Formula> implements SocialWelfareFunction<A>{
    protected Map<A, Float> scores;

    /**
     * Returns a scoring vector of certain length for the according scoring rule implementation
     * (e.g. "Borda" scoring vector = [n-1,n-2,...,1,0])
     * @param length length of the result vector
     * @return scoring vector of rule
     */
    public abstract List<Float> getScoringVector(Integer length);
    /**
     * Returns a ranked list of alternatives for a list of votes/ballots. The lower the index of the list, the better ranked it is.
     * @param profiles a list of votes/ballots for alternatives of type "A"
     * @return a ranking of alternatives
     */
   public abstract List<List<A>> calculateRanks(List<Profile<A>> profiles);
}
