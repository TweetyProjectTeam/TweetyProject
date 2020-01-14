/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.bipolar.syntax;

import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.graphs.Graph;

import java.util.*;

/**
 * This class implements a bipolar abstract argumentation theory with support in an evidential sense.
 * ie. we distinguish between prima-facie and standard arguments. Prima-facie arguments do not
 * require any support from other arguments to stand, while standard arguments must be supported
 * by at least one prima-facie argument.
 * <br>
 * <br>See
 * <br>
 * <br>Polberg, Oren. Revisiting Support in Abstract Argumentation Systems. 2014
 *
 *
 * 
 *
 */
public class EvidentialArgSystem extends BipolarArgFramework {
    /**
     * For archiving sub graphs
     */
    private static Map<BipolarArgFramework, Collection<Graph<Argument>>> archivedSubgraphs = new HashMap<BipolarArgFramework, Collection<Graph<Argument>>>();

    /**
     * Special argument Epsilon, which serves as a representation of the prima facie arguments
     */
    private Argument epsilon;

    /**
     * Default constructor; initializes empty sets of arguments and attacks
     */
    public EvidentialArgSystem() {
        super();
        this.epsilon = new Argument("epsilon");
        this.add(epsilon);
    }

    /**
     * Creates a new theory from the given graph.
     *
     * @param graph some graph
     */
    public EvidentialArgSystem(Graph<Argument> graph) {
        //TODO
    }

    /**
     * The characteristic function of an abstract argumentation framework: F_ES(S) = {A|A is acceptable wrt. S}.
     * @param extension an extension (a set of arguments).
     * @return an extension (a set of arguments).
     */
    public Extension fes(Extension extension){
        Extension newExtension = new Extension();
        for (Argument argument : this) {
            if (this.isAcceptable(argument, extension))
                newExtension.add(argument);
        }
        return newExtension;
    }

    /**
     * returns true if argument has reasoner.evidential support from set <code>ext</code>.
     * An argument a has e-support from ext iff a=epsilon or there exists an
     * argument x of ext which supports a and all x has e-support from ext \ {a}
     * @param argument an argument
     * @param ext an extension, ie. a set of arguments
     * @return true if argument has e-support from <code>ext</code>.
     */
    public boolean hasEvidentialSupport(Argument argument, Collection<Argument> ext){
        if(argument == this.epsilon)
            return true;
        Set<Argument> extWithoutArgument = new HashSet<Argument>(ext);
        extWithoutArgument.remove(argument);
        for(Argument supporter: ext) {
            if (this.isDirectSupportedBy(argument, supporter) && this.hasEvidentialSupport(supporter, extWithoutArgument))
                return true;
        }
        return false;
    }

    /**
     * returns true if argument has minimal reasoner.evidential support from set <code>ext</code>.
     * An argument a has minimal e-support from ext iff ext e-supports a and
     * no true subset of ext e-supports a
     * @param argument an argument
     * @param ext an extension, ie. a set of arguments
     * @return true if argument has e-support from <code>ext</code>.
     */
    public boolean hasMinimalEvidentialSupport(Argument argument, Collection<Argument> ext){
        //TODO implement minimal e-support
        return false;
    }

    /**
     * returns true if <code>ext</code> carries out an evidence supported attack on argument
     * ext e-support-attacks an argument a iff an element x of ext attacks a and x has e-support from ext.
     * @param argument an argument
     * @param ext an extension, ie. a set of arguments
     * @return true if <code>ext</code> e-support-attacks argument
     */
    public boolean isEvidenceSupportedAttack(Collection<Argument> ext, Argument argument){
        for(Argument attacker: ext) {
            if (this.isAttackedBy(argument, attacker) && this.hasEvidentialSupport(attacker, ext))
               return true;
        }
        return false;
    }

    /**
     * returns true if <code>ext</code> carries out a minimal evidence supported attack on argument
     * i.e. there is no true subset of <code>ext</code> which e-support-attacks argument
     * @param argument an argument
     * @param ext an extension, ie. a set of arguments
     * @return true if <code>ext</code> e-support-attacks argument
     */
    public boolean isMinimalEvidenceSupportedAttack(Collection<Argument> ext, Argument argument){
        if (!this.isEvidenceSupportedAttack(ext, argument))
            return false;
        Set<Set<Argument>> subsets = new SetTools<Argument>().subsets(ext);
        subsets.remove(ext);
        for (Collection<Argument> subExt: subsets) {
            if (this.isEvidenceSupportedAttack(subExt, argument))
                return false;
        }
        return true;
    }

    /**
     * return true if argument is acceptable with respect to <code>ext</code>
     * argument is acceptable wrt. S iff argument is e-supported by S and if
     * a set T minimal e-support-attacks argument, then S carries out an
     * e-supported attack against a member of T.
     * @param argument an argument
     * @param ext an extension i.e. a set of arguments
     * @return true if argument is acceptable wrt. <code>ext</code>
     */
    public boolean isAcceptable(Argument argument, Collection<Argument> ext){
        //TODO fix supported attacks not being accounted for
        boolean result = true;
        if (!this.hasEvidentialSupport(argument, ext)) {
            return false;
        }
        for (Argument attacker: this.getAttackers(argument)) {
            Set<Argument> attackingSet = new HashSet<Argument>();
            attackingSet.add(attacker);
            attackingSet.add(this.getEpsilon());
            result &= !this.isMinimalEvidenceSupportedAttack(attackingSet, argument) || this.isEvidenceSupportedAttack(ext, attacker);

        }
        return result;
    }

    /**
     * Adds a argument with reasoner.evidential support to this reasoner.evidential argumentation system
     * @param argument some argument
     * @return "true" if the argument has been modified.
     */
    public boolean addPrimaFacie(Argument argument){
        boolean result = false;
        if (!this.contains(argument)) {
            result |= this.add(argument);
        }
        result |= this.addSupport(this.getEpsilon(), argument);

        return result;
    }

    /**
     * returns the special argument epsilon
     * @return epsilon
     */
    public Argument getEpsilon() {
        return epsilon;
    }


    public Set<Argument> getEvidenceSupportedArguments() {
        return this.getSupported(this.getEpsilon());
    }

    /** Pretty print of the framework.
     * @return the pretty print of the framework.
     */
    public String prettyPrint(){
        String output = new String();
        Iterator<Argument> it = this.iterator();
        while(it.hasNext())
            output += "argument("+it.next().toString()+").\n";
        output += "\n";
        Iterator<Attack> it2 = this.getAttacks().iterator();
        while(it2.hasNext())
            output += "attack"+it2.next().toString()+".\n";
        output += "\n";
        Iterator<Support> it3 = this.getSupports().iterator();
        while(it3.hasNext())
            output += "support"+it3.next().toString()+".\n";
        return output;
    }
}
