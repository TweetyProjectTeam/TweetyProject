package org.tweetyproject.arg.bipolar.analysis.extensions;

import org.tweetyproject.arg.bipolar.analysis.AnalysisType;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.bipolar.syntax.NamedPEAFTheory;

/**
 * Computes the preferred extension of the given PEAF
 * <p>
 * Uses `jargsemsat` for computing extensions.
 *
 * @author Taha Dogan Gunes
 */
public class PreferredAnalysis extends AbstractExtensionAnalysis {
    public PreferredAnalysis(NamedPEAFTheory peaf) {
        super(peaf, new SimplePreferredReasoner(), AnalysisType.PREFERRED);
    }
}
