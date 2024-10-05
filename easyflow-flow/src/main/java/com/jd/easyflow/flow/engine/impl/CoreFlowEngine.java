package com.jd.easyflow.flow.engine.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.easyflow.flow.el.ElEvaluator;
import com.jd.easyflow.flow.el.ElFactory;
import com.jd.easyflow.flow.engine.FlowContext;
import com.jd.easyflow.flow.engine.FlowEngine;
import com.jd.easyflow.flow.engine.FlowParam;
import com.jd.easyflow.flow.engine.FlowResult;
import com.jd.easyflow.flow.engine.FlowRunner;
import com.jd.easyflow.flow.engine.event.FlowEventListener;
import com.jd.easyflow.flow.engine.event.FlowEventTrigger;
import com.jd.easyflow.flow.exception.FlowException;
import com.jd.easyflow.flow.filter.Filter;
import com.jd.easyflow.flow.filter.FilterChain;
import com.jd.easyflow.flow.model.Flow;
import com.jd.easyflow.flow.model.NodeContext;
import com.jd.easyflow.flow.model.parser.FlowParser;
import com.jd.easyflow.flow.model.parser.FlowParserImpl;
import com.jd.easyflow.flow.util.FlowEventTypes;
import com.jd.easyflow.flow.util.FlowIOUtil;
import com.jd.easyflow.flow.util.JsonUtil;
import com.jd.easyflow.flow.util.Pair;

/**
 * 
 * @author liyuliang5
 */
public abstract class CoreFlowEngine implements FlowEngine {

    public static final Logger logger = LoggerFactory.getLogger(CoreFlowEngine.class);

    protected Map<String, Flow> flowMap = new ConcurrentHashMap<>();

    protected Map<String, String> flowDefinitionMap = new ConcurrentHashMap<String, String>();

    protected FlowEventTrigger eventTrigger = new FlowEventTrigger();

    protected List<FlowEventListener> listeners;

    protected List<Filter<Pair<FlowParam, FlowEngine>, FlowResult>> filters;

    protected String flowPath;

    protected volatile boolean inited;

    protected FlowRunner defaultFlowRunner = new SingleThreadFlowRunner();

    /**
     * Default is json definition parser.
     */
    protected FlowParser flowParser = new FlowParserImpl();

    protected Map<String, Object> properties = new ConcurrentHashMap<>();
    
    protected ElEvaluator elEvaluator;

    public void init() {
        if (inited) {
            return;
        }
        if (elEvaluator == null) {
            elEvaluator = ElFactory.get();
        }
        if (flowParser instanceof FlowParserImpl) {
            ((FlowParserImpl) flowParser).setElEvaluator(elEvaluator);
        }
        loadFlow();
        if (listeners != null) {
            listeners.forEach(listener -> {
                eventTrigger.addListener(listener);
            });
        }
        eventTrigger.init(null, null);
        if (filters != null) {
            filters.forEach(filter -> {
                filter.init(null, null);
            });
        }
        if (defaultFlowRunner != null) {
            defaultFlowRunner.init(null, null);
        }
        inited = true;
    }

    protected abstract void loadFlow();
    
    protected void loadFlowInputStream(InputStream inputStream) throws IOException  {
        String flowDefinition = FlowIOUtil.toString(inputStream);
        List<Flow> flowList = flowParser.parse(flowDefinition);
        flowDefinitionMap.put(flowList.get(0).getId(), flowDefinition);
        flowList.forEach(flow -> {
            if (flowMap.containsKey(flow.getId())) {
                throw new FlowException("Flow " + flow.getId() + " exists");
            }
            flowMap.put(flow.getId(), flow);
        });
    }
    
    

