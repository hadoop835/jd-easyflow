package com.jd.easyflow.flow.model.pre;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.easyflow.flow.engine.FlowContext;
import com.jd.easyflow.flow.model.FlowNode;
import com.jd.easyflow.flow.model.NodeContext;
import com.jd.easyflow.flow.model.NodePreHandler;
import com.jd.easyflow.flow.util.FlowConstants;

/**
 * Inclusive check pre handler.
 * 
 *  IMPORTANT NOTICE! This class should not be singleton!
 *  
 * !!! Currently only support simple mode as below: For example, A->B,C->D, for
 * node D, we check if all next nodes of node A is finished.
 * 
 * @author liyuliang5
 *
 */
public class InclusiveCheckPreHandler implements NodePreHandler, NodePrePropertyGetter {

    private static final Logger logger = LoggerFactory.getLogger(InclusiveCheckPreHandler.class);

    private List<String> preNodes;

    public InclusiveCheckPreHandler() {

    }

    public InclusiveCheckPreHandler(List<String> preNodes) {
        this.preNodes = preNodes;
    }

    /**
     * Judge all exptected pre nodes finished.
     * 
     */
    @Override
    public boolean preHandle(NodeContext nodeContext, final FlowContext context) {
        Boolean checkResult = nodeContext.get(FlowConstants.NODECTX_PRE_RESULT);
        if (checkResult != null) {
            if (context.isLogOn() && logger.isInfoEnabled()) {
                logger.info("Pre result checked:" + checkResult);
            }
            return checkResult;
        }
        final Object lockObj;
        final FlowContext lockContext = context;
        synchronized (lockContext) {
            Object contextLockObj = context.get(FlowConstants.CTX_LOCK_PREFIX + nodeContext.getNodeId());
            if (contextLockObj == null) {
                contextLockObj = new Object();
                context.put(FlowConstants.CTX_LOCK_PREFIX + nodeContext.getNodeId(), contextLockObj);
            }
            lockObj = contextLockObj;
        }
        synchronized (lockObj) {
            List<String> preNodes = context.get(FlowConstants.CTX_PRE_NODES_PREFIX + nodeContext.getNodeId());
            if (preNodes == null) {
                preNodes = new ArrayList<String>();
                context.put(FlowConstants.CTX_PRE_NODES_PREFIX + nodeContext.getNodeId(), preNodes);
            }
            preNodes.add(nodeContext.getPreviousNode().getNodeId());
            if (context.isLogOn() && logger.isInfoEnabled()) {
                logger.info("Pre nodes executed:" + preNodes);
            }

            FlowNode currentNode = context.getFlow().getNode(nodeContext.getNodeId());
            List<String> configPreNodes = this.preNodes != null ? this.preNodes
                    : currentNode.getProperty(FlowConstants.PROP_PRENODES);
            if (nodeContext.getPreviousNode() == null || nodeContext.getPreviousNode().getPreviousNode() == null) {
                if (context.isLogOn() && logger.isWarnEnabled()) {
                    logger.warn("Previous node of " + nodeContext.getNodeId() + "is null");
                }
                return false;
            }

            NodeContext[] nextNodes = nodeContext.getPreviousNode().getPreviousNode().getNextNodes();
            List<String> nextNodeIds = new ArrayList<>(nextNodes.length);
            for (NodeContext node : nextNodes) {
                nextNodeIds.add(node.getNodeId());
            }

            List<String> expectedNodeIds = intersection(nextNodeIds, configPreNodes);
            if (context.isLogOn() && logger.isInfoEnabled()) {
                logger.info("Expected node ids:" + expectedNodeIds + "(next node ids:" + nextNodeIds + ")");
            }

            for (String s : expectedNodeIds) {
                if (!preNodes.contains(s)) {
                    return false;
                }
            }
            if (context.isLogOn() && logger.isInfoEnabled()) {
                logger.info("All expected pre nodes finished");
            }
            context.remove(FlowConstants.CTX_PRE_NODES_PREFIX + nodeContext.getNodeId());
            return true;

        }
    }
    
    private static List<String> intersection(List<String> list1, List<String> list2) {
        List<String> result = new ArrayList<>();
        if (list1 == null || list2 == null) {
            return result;
        }
        for (String s : list1) {
            if (list2.contains(s)) {
                result.add(s);
            }
        }
        return result;
    }

    public List<String> getPreNodes() {
        return preNodes;
    }

    public void setPreNodes(List<String> preNodes) {
        this.preNodes = preNodes;
    }

    @Override
    public String getCheckType() {
        return FlowConstants.NODE_PRE_CHECK_TYPE_INCLUSIVECHECK;
    }

}
