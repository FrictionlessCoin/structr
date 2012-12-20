/*
 *  Copyright (C) 2010-2012 Axel Morgner
 * 
 *  This file is part of structr <http://structr.org>.
 * 
 *  structr is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  structr is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with structr.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.structr.core.converter;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.structr.common.SecurityContext;
import org.structr.core.Services;
import org.structr.core.entity.AbstractNode;
import org.structr.core.graph.GraphDatabaseCommand;
import org.structr.core.graph.NodeFactory;

/**
 * Converts between nodeId and node
 *
 * @author Axel Morgner
 */
public class NodeIdNodeConverter extends PropertyConverter<AbstractNode, Long> {

	public NodeIdNodeConverter(SecurityContext securityContext) {
		super(securityContext, null);
	}
	
	@Override
	public Long convert(AbstractNode node) {
		if (node == null) return null;
		return node.getId();
	}

	@Override
	public AbstractNode revert(Long nodeId) {
		
		if (nodeId == null) return null;
		
		NodeFactory factory = new NodeFactory(securityContext);
		GraphDatabaseService graphDb = Services.command(securityContext, GraphDatabaseCommand.class).execute();
		
		Node node = graphDb.getNodeById(nodeId);
		if (node != null) {

			return factory.createNode(node);
		}
		
		return null;
	}
}