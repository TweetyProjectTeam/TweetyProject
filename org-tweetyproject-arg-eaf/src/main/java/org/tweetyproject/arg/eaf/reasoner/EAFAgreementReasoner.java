package org.tweetyproject.arg.eaf.reasoner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tweetyproject.arg.dung.semantics.Labeling;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.ml.parser.MlParser;

/**
 * This reasoner for epistemic Dung theories performs inference on the agreement states of arguments for multiple EAFs.
 *
 * @author Sandra Hoffmann
 */
public class EAFAgreementReasoner {
    private DungTheory af;
    private Collection<EpistemicArgumentationFramework> eafs = new ArrayList<>();
    private FolSignature sig = new FolSignature();
    private MlParser parser = new MlParser();

    /**
     * Constructor with one initial EAF
     * @param eaf the initial epistemic argumentation framework
     */
    public EAFAgreementReasoner(EpistemicArgumentationFramework eaf) {
        this.af = new DungTheory(eaf);
        this.eafs.add(eaf);
        // Get all arguments from graph to set signature for parser
        Collection<Argument> arguments = af.getNodes();
        Set<String> validArgumentNames = new HashSet<>();

        for (Argument arg : arguments) {
            String argName = arg.getName();
            validArgumentNames.add(argName);
            sig.add(new Predicate(argName, 0));
        }
        parser.setSignature(sig);
    }

    /**
     * Add another EAF with the same underlying AF
     * @param eaf the EAF to add
     * @return true if successfully added
     * @throws Exception if the underlying AF doesn't match
     */
    public boolean addEAF(EpistemicArgumentationFramework eaf) throws Exception {
        // Check whether underlying AFs are the same
        DungTheory underlyingAF = new DungTheory(eaf);
        if (!this.af.equals(underlyingAF))
            throw new Exception("underlying AF does not match AF for this Agreement Reasoner");
        this.eafs.add(eaf);
        return true;
    }

    /**
     * Add another EAF with the same underlying AF but different constraint
     * @param constraint the constraint for the new EAF
     * @return true if successfully added
     */
    public boolean addEAF(String constraint) {
           return this.eafs.add(new EpistemicArgumentationFramework(this.af, constraint));
    }

    /**
     * Remove a specific EAF from the reasoner
     * @param eaf the EAF to remove
     * @return true if successfully removed, false otherwise
     */
    public boolean removeEAF(EpistemicArgumentationFramework eaf) {
        return this.eafs.remove(eaf);
    }

    /**
     * Remove an EAF with a specific constraint
     * @param constraint the constraint of the EAF to remove
     * @return true if an EAF with the given constraint was found and removed, false otherwise
     */
    public boolean removeEAF(String constraint) {
        // Find and remove EAFs with the matching constraint
        return this.eafs.removeIf(eaf -> eaf.getConstraint().equals(constraint));
    }

    /**
     * Replace an EAF with a new one. This is akin to a believe change. If there are multiple EAFs with the same
     * constraint as oldEAF, the first EAF that is found with this constraint is replaced by newEAF.
     * @param oldEAF the existing EAF to be replaced
     * @param newEAF the new EAF to replace the old one
     * @return true if replacement was successful, false otherwise
     * @throws Exception if the underlying AF doesn't match
     */
    public boolean changeEAF(EpistemicArgumentationFramework oldEAF, EpistemicArgumentationFramework newEAF) throws Exception {
    	this.removeEAF(oldEAF);
    	return this.addEAF(newEAF);
    }

    /**
     * Replace an EAF with a given constraint. This is akin to a believe change. If there are multiple EAFs with the same
     * constraint as oldConstraint, the first EAF that is found with this constraint is replaced by a new EAF with newConstraint.
     * @param oldConstraint the constraint of the existing EAF to be replaced
     *@param newConstraint the constraint of the new EAF to replace the old one
     * @return true if replacement was successful, false otherwise
     * @throws Exception if the underlying AF doesn't match
     */
    public boolean changeEAF(String oldConstraint, String newConstraint) throws Exception {
    	EpistemicArgumentationFramework oldEAF = new EpistemicArgumentationFramework(this.af, oldConstraint);
    	EpistemicArgumentationFramework newEAF = new EpistemicArgumentationFramework(this.af, newConstraint);
        return changeEAF(oldEAF, newEAF) ;
    }

