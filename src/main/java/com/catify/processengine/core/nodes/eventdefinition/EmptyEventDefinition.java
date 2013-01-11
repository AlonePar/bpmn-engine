/**
 * *******************************************************
 * Copyright (C) 2013 catify <info@catify.com>
 * *******************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package com.catify.processengine.core.nodes.eventdefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catify.processengine.core.messages.ActivationMessage;
import com.catify.processengine.core.messages.DeactivationMessage;
import com.catify.processengine.core.messages.TriggerMessage;

/**
 * The Class EmptyEventDefinition is for Events that do not define an EventDefinition, which is valid in bpmn.
 *
 * @author chris
 */
public class EmptyEventDefinition implements EventDefinition {

	static final Logger LOG = LoggerFactory.getLogger(EmptyEventDefinition.class);
	
	/* (non-Javadoc)
	 * @see com.catify.processengine.core.nodes.eventdefinition.EventDefinition#acitivate(com.catify.processengine.core.messages.ActivationMessage)
	 */
	@Override
	public void acitivate(ActivationMessage message) {
		LOG.debug(String.format("%s received %s", this.getClass().getSimpleName(), message
				.getClass().getSimpleName()));
	}

	/* (non-Javadoc)
	 * @see com.catify.processengine.core.nodes.eventdefinition.EventDefinition#deactivate(com.catify.processengine.core.messages.DeactivationMessage)
	 */
	@Override
	public void deactivate(DeactivationMessage message) {
		LOG.debug(String.format("%s received %s", this.getClass().getSimpleName(), message
				.getClass().getSimpleName()));
	}

	/* (non-Javadoc)
	 * @see com.catify.processengine.core.nodes.eventdefinition.EventDefinition#trigger(com.catify.processengine.core.messages.TriggerMessage)
	 */
	@Override
	public void trigger(TriggerMessage message) {
		LOG.debug(String.format("%s received %s", this.getClass().getSimpleName(), message
				.getClass().getSimpleName()));
	}

}
