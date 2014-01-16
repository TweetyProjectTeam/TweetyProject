package net.sf.tweety.agents.dialogues.oppmodels;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.dung.syntax.Argument;

/**
 * This class realizes a recognition function as explained in
 * [Rienstra, Thimm, in preparation].
 * 
 * @author Matthias Thimm
 */
public class RecognitionFunction extends HashMap<Argument,Set<Argument>> {

	/** For serialization */
	private static final long serialVersionUID = 1L;
	
	/** Returns the argument which maps to the set containing
	 * the given argument.
	 * @param a an argument.
	 * @return an argument.
	 */
	public Argument getPreimage(Argument a){
		for(Map.Entry<Argument,Set<Argument>> entry: this.entrySet()){
			if(entry.getValue().contains(a))
				return entry.getKey();
		}
		return null;
	}

}
