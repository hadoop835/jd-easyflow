if (!window.J) {
    window.J = {};
}

(function(J) {
    J.msg = {};
    if (!window.jLocale) {
        var lang = (navigator.language || navigator.userLanguage).toLowerCase();
        if (lang.indexOf('zh') == -1) {
            window.jLocale = "en_US";
        } else {
            window.jLocale = "zh_CN";
        }
    }
    $.extend(J.msg, {
    "locale":"en_US",

    "multiselect.nonSelectedText":"Please Select",
    "multiselect.nSelectedText":"Selected",
    "multiselect.allSelectedText":"All Selected",
    "multiselect.filterPlaceholder":"Search",
    "multiselect.selectAllText":"Select All",
    
    "jqueryValidate.strictDate":"Date format is illegal",  
    "jqueryValidate.json":"JSON format is illegal",  
    "jqueryValidate.fileInput":"File cannot be null", 
    "jqueryValidate.required":"Required filed", 
    "jqueryValidate.remote":"Please correct this field", 
    "jqueryValidate.email":"Email format is illegal", 
    "jqueryValidate.url":"URL is illegal", 
    "jqueryValidate.date":"Date format is illegal", 
    "jqueryValidate.dateISO":"Please input date (ISO).", 
    "jqueryValidate.number":"Please input digit", 
    "jqueryValidate.digits":"Please input integer number(>=0)", 
    "jqueryValidate.creditcard":"Please input valid credit card", 
    "jqueryValidate.equalTo":"Please input same value", 
    "jqueryValidate.accept":"Please input valid suffix string", 
    "jqueryValidate.maxlength":"Max length is {0}", 
    "jqueryValidate.minlength":"Min length is {0}", 
    "jqueryValidate.rangelength":"Length should between {0} and {1}", 
    "jqueryValidate.range":"Please input value between {0} and {1}", 
    "jqueryValidate.max":"Max value is {0}", 
    "jqueryValidate.min":"Min value is {0}",
    
    "ajaxSuccessCallback.successShowMsg":"Operate Success!",
    "ajaxSuccessCallback.exceptionShowMsg":"System Exception!",
    
    "jDialog.confirm":"Confirm",
    "jDialog.cancel":"Cancel",
    "jDialog.close":"Close",
    "jDialog.view":"View",
    
    "jAjax.requestException":"Request Exception",
    
    "jListSelect.select":"Select",   
    "jListSelect.value":"Value",
    "jListSelect.info":"Info",
    "jListSelect.inputTip":"Plase input value of element",
    "jListSelect.view":"View",
    
    "jListSelect.select":"Select",
    
    "flow.jsonDefTitle":"JSON Flow Definition",
    "flow.flowJsonDefinition":"JSON Flow Definition",    
    "flow.newFlowDef":"New Flow Definition",
    "flow.oldFlowDef":"Old Flow Definition",
    "flow.noJsonDataTip":"No JSON flow definition data",
    "flow.id":"Flow ID",    
    "flow.nodeId":"Node ID",
    "flow.nodeName":"Node Name",
    "flow.flowDefId":"Flow Definition ID",
    "flow.flowDefName":"Flow Definition Name",
    "flow.flowDefType":"Flow Definition Type",
    "flow.flowDefType.flowBpmn":"FlowEngine-BPMN",
    "flow.flowDefType.flowJson":"FlowEngine-JSON",
    "flow.flowDefType.fsmJson":"StateMachine-JSON",
    "flow.bizType":"Business Type",
    "flow.category":"Category",
    "flow.flowDefinition":"Flow Definition",
    "flow.flowDiagram":"Flow Diagram",
    "flow.jsonDefinition":"JSON Definition",
    "flow.flowJsonDefinitioin":"Flow JSON Definition",
    "flow.update":"Update",
    "flow.oldNewCompare":"Old/New Compare",
    "flow.cancel":"Cancel",
    
    "bpmn.detailInfoType":"Detail Info Type",
    "bpmn.bpmnFlowDefinition":"BPMN Flow Definition",
    "bpmn.flowJsonDefinition":"Flow JSON Definition",
    "bpmn.jsonContent":"JSON Content",
    "bpmn.convertErrorTip":"BPMN convert to JSON request exception,this is ajax request, please confirm url or service whether or not normal,current url:",
    "bpmn.newFlowDefinition":"New Flow Definition",
    "bpmn.oldFlowDefinition":"Old Flow Definition",
    "bpmn.idTooltip":"Element ID,Not NULL,IMPORTANT!suggest english with business meaning",
    "bpmn.idEmptyError":"ID cannot be null",
    "bpmn.idExistsError":"ID exists",    
    "bpmn.name":"Name",
    "bpmn.nameTooltip":"Element Name,can be null",
    "bpmn.documentation":"Documentation",
    "bpmn.documentationTooltip":"Element description",    
    "bpmn.property":"Properties",
    "bpmn.propertyTooltip":"Element properties,IMPORTANT!JSON format,see reference doc",    
    "bpmn.conditionType":"Branch Condition Type",
    "bpmn.conditionType.exclusive":"Exclusive",
    "bpmn.conditionType.inclusive":"Inclusive",
    "bpmn.conditionTypeTooltip":"Branch type,default is inclusive(every branch will be evaluated,all branch with true result will be executed); Use default option when only one branch.",   
    "bpmn.startNode":"Start Node",
    "bpmn.startNode.true":"True",
    "bpmn.startNode.false":"False",
    "bpmn.startNodeTooltip":"Identify this is a start node",
    "bpmn.selfPre":"Customize PreHandler",
    "bpmn.selfPreTooltip":"Customize node pre handler, will override pre handler of diagram semantics",
    "bpmn.selfAction":"Customize Action",
    "bpmn.selfActionTooltip":"Customize node action,will override node action of diagram semantics",        
    "bpmn.selfPost":"Customize Branch",
    "bpmn.selfPostTooltip":"Customize branch,will override branch of diagram,for free flow",
    "bpmn.listeners":"Listeners",
    "bpmn.listenersTooltip":"Flow listeners,JSON array format,see reference doc",
    "bpmn.filters":"Flow Filters",
    "bpmn.filtersTooltip":"Flow Filters,JSON array format,see reference doc",
    "bpmn.nodeFilters":"Node Filters",
    "bpmn.nodeFiltersTooltip":"Node Filters,JSON array format,see reference doc",    
    "bpmn.nodePreHandlerFilters":"Node PreHandler Filters",
    "bpmn.nodePreHandlerFiltersTooltip":"Node PreHandler Filters,JSON array format,see reference doc",      
    "bpmn.nodeActionFilters":"Node Action Filters",
    "bpmn.nodeActionFiltersTooltip":"Node Action Filters,JSON array format,see reference doc",    
    "bpmn.nodePostHandlerFilters":"Node PostHandler Filters",
    "bpmn.nodePostHandlerFiltersTooltip":"Node PostHandler Filters,JSON array format,see reference doc",    
    "bpmn.flowPreHandlerFilters":"Flow PreHandler Filters",
    "bpmn.flowPreHandlerFiltersTooltip":"Flow PreHandler Filters,JSON array format,see reference doc",      
    "bpmn.flowPostHandlerFilters":"Flow PostHandler Filters",
    "bpmn.flowPostHandlerFiltersTooltip":"Flow PostHandler Filters,JSON array format,see reference doc",        
    "bpmn.runner":"Flow Runner",
    "bpmn.runnerTooltip":"Flow Runner, default is sequential runner",
    "bpmn.parseListeners":"Flow Parse Event Listeners",
    "bpmn.parseListenersTooltip":"Flow parse event listeners",      
    "bpmn.scriptFormat":"Script Format",
    "bpmn.scriptFormat.exp":"SpringEL Expression",
    "bpmn.scriptFormat.createExp":"Create Expression",
    "bpmn.scriptFormatTooltip":"Script format",
    "bpmn.script":"Script",
    "bpmn.scriptTooltip":"Script,see reference doc",
    "bpmn.conditionExp":"Condition Expression",
    "bpmn.conditionExpTooltip":"Condition Expression,SPEL Format",
    "bpmn.condition":"Condition",
    "bpmn.none":"None",
    "bpmn.branchType":"Branch Type",
    "bpmn.branchType.exclusive":"Exclusive",
    "bpmn.bpmnDefinition":"BPMN Definition",
    "bpmn.import":"Import",
    "bpmn.export":"Export",
    "bpmn.svg":"SVG",
    "bpmn.zoomIn":"Zoom In",
    "bpmn.zoomOut":"Zoom Out",
    "bpmn.fullScreen":"Full Screen",
    "bpmn.node":"Node",
    "bpmn.infoPanel":"Information Panel",
    "bpmn.flowBpmnDefinition":"Flow BPMN Definition",
    "bpmn.renderFlowDiagram":"Render Flow Diagram",
    "bpmn.viewFlowDefinition":"View EasyFlow Definition",
    "bpmn.oldNewBpmnCompare":"Old/New BPMN Definition Compare",
    "bpmn.oldNewEasyFlowCompare":"Old/New EasyFlow Definition Compare",
    "bpmn.cancel":"Cancel",
    "bpmn.flowPreHandler":"Flow PreHandler",
    "bpmn.flowPreHandlerTooltip":"Flow PreHandler",
    "bpmn.flowPostHandler":"Flow PostHandler",
    "bpmn.flowPostHandlerTooltip":"Flow PostHandler",
    "bpmn.calledElement":"Sub Flow ID",
    "bpmn.calledElementTooltip":"Sub Flow ID",
    "bpmn.flow":"Flow Info",
    "bpmn.flowTooltip":"Flow info, id is required",
    "bpmn.calledElement":"Called ID",
    "bpmn.logFlag":"Log flag",
    "bpmn.logFlagTooltip":"Print log flag, default true",
    "bpmn.default":"Default",
    "bpmn.true":"True",
    "bpmn.false":"False",
    "bpmn.switchRowMode":"Switch Row Mode",
    "bpmn.switchJsonMode":"Switch JSON Mode",
    "bpmn.property.propertyKey":"Key",    
    "bpmn.property.propertyValue":"Value",    
    "bpmn.property.typeString":"String",    
    "bpmn.property.typeNumber":"Number",    
    "bpmn.property.typeBoolean":"Boolean",    
    "bpmn.property.typeObject":"Object",    
    "bpmn.property.typeArray":"Array",    
    "bpmn.property.typeNull":"Null",       
    "bpmn.property.valueFormatInvalid":"Value Format Invalid!",      
    });
    
    J.bpmnIOTranslations = {};

    // bpmn io translations
    $.extend(J.bpmnIOTranslations, {
    });

})(window.J)