package net.sf.tweety.agents.gridworldsim.server;

import java.util.Collection;
import java.util.HashSet;

/**
 * Objects made from this class represent a locker, i.e. a {@code GridObject} whose inventory can be locked/unlocked with password.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class LockerObject extends GridObject {

    private String password;
    private boolean locked;

    /**
     * Constructs a new {@code LockerObject}.
     */
    public LockerObject() {
        super();
        password = null;
        locked = false;
        addParameter(new GridObjectParameter("property", "Locker"));
    }

    /**
     * Locks the {@code LockerObject} with a password.
     * @param password the password to use for locking
     * @return true if locking was successful, false otherwise
     */
    public boolean lock(String password) {
        if (!locked) {
            this.password = password;
            locked = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Unlock the {@code LockerObject} using a password.
     * @param password the password to use for unlocking
     * @return true if unlocking was succesfull, false otherwise
     */
    public boolean unlock(String password) {
        if (locked && password.equals(this.password)) {
            this.password = null;
            locked = false;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tells if the inventory of this {@code LockerObject} is accessible.
     * @return true if unlocked, false otherwise
     */
    @Override
    public boolean isInventoryAccessible() {
        return !locked;
    }

    /**
     * Remove a {@link GridObject} from the inventory of this {@code LockerObject} (only works if unlocked).
     * @param obj the {@link GridObject} to remove
     * @return true if the removal was successful, false otherwise
     */
    @Override
    protected boolean removeFromInventory(GridObject obj) {
        if (!locked) {
            return super.removeFromInventory(obj);
        } else {
            return false;
        }
    }

    /**
     * Get the properties of this {@code LockerObject}. Automatically created properties: "Locked", "Unlocked" and "Password(secret)". The last is not visible to {@link Agent}s.
     * @param visibleOnly sets if only visible properties should be returned (other {@link Agent}s can't see the password, observer clients can)
     * @return the properties of this {@code LockerObject}
     */
    @Override
    public Collection<String> getProperties(boolean visibleOnly) {
        Collection<String> properties = super.getProperties(false);
        Collection<String> returnMe = new HashSet<String>();
        returnMe.addAll(properties);

        if (locked) {
            returnMe.add("Locked");
        } else {
            returnMe.add("Unlocked");
        }

        if (!visibleOnly && password != null) {
            returnMe.add("Password(" + password + ")");
        }
        return returnMe;
    }
}
