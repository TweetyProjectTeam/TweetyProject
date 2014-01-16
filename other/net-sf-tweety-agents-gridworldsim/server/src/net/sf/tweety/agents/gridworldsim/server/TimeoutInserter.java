package net.sf.tweety.agents.gridworldsim.server;

import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import net.sf.tweety.agents.gridworldsim.server.items.QueueItem;

/**
 * Objects made from this class are {@code TimerTask}s which insert a {@link QueueItem} of type {@code STATETRANSITEM} into
 * the queue of incoming incidents.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class TimeoutInserter extends TimerTask {

    private final LinkedBlockingQueue<QueueItem> inItems;

    /**
     * Constructs a new {@code TimeoutInserter}
     * @param inItems the queue of incoming incidents
     */
    public TimeoutInserter(LinkedBlockingQueue<QueueItem> inItems) {
        this.inItems = inItems;
    }

    /**
     * The run() method of this {@code TimeoutInserter}.
     */
    @Override
    public void run() {
        inItems.add(new QueueItem(null, QueueItem.STATETRANSITEM));
    }
}
