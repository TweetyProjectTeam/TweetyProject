package org.tweetyproject.arg.bipolar.syntax;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class implements an abstract argumentation theory
 * in the sense of Probabilistic Extended Evidential Argumentation Frameworks (PrEEAF).
 * </br>
 * </br>See
 * </br>
 * </br> Li, Hengfei. Probabilistic argumentation. 2015. PhD Thesis. Aberdeen University.
 *
 * @author Taha Dogan Gunes
 */
public class PEEAFTheory {

    /**
     * Creates an argument with identifier and textContent and adds the argument
     *
     * @param identifier  the identifier of the argument
     * @param textContent explanation of the argument
     */
    public void addArgument(String identifier, String textContent) {
        Argument arg = new Argument(identifier, textContent);
        this.arguments.add(arg);
        identifierElementMap.put(identifier, arg);
    }

    /**
     * Argument is a class, that is used for PEEAFTheory
     */
    public class Argument extends Element {
        /**
         * Used for specifying the name of the argument
         */
        private final String name;

        /**
         * The default constructor of the PEEAF.Argument
         *
         * @param identifier the identifier of the argument as string
         * @param name       the name of the argument as string
         */
        Argument(String identifier, String name) {
            super(identifier);
            this.name = name;
        }

        /**
         * Returns Argument as a string (good for debugging)
         *
         * @return a verbose string output of stored variables
         */
        @Override
        public String toString() {
            return "Arg{" +
                    "id=`" + identifier + "` - " +
                    name +
                    '}';
        }

        /**
         * @return the name of the argument
         */
        public String getName() {
            return name;
        }
    }

    /**
     * Adds attack from the identifier of an argument to the identifier of the argument, support or attack.
     *
     * @param identifier     the name of the attack
     * @param fromIdentifier the identifier of argument that originate the attack
     * @param toIdentifier   the identifier of the targeted argument
     * @param probability    the uncertainty regarding the attack link (must be in range [0.0, 1.0])
     */
    public void addAttack(String identifier, String fromIdentifier, String toIdentifier, double probability) {
        Attack attack = new Attack(identifier, probability);

        Argument from = checkAndGetArgument(fromIdentifier);
        Element to = checkAndGetElement(toIdentifier);

        attack.setFrom(from);
        attack.setTo(to);

        attacks.add(attack);
        identifierElementMap.put(identifier, attack);
    }

    /**
     * Attack is a class, that is used for PEEAFTheory
     * <p>
     * Each link has one-to-one relationship with some result in PEEAF.
     */
    public class Attack extends Element {

        /**
         * The argument that the attack originates from
         */
        private Argument from;

        /**
         * The element (attack, support or argument) that originates the attack
         */
        private Element to;

        /**
         * The result assigned to the attack link
         */
        private final double probability;


        /**
         * The default constructor of Attack
         *
         * @param identifier  identifier as string
         * @param probability the result assigned to the attack link
         */
        Attack(String identifier, double probability) {
            super(identifier);
            this.probability = probability;
        }

        /**
         * Sets the argument that the attack originates from
         *
         * @param from The argument that the attack originates from
         */
        public void setFrom(Argument from) {
            this.from = from;
        }

        /**
         * The element (attack, support or argument) that originates the attack
         *
         * @param to element
         */
        public void setTo(Element to) {
            this.to = to;
        }

        /**
         * Return the argument that originates the attack
         *
         * @return the argument
         */
        public Argument getFrom() {
            return from;
        }

        /**
         * Get the element (attack, support or argument) that originates the attack
         *
         * @return the element
         */
        public Element getTo() {
            return to;
        }

        /**
         * Returns Attack as a string (good for debugging)
         *
         * @return a verbose string output of stored variables
         */
        @Override
        public String toString() {
            return "Att{" + identifier + ", " +
                    "from=" + from +
                    ", to=" + to +
                    ", result=" + probability +
                    '}';
        }

        /**
         * @return the result assigned to the attack link
         */
        public double getProbability() {
            return probability;
        }
    }

