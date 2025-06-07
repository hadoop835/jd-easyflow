package com.jd.easyflow.flow.model.builder;

import com.jd.easyflow.flow.model.Flow;
import com.jd.easyflow.flow.model.FlowNode;
import com.jd.easyflow.flow.model.FlowPostHandler;
import com.jd.easyflow.flow.model.FlowPreHandler;
import com.jd.easyflow.flow.model.InitContext;
import com.jd.easyflow.flow.model.NodeAction;
import com.jd.easyflow.flow.model.NodePostHandler;
import com.jd.easyflow.flow.model.node.NodeImpl;
import com.jd.easyflow.flow.model.parser.FlowParser;

/**
 * 
 * @author liyuliang5
 *
 */
public class FlowBuilder {

	private Flow flow;

	public static FlowBuilder create(String id, String name) {
		Flow flow = new Flow();
		flow.setId(id);
		flow.setName(name);
		FlowBuilder builder = new FlowBuilder();
		builder.flow = flow;
		return builder;
	}

	public FlowBuilder setProperty(String key, Object value) {
		flow.setProperty(key, value);
		return this;
	}
	
	public FlowBuilder addNode(String nodeId, NodeAction action) {
		return addNode(nodeId, action, null);
	}

	public FlowBuilder addNode(String nodeId, NodeAction action, NodePostHandler postHandler) {
		NodeImpl node = new NodeImpl();
		node.setId(nodeId);
		node.setAction(action);
		node.setPostHandler(postHandler);
		flow.addNode(node);
		return this;
	}
	
	public FlowBuilder addNode(FlowNode node) {
        flow.addNode(node);
        return this;
    }
	
    public FlowBuilder setStartNodeId(String startNodeId) {
        flow.setStartNodeIds(new String[] {startNodeId});
        return this;
    }
	
    public FlowBuilder setStartNodeIds(String[] startNodeIds) {
        flow.setStartNodeIds(startNodeIds);
        return this;
    }
    
    public FlowBuilder setFlowPreHandler(FlowPreHandler preHandler) {
        flow.setPreHandler(preHandler);
        return this;
    }
    
    public FlowBuilder setFlowPostHandler(FlowPostHandler postHandler) {
        flow.setPostHandler(postHandler);
        return this;
    }
    
    

	public Flow build() {
		return flow;
	}
	
	public Flow buildAndInit() {
	    InitContext initContext = new InitContext();
        initContext.setFlowParser(null);
        initContext.setParseEl(true);
        initContext.setFlowList(null);
        initContext.setFlowDefinitionMap(null);
        initContext.setFlow(flow);
	    flow.init(initContext, null);
	    return flow;
	}
	
    public Flow buildAndInit(FlowParser flowParser) {
        InitContext initContext = new InitContext();
        initContext.setFlowParser(flowParser);
        initContext.setParseEl(true);
        initContext.setFlowList(null);
        initContext.setFlowDefinitionMap(null);
        initContext.setFlow(flow);
        flow.init(initContext, null);
        return flow;
    }

}
