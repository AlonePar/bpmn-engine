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
package com.catify.processengine.core.data.repositories;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.catify.processengine.core.data.model.entities.ProcessInstanceNode;

/**
 * The Spring Data neo4j managed interface ProcessInstanceNodeRepository provides 
 * convenient methods for accessing {@link ProcessInstanceNode}s in the database and 
 * has been enhanced with custom Cypher queries.
 * For implementation details please see {@link GraphRepository}.
 *
 * @author chris
 */
public interface ProcessInstanceNodeRepository extends GraphRepository<ProcessInstanceNode>{
	
	/**
	 * Find a process instance based on the unique process id and instance id.
	 *
	 * @param uniqueProcessId the unique process id
	 * @param processInstanceId the process instance id
	 * @return the process instance node
	 */
	@Query("start process=node:ProcessNode(uniqueProcessId={0}) match process-[r:HAS_PROCESS_INSTANCE]->processInctanceNode where processInctanceNode.instanceId = {1} return processInctanceNode")
	ProcessInstanceNode findProcessInstanceNode(String uniqueProcessId, String processInstanceId);
}
