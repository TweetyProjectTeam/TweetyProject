package org.tweetyproject.arg.rankings.extensionreasoner;

import java.util.Collection;
import java.util.Vector;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.CategorizerRankingReasoner;
import org.tweetyproject.comparator.NumericalPartialOrder;

/**
 * This class implements the <code>StrengthVectorFunction<code> interface and realizes the categorizer ranking based on 
 * [Pu, Zhang, Luo, Luo. Argument Ranking with Categorizer Function. KSEM 2014].
 * 
 * @author Benjamin Birner
 *
 */
public class CategorizerRanking implements StrengthVectorFunction {

	@Override
	public Vector<Double> getStrengthVector(Extension<DungTheory> ext, Collection<Extension<DungTheory>> extensions, DungTheory dung) {
		
		CategorizerRankingReasoner crr = new CategorizerRankingReasoner();
        NumericalPartialOrder<Argument, DungTheory> npo = crr.getModel(dung);
		Vector<Double> strVec = new Vector<Double>();
		for(Argument a : ext) {
			strVec.add(1-npo.get(a));	
		}
		return strVec;
	}

}
