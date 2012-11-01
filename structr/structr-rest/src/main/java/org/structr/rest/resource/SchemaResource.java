/*
 *  Copyright (C) 2012 Axel Morgner
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
package org.structr.rest.resource;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import org.structr.common.CaseHelper;
import org.structr.common.property.Property;
import org.structr.common.property.PropertyKey;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.*;
import org.structr.core.converter.PropertyConverter;
import org.structr.core.module.ModuleService;
import org.structr.rest.RestMethodResult;
import org.structr.rest.exception.IllegalMethodException;

/**
 *
 * @author Christian Morgner
 */
public class SchemaResource extends Resource {

	public enum UriPart {
		_schema
	}
	
	@Override
	public boolean checkAndConfigure(String part, SecurityContext securityContext, HttpServletRequest request) throws FrameworkException {
		
		this.securityContext = securityContext;
		
		if (UriPart._schema.name().equals(part)) {
			
			return true;
		}
		
		return false;
	}

	@Override
	public Result doGet(PropertyKey sortKey, boolean sortDescending, int pageSize, int page, String offsetId) throws FrameworkException {
		
		List<GraphObjectMap> resultList = new LinkedList<GraphObjectMap>();
		
		// extract types from ModuleService
		ModuleService moduleService = (ModuleService)Services.getService(ModuleService.class);
		for (String rawType : moduleService.getCachedNodeEntityTypes()) {
			
			// create & add schema information
			Class type               = EntityContext.getEntityClassForRawType(rawType);
			GraphObjectMap schema = new GraphObjectMap();
			resultList.add(schema);
			
			String url = "/".concat(CaseHelper.toUnderscore(rawType, true));
			
			schema.setProperty(new Property("url"),   url);
			schema.setProperty(new Property("type"),  rawType);
			schema.setProperty(new Property("flags"), SecurityContext.getResourceFlags(rawType));
			
			// list property sets for all views
			Map<String, Map<String, Object>> views = new TreeMap<String, Map<String, Object>>();
			Set<String> propertyViews              = EntityContext.getPropertyViews();
			schema.setProperty(new Property("views"), views);
			
			for (String view : propertyViews) {
				
				Map<String, Object> propertyConverterMap = new TreeMap<String, Object>();
				Set<PropertyKey> properties              = EntityContext.getPropertySet(type, view);
				
				// ignore "all" and empty views
				if (!"all".equals(view) && !properties.isEmpty()) {
					
					for (PropertyKey property : properties) {

						StringBuilder converterPlusValue    = new StringBuilder();
						PropertyConverter databaseConverter = property.databaseConverter(securityContext, null);
						PropertyConverter inputConverter    = property.inputConverter(securityContext);
						
						converterPlusValue.append("(");

						if (databaseConverter != null) {
							converterPlusValue.append(" databaseConverter: ");
							converterPlusValue.append(databaseConverter.getClass().getName());
							converterPlusValue.append(" ");
						}

						if (inputConverter != null) {
							converterPlusValue.append(" inputConverter: ");
							converterPlusValue.append(inputConverter.getClass().getName());
							converterPlusValue.append(" ");
						}
						
						converterPlusValue.append(")");

						propertyConverterMap.put(property.name(), converterPlusValue.toString());
					}
					
					views.put(view, propertyConverterMap);
				}
			}
			
			
		}
		
		return new Result(resultList, resultList.size(), false, false);
	}

	@Override
	public RestMethodResult doPost(Map<String, Object> propertySet) throws FrameworkException {
		throw new IllegalMethodException();
	}

	@Override
	public RestMethodResult doHead() throws FrameworkException {
		throw new IllegalMethodException();
	}

	@Override
	public RestMethodResult doOptions() throws FrameworkException {
		throw new IllegalMethodException();
	}

	@Override
	public Resource tryCombineWith(Resource next) throws FrameworkException {
		return null;
	}

	@Override
	public String getUriPart() {
		return "";
	}

	@Override
	public Class getEntityClass() {
		return null;
	}

	@Override
	public String getResourceSignature() {
		return UriPart._schema.name();
	}

	@Override
	public boolean isCollectionResource() throws FrameworkException {
		return true;
	}
}