    /**
     * The argument that is the root argument (that argument that is the initial point)
     */
    protected Argument eta;

    /**
     * This is for quickly checking or retrieving existing elements
     */
    protected Map<String, Element> identifierElementMap = new HashMap<String, Element>();

    /**
     * Ordered list of arguments
     */
    protected ArrayList<Argument> arguments = new ArrayList<>();

    /**
     * Ordered list of supports
     */
    protected ArrayList<Support> supports = new ArrayList<>();

    /**
     * Ordered list of attacks
     */
    protected ArrayList<Attack> attacks = new ArrayList<>();

    /**
     * Default constructor that initializes an empty PEEAFTheory
     */
    public PEEAFTheory() {
    }

    /**
     * Element is the parent of PEEAFTheory.Argument, PEEAFTheory.Support and PEEAFTheory.Attack
     */
    @SuppressWarnings("InnerClassMayBeStatic")
    public abstract class Element {
        /**
         * Identifier is used internally during conversion to PEEAF
         */
        protected final String identifier;

        /**
         * Default constructor of Element
         *
         * @param identifier identifier denotes the Element
         */
        Element(String identifier) {
            this.identifier = identifier;
        }

        /**
         * @return identifier as string
         */
        public String getIdentifier() {
            return identifier;
        }
    }

    /**
     * Adds support to PEEAF with from identifiers of arguments and to the identifier of the targeted argument
     *
     * @param identifier      the identifier for the support
     * @param fromIdentifiers the identifiers of arguments that originate the support
     * @param toIdentifier    the identifier of the targeted argument
     * @param probability     the uncertainty regarding the support link (must be in range [0.0, 1.0])
     */
    public void addSupport(String identifier, String[] fromIdentifiers, String toIdentifier, double probability) {
        Support support = new Support(identifier, probability);

        for (String fromIdentifier : fromIdentifiers) {
            Argument from = checkAndGetArgument(fromIdentifier);
            support.addFrom(from);
        }

        Argument to = checkAndGetArgument(toIdentifier);
        support.setTo(to);

        supports.add(support);
        identifierElementMap.put(identifier, support);
    }

    /**
     * Support is a class, that is used for PEEAFTheory
     * <p>
     * The difference between support links of PEAFTheory and EAFTheory
     * is that, each link has many-to-one relationship with some result.
     */
    public class Support extends Element {

        /**
         * A set of arguments that the support originates from
         */
        private final Set<Argument> froms = new HashSet<Argument>();
        /**
         * The result assigned to the support link
         */
        private final double probability;
        /**
         * The argument that originates the support
         */
        private Argument to;

        /**
         * The default constructor of Support
         *
         * @param identifier  identifier as string
         * @param probability the result assigned to the support link
         */
        Support(String identifier, double probability) {
            super(identifier);
            this.probability = probability;
        }

        /**
         * Adds the argument into the set of arguments that originates the support
         *
         * @param from the argument that originates the support
         */
        public void addFrom(Argument from) {
            this.froms.add(from);
        }

        /**
         * Return the set of arguments that originates the support
         *
         * @return the set of the arguments
         */
        public Set<Argument> getFroms() {
            return froms;
        }

        /**
         * Return the argument that is targeted
         *
         * @return the argument that is targeted
         */
        public Argument getTo() {
            return to;
        }

        /**
         * Set the argument that the support link targets
         *
         * @param to the argument that is targeted
         */
        public void setTo(Argument to) {
            this.to = to;
        }

        /**
         * Returns Argument as a string (good for debugging)
         *
         * @return a verbose string output of stored variables
         */
        @Override
        public String toString() {
            return "Supp{" + identifier + ", " +
                    "froms=" + froms +
                    ", to=" + to +
                    ", result=" + probability +
                    '}';
        }

        /**
         * @return the result assigned to the support link
         */
        public double getProbability() {
            return probability;
        }
    }

