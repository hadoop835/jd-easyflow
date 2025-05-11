package com.jd.easyflow.fsm.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.easyflow.fsm.Fsm;
import com.jd.easyflow.fsm.builder.FsmBuilder;
import com.jd.easyflow.fsm.el.ElEvaluator;
import com.jd.easyflow.fsm.el.ElFactory;
import com.jd.easyflow.fsm.event.ExpFsmEventListener;
import com.jd.easyflow.fsm.event.FsmEventListener;
import com.jd.easyflow.fsm.filter.ExpFilter;
import com.jd.easyflow.fsm.filter.Filter;
import com.jd.easyflow.fsm.model.FsmPostHandler;
import com.jd.easyflow.fsm.model.FsmPreHandler;
import com.jd.easyflow.fsm.model.InitContext;
import com.jd.easyflow.fsm.model.Transition;
import com.jd.easyflow.fsm.model.TransitionAction;
import com.jd.easyflow.fsm.model.TransitionPostHandler;
import com.jd.easyflow.fsm.model.TransitionPreHandler;
import com.jd.easyflow.fsm.model.builder.TransitionBuilder;
import com.jd.easyflow.fsm.model.definition.DefConstants;
import com.jd.easyflow.fsm.model.impl.StateImpl;
import com.jd.easyflow.fsm.model.impl.action.ExpTransitionAction;
import com.jd.easyflow.fsm.model.impl.fsm.post.ExpFsmPostHandler;
import com.jd.easyflow.fsm.model.impl.fsm.pre.ExpFsmPreHandler;
import com.jd.easyflow.fsm.model.impl.post.ConditionalTransitionPostHandler;
import com.jd.easyflow.fsm.model.impl.post.ExpTransitionPostHandler;
import com.jd.easyflow.fsm.model.impl.post.FixedTransitionPostHandler;
import com.jd.easyflow.fsm.model.impl.pre.ExpTransitionPreHandler;
import com.jd.easyflow.fsm.parser.event.ExpFsmParseEventListener;
import com.jd.easyflow.fsm.parser.event.FsmParseEvent;
import com.jd.easyflow.fsm.parser.event.FsmParseEventListener;
import com.jd.easyflow.fsm.parser.event.FsmParseEventTypes;
import com.jd.easyflow.fsm.util.JsonUtil;

/**
 * 
 * Fsm Parser.
 * 
 * @author liyuliang5
 * @version 1.0
 * @since 1.0
 */
public class FsmParser {

    private static final Logger logger = LoggerFactory.getLogger(FsmParser.class);

    private static final String FSM_STRING_KEY = "_fsm_string";
    
    private static List<FsmParseEventListener> preListeners;
    
    private static List<FsmParseEventListener> postListeners;
    
    public static Fsm parse(String data) {
        return parse(data, true);
    }
    
    public static Fsm parse(String data, boolean parseEl)  {
        return parse(data, parseEl, ElFactory.get());
    }
    