    /**
     * Remove an EAF by its index
     * @param index the index of the EAF to remove
     * @return the removed EAF, or null if index is out of bounds
     */
    public EpistemicArgumentationFramework removeEAF(int index) {
        List<EpistemicArgumentationFramework> eafList = new ArrayList<>(this.eafs);

        // Check if index is valid
        if (index >= 0 && index < eafList.size()) {
            // Remove and return the EAF at the specified index
            EpistemicArgumentationFramework removedEAF = eafList.remove(index);

            // Update the original collection
            this.eafs = eafList;

            return removedEAF;
        }
        return null;
    }

    /**
     * Replace an EAF at a specific index with a new EAF
     * @param index the index of the EAF to replace
     * @param newEAF the new EAF to replace the old one
     * @return the old EAF that was replaced, or null if index is out of bounds
     * @throws Exception if the underlying AF doesn't match
     */
    public EpistemicArgumentationFramework changeEAF(int index, EpistemicArgumentationFramework newEAF) throws Exception {
    	EpistemicArgumentationFramework removedEAF = this.removeEAF(index);
    	this.addEAF(newEAF);
    	return removedEAF;
    }

    /**
     * Print out all EAFs in the reasoner with their indices
     * Prints to standard output
     */
    public void listEAFs() {
        System.out.println("EAFs in the Reasoner:");
        List<EpistemicArgumentationFramework> eafList = new ArrayList<>(this.eafs);
        for (int i = 0; i < eafList.size(); i++) {
            System.out.println("Index " + i + ": " + eafList.get(i));
        }
    }

    /**
     * Get a list of all EAFs in the reasoner
     * @return a list of EAFs
     */
    public List<EpistemicArgumentationFramework> getEAFs() {
        return new ArrayList<>(this.eafs);
    }


    /**
     * Check if all EAFs agree on a formula under the given semantics and inference mode
     * @param queryNode string representing the query (in(a), out(a), or und(a))
     * @param mode credulous or skeptical mode
     * @param sem the semantics to use
     * @throws IllegalArgumentException if the query format is invalid or if an argument in the query is not found in the labeling
     * @return  boolean indicating whether all EAFs agree on the query
     */
    public Boolean query(String queryNode, InferenceMode mode, Semantics sem){
        if (mode == InferenceMode.CREDULOUS) {
            // Credulous agreement: each EAF has at least one labelling set contains queryNode
            for (EpistemicArgumentationFramework eaf : eafs) {
                Set<Set<Labeling>> eafLabellingSets = eaf.getWEpistemicLabellingSets(sem);
                boolean eafSatisfies = false;

                // Check if any labelling set satisfies the formula (M formula)
                for (Collection<Labeling> labellingSet : eafLabellingSets) {
                    if (satisfiesQueryInAnyLabelling(labellingSet, queryNode)) {
                        eafSatisfies = true;
                        break;
                    }
                }

                if (!eafSatisfies) {
                    return false; // This EAF doesn't credulous agree
                }
            }
            return true; // All EAFs credulous agree

        } else {
            // Skeptical agreement: all labelling sets of all EAFs contain queryNode
            for (EpistemicArgumentationFramework eaf : eafs) {
                Set<Set<Labeling>> eafLabellingSets = eaf.getWEpistemicLabellingSets(sem);

                // Check if all labelling sets satisfy the formula in all labellings (K formula)
                for (Collection<Labeling> labellingSet : eafLabellingSets) {
                    if (!satisfiesQueryInAllLabellings(labellingSet, queryNode)) {
                        return false; // This EAF doesn't skeptically agree
                    }
                }
            }
            return true; // All EAFs skeptically agree
        }
    }


