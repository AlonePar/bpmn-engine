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

import com.catify.processengine.core.data.model.NodeInstaceStates;
import com.catify.processengine.core.messages.ActivationMessage;
import com.catify.processengine.core.messages.DeactivationMessage;
import com.catify.processengine.core.messages.TriggerMessage;
import com.catify.processengine.core.services.NodeInstanceMediatorService;

/**
 * The SubProcessNode can embed other FlowElements. To implement this it triggers the embedded start event.
 * When the embedded end event is reached it will trigger this sub process. After that the sub process continues 
 * like a usual node.
 * 
 * @author christopher köster
 * 
 */
public class SubProcessNode extends Task {
	
	private List<ActorRef> embeddedStartNodes;
	private List<ActorRef> embeddedNodes;

	public SubProcessNode (String uniqueProcessId, String uniqueFlowNodeId, List<ActorRef> outgoingNodes, 
			List<ActorRef> embeddedStartNodes, List<ActorRef> embeddedNodes, List<ActorRef> boundaryEvent) {
		this.setUniqueProcessId(uniqueProcessId);
		this.setUniqueFlowNodeId(uniqueFlowNodeId);
		this.setOutgoingNodes(outgoingNodes);
		this.setNodeInstanceMediatorService(new NodeInstanceMediatorService(
				uniqueProcessId, uniqueFlowNodeId));
		this.setBoundaryEvents(boundaryEvent);
		
		this.embeddedStartNodes = embeddedStartNodes;
		this.embeddedNodes = embeddedNodes;
	}

	@Override
	protected void activate(ActivationMessage message) {
		this.getNodeInstanceMediatorService().setState(
				message.getProcessInstanceId(), NodeInstaceStates.ACTIVE_STATE);
		
		this.activateBoundaryEvents(message);
		
		this.getNodeInstanceMediatorService().setNodeInstanceStartTime(message.getProcessInstanceId(), new Date());
		
		this.getNodeInstanceMediatorService().persistChanges();
		
		// start the sub process
		this.sendMessageToNodeActors(new TriggerMessage(message.getProcessInstanceId(), null), getStartNodes());
	}

	@Override
	protected void deactivate(DeactivationMessage message) {
		this.getNodeInstanceMediatorService().setNodeInstanceEndTime(message.getProcessInstanceId(), new Date());
		
		this.getNodeInstanceMediatorService().setState(
				message.getProcessInstanceId(), NodeInstaceStates.DEACTIVATED_STATE);
		
		this.deactivateBoundaryEvents(message);
		
		this.getNodeInstanceMediatorService().persistChanges();
		
		// deactivate embedded nodes
		this.sendMessageToNodeActors(message, getEmbeddedNodes());
	}

	@Override
	protected void trigger(TriggerMessage message) {
		this.getNodeInstanceMediatorService().setNodeInstanceEndTime(message.getProcessInstanceId(), new Date());
		
		this.getNodeInstanceMediatorService().setState(
				message.getProcessInstanceId(), NodeInstaceStates.PASSED_STATE);
		
		this.deactivateBoundaryEvents(message);
		
		this.getNodeInstanceMediatorService().persistChanges();

		this.sendMessageToNodeActors(new ActivationMessage(message.getProcessInstanceId()), getOutgoingNodes());
	}

	
	public List<ActorRef> getStartNodes() {
		return embeddedStartNodes;
	}

	public void setStartNodes(List<ActorRef> startNodes) {
		this.embeddedStartNodes = startNodes;
	}

	public List<ActorRef> getEmbeddedNodes() {
		return embeddedNodes;
	}

	public void setEmbeddedNodes(List<ActorRef> subNodes) {
		this.embeddedNodes = subNodes;
	}

}
