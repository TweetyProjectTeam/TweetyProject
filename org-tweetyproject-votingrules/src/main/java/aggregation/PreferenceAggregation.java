package aggregation;

import functions.SocialWelfareFunction;
import org.tweetyproject.commons.Formula;

import java.util.*;
/**
 *
 * @author Daniel Letkemann
 * Preference aggregation from SWF's
 * (WIP, may be redundant)
 */
public class PreferenceAggregation<A extends Formula> {
    private final SocialWelfareFunction<A> swf;
    public List<List<A>> preferenceRanking;
    public PreferenceAggregation(SocialWelfareFunction<A> swf){
        this.swf = swf;

    }


    public PreferenceAggregation(SocialWelfareFunction<A> swf,List<Profile<A>> profiles){
        this.swf = swf;
        this.preferenceRanking = swf.calculateRanks(profiles);

    }

    public List<List<A>> getPreferenceRanking(List<Profile<A>> profiles){
        return swf.calculateRanks(profiles);
    }

}