   //Check if a formula is satisfied in any labeling of the labeling set (M formula)
    private boolean satisfiesQueryInAnyLabelling(Collection<Labeling> labelingSet, String query) {
        for (Labeling labeling : labelingSet) {
            if (satisfiesQuery(labeling, query)) {
                return true;
            }
        }
        return false;
    }


    //Check if a formula is satisfied in all labelings of the labeling set (K formula)
    private boolean satisfiesQueryInAllLabellings(Collection<Labeling> labelingSet, String query) {
        for (Labeling labeling : labelingSet) {
            if (!satisfiesQuery(labeling, query)) {
                return false;
            }
        }
        return true;
    }


    //Check if a labeling satisfies a specific query
    private boolean satisfiesQuery(Labeling labeling, String query) {
        // Parse the query to extract argument name and label
        Pattern pattern = Pattern.compile("(in|out|und)\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(query);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid query format. Expected in(a), out(a), or und(a)");
        }

        String label = matcher.group(1);
        String argName = matcher.group(2);

        // Find the argument in the labeling
        Argument arg = null;
        for (Argument a : labeling.keySet()) {
            if (a.getName().equals(argName)) {
                arg = a;
                break;
            }
        }

        if (arg == null) {
            throw new IllegalArgumentException("Argument " + argName + " not found in the labeling");
        }

        // Check the labeling based on the query label using the ArgumentStatus
        switch (label) {
            case "in":
                return labeling.get(arg) == ArgumentStatus.IN;
            case "out":
                return labeling.get(arg) == ArgumentStatus.OUT;
            case "und":
                return labeling.get(arg) == ArgumentStatus.UNDECIDED;
            default:
                return false;
        }
    }

    /**
     * Performs majority voting over multiple EAFs to decide whether the given argument label
     * (e.g., in(a), out(a), or und(a)) is accepted by the majority under the specified inference
     * mode and semantics.
     *
     * In credulous mode, the label must be satisfied in at least one labelling set (M φ).
     * In skeptical mode, the label must be satisfied in all labelling sets (K φ).
     * The query label wins if it gets strictly more votes than any other label.
     *
     * @param query the argument label to evaluate, in the form in(arg), out(arg), or und(arg)
     * @param mode the inference mode to use (CREDULOUS or SKEPTICAL)
     * @param sem the argumentation semantics to apply
     * @return true if the query label wins the majority vote, false otherwise
     * @throws IllegalArgumentException if the query format is invalid
     */
    public boolean majorityVote(String query, InferenceMode mode, Semantics sem) {
        String[] labels = {"in", "out", "und"};
        Map<String, Integer> voteCounts = new HashMap<>();
        for (String label : labels) {
            voteCounts.put(label, 0);
        }

        // Parse the input query: e.g., in(a)
        Pattern pattern = Pattern.compile("(in|out|und)\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(query);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid query format. Expected in(a), out(a), or und(a)");
        }

        String targetLabel = matcher.group(1);
        String argName = matcher.group(2);

        for (EpistemicArgumentationFramework eaf : eafs) {
            Set<Set<Labeling>> labellingSets = eaf.getWEpistemicLabellingSets(sem);
            for (String label : labels) {
                String candidateQuery = label + "(" + argName + ")";
                boolean satisfied = false;

                for (Collection<Labeling> labellingSet : labellingSets) {
                    if (mode == InferenceMode.CREDULOUS) {
                        if (satisfiesQueryInAnyLabelling(labellingSet, candidateQuery)) {
                            satisfied = true;
                            break;
                        }
                    } else {
                        if (satisfiesQueryInAllLabellings(labellingSet, candidateQuery)) {
                            satisfied = true;
                            break;
                        }
                    }
                }

                if (satisfied) {
                    voteCounts.put(label, voteCounts.get(label) + 1);
                    //break; // Only one label per agent counts
                }
            }
        }

        // Check if the target label wins the majority
        int targetVotes = voteCounts.get(targetLabel);
        for (String label : labels) {
            if (!label.equals(targetLabel) && voteCounts.get(label) >= targetVotes) {
                return false;
            }
        }

        return true;
    }

}