    /**
     * Start flow engine, exeucte flow.
     */
    @Override
    public FlowResult execute(FlowParam param) {
        if (! inited) {
            throw new FlowException("Flow engine is not inited. flowId:" + param.getFlowId());
        }
        boolean logOn = (param.getContext() != null && param.getContext().getLogFlag() != null) ? param.getContext().getLogFlag() : (param.getLogFlag() == null || param.getLogFlag());
        if (logOn && logger.isInfoEnabled()) {
            logger.info("START EXECUTE FLOW, flowId:" + param.getFlowId() + " nodeIds:"
                    + Arrays.toString(param.getNodeIds()));
        }
        if (logOn && logger.isDebugEnabled()) {
            logger.debug("Flow param:" + JsonUtil.toJsonString(param));
        }
        if (filters == null || filters.size() == 0) {
            return invokeFlowEngine(param);
        } else {
            FilterChain<Pair<FlowParam, FlowEngine>, FlowResult> chain = new FilterChain<Pair<FlowParam, FlowEngine>, FlowResult>(filters,
                    p -> invokeFlowEngine(p.getLeft()));
            return chain.doFilter(Pair.of(param, this));
        }
    }

    protected FlowResult invokeFlowEngine(FlowParam param) {
        // No flow engine listener scenario
        if (eventTrigger.getListenerList() == null || eventTrigger.getListenerList().size() == 0) {
            FlowResult result = executeFlow(param);
            return result;
        }
        // Has flow engine listener scenario
        Map<String, Object> data = new HashMap<>();
        data.put("param", param);
        data.put("flowEngine", this);
        eventTrigger.triggerEvent(FlowEventTypes.FLOW_ENGINE_START, data, null, false);
        try {
            FlowResult result = executeFlow(param);
            eventTrigger.triggerEvent(FlowEventTypes.FLOW_ENGINE_END, data, null, false);
            return result;
        } finally {
            eventTrigger.triggerEvent(FlowEventTypes.FLOW_ENGINE_COMPLETE, data, null, true);
        }
    }

    protected FlowResult executeFlow(FlowParam param) {
        // init flow context
        FlowContext context = initContext(param);
        // find flow definition
        Flow flow = findFlow(context);
        // set log flag
        if (context.getLogFlag() == null) {
            if (param.getLogFlag() != null) {
                context.setLogFlag(param.getLogFlag());
            } else {
                context.setLogFlag(flow.getLogFlag());
            }
        }
        if (context.isLogOn() && logger.isInfoEnabled()) {
            logger.info("EXECUTE FLOW, flowId:" + flow.getId());
        }
        if (flow.getFilters() == null || flow.getFilters().size() == 0) {
            return invokeFlow(context);
        } else {
            FilterChain<FlowContext, FlowResult> chain = new FilterChain<FlowContext, FlowResult>(flow.getFilters(),
                    p -> invokeFlow(p));
            return chain.doFilter(context);
        }
    }

    protected FlowResult invokeFlow(FlowContext context) {
        Flow flow = context.getFlow();
        Throwable throwable = null;
        try {
            flow.triggerEvent(FlowEventTypes.FLOW_START, context);
            init(context);
            run(context);
            flow.triggerEvent(FlowEventTypes.FLOW_END, context);
            return wrapResult(context);
        } catch (Throwable t) { // NOSONAR
            throwable = t;
            logger.error(t.getMessage(), t);
            throw t;
        } finally {
            flow.triggerEvent(FlowEventTypes.FLOW_COMPLETE, throwable, context, true);
        }
    }

    protected FlowContext initContext(FlowParam param) {
        FlowContext context = param.getContext() != null ? param.getContext() : new FlowContextImpl();
        if (context.getParam() == null) {
            context.setParam(param);
        }
        if (context.getResult() == null) {
            FlowResult result = new FlowResult();
            context.setResult(result);
            result.setContext(context);
        }
        if (context.getFlowId() == null) {
            context.setFlowId(param.getFlowId());
        }
        ((FlowContextImpl) context).setFlowEngine(this);
        if (context.getElEvaluator() == null) {
            ((FlowContextImpl) context).setElEvaluator(getElEvaluator());
        }
        return context;
    }

    protected void run(FlowContext context) {
        FlowRunner flowRunner = context.getFlow().getRunner();
        if (flowRunner == null) {
            flowRunner = defaultFlowRunner;
        }
        flowRunner.run(context);
    }

