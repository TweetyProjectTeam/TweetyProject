package aggregation;

import functions.SocialWelfareFunction;
import java.util.*;

public class PreferenceAggregation<A> {
    private final SocialWelfareFunction<A> swf;
    private List<List<A>> preferenceRanking;
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

