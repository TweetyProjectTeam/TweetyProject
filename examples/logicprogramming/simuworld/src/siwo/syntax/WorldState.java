/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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