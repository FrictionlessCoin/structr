/**
 * Copyright (C) 2010-2013 Axel Morgner, structr <structr@structr.org>
 *
 * This file is part of structr <http://structr.org>.
 *
 * structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.core.agent;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.Delayed;
import org.structr.core.entity.AbstractNode;
import org.structr.core.entity.Principal;

/**
 * A task that an {@link Agent} can operate on.

 * @author Christian Morgner
 */
public interface Task extends Delayed, StatusInfo {

    /**
     * Principal to process the task as
     *
     * @return
     */
    public Principal getUser();

    /**
     * Returns the nodes this task should operate on.
     *
     * TODO: Set or List?
     *
     * @return a set of nodes relevant to this task.
     */
    public Set<AbstractNode> getNodes();

    /**
     * Returns the priority of this task.
     *
     * @return the priority of this task
     */
    public int priority();

    /**
     * Returns the time this task is scheduled for.
     *
     * TODO: return Date, long, or Calendar, or something else?
     * TODO: relative / absolute time? (relative only with timestamped tasks)
     *
     * @return
     */
    public Date getScheduledTime();

    /**
     * Returns the time this task was created.
     *
     * @return the time this task was created
     */
    public Date getCreationTime();

    /**
     * Returns the task type
     *
     * @return the task type
     */
    public String getType();
}
