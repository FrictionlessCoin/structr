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

package org.structr.core.app;

import java.util.List;
import org.structr.common.error.FrameworkException;
import org.structr.core.GraphObject;
import org.structr.core.entity.Relation;
import org.structr.core.graph.NodeAttribute;
import org.structr.core.graph.NodeInterface;
import org.structr.core.graph.RelationshipInterface;
import org.structr.core.property.PropertyMap;

/**
 * 
 * @author Christian Morgner
 */
public interface App {

	public void beginTx();
	public void commitTx() throws FrameworkException;
	
	public <T extends NodeInterface> T create(final Class<T> type, final String name) throws FrameworkException;
	public <T extends NodeInterface> T create(final Class<T> type, final PropertyMap properties) throws FrameworkException;
	public <T extends NodeInterface> T create(final Class<T> type, final NodeAttribute<?>... attributes) throws FrameworkException;

	public void delete(final NodeInterface node) throws FrameworkException;

	public <T extends Relation> T create(final NodeInterface fromNode, final NodeInterface toNode, final Class<T> relType) throws FrameworkException;
	public <T extends Relation> T create(final NodeInterface fromNode, final NodeInterface toNode, final Class<T> relType, final PropertyMap properties) throws FrameworkException;
	
	public void delete(final RelationshipInterface relationship) throws FrameworkException;

	public GraphObject get(final String uuid) throws FrameworkException;
	public <T extends GraphObject> List<T> get(final Class<T> type) throws FrameworkException;
	public <T extends GraphObject> T get(final Class<T> type, final String uuid) throws FrameworkException;

	public <T extends NodeInterface> Query<T> nodeQuery(final Class<T> type);
	public <T extends RelationshipInterface> Query<T> relationshipQuery(final Class<T> type);
	
	public void shutdown();
}
