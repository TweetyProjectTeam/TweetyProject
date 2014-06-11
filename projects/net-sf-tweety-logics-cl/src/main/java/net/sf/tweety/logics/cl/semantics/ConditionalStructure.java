package net.sf.tweety.logics.cl.semantics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.cl.syntax.Conditional;
import net.sf.tweety.logics.pl.semantics.NicePossibleWorld;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * Represents a conditional structure as introduced in Section 3.5
 * "Conditionals in Nonmonotonic Reasoning and Belief Revision" of 
 * Gabrielle Kern-Isberner
 * 
 * Internally it saves the data to two nested Maps. The first map maps to each PossibleWorld
 * a map of Conditionals to Generators and the second map maps to each Conditional
 * a map of PossibleWorlds to Generators. Those two maps allow fast access to
 * the data of the ConditionalStructure. The first map can be used to support
 * a nice representation of the ConditionalStructure and the second map can
 * be used for further processing depending on the Conditionals (for Kappa 
 * values in C-Representation for example).
 * 
 * To change the representation of the ConditionalStructure the user can
 * provide a Comparator<NicePossibleWorld> implementation that changes the
 * sort behavior of the world data map or he/she can provide a 
 * Comparator<Pair<Proposition, Boolean>> to change the ordering of the literals
 * in the NicePossibleWorld representation.
 * The later approach will fit for most situations.
 * 
 * The default sorting behavior depends on the representation behavior of NicePossibleWorld
 * that can also be adapt because it uses the toString method() and sorts this string alphabetically
 * so that all possible worlds for a,b,c are represented in the following order:
 * 
 *  a b c
 *  a b-c
 *  a-b c
 *  a-b-c
 * -a b c
 * -a b-c
 * -a-b c
 * -a-b-c
 * 
 * The default Sorting provides a clean binary switching of true/false like counting a bit string. If
 * the user wants another ordering like c a b the Sorting behavior of the NicePossibleWorld
 * shall be adapted by providing a Comparator<Pair<Proposition, Boolean>> implementation that
 * implements the relation "c < a < b" by using the setWorldRepresentation() method.
 * 
 * If the user wants to use a sorting depending on the Generator count for the worlds the
 * setWorldSorting() method can be used to provide the correct sorting method.
 * 
 * @author Tim Janus
 */
public class ConditionalStructure implements Comparator<NicePossibleWorld>{
	
	/**
	 * The possible values of a conditional for a possible world
	 * @author Tim Janus
	 */
	public static enum Generator {
		/** The possible world verifies the conditional */
		CG_PLUS,
		/** The possible world falsifies the conditional */
		CG_MINUS,
		/** The possible world neither verifies nor falsifies the conditional because the premise is false */
		CG_ONE
	}
	
	/** 
	 * The data of the conditional structure maps to every PossibleWorld a map
	 * of Conditionals and their Generators.
	 * The PossibleWorld is implemented as NicePossibleWorld and therefore 
	 * stores a data structure for it's representation that is used by
	 * the ConditionalStructure to represent itself
	 */
	private SortedMap<NicePossibleWorld, Map<Conditional, Generator>> worldData;
	
	/**
	 * This maps saves the same data as worldData but uses another mapping allowing fast
	 * access to the Generators using Conditionals. This is important for processing 
	 * semantics like C-representations because they mainly depend on these mapping.
	 */
	private Map<Conditional, SortedMap<NicePossibleWorld, Generator>> conditionalData = 
			new HashMap<Conditional, SortedMap<NicePossibleWorld,Generator>>();

	private Map<Conditional, List<NicePossibleWorld>> verifyingWorlds = new HashMap<Conditional, List<NicePossibleWorld>>();
	
	private Map<Conditional, List<NicePossibleWorld>> falsifyingWorlds = new HashMap<Conditional, List<NicePossibleWorld>>();
	
	/**
	 * The object used to sort the Nice Possible worlds
	 */
	private Comparator<NicePossibleWorld> worldSorting;
	
