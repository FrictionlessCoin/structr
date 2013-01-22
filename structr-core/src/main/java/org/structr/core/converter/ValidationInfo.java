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
package org.structr.core.converter;

import org.structr.core.property.PropertyKey;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.Value;

/**
 * Encapsulates validation information for String properties.
 * 
 * @author Axel Morgner
 */
public class ValidationInfo implements Value<ValidationInfo> {

	private PropertyKey<String> errorKey = null;
	private String errorType = null;
	private int minLength = -1;

	public ValidationInfo(String errorType, PropertyKey<String> errorKey, int minLength) {
		this.errorType = errorType;
		this.errorKey  = errorKey;
		this.minLength = minLength;
	}

	@Override
	public void set(SecurityContext securityContext, ValidationInfo value) throws FrameworkException {
	}

	@Override
	public ValidationInfo get(SecurityContext securityContext) {
		return this;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorKey(PropertyKey<String> errorKey) {
		this.errorKey = errorKey;
	}
	
	public PropertyKey getErrorKey() {
		return errorKey;
	}

	public int getMinLength() {
		return minLength;
	}
}
