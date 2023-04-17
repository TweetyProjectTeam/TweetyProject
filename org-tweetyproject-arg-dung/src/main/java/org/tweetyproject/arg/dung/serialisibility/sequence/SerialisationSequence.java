package org.tweetyproject.arg.dung.serialisibility.sequence;

import java.util.LinkedHashSet;

import org.tweetyproject.arg.dung.semantics.*;
import org.tweetyproject.arg.dung.syntax.*;

public class SerialisationSequence extends LinkedHashSet<Extension<DungTheory>> {

	private static final long serialVersionUID = -109538431325318647L;
	
	/**
	 * @return The extension of this sequence, which has the most arguments and which is thus seen as the final extension
	 */
	public Extension<DungTheory> getExtensionFinal() {
		Extension<DungTheory> output = null;
		for (Extension<DungTheory> extension : this) {
			if(output == null || extension.size() > output.size()) output = extension;
		}
		return output;
	}
}