    /**
     * Get the argument with the identifier
     *
     * @param identifier an argument's identifier
     * @return the argument
     */
    public Argument checkAndGetArgument(String identifier) {
        Object obj = this.identifierElementMap.get(identifier);
        if (!(obj instanceof Argument)) {
            throw new NotAnArgumentException("The given argument `" + obj + "` is not instance of Argument.");
        }
        Argument to = (Argument) this.identifierElementMap.get(identifier);
        if (to == null) {
            throw new NotAnArgumentException("The argument with id=`" + identifier + "` was not found.");
        }
        return to;
    }

    /**
     * Get the element with the identifier
     *
     * @param identifier an element's argument
     * @return the element
     */
    private Element checkAndGetElement(String identifier) {
        Element to = this.identifierElementMap.get(identifier);
        if (to == null) {
            throw new ElementNotFoundException("The element with id=`" + identifier + "` was not found.");
        }
        return to;
    }

    /**
     * Get the insert ordered list of arguments.
     *
     * @return array list of the arguments
     */
    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    /**
     * Get the insert ordered list of supports.
     *
     * @return array list of the supports
     */
    public ArrayList<Support> getSupports() {
        return supports;
    }

    /**
     * Get the insert ordered list of attacks.
     *
     * @return array list of the attacks
     */
    public ArrayList<Attack> getAttacks() {
        return attacks;
    }

    /**
     * Print the PEEAF for debugging purposes.
     */
    public void prettyPrint() {
        System.out.println("\nPEEAF:");
        System.out.println("-- Arguments --");
        int i = 0;
        for (Argument argument : this.getArguments()) {
            System.out.println(i + ". " + argument.toString());
            i++;
        }

        System.out.println();
        System.out.println("-- Supports --");
        i = 0;
        for (Support support : this.getSupports()) {
            System.out.println(i + ". " + support.toString());
            i++;
        }

        System.out.println();
        System.out.println("-- Attacks --");
        i = 0;
        for (Attack attack : this.getAttacks()) {
            System.out.println(i + ". " + attack.toString());
            i++;
        }

        System.out.println("\n");
    }

    /**
     * The exception occurs when the argument requested is not found.
     * The exception has an internal atomic variable for keeping track of the number of times this occurs.
     */
    public static class NotAnArgumentException extends RuntimeException {

        /**
         * The atomic variable to store the occurrence of this exception.
         */
        private static final AtomicLong atomicLong = new AtomicLong(0);

        /**
         * Default constructor
         *
         * @param message the reason for the exception
         */
        public NotAnArgumentException(String message) {
            super(message);
            atomicLong.getAndIncrement();
        }

        /**
         * Get the occurrence count of exception thrown.
         *
         * @return the count
         */
        public static long getOccurrenceCount() {
            return atomicLong.get();
        }
    }

    /**
     * The exception occurs when the element requested is not found.
     * The exception has an internal atomic variable for keeping track of the number of times this occurs.
     */
    public static class ElementNotFoundException extends RuntimeException {

        /**
         * The atomic variable to store the occurrence of this exception.
         */
        private static final AtomicLong atomicLong = new AtomicLong(0);

        /**
         * The atomic variable to store the occurrence of this exception.
         */
        public ElementNotFoundException(String message) {
            super(message);
            atomicLong.getAndIncrement();
        }

        /**
         * Get the occurrence count of exception thrown.
         *
         * @return the count
         */
        public static long getOccurrenceCount() {
            return atomicLong.get();
        }
    }

    /**
     * Helper class for displaying associated exceptions' occurrence
     */
    public static class Exceptions {

        /**
         * Outputs the occurrence of PEEAF exceptions.
         *
         * @return the count of the total occurrence of exceptions
         */
        public static long describe() {
            long count = 0;
            System.out.println("PEEAF.NotAnArgumentException count: " + NotAnArgumentException.getOccurrenceCount());
            count += NotAnArgumentException.getOccurrenceCount();
            System.out.println("PEEAF.ElementNotFoundException count: " + ElementNotFoundException.getOccurrenceCount());
            count += ElementNotFoundException.getOccurrenceCount();
            return count;
        }

    }
}