	/**
	 * The object used to sort the literals in the PossibleWorld
	 */
	private Comparator<Pair<Proposition, Boolean>> worldRepresentation;
	
	/** the signature of the conditional structure */
	private PropositionalSignature signature = new PropositionalSignature();
	
	/** Default-Ctor generates empty Conditional structure */
	public ConditionalStructure() {
		this(new HashSet<Conditional>(), null);
	}
	
	/**
	 * Ctor: Generates an empty ConditionalStructure that uses the given
	 * Comparator to sort its worldData map.
	 * @param comparator	An implementation for sorting the world data 
	 * 						if null is given the default sorting behavior is used.
	 */
	public ConditionalStructure(Comparator<NicePossibleWorld> comparator) {
		this(new HashSet<Conditional>(), comparator);
	}
	
	/**
	 * Ctor: generates a conditional structure containing the given conditionals
	 * @param conditionals	A collection of conditionals that shall form the 
	 * 						ConditionalStructure
	 */
	public ConditionalStructure(Collection<Conditional> conditionals) {
		this(conditionals, null);
	}
	
	/**
	 * Ctor: Generates a ConditionalStructure containing the given conditionals and
	 * using the given comparator to sort the worldData map.
	 * @param conditionals	A collection of conditionals that shall form the 
	 * 						ConditionalStructure
	 * @param comparator	An implementation for sorting the world data 
	 * 						if null is given the default sorting behavior is used.
	 */
	public ConditionalStructure(Collection<Conditional> conditionals, Comparator<NicePossibleWorld> comparator) {
		this.worldSorting = comparator == null ? this : comparator;
		worldData = new TreeMap<NicePossibleWorld, Map<Conditional, Generator>>(this.worldSorting);
		for(Conditional cond : conditionals) {
			addConditional(cond);
		}
	}
	
	/** @return An unmodifiable set of all conditionals in this ConditionalStructure */
	public Set<Conditional> getConditionals() {
		return Collections.unmodifiableSet(conditionalData.keySet());
	}
	
	/** @return An unmodifiable set of all possible worlds in this ConditionalStructure */
	public Set<NicePossibleWorld> getPossibleWorlds() {
		return Collections.unmodifiableSet(worldData.keySet());
	}
	
	public List<NicePossibleWorld> getFalsifiyingWorlds(Conditional cond) {
		List<NicePossibleWorld> toWrap = falsifyingWorlds.get(cond);
		return toWrap == null ? null : Collections.unmodifiableList(toWrap);
	}
	
	public List<NicePossibleWorld> getVerifyingWorlds(Conditional cond) {
		List<NicePossibleWorld> toWrap = verifyingWorlds.get(cond);
		return toWrap == null ? null : Collections.unmodifiableList(toWrap);
	}
	
	/**
	 * Processes the map from Conditionals to Generators for a given PossibleWorld
	 * @param world	The PossibleWorld thats Generators shall be returned
	 * @return	A map containing all Generators for the world as values and
	 * 			the associated conditionals as key.
	 */
	public Map<Conditional, Generator> getWorldGenerators(NicePossibleWorld world) {
		Map<Conditional, Generator> toWrap = worldData.get(world);
		return toWrap == null ? null : Collections.unmodifiableMap(toWrap);
	}
	
	/**
	 * Processes the Map form PossibleWorlds to the Generators of the given Conditional
	 * @param conditional	The Conditional 
	 * @return		null if no such map exists or the map mapping the PossibleWorlds
	 * 				to Generators for the given Conditional
	 */
	public Map<NicePossibleWorld, Generator> getConditionalGenerators(Conditional conditional) {
		Map<NicePossibleWorld, Generator> toWrap = conditionalData.get(conditional);
		return toWrap == null ? null : Collections.unmodifiableMap(toWrap);
	}
	
