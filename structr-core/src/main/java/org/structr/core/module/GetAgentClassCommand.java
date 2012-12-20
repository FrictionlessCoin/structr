/*
 *  Copyright (C) 2010-2012 Axel Morgner, structr <structr@structr.org>
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
package org.structr.core.module;

import org.structr.core.agent.Agent;

/**
 * Command for finding entity classes by class name. This command exists
 * because the ModuleService must be initialized before entity classes
 * can be found, and initialization is intiated automatically when a
 * command is requested.
 *
 * @author Christian Morgner
 */
public class GetAgentClassCommand extends ModuleServiceCommand {

	public Class<? extends Agent> execute(String name) {
		
		ModuleService service = (ModuleService)getArgument("moduleService");
		if(service != null) {
			
			return service.getAgentClass(name);
		}

		return null;
	}
}