    /**
     * Parse string definition to java model.
     * 
     * @param data
     * @return
     */
    public static Fsm parse(String data, boolean parseEl, ElEvaluator elEvaluator) {
        Map<String, Object> map = JsonUtil.parseObject(data, Map.class);
        String fsmId = (String) map.get(DefConstants.COMMON_PROP_ID);
        String fsmName = (String) map.get(DefConstants.COMMON_PROP_NAME);
        FsmBuilder builder = FsmBuilder.create(fsmId, fsmName);
        builder.logFag((Boolean) map.get(DefConstants.FSM_PROP_LOG_FLAG));
        List<FsmParseEventListener> parseListeners = parseParseListeners(map, builder.build(), parseEl, elEvaluator);
        if (preListeners != null) {
            if (parseListeners == null) {
                parseListeners = new ArrayList<FsmParseEventListener>();
            }
            parseListeners.addAll(0, preListeners);
        }
        if (postListeners != null) {
            if (parseListeners == null) {
                parseListeners = new ArrayList<FsmParseEventListener>();
            }
            parseListeners.addAll(postListeners);
        }
        
        triggerParseEvent(parseListeners, FsmParseEventTypes.PARSE_FSM_START, map, builder.build(), null, elEvaluator, parseEl);
        
        // Parse property
        Map<String, Object> properties = (Map<String, Object>) map.get(DefConstants.COMMON_PROP_PROPERTIES);
        builder.putProperties(properties);
        
        // Parse pre handler
        FsmPreHandler fsmPreHandler = parseFsmPreHandler(map.get(DefConstants.FSM_PROP_PRE), parseEl, elEvaluator);
        builder.fsmPreHandler(fsmPreHandler);
        // Parse state
        List<Map<String, Object>> states = (List<Map<String, Object>>) map.get(DefConstants.FSM_PROP_STATES);
        if (states != null) {
            for (Map<String, Object> state : states) {
                boolean start = Boolean.TRUE.equals(state.get(DefConstants.STATE_PROP_START));
                StateImpl stateInfo = new StateImpl((String) state.get(DefConstants.COMMON_PROP_ID),
                        (String) state.get(DefConstants.COMMON_PROP_NAME),
                        (Map<String, Object>) state.get(DefConstants.COMMON_PROP_PROPERTIES));
                if (start) {
                    builder.startState(stateInfo);
                } else {
                    builder.state(stateInfo);
                }
            }
        }
        // Parse post handler
        FsmPostHandler fsmPostHandler = parseFsmPostHandler(map.get(DefConstants.FSM_PROP_POST), parseEl, elEvaluator);
        builder.fsmPostHandler(fsmPostHandler);
        // Parse event
        List<Map<String, Object>> events = (List<Map<String, Object>>) map.get(DefConstants.FSM_PROP_EVENTS);
        if (events != null) {
            for (Map<String, Object> event : events) {
                builder.event((String) event.get(DefConstants.COMMON_PROP_ID),
                        (String) event.get(DefConstants.COMMON_PROP_NAME));
            }
        }
        // Parse transition
        List<Map<String, Object>> transitions = (List<Map<String, Object>>) map.get(DefConstants.FSM_PROP_TRANSITIONS);
        if (transitions != null) {
            for (Map<String, Object> transition : transitions) {
                // create exp is unsupported.
                TransitionPreHandler preHandler = parseTransitionPreHandler(transition.get(DefConstants.TST_PROP_PRE),
                        parseEl, elEvaluator);
                TransitionAction transAction = parseTransitionAction(transition.get(DefConstants.TST_PROP_ACTION),
                        parseEl, elEvaluator);
                TransitionPostHandler postHandler = parseTransitionPostHandler(
                        transition.get(DefConstants.TST_PROP_POST), parseEl, elEvaluator);
                List<String> toList = (List<String>) transition.get(DefConstants.TST_PROP_TOLIST);

                Object from = transition.get(DefConstants.TST_PROP_FROM);
                List<String> froms = from instanceof String ? Arrays.asList((String) from) : (List<String>) from;
                Object event = transition.get(DefConstants.TST_PROP_EVENT);
                List<String> evts = event instanceof String ? Arrays.asList((String) event) : (List<String>) event;
                for (String fromId : froms) {
                    for (String eventId : evts) {
                        TransitionBuilder transBuilder = TransitionBuilder.create();
                        Transition trans = transBuilder.fromId(fromId).eventId(eventId).toIdList(toList)
                                .preHandler(preHandler).action(transAction).postHandler(postHandler).build();
                        if (trans != null) {
                            trans.postConstruct(transition, null);
                            builder.transition(trans);
                        }
                    }
                }

            }
        }
        // Listener
        parseListeners(map, builder, parseEl, elEvaluator);
        // Filter
        parseFilters(map, builder, parseEl, elEvaluator);
        // Transition Filter.
        parseTransitionFilters(map, builder, parseEl, elEvaluator);
        // Transition PreHandler Filter.
        parseTransitionPreHandlerFilters(map, builder, parseEl, elEvaluator);
        // Transition Action Filter.
        parseTransitionActionFilters(map, builder, parseEl, elEvaluator);
        // Transition PostHandler Filter.
        parseTransitionPostHandlerFilters(map, builder, parseEl, elEvaluator);

        
        Fsm fsm = builder.build();
        fsm.setProperty(FSM_STRING_KEY, data);
        
        fsm.postConstruct(map, null);
        
        triggerParseEvent(parseListeners, FsmParseEventTypes.PARSE_FSM_END, map, fsm, null, elEvaluator, parseEl);
        
        triggerParseEvent(parseListeners, FsmParseEventTypes.INIT_FSM_START, map, fsm, null, elEvaluator, parseEl);
        InitContext initContext = new InitContext();
        initContext.setParseEl(parseEl);
        initContext.setFsm(fsm);
        initContext.setFsmDefinitionMap(map);
        fsm.init(initContext, null);
        triggerParseEvent(parseListeners, FsmParseEventTypes.INIT_FSM_END, map, fsm, null, elEvaluator, parseEl);
                
        return fsm;
    }