	/**
	 * @return 	An unmodifiable Collection containing all propositions that altogether form
	 * 			the signature of the ConditionalStructure
	 */
	public Collection<Proposition> getSignature() {
		return Collections.unmodifiableCollection(signature);
	}
	
	/**
	 * Changes the ordering of the PossibleWorlds for representation purposes, before
	 * using this method proof if the goal can be easier achieved using the 
	 * setWorldRepresentation() method that allows changing the ordering of
	 * the literals in the world representation.
	 * @param comparator	The implementation sorting the possible worlds
	 */
	public void setWorldSorting(Comparator<NicePossibleWorld> comparator) {
		this.worldSorting = comparator == null ? this : comparator;
		SortedMap<NicePossibleWorld, Map<Conditional, Generator>> temp = worldData;
		worldData = new TreeMap<NicePossibleWorld, Map<Conditional,Generator>>(comparator);
		worldData.putAll(temp);
		
		for(Conditional cond : conditionalData.keySet()) {
			SortedMap<NicePossibleWorld, Generator> newMap = 
					new TreeMap<NicePossibleWorld, ConditionalStructure.Generator>(this.worldSorting);
			newMap.putAll(conditionalData.get(cond));
			conditionalData.put(cond, newMap);
		}
	}
	
	/**
	 * Changes the internal representations of the worlds, normally the propositions of a world
	 * are ordered alphabetically but this behavior can be changed using this method.
	 * @param comparator	The new implementation of a Comparator that provides the new
	 * 						sorting behavior for the Propositions in a PossibleWorld.
	 */
	public void setWorldRepresentation(Comparator<Pair<Proposition, Boolean>> comparator) {
		this.worldRepresentation = comparator;
		for(NicePossibleWorld npw : worldData.keySet()) {
			npw.setComparator(comparator);
		}
	}
	
	/**
	 * Adds the given Conditional to the ConditionalStructure and updates the
	 * structure.
	 * @param cond	The new Conditional
	 * @return		True if the conditional is added and ConditionalStructure is
	 * 				updated, false if the Conditional is already part of the
	 * 				ConditionalStructure
	 */
	public boolean addConditional(Conditional cond) {
		if(this.conditionalData.containsKey(cond)) {
			return false;
		}
		
		// Check for signature changes:
		int sizeBefore = this.signature.size();
		this.signature.addSignature(cond.getSignature());
		if(sizeBefore < this.signature.size()) {
			// if a change occurred update the worldData.
			updateSignature(this.signature);
		}
		
		// generate fast falsifying and verifying lists:
		this.verifyingWorlds.put(cond, new ArrayList<NicePossibleWorld>());
		this.falsifyingWorlds.put(cond, new ArrayList<NicePossibleWorld>());
		
		// add the new conditional and update the world data:
		this.conditionalData.put(cond, 
				new TreeMap<NicePossibleWorld, ConditionalStructure.Generator>(this.worldSorting));
		for(NicePossibleWorld npw : worldData.keySet()) {
			putGenerator(npw, cond);
		}
		
		return true;
	}
	
	/**
	 * Removes the given Conditional from the ConditionalStructure and updates
	 * the structure.
	 * @param cond	The Conditional that shall be removed
	 * @return		True if the Conditional is part of the ConditionalStructure and
	 * 				was removed, false if the Conditional is not part of the 
	 * 				ConditionalStructure and nothing happened.
	 */
	public boolean removeConditional(Conditional cond) {
		if(this.conditionalData.remove(cond) != null) {
			// Check for signature changes
			int sizeBefore = this.signature.size();
			this.signature.clear();
			for(Conditional c : this.conditionalData.keySet()) {
				this.signature.addSignature(c.getSignature());
			}
			if(sizeBefore > this.signature.size()) {
				// if a change occurred update the worldData.
				updateSignature(this.signature);
			}
			
			// remove generators of this conditional from world data
			for(NicePossibleWorld npw : worldData.keySet()) {
				worldData.get(npw).remove(cond);
			}
			
			// remove fast access lists:
			verifyingWorlds.remove(cond);
			falsifyingWorlds.remove(cond);
			return true;
		}
		return false;
	}
	