    /**
     * 
     * Find flow definition.
     *
     * @return
     */
    protected Flow findFlow(FlowContext context) {
        Flow flow = context.getFlow();
        if (flow == null) {
            flow = getFlow(context.getFlowId());
            if (flow == null) {
                throw new FlowException("Flow " + context.getFlowId() + " not exists");
            }
            context.setFlow(flow);
        }
        // Exists scenario changing flow id
        context.setFlowId(flow.getId());
        return flow;
    }

    @Override
    public Flow getFlow(String flowId) {
        return flowMap.get(flowId);
    }

    /**
     * 
     * Init flow context.
     *
     * @param param
     */
    protected void init(FlowContext context) {
        context.getFlow().triggerEvent(FlowEventTypes.INIT_START, context);
        if (context.getStartNodes() == null) {
            String[] nodeIds = context.getParam().getNodeIds();
            // If nodeIds is null, using startNodeIds: if is empty array, run empty flow
            // instance.
            if (nodeIds == null) {
                nodeIds = context.getFlow().getStartNodeIds();
            }
            if (nodeIds == null) {
                throw new FlowException("no start node");
            }
            NodeContext[] nodes = new NodeContext[nodeIds.length];
            for (int i = 0; i < nodeIds.length; i++) {
                nodes[i] = new NodeContext(nodeIds[i]);
            }
            ((FlowContextImpl) context).addNodes(nodes);
            context.setStartNodes(Arrays.asList(nodes));
        } else {
            ((FlowContextImpl) context).addNodes(context.getStartNodes().toArray(new NodeContext[context.getStartNodes().size()]));
        }
        context.getFlow().triggerEvent(FlowEventTypes.INIT_END, context);
    }
    
    

    public void addFlow(Flow flow) {
        flowMap.put(flow.getId(), flow);
    }

    private FlowResult wrapResult(FlowContext context) {
        FlowResult result = context.getResult();
        return result;
    }
    
    public void destroy() {
        if (defaultFlowRunner != null) {
            defaultFlowRunner.destroy();
        }
        if (flowMap != null && flowMap.size() > 0) {
            for (Entry<String, Flow> entry : flowMap.entrySet()) {
                entry.getValue().destroy();
            }
        }
        this.eventTrigger.destroy();
        if (this.filters != null) {
            filters.forEach( filter -> {
                filter.destroy();
            });
        }
    }

    public String getFlowPath() {
        return flowPath;
    }

    public void setFlowPath(String flowPath) {
        this.flowPath = flowPath;
    }


    public Map<String, Flow> getFlowMap() {
        return flowMap;
    }

    public void setFlowMap(Map<String, Flow> flowMap) {
        this.flowMap = flowMap;
    }

    public Map<String, String> getFlowDefinitionMap() {
        return flowDefinitionMap;
    }

    public void setFlowDefinitionMap(Map<String, String> flowDefinitionMap) {
        this.flowDefinitionMap = flowDefinitionMap;
    }

    public List<Filter<Pair<FlowParam, FlowEngine>, FlowResult>> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter<Pair<FlowParam, FlowEngine>, FlowResult>> filters) {
        this.filters = filters;
    }

    public FlowEventTrigger getEventTrigger() {
        return eventTrigger;
    }

    public void setEventTrigger(FlowEventTrigger eventTrigger) {
        this.eventTrigger = eventTrigger;
    }

    public List<FlowEventListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<FlowEventListener> listeners) {
        this.listeners = listeners;
    }

    public FlowRunner getDefaultFlowRunner() {
        return defaultFlowRunner;
    }

    public void setDefaultFlowRunner(FlowRunner defaultFlowRunner) {
        this.defaultFlowRunner = defaultFlowRunner;
    }

    @Override
    public FlowParser getFlowParser() {
        return flowParser;
    }

    public void setFlowParser(FlowParser flowParser) {
        this.flowParser = flowParser;
    }

    public boolean isInited() {
        return inited;
    }

    public void setInited(boolean inited) {
        this.inited = inited;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    @Override
    public <T>T getProperty(String key) {
        return (T) properties.get(key);
    }
    
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public ElEvaluator getElEvaluator() {
        return elEvaluator;
    }

    public void setElEvaluator(ElEvaluator elEvaluator) {
        this.elEvaluator = elEvaluator;
    }
    
    
    
}