    /**
     * Listener.
     * 
     * @param map
     * @param builder
     * @param parseEl
     */
    private static void parseListeners(Map<String, Object> map, FsmBuilder builder, boolean parseEl, ElEvaluator elEvaluator) {
        List<Map<String, Object>> listeners = (List<Map<String, Object>>) map.get(DefConstants.FSM_PROP_LISTENERS);
        if (listeners != null) {
            for (Object listenerObj : listeners) {
                if (listenerObj instanceof String) {
                    ExpFsmEventListener expFsmEventListener = new ExpFsmEventListener((String) listenerObj);
                    builder.listener(expFsmEventListener);
                } else {
                    Map<String, Object> listener = (Map<String, Object>) listenerObj;
                    String type = (String) listener.get(DefConstants.COMMON_PROP_TYPE);
                    if (DefConstants.COMMON_PROP_CREATE.equals(type)
                            || listener.containsKey(DefConstants.COMMON_PROP_CREATE_EXP)) {
                        if (parseEl) {
                            String exp = (String) listener.get(DefConstants.COMMON_PROP_CREATE_EXP);
                            Map<String, Object> createElContext = createElContext(listener);
                            FsmEventListener eventListener = elEvaluator.evalWithDefaultContext(exp, createElContext, false);
                            if (eventListener != null) {
                                eventListener.postConstruct(listener, null);
                                builder.listener(eventListener);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Filter.
     * 
     * @param map
     * @param builder
     * @param parseEl
     */
    private static void parseFilters(Map<String, Object> map, FsmBuilder builder, boolean parseEl, ElEvaluator elEvaluator) {
        List<Map<String, Object>> filters = (List<Map<String, Object>>) map.get(DefConstants.FSM_PROP_FILTERS);
        if (filters != null) {
            for (Object filterObj : filters) {
                if (filterObj instanceof String) {
                    ExpFilter expFilter = new ExpFilter<>((String) filterObj);
                    builder.filter(expFilter);
                } else {
                    Map<String, Object> filter = (Map<String, Object>) filterObj;
                    String type = (String) filter.get(DefConstants.COMMON_PROP_TYPE);
                    if (DefConstants.COMMON_PROP_CREATE.equals(type)
                            || filter.containsKey(DefConstants.COMMON_PROP_CREATE_EXP)) {
                        if (parseEl) {
                            String exp = (String) filter.get(DefConstants.COMMON_PROP_CREATE_EXP);
                            Map<String, Object> createElContext = createElContext(filter);
                            Filter fsmFilter = elEvaluator.evalWithDefaultContext(exp, createElContext, false);
                            if (fsmFilter != null) {
                                fsmFilter.postConstruct(filter, null);
                                builder.filter(fsmFilter);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Transition Filter.
     * 
     * @param map
     * @param builder
     * @param parseEl
     */
    private static void parseTransitionFilters(Map<String, Object> map, FsmBuilder builder, boolean parseEl, ElEvaluator elEvaluator) {
        List<Map<String, Object>> transitionFilters = (List<Map<String, Object>>) map
                .get(DefConstants.FSM_PROP_TRANSITION_FILTERS);
        if (transitionFilters != null) {
            for (Object filterObj : transitionFilters) {
                if (filterObj instanceof String) {
                    ExpFilter expFilter = new ExpFilter<>((String) filterObj);
                    builder.transitionFilter(expFilter);
                } else {
                    Map<String, Object> filter = (Map<String, Object>) filterObj;
                    String type = (String) filter.get(DefConstants.COMMON_PROP_TYPE);
                    if (DefConstants.COMMON_PROP_CREATE.equals(type)
                            || filter.containsKey(DefConstants.COMMON_PROP_CREATE_EXP)) {
                        if (parseEl) {
                            String exp = (String) filter.get(DefConstants.COMMON_PROP_CREATE_EXP);
                            Map<String, Object> createElContext = createElContext(filter);
                            Filter transitionFilter = elEvaluator.evalWithDefaultContext(exp, createElContext, false);
                            if (transitionFilter != null) {
                                transitionFilter.postConstruct(filter, null);
                                builder.transitionFilter(transitionFilter);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Transition action filter.
     * 
     * @param map
     * @param builder
     * @param parseEl
     */
    private static void parseTransitionActionFilters(Map<String, Object> map, FsmBuilder builder, boolean parseEl, ElEvaluator elEvaluator) {
        List<Map<String, Object>> transitionActionFilters = (List<Map<String, Object>>) map
                .get(DefConstants.FSM_PROP_TRANSITION_ACTION_FILTERS);
        if (transitionActionFilters != null) {
            for (Object filterObj : transitionActionFilters) {
                if (filterObj instanceof String) {
                    ExpFilter expFilter = new ExpFilter<>((String) filterObj);
                    builder.transitionActionFilter(expFilter);
                } else {
                    Map<String, Object> filter = (Map<String, Object>) filterObj;
                    String type = (String) filter.get(DefConstants.COMMON_PROP_TYPE);
                    if (DefConstants.COMMON_PROP_CREATE.equals(type)
                            || filter.containsKey(DefConstants.COMMON_PROP_CREATE_EXP)) {
                        if (parseEl) {
                            String exp = (String) filter.get(DefConstants.COMMON_PROP_CREATE_EXP);
                            Map<String, Object> createElContext = createElContext(filter);
                            Filter transitionActionFilter = elEvaluator.evalWithDefaultContext(exp, createElContext, false);
                            if (transitionActionFilter != null) {
                                transitionActionFilter.postConstruct(filter, null);
                                builder.transitionActionFilter(transitionActionFilter);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Transition preHandler filter.
     * 
     * @param map
     * @param builder
     * @param parseEl
     */
    private static void parseTransitionPreHandlerFilters(Map<String, Object> map, FsmBuilder builder, boolean parseEl, ElEvaluator elEvaluator) {
        List<Map<String, Object>> transitionPreHandlerFilters = (List<Map<String, Object>>) map
                .get(DefConstants.FSM_PROP_TRANSITION_PRE_HANDLER_FILTERS);
        if (transitionPreHandlerFilters != null) {
            for (Object filterObj : transitionPreHandlerFilters) {
                if (filterObj instanceof String) {
                    ExpFilter expFilter = new ExpFilter<>((String) filterObj);
                    builder.transitionPreHandlerFilter(expFilter);
                } else {
                    Map<String, Object> filter = (Map<String, Object>) filterObj;
                    String type = (String) filter.get(DefConstants.COMMON_PROP_TYPE);
                    if (DefConstants.COMMON_PROP_CREATE.equals(type)
                            || filter.containsKey(DefConstants.COMMON_PROP_CREATE_EXP)) {
                        if (parseEl) {
                            String exp = (String) filter.get(DefConstants.COMMON_PROP_CREATE_EXP);
                            Map<String, Object> createElContext = createElContext(filter);
                            Filter transitionPreHandlerFilter = elEvaluator.evalWithDefaultContext(exp, createElContext, false);
                            if (transitionPreHandlerFilter != null) {
                                transitionPreHandlerFilter.postConstruct(filter, null);
                                builder.transitionPreHandlerFilter(transitionPreHandlerFilter);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Transition postHandler filter.
     * 
     * @param map
     * @param builder
     * @param parseEl
     */
    private static void parseTransitionPostHandlerFilters(Map<String, Object> map, FsmBuilder builder, boolean parseEl, ElEvaluator elEvaluator) {
        List<Map<String, Object>> transitionPostHandlerFilters = (List<Map<String, Object>>) map
                .get(DefConstants.FSM_PROP_TRANSITION_POST_HANDLER_FILTERS);
        if (transitionPostHandlerFilters != null) {
            for (Object filterObj : transitionPostHandlerFilters) {
                if (filterObj instanceof String) {
                    ExpFilter expFilter = new ExpFilter<>((String) filterObj);
                    builder.transitionPostHandlerFilter(expFilter);
                } else {
                    Map<String, Object> filter = (Map<String, Object>) filterObj;
                    String type = (String) filter.get(DefConstants.COMMON_PROP_TYPE);
                    if (DefConstants.COMMON_PROP_CREATE.equals(type)
                            || filter.containsKey(DefConstants.COMMON_PROP_CREATE_EXP)) {
                        if (parseEl) {
                            String exp = (String) filter.get(DefConstants.COMMON_PROP_CREATE_EXP);
                            Map<String, Object> createElContext = createElContext(filter);
                            Filter transitionPostHandlerFilter = elEvaluator.evalWithDefaultContext(exp, createElContext, false);
                            if (transitionPostHandlerFilter != null) {
                                transitionPostHandlerFilter.postConstruct(filter, null);
                                builder.transitionPostHandlerFilter(transitionPostHandlerFilter);
                            }
                        }
                    }
                }
            }
        }
    }

    public static FsmPreHandler parseFsmPreHandler(Object preObj, boolean parseEl, ElEvaluator elEvaluator) {
        if (preObj == null) {
            return null;
        }
        if (preObj instanceof String) {
            ExpFsmPreHandler preHandler = new ExpFsmPreHandler();
            preHandler.setExp((String) preObj);
            return preHandler;
        }
        Map<String, Object> pre = (Map<String, Object>) preObj;
        String type = (String) pre.get(DefConstants.COMMON_PROP_TYPE);
        if (DefConstants.COMMON_PROP_CREATE.equals(type) || pre.containsKey(DefConstants.COMMON_PROP_CREATE_EXP)) {
            if (!parseEl) {
                return null;
            }
            String exp = (String) pre.get(DefConstants.COMMON_PROP_CREATE_EXP);
            Map<String, Object> createElContext = createElContext(pre);
            FsmPreHandler preHandler = elEvaluator.evalWithDefaultContext(exp, createElContext, false);
            if (preHandler != null) {
                preHandler.postConstruct(pre, null);
            }
            return preHandler;
        } else if (DefConstants.COMMON_PROP_EXP.equals(type) || pre.containsKey(DefConstants.COMMON_PROP_EXP)) {
            ExpFsmPreHandler preHandler = new ExpFsmPreHandler();
            String exp = (String) pre.get(DefConstants.COMMON_PROP_EXP);
            preHandler.setExp(exp);
            preHandler.postConstruct(pre, null);
            return preHandler;
        }
        throw new IllegalArgumentException("Param illegal:" + pre);
    }

    public static FsmPostHandler parseFsmPostHandler(Object postObj, boolean parseEl, ElEvaluator elEvaluator) {
        if (postObj == null) {
            return null;
        }
        if (postObj instanceof String) {
            ExpFsmPostHandler postHandler = new ExpFsmPostHandler();
            postHandler.setExp((String) postObj);
            return postHandler;
        }
        Map<String, Object> post = (Map<String, Object>) postObj;
        String type = (String) post.get(DefConstants.COMMON_PROP_TYPE);
        if (DefConstants.COMMON_PROP_CREATE.equals(type) || post.containsKey(DefConstants.COMMON_PROP_CREATE_EXP)) {
            if (!parseEl) {
                return null;
            }
            String exp = (String) post.get(DefConstants.COMMON_PROP_CREATE_EXP);
            Map<String, Object> createElContext = createElContext(post);
            FsmPostHandler postHandler = elEvaluator.evalWithDefaultContext(exp, createElContext, false);
            if (postHandler != null) {
                postHandler.postConstruct(post, null);
            }
            return postHandler;
        } else if (DefConstants.COMMON_PROP_EXP.equals(type) || post.containsKey(DefConstants.COMMON_PROP_EXP)) {
            ExpFsmPostHandler postHandler = new ExpFsmPostHandler();
            String exp = (String) post.get(DefConstants.COMMON_PROP_EXP);
            postHandler.setExp(exp);
            postHandler.postConstruct(post, null);
            return postHandler;
        }
        throw new IllegalArgumentException("Param illegal:" + post);
    }

    public static TransitionPreHandler parseTransitionPreHandler(Object preObj, boolean parseEl, ElEvaluator elEvaluator) {
        if (preObj == null) {
            return null;
        }
        if (preObj instanceof String) {
            ExpTransitionPreHandler preHandler = new ExpTransitionPreHandler();
            preHandler.setExp((String) preObj);
            return preHandler;
        }
        Map<String, Object> pre = (Map<String, Object>) preObj;
        String type = (String) pre.get(DefConstants.COMMON_PROP_TYPE);
        if (DefConstants.COMMON_PROP_CREATE.equals(type) || pre.containsKey(DefConstants.COMMON_PROP_CREATE_EXP)) {
            if (!parseEl) {
                return null;
            }
            String exp = (String) pre.get(DefConstants.COMMON_PROP_CREATE_EXP);
            Map<String, Object> createElContext = createElContext(pre);
            TransitionPreHandler preHandler = elEvaluator.evalWithDefaultContext(exp, createElContext, false);
            if (preHandler != null) {
                preHandler.postConstruct(pre, null);
            }
            return preHandler;
        } else if (DefConstants.COMMON_PROP_EXP.equals(type) || pre.containsKey(DefConstants.COMMON_PROP_EXP)) {
            ExpTransitionPreHandler preHandler = new ExpTransitionPreHandler();
            String exp = (String) pre.get(DefConstants.COMMON_PROP_EXP);
            preHandler.setExp(exp);
            preHandler.postConstruct(pre, null);
            return preHandler;
        }
        throw new IllegalArgumentException("Param illegal:" + pre);

    }

    public static TransitionAction parseTransitionAction(Object actionObj, boolean parseEl, ElEvaluator elEvaluator) {
        if (actionObj == null) {
            return null;
        }
        if (actionObj instanceof String) {
            ExpTransitionAction transAction = new ExpTransitionAction();
            transAction.setExp((String) actionObj);
            return transAction;
        }
        Map<String, Object> action = (Map<String, Object>) actionObj;
        String type = (String) action.get(DefConstants.COMMON_PROP_TYPE);
        if (DefConstants.COMMON_PROP_CREATE.equals(type) || action.containsKey(DefConstants.COMMON_PROP_CREATE_EXP)) {
            if (!parseEl) {
                return null;
            }
            String exp = (String) action.get(DefConstants.COMMON_PROP_CREATE_EXP);
            Map<String, Object> createElContext = createElContext(action);
            TransitionAction nodeAction = elEvaluator.evalWithDefaultContext(exp, createElContext, false);
            if (nodeAction != null) {
                nodeAction.postConstruct(action, null);
            }
            return nodeAction;
        } else if (DefConstants.COMMON_PROP_EXP.equals(type) || action.containsKey(DefConstants.COMMON_PROP_EXP)) {
            ExpTransitionAction transAction = new ExpTransitionAction();
            String exp = (String) action.get(DefConstants.COMMON_PROP_EXP);
            transAction.setExp(exp);
            transAction.postConstruct(action, null);
            return transAction;
        }
        throw new IllegalArgumentException("Param illegal:" + action);
    }

    public static TransitionPostHandler parseTransitionPostHandler(Object postObj, boolean parseEl, ElEvaluator elEvaluator) {
        if (postObj == null) {
            return null;
        }
        if (postObj instanceof String) {
            ExpTransitionPostHandler postHandler = new ExpTransitionPostHandler();
            postHandler.setExp((String) postObj);
            return postHandler;
        }
        Map<String, Object> post = (Map<String, Object>) postObj;
        String type = (String) post.get(DefConstants.COMMON_PROP_TYPE);
        if (DefConstants.COMMON_PROP_CREATE.equals(type) || post.containsKey(DefConstants.COMMON_PROP_CREATE_EXP)) {
            if (!parseEl) {
                return null;
            }
            String exp = (String) post.get(DefConstants.COMMON_PROP_CREATE_EXP);
            Map<String, Object> createElContext = createElContext(post);
            TransitionPostHandler postHandler = elEvaluator.evalWithDefaultContext(exp, createElContext, false);
            if (postHandler != null) {
                postHandler.postConstruct(post, null);
            }
            return postHandler;
        } else if (DefConstants.COMMON_PROP_EXP.equals(type) || post.containsKey(DefConstants.COMMON_PROP_EXP)) {
            ExpTransitionPostHandler postHandler = new ExpTransitionPostHandler();
            String exp = (String) post.get(DefConstants.COMMON_PROP_EXP);
            postHandler.setExp(exp);
            postHandler.postConstruct(post, null);
            return postHandler;
        } else if (DefConstants.TST_POST_TYPE_CONDITION.equals(type)
                || post.containsKey(DefConstants.TST_POST_PROP_CONDITIONS)
                || post.containsKey(DefConstants.TST_POST_PROP_WHEN)) {
            List<Map<String, Object>> conditionList = null;
            if (post.containsKey(DefConstants.TST_POST_PROP_CONDITIONS)) {
                conditionList = (List<Map<String, Object>>) post.get(DefConstants.TST_POST_PROP_CONDITIONS);
            } else {
                conditionList = Arrays.asList(post);
            }
            ConditionalTransitionPostHandler postHandler = new ConditionalTransitionPostHandler(conditionList);
            postHandler.postConstruct(post, null);
            return postHandler;

        } else if (DefConstants.TST_POST_TYPE_FIXED.equals(type) || post.containsKey(DefConstants.TST_POST_PROP_TO)) {
            Object nextState = (Object) post.get(DefConstants.TST_POST_PROP_TO);
            FixedTransitionPostHandler postHandler = new FixedTransitionPostHandler(nextState);
            return postHandler;
        }
        throw new IllegalArgumentException("Param illegal:" + post);
    }
    
    protected static List<FsmParseEventListener> parseParseListeners(Map<String, Object> map, Fsm fsm,
            boolean parseEl, ElEvaluator elEvaluator) {
        List<Object> parseListenerConfList = (List<Object>) map.get(DefConstants.FSM_PROP_PARSE_LISTENERS);
        if (parseListenerConfList == null) {
            return null;
        }

        List<FsmParseEventListener> parseListeners = new ArrayList<>();
        for (Object listenerObj : parseListenerConfList) {
            if (listenerObj instanceof String) {
                ExpFsmParseEventListener parseListener = new ExpFsmParseEventListener((String) listenerObj);
                parseListeners.add(parseListener);
            } else {
                Map<String, Object> listener = (Map<String, Object>) listenerObj;
                String type = (String) listener.get(DefConstants.COMMON_PROP_TYPE);
                if (DefConstants.COMMON_PROP_CREATE.equals(type)
                        || listener.containsKey(DefConstants.COMMON_PROP_CREATE_EXP)) {
                    if (parseEl) {
                        String exp = (String) listener.get(DefConstants.COMMON_PROP_CREATE_EXP);
                        Map<String, Object> elContext = new HashMap<>();
                        elContext.put("def", map);
                        elContext.put("fsm", fsm);
                        FsmParseEventListener parseListener = elEvaluator.evalWithDefaultContext(exp, elContext,
                                false);
                        if (parseListener != null) {
                            parseListener.postConstruct(listener, null);
                            parseListeners.add(parseListener);
                        }
                    }
                }
            }
        }
        return parseListeners;
    }

    private static void triggerParseEvent(List<FsmParseEventListener> listeners, String eventType,
            Map<String, Object> fsmDef, Fsm fsm, Object data, ElEvaluator elEvaluator, boolean parseEl) {
        if (listeners == null || listeners.size() == 0) {
            return;
        }
        FsmParseEvent event = new FsmParseEvent();
        event.setType(eventType);
        event.setFsm(fsm);
        event.setFsmDef(fsmDef);
        event.setData(data);
        event.setElEvaluator(elEvaluator);
        event.setParseEl(parseEl);
        for (FsmParseEventListener listener : listeners) {
            listener.on(event);
        }
    }

    /**
     * 
     * Java model to string.
     *
     * @param flow
     * @return
     */
    public static String stringify(Fsm fsm) {
        if (fsm.getProperty(FSM_STRING_KEY) != null) {
            return fsm.getProperty(FSM_STRING_KEY);
        }
        logger.warn("Original fsm string definition not found, unsupported now. fsmId:" + fsm.getId());
        return null;
    }
    
    private static Map<String, Object> createElContext(Map<String, Object> currentDef) {
        Map<String, Object> context = new HashMap<>(3);
        context.put("definition", currentDef);
        return context;
    }

    public static List<FsmParseEventListener> getPreListeners() {
        return preListeners;
    }

    public static void setPreListeners(List<FsmParseEventListener> preListeners) {
        FsmParser.preListeners = preListeners;
    }

    public static List<FsmParseEventListener> getPostListeners() {
        return postListeners;
    }

    public static void setPostListeners(List<FsmParseEventListener> postListeners) {
        FsmParser.postListeners = postListeners;
    }
    
    
}
