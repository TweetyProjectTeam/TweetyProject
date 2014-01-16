package net.sf.tweety.agents.gridworldsim.server;

/**
 * Objects made from this class represent observer client login data.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class ObserverLogin {

    private final String name;
    private final String password;

    /**
     * Constructs a new {@code ObserverLogin}.
     * @param name the login name
     * @param password the login password
     */
    public ObserverLogin(String name, String password) {
        this.name = name;
        this.password = password;
    }

    /**
     * Constructs the new {@code ObserverLogin}.
     * @param name the login name
     */
    public ObserverLogin(String name) {
        this.name = name;
        this.password = null;
    }

    /**
     * Tells if this {@code ObserverLogin} has a password.
     * @return true if it has a password, false otherwise
     */
    public boolean hasPassword() {
        return !(password == null);
    }

    /**
     * Get the password of this {@code ObserverLogin}.
     * @return the password of this {@code ObserverLogin}
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get the login name of this {@code ObserverLogin}.
     * @return the login name of this {@code ObserverLogin}
     */
    public String getName() {
        return name;
    }

    /**
     * Generate a hash code for this {@code ObserverLogin}.
     * @return a hash code for this {@code ObserverLogin}
     */
    @Override
    public int hashCode() {
        int hash = 7;
        if (hasPassword()) {
            hash = 67 * name.hashCode() * password.hashCode();
        } else {
            hash = 67 * name.hashCode();
        }
        return hash;
    }

    /**
     * Checks if this {@code ObserverLogin} is equal to another {@code Object}
     * @param obj the {@code Object} to check equality for
     * @return true if this {@code ObserverLogin} is equal to the other {@code Object}, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || this.getClass() != obj.getClass()) {
            return false;
        }

        ObserverLogin checkMe = (ObserverLogin) (obj);

        if (hasPassword() && checkMe.hasPassword() && name.equals(checkMe.getName()) && password.equals(checkMe.getPassword())) {
            return true;
        } else if (!hasPassword() && !checkMe.hasPassword() && name.equals(checkMe.getName())) {
            return true;
        } else {
            return false;
        }
    }
}
