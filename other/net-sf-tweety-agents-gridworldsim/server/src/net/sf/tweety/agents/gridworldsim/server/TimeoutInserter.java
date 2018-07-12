/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