	/**
	 * Clears the ConditionalStructure, after calling this method the
	 * Structure is empty.
	 */
	public void clear() {
		this.conditionalData.clear();
		this.worldData.clear();
		this.verifyingWorlds.clear();
		this.falsifyingWorlds.clear();
		this.signature.clear();
	}
	
	/**
	 * Update the data structure worldData by removing worlds that are not
	 * representable by the given signature and adding worlds that are new
	 * with the new signature.
	 * 
	 * @param signature	The propositional signature representing the problem domain.
	 */
	private void updateSignature(Collection<Proposition> signature) {
		Set<NicePossibleWorld> newWorlds = NicePossibleWorld.getAllPossibleWorlds(signature);
		
		// First: Remove all Worlds that are not representable by the signature anymore:
		Set<NicePossibleWorld> toRemove = new HashSet<NicePossibleWorld>(worldData.keySet());
		toRemove.removeAll(newWorlds);
		for(NicePossibleWorld npw : toRemove) {
			worldData.remove(npw);
		}
		
		// Second: Update the signature of all Possible Worlds:
		for(NicePossibleWorld npw : worldData.keySet()) {
			npw.setSignature(signature);
		}
		
		// Third: Add all Worlds were not representable before:
		Set<NicePossibleWorld> toAdd = new HashSet<NicePossibleWorld>(newWorlds);
		toAdd.removeAll(worldData.keySet());
		for(NicePossibleWorld npw : toAdd) {
			if(worldRepresentation != null) {
				npw.setComparator(worldRepresentation);
			}
			
			worldData.put(npw, new HashMap<Conditional, ConditionalStructure.Generator>());
			for(Conditional cond : conditionalData.keySet()) {
				putGenerator(npw, cond);
			}
		}
	}
	
	/**
	 * Processes the generator of the given Conditional cond for the PossibleWorld
	 * npw and saves it in the worldData data structure if it is not equal CG_ONE.
	 * @param npw	The PossibleWorld 
	 * @param cond	The Conditional
	 * @return		True if the Generator is not equal CG_ONe and is added to worldData or
	 * 				false if the Generator is CG_ONE and is not added to worldData.
	 */
	private boolean putGenerator(NicePossibleWorld npw, Conditional cond) {
		// generate propositional head and body formulas
		Conjunction conjunction = new Conjunction();
		conjunction.addAll(cond.getPremise());
		boolean head = npw.satisfies(cond.getConclusion());
		boolean body = npw.satisfies((PropositionalFormula)conjunction);
		
		// process the generator for the given world and conditional:
		Generator gen = null;
		if(head && body) {
			gen = Generator.CG_PLUS;
		} else if(!head && body) {
			gen = Generator.CG_MINUS;
		} else {
			gen = Generator.CG_ONE;
		}

		// save the generator to both maps if it is not CG_ONE:
		if(gen != Generator.CG_ONE) {
			worldData.get(npw).put(cond, gen);
			conditionalData.get(cond).put(npw, gen);
			if(gen == Generator.CG_PLUS) {
				verifyingWorlds.get(cond).add(npw);
			} else if(gen == Generator.CG_MINUS) {
				falsifyingWorlds.get(cond).add(npw);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		String reval = "";
		for(NicePossibleWorld npw : worldData.keySet()) {
			reval += npw.toString() + ": ";
			if(worldData.get(npw).size() > 0) {
				for(Conditional cond : worldData.get(npw).keySet()) {
					reval += worldData.get(npw).get(cond).toString() + ", ";
				}
				reval = reval.substring(0, reval.length()-2);
			} else {
				reval += "-";
			}
			reval += "\n";
		}
		return reval;
	}

	@Override
	public int compare(NicePossibleWorld o1, NicePossibleWorld o2) {
		return o1.toString().compareTo(o2.toString());
	}
}