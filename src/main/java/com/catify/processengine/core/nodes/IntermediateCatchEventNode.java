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
package com.catify.processengine.core.nodes;

import java.util.Date;
import java.util.List;

import akka.actor.ActorRef;

import com.catify.processengine.core.data.dataobjects.DataObjectService;
import com.catify.processengine.core.data.model.NodeInstaceStates;
import com.catify.processengine.core.messages.ActivationMessage;
import com.catify.processengine.core.messages.DeactivationMessage;
import com.catify.processengine.core.messages.TriggerMessage;
import com.catify.processengine.core.nodes.eventdefinition.EventDefinition;
import com.catify.processengine.core.services.NodeInstanceMediatorService;

/**
 * An intermediate catch event can receive (and save) messages sent from outside
 * of the process engine.
 * 
 * @author chris
 * 
 */
public class IntermediateCatchEventNode extends CatchEvent {

	public IntermediateCatchEventNode() {
	}

	/**
	 * Instantiates a new catch event node.
	 * 
	 * @param uniqueProcessId
	 *            the process id
	 * @param uniqueFlowNodeId
	 *            the unique flow node id
	 * @param eventDefinition
	 *            the event definition
	 * @param outgoingNodes
	 *            the outgoing nodes
	 * @param nodeInstanceMediatorService
	 *            the node instance service
	 */
	public IntermediateCatchEventNode(String uniqueProcessId,
			String uniqueFlowNodeId, EventDefinition eventDefinition,
			List<ActorRef> outgoingNodes, DataObjectService dataObjectHandling) {
		this.setUniqueProcessId(uniqueProcessId);
		this.setUniqueFlowNodeId(uniqueFlowNodeId);
		this.setEventDefinition(eventDefinition);
		this.setOutgoingNodes(outgoingNodes);
		this.setNodeInstanceMediatorService(new NodeInstanceMediatorService(
				uniqueProcessId, uniqueFlowNodeId));
		this.setDataObjectHandling(dataObjectHandling);
	}

	@Override
	protected void activate(ActivationMessage message) {
		this.getNodeInstanceMediatorService().setState(
				message.getProcessInstanceId(), NodeInstaceStates.ACTIVE_STATE);
		
		this.getNodeInstanceMediatorService().setNodeInstanceStartTime(message.getProcessInstanceId(), new Date());
		
		this.getNodeInstanceMediatorService().persistChanges();
		
		eventDefinition.acitivate(message);
	}

	@Override
	protected void deactivate(DeactivationMessage message) {
		eventDefinition.deactivate(message);
		
		this.getNodeInstanceMediatorService().setNodeInstanceEndTime(message.getProcessInstanceId(), new Date());
		
		this.getNodeInstanceMediatorService().setState(
				message.getProcessInstanceId(),
				NodeInstaceStates.DEACTIVATED_STATE);
		
		this.getNodeInstanceMediatorService().persistChanges();
	}

	@Override
	protected void trigger(TriggerMessage message) {
		this.getDataObjectService().saveObject(this.getUniqueProcessId(), message.getProcessInstanceId(), message.getPayload());
		
		eventDefinition.trigger(message);
		
		this.getNodeInstanceMediatorService().setNodeInstanceEndTime(message.getProcessInstanceId(), new Date());
		
		this.getNodeInstanceMediatorService().setState(
				message.getProcessInstanceId(), NodeInstaceStates.PASSED_STATE);
		
		this.getNodeInstanceMediatorService().persistChanges();
		
		this.sendMessageToNodeActors(
				new ActivationMessage(message.getProcessInstanceId()),
				this.getOutgoingNodes());
	}

}
