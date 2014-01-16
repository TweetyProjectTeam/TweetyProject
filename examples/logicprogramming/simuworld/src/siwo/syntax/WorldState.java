package siwo.syntax;

import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import net.sf.tweety.logicprogramming.asplibrary.syntax.Atom;
import net.sf.tweety.logicprogramming.asplibrary.syntax.Rule;
import net.sf.tweety.logicprogramming.asplibrary.util.BeliefSet;

import siwo.parser.*;

/**
 * this class represents a simuworld world state.
 *
 * @author Thomas Vengels
 *
 */
public class WorldState {
	public WorldState() {
		F = new BeliefSet();
		A = new LinkedList<ActionRule>();
		C = new LinkedList<ConstraintRule>();
		I = new LinkedList<Atom>();
		E = new LinkedList<EffectRule>();
		Rpre = new LinkedList<Rule>();
	}
	
	// set of facts describing a new world
	public BeliefSet			F;
	
	// set of actions agents can commit on this world
	public List<ActionRule>		A;
	
	// set of constraints, controlling possible actions
	public List<ConstraintRule>	C;
	
	// set of effect rules
	public List<EffectRule>		E;
	
	// list of actions agents intend to perform
	public List<Atom>			I;
	
	// list of rules used at the beginning of a simulation
	public List<Rule>			Rpre;	
	
	/**
	 * this method loads a simulation world from a
	 * file.
	 *  
	 * @param filename ressource to load
	 * @return parsed simulation world
	 */
	public static WorldState loadFrom(String filename) {
		WorldState sw = null;
		
		try {
			
			SiWoParser parser = new SiWoParser(new FileReader(filename));
			sw = parser.siwo_parse();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sw;
	}
	
	@Override
	public String toString() {
		return "facts: "+this.F.size()+" rules: "+this.Rpre.size()+
		"\nactions: "+this.A.size() + " constraints:"+this.C.size()+
		"\neffects: "+this.E.size();
	}
		
}