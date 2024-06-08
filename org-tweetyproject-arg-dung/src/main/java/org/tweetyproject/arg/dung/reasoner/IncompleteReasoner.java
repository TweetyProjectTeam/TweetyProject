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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.syntax.IncompleteTheory;
import org.tweetyproject.commons.InferenceMode;

/**
 * Reasoner for incomplete argumentation frameworks
 * @author Sebastian Franke
 * @author Lars Bengel
 */
public class IncompleteReasoner{
    /**
     * the underlying Dung reasoner
     */
    private AbstractExtensionReasoner reasoner;

    /**
     * constructor for direct initialization of reasoner
     * @param reasoner some extension reasoner
     */
    public IncompleteReasoner(AbstractExtensionReasoner reasoner) {
        this.reasoner = reasoner;
    }

    /**
     * constructor for direct initialization of semantics
     * @param semantics the Dung semantics
     */
    public IncompleteReasoner(Semantics semantics) {
        reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
    }

    /**
     *
     * @param theory incomplete theory
     * @return all possible models
     */
    public Collection<Collection<Extension<DungTheory>>> getAllModels(IncompleteTheory theory){
        Collection<Collection<Extension<DungTheory>>> models = new HashSet<Collection<Extension<DungTheory>>>();
        Collection<Collection<Argument>> powerSet = theory.powerSet(theory.uncertainArgument);
        for(Collection<Argument> instance : powerSet) {
            Collection<Extension<DungTheory>> instanceModels = new HashSet<Extension<DungTheory>>();
            //uncertain attacks that can occur in this instance
            HashSet<Attack> uncertainAttacksInInstance = new HashSet<Attack>();
            for(Attack att : theory.uncertainAttacks) {
                //only add attack to possible attacks if both parties appear in instance
                if((theory.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
                        (theory.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
                    uncertainAttacksInInstance.add(att);
                }
            }
            Collection<Collection<Attack>> powerSetAttacks = theory.powerSet(uncertainAttacksInInstance);
            //create new instance for each member of the power set and evaluate it
            for(Collection<Attack> j : powerSetAttacks) {
                theory.instantiate(instance, j);
                instanceModels.addAll(this.reasoner.getModels(theory));
            }

            models.add(instanceModels);
        }
        return models;
    }

    /**
     * Computes the set of possible extensions
     * @param theory incomplete theory
     * @return all possible models
     */
    public Collection<Extension<DungTheory>> getPossibleModels(IncompleteTheory theory){
        Collection<Extension<DungTheory>> models = new HashSet<>();
        Collection<Collection<Argument>> powerSet = theory.powerSet(theory.uncertainArgument);
        for(Collection<Argument> instance : powerSet) {
            Collection<Extension<DungTheory>> instanceModels = new HashSet<>();
            //uncertain attacks that can occur in this instance
            HashSet<Attack> uncertainAttacksInInstance = new HashSet<>();
            for(Attack att : theory.uncertainAttacks) {
                //only add attack to possible attacks if both parties appear in instance
                if((theory.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
                        (theory.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
                    uncertainAttacksInInstance.add(att);
                }
            }
            Collection<Collection<Attack>> powerSetAttacks = theory.powerSet(uncertainAttacksInInstance);
            //create new instance for each member of the power set and evaluate it
            for(Collection<Attack> j : powerSetAttacks) {
                theory.instantiate(instance, j);
                models.addAll(this.reasoner.getModels(theory));
            }
        }
        return models;
    }

    /**
     * Computes the set of necessary extensions
     * @param theory incomplete theory
     * @return all necessary models
     */
    public Collection<Extension<DungTheory>> getNecessaryModels(IncompleteTheory theory){
        boolean first = true;
        Collection<Extension<DungTheory>> models = new HashSet<>();
        Collection<Collection<Argument>> powerSet = theory.powerSet(theory.uncertainArgument);
        for(Collection<Argument> instance : powerSet) {
            Collection<Extension<DungTheory>> instanceModels = new HashSet<>();
            //uncertain attacks that can occur in this instance
            HashSet<Attack> uncertainAttacksInInstance = new HashSet<>();
            for(Attack att : theory.uncertainAttacks) {
                //only add attack to possible attacks if both parties appear in instance
                if((theory.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
                        (theory.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
                    uncertainAttacksInInstance.add(att);
                }
            }
            Collection<Collection<Attack>> powerSetAttacks = theory.powerSet(uncertainAttacksInInstance);
            //create new instance for each member of the power set and evaluate it
            for(Collection<Attack> j : powerSetAttacks) {
                theory.instantiate(instance, j);
                if (first) {
                    first = false;
                    models.addAll(this.reasoner.getModels(theory));
                }
                models.retainAll(this.reasoner.getModels(theory));
            }
        }
        return models;
    }

    /**
     * Decides whether the given argument is a possible credulous conclusion of the incomplete theory
     * @param theory some incomplete theory
     * @param arg some argument
     * @return "true" iff the argument is a possible credulous conclusion
     */
    public boolean isPossibleCredulous(IncompleteTheory theory, Argument arg) {
        Collection<Collection<Argument>> powerSet = theory.powerSet(theory.uncertainArgument);
        for(Collection<Argument> instance : powerSet) {
            Collection<Extension<DungTheory>> instanceModels = new HashSet<>();
            //uncertain attacks that can occur in this instance
            Collection<Attack> uncertainAttacksInInstance = new HashSet<>();
            for(Attack att : theory.uncertainAttacks) {
                //only add attack to possible attacks if both parties appear in instance
                if((theory.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
                        (theory.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
                    uncertainAttacksInInstance.add(att);
                }
            }
            Collection<Collection<Attack>> powerSetAttacks = theory.powerSet(uncertainAttacksInInstance);
            //create new instance for each member of the power set and evaluate it
            for(Collection<Attack> j : powerSetAttacks) {
                theory.instantiate(instance, j);
                if (this.reasoner.query(theory, arg, InferenceMode.CREDULOUS)) return true;
            }
        }
        return false;
    }

    /**
     * Decides whether the given argument is a necessary credulous conclusion of the incomplete theory
     * @param theory some incomplete theory
     * @param arg some argument
     * @return "true" iff the argument is a necessary credulous conclusion
     */
    public boolean isNecessaryCredulous(IncompleteTheory theory, Argument arg) {
        Collection<Collection<Argument>> powerSet = theory.powerSet(theory.uncertainArgument);
        for(Collection<Argument> instance : powerSet) {
            Collection<Extension<DungTheory>> instanceModels = new HashSet<>();
            //uncertain attacks that can occur in this instance
            HashSet<Attack> uncertainAttacksInInstance = new HashSet<>();
            for(Attack att : theory.uncertainAttacks) {
                //only add attack to possible attacks if both parties appear in instance
                if((theory.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
                        (theory.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
                    uncertainAttacksInInstance.add(att);
                }
            }
            Collection<Collection<Attack>> powerSetAttacks = theory.powerSet(uncertainAttacksInInstance);
            //create new instance for each member of the power set and evaluate it
            for(Collection<Attack> j : powerSetAttacks) {
                theory.instantiate(instance, j);
                if (!this.reasoner.query(theory, arg, InferenceMode.CREDULOUS)) return false;
            }
        }
        return true;
    }

    /**
     * Decides whether the given argument is a possible skeptical conclusion of the incomplete theory
     * @param theory some incomplete theory
     * @param arg some argument
     * @return "true" iff the argument is a skeptical credulous conclusion
     */
    public boolean isPossibleSkeptical(IncompleteTheory theory, Argument arg) {
        Collection<Collection<Argument>> powerSet = theory.powerSet(theory.uncertainArgument);
        for(Collection<Argument> instance : powerSet) {
            Collection<Extension<DungTheory>> instanceModels = new HashSet<>();
            //uncertain attacks that can occur in this instance
            HashSet<Attack> uncertainAttacksInInstance = new HashSet<>();
            for(Attack att : theory.uncertainAttacks) {
                //only add attack to possible attacks if both parties appear in instance
                if((theory.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
                        (theory.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
                    uncertainAttacksInInstance.add(att);
                }
            }
            Collection<Collection<Attack>> powerSetAttacks = theory.powerSet(uncertainAttacksInInstance);
            //create new instance for each member of the power set and evaluate it
            for(Collection<Attack> j : powerSetAttacks) {
                theory.instantiate(instance, j);
                if (this.reasoner.query(theory, arg, InferenceMode.SKEPTICAL)) return true;
            }
        }
        return false;
    }

    /**
     * Decides whether the given argument is a necessary skeptical conclusion of the incomplete theory
     * @param theory some incomplete theory
     * @param arg some argument
     * @return "true" iff the argument is a necessary skeptical conclusion
     */
    public boolean isNecessarySkeptical(IncompleteTheory theory, Argument arg) {
        Collection<Collection<Argument>> powerSet = theory.powerSet(theory.uncertainArgument);
        for(Collection<Argument> instance : powerSet) {
            Collection<Extension<DungTheory>> instanceModels = new HashSet<>();
            //uncertain attacks that can occur in this instance
            Collection<Attack> uncertainAttacksInInstance = new HashSet<>();
            for(Attack att : theory.uncertainAttacks) {
                //only add attack to possible attacks if both parties appear in instance
                if((theory.definiteArguments.contains(att.getAttacker()) || instance.contains(att.getAttacker())) &&
                        (theory.definiteArguments.contains(att.getAttacked()) || instance.contains(att.getAttacked()))) {
                    uncertainAttacksInInstance.add(att);
                }
            }
            Collection<Collection<Attack>> powerSetAttacks = theory.powerSet(uncertainAttacksInInstance);
            //create new instance for each member of the power set and evaluate it
            for(Collection<Attack> j : powerSetAttacks) {
                theory.instantiate(instance, j);
                if (!this.reasoner.query(theory, arg, InferenceMode.SKEPTICAL)) return false;
            }
        }
        return true;
    }

    /**
     *
     * @return whether the solver is installed
     */
    public boolean isInstalled() {
        return true;
    }

}
