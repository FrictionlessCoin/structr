/*
 *  Copyright (C) 2010-2012 Axel Morgner
 *
 *  This file is part of structr <http://structr.org>.
 *
 *  structr is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  structr is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with structr.  If not, see <http://www.gnu.org/licenses/>.
 */



package org.structr.websocket.command;

import org.structr.common.RelType;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.EntityContext;
import org.structr.core.Services;
import org.structr.core.entity.AbstractNode;
import org.structr.core.entity.AbstractRelationship;
import org.structr.core.entity.RelationClass;
import org.structr.core.node.CreateNodeCommand;
import org.structr.core.node.NodeAttribute;
import org.structr.core.node.StructrTransaction;
import org.structr.core.node.TransactionCommand;
import org.structr.web.common.RelationshipHelper;
import org.structr.web.entity.Content;
import org.structr.websocket.message.MessageBuilder;
import org.structr.websocket.message.WebSocketMessage;

//~--- JDK imports ------------------------------------------------------------

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

//~--- classes ----------------------------------------------------------------

/**
 *
 * @author Christian Morgner
 * @author Axel Morgner
 */
public class AddCommand extends AbstractCommand {

	private static final Logger logger = Logger.getLogger(AddCommand.class.getName());

	//~--- methods --------------------------------------------------------

	@Override
	public void processMessage(WebSocketMessage webSocketData) {

		final SecurityContext securityContext = getWebSocket().getSecurityContext();

		// create static relationship
		final Map<String, Object> nodeData = webSocketData.getNodeData();
		String nodeToAddId                 = (String) nodeData.get("id");
		String childContent                = (String) nodeData.get("childContent");
		final Map<String, Object> relData  = webSocketData.getRelData();
		String parentId                    = webSocketData.getId();
		boolean newNodeCreated             = false;

		if (parentId != null) {

			AbstractNode nodeToAdd  = null;
			AbstractNode parentNode = getNode(parentId);

			if (nodeToAddId != null) {

				nodeToAdd = getNode(nodeToAddId);
			} else {

				StructrTransaction transaction = new StructrTransaction() {

					@Override
					public Object execute() throws FrameworkException {

						return Services.command(securityContext, CreateNodeCommand.class).execute(nodeData);

					}

				};

				try {

					// create node in transaction
					nodeToAdd      = (AbstractNode) Services.command(securityContext, TransactionCommand.class).execute(transaction);
					newNodeCreated = true;
				} catch (FrameworkException fex) {

					logger.log(Level.WARNING, "Could not create node.", fex);
					getWebSocket().send(MessageBuilder.status().code(fex.getStatus()).message(fex.getMessage()).build(), true);

				}

			}

			if ((nodeToAdd != null) && (parentNode != null)) {

				String originalResourceId = (String) nodeData.get("sourceResourceId");
				String newResourceId      = (String) nodeData.get("targetResourceId");
				RelationClass rel         = EntityContext.getRelationClass(parentNode.getClass(), nodeToAdd.getClass());

				if (rel != null) {

					try {

						if (newNodeCreated || (originalResourceId == null && newResourceId == null)) {

							// A new node was created, no relationship exists,
							// so we create a new one.
							rel.createRelationship(securityContext, parentNode, nodeToAdd, relData);
							//relData.clear();
						} else {

							// An existing node was added to the parent node.
							// All we need to do here is add another property to the relationship with
							// the new resource id as key and the designated position as value
							for (AbstractRelationship r : parentNode.getOutgoingRelationships(RelType.CONTAINS)) {

								Long pos = r.getLongProperty(originalResourceId);

								if (pos != null) {

									r.setProperty(newResourceId, Long.parseLong((String) relData.get(newResourceId)));
									//relData.clear();
								}

							}
						}

						// set resource ID on copied branch
						if ((originalResourceId != null) && (newResourceId != null) && !originalResourceId.equals(newResourceId)) {

							RelationshipHelper.tagOutgoingRelsWithResourceId(nodeToAdd, nodeToAdd, originalResourceId, newResourceId);
						}

					} catch (Throwable t) {

						getWebSocket().send(MessageBuilder.status().code(400).message(t.getMessage()).build(), true);

					}

				}

				// If text for a content child node is given, create and link a content node
				if (childContent != null) {

					Content contentNode             = null;
					final List<NodeAttribute> attrs = new LinkedList<NodeAttribute>();

					attrs.add(new NodeAttribute(Content.UiKey.content, childContent));
					attrs.add(new NodeAttribute(Content.UiKey.contentType, "text/plain"));
					attrs.add(new NodeAttribute(AbstractNode.Key.type, Content.class.getSimpleName()));

					StructrTransaction transaction = new StructrTransaction() {

						@Override
						public Object execute() throws FrameworkException {

							return Services.command(securityContext, CreateNodeCommand.class).execute(attrs);

						}

					};

					try {

						// create content node in transaction
						contentNode = (Content) Services.command(securityContext, TransactionCommand.class).execute(transaction);
					} catch (FrameworkException fex) {

						logger.log(Level.WARNING, "Could not create content child node.", fex);
						getWebSocket().send(MessageBuilder.status().code(fex.getStatus()).message(fex.getMessage()).build(), true);

					}

					if (contentNode != null) {

						try {

							// New content node is at position 0!!
							relData.put(newResourceId, 0L);
							rel.createRelationship(securityContext, nodeToAdd, contentNode, relData);

							// set resource ID on copied branch
							if ((originalResourceId != null) && (newResourceId != null) && !originalResourceId.equals(newResourceId)) {

								RelationshipHelper.tagOutgoingRelsWithResourceId(contentNode, contentNode, originalResourceId, newResourceId);
							}
						} catch (Throwable t) {

							getWebSocket().send(MessageBuilder.status().code(400).message(t.getMessage()).build(), true);

						}

					}

				}

			} else {

				getWebSocket().send(MessageBuilder.status().code(404).build(), true);
			}

		} else {

			getWebSocket().send(MessageBuilder.status().code(400).message("Add needs id and data.id!").build(), true);
		}

	}

	//~--- get methods ----------------------------------------------------

	@Override
	public String getCommand() {

		return "ADD";

	}

}
