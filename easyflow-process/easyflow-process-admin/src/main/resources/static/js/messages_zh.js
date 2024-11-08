(function (J) {
if (window.jLocale=='zh_CN') {
$.extend(J.msg, {
    "locale":"zh_CN",

    "multiselect.nonSelectedText":"请选择",
    "multiselect.nSelectedText":"已选择",
    "multiselect.allSelectedText":"已全选",
    "multiselect.filterPlaceholder":"搜索",
    "multiselect.selectAllText":"全部选择",
    
    "jqueryValidate.strictDate":"日期格式不合法",  
    "jqueryValidate.json":"JSON格式不合法",  
    "jqueryValidate.fileInput":"文件不能为空", 
    "jqueryValidate.required":"必须字段", 
    "jqueryValidate.remote":"请修正该字段", 
    "jqueryValidate.email":"请输入正确格式的电子邮件", 
    "jqueryValidate.url":"请输入合法的网址", 
    "jqueryValidate.date":"请输入合法的日期", 
    "jqueryValidate.dateISO":"请输入合法的日期 (ISO).", 
    "jqueryValidate.number":"请输入合法的数字", 
    "jqueryValidate.digits":"只能输入大于等于零的整数", 
    "jqueryValidate.creditcard":"请输入合法的信用卡号", 
    "jqueryValidate.equalTo":"请再次输入相同的值", 
    "jqueryValidate.accept":"请输入拥有合法后缀名的字符串", 
    "jqueryValidate.maxlength":"长度最长为 {0}", 
    "jqueryValidate.minlength":"长度最少为 {0}", 
    "jqueryValidate.rangelength":"长度必须在 {0}到 {1}之间", 
    "jqueryValidate.range":"请输入一个介于 {0} 和 {1} 之间的值", 
    "jqueryValidate.max":"请输入一个最大为{0} 的值", 
    "jqueryValidate.min":"请输入一个最小为{0} 的值",
    
    "ajaxSuccessCallback.successShowMsg":"操作成功",
    "ajaxSuccessCallback.exceptionShowMsg":"系统异常",
    
    "jDialog.confirm":"确定",
    "jDialog.cancel":"取消",
    "jDialog.close":"关闭",
    "jDialog.view":"查看",
    
    "jAjax.requestException":"请求异常",
    
    "jListSelect.value":"值",
    "jListSelect.info":"信息",
    "jListSelect.inputTip":"请先输入元素值",
    "jListSelect.view":"查看",
    
    "jListSelect.select":"选择",
    
    "flow.jsonDefTitle":"JSON流程定义",
    "flow.flowJsonDefinition":"JSON流程定义",        
    "flow.newFlowDef":"新流程定义",
    "flow.oldFlowDef":"旧流程定义",
    "flow.noJsonDataTip":"无JSON流程定义数据",
    "flow.id":"流程ID",        
    "flow.nodeId":"节点ID",
    "flow.nodeName":"节点名称",
    "flow.flowDefId":"流程定义ID",
    "flow.flowDefName":"流程定义名称",
    "flow.flowDefType":"流程定义类型",
    "flow.flowDefType.flowBpmn":"流程引擎-BPMN",
    "flow.flowDefType.flowJson":"流程引擎-JSON",
    "flow.flowDefType.fsmJson":"状态机-JSON",
    "flow.bizType":"业务类型",
    "flow.category":"流程类别",
    "flow.flowDefinition":"流程定义",
    "flow.flowDiagram":"流程图",
    "flow.jsonDefinition":"JSON定义",
    "flow.flowJsonDefinitioin":"流程JSON定义",
    "flow.update":"更新",
    "flow.oldNewCompare":"新旧比对",
    "flow.cancel":"取消",
    
    "bpmn.detailInfoType":"详情信息类型",
    "bpmn.bpmnFlowDefinition":"BPMN流程定义",
    "bpmn.flowJsonDefinition":"流程JSON定义",
    "bpmn.jsonContent":"JSON内容",
    "bpmn.convertErrorTip":"BPMN转换JSON请求异常，此请求为ajax请求,请确认地址或服务是否正确,当前地址:",
    "bpmn.newFlowDefinition":"新流程定义",
    "bpmn.oldFlowDefinition":"旧流程定义",
    "bpmn.idTooltip":"元素ID，非空，重要！建议英文，命名要有业务含义",
    "bpmn.idEmptyError":"ID不能为空",
    "bpmn.idExistsError":"ID已存在",
    "bpmn.name":"名称",
    "bpmn.nameTooltip":"元素名称，中文，可为空",
    "bpmn.documentation":"说明",
    "bpmn.documentationTooltip":"元素描述说明",    
    "bpmn.property":"属性",
    "bpmn.propertyTooltip":"元素属性，重要！JSON格式，具体参考配置文档",    
    "bpmn.conditionType":"分支条件类型",
    "bpmn.conditionType.exclusive":"排他网关",
    "bpmn.conditionType.inclusive":"包容网关",
    "bpmn.conditionTypeTooltip":"分支类型，默认为包容类型(每个分支条件都会计算，结果为true的均执行);一个分支时选择默认,非并行分支选择排他",   
    "bpmn.startNode":"开始节点",
    "bpmn.startNode.true":"是",
    "bpmn.startNode.false":"否",
    "bpmn.startNodeTooltip":"开始节点标识",
    "bpmn.selfPre":"自定义前置",
    "bpmn.selfPreTooltip":"自定义前置处理, 会覆盖流程图原语义，高级用法",
    "bpmn.selfAction":"自定义动作",
    "bpmn.selfActionTooltip":"自定义节点动作，会覆盖流程图原语义，高级用法",        
    "bpmn.selfPost":"自定义流转",
    "bpmn.selfPostTooltip":"自定义流转，会覆盖图中流转方向，自由流场景使用",
    "bpmn.listeners":"监听器",
    "bpmn.listenersTooltip":"流程监听器，JSON数组格式，具体参考配置文档",
    "bpmn.filters":"流程过滤器",
    "bpmn.filtersTooltip":"流程过滤器，JSON数组格式，具体参考配置文档",
    "bpmn.nodeFilters":"节点过滤器",
    "bpmn.nodeFiltersTooltip":"节点过滤器，JSON数组格式，具体参考配置文档",    
    "bpmn.nodePreHandlerFilters":"节点前置处理过滤器",
    "bpmn.nodePreHandlerFiltersTooltip":"节点前置处理过滤器，JSON数组格式，具体参考配置文档",        
    "bpmn.nodeActionFilters":"节点动作过滤器",
    "bpmn.nodeActionFiltersTooltip":"节点动作过滤器，JSON数组格式，具体参考配置文档",  
    "bpmn.nodePostHandlerFilters":"节点后置处理过滤器",
    "bpmn.nodePostHandlerFiltersTooltip":"节点后置处理过滤器，JSON数组格式，具体参考配置文档",     
    "bpmn.flowPreHandlerFilters":"流程前置处理过滤器",
    "bpmn.flowPreHandlerFiltersTooltip":"流程前置处理过滤器，JSON数组格式，具体参考配置文档",        
    "bpmn.flowPostHandlerFilters":"流程后置处理过滤器",
    "bpmn.flowPostHandlerFiltersTooltip":"流程后置处理过滤器，JSON数组格式，具体参考配置文档",        
    "bpmn.runner":"流程执行器",
    "bpmn.runnerTooltip":"流程执行器，默认为串行执行器",
    "bpmn.parseListeners":"流程解析事件监听器",
    "bpmn.parseListenersTooltip":"流程解析事件监听器",            
    "bpmn.scriptFormat":"脚本格式",
    "bpmn.scriptFormat.exp":"spel表达式",
    "bpmn.scriptFormat.createExp":"create表达式",
    "bpmn.scriptFormatTooltip":"脚本格式",
    "bpmn.script":"脚本",
    "bpmn.scriptTooltip":"脚本，具体格式参考配置文档",
    "bpmn.conditionExp":"条件表达式",
    "bpmn.conditionExpTooltip":"条件表达式，SPEL格式",
    "bpmn.condition":"条件",
    "bpmn.none":"无",
    "bpmn.branchType":"分支类型",
    "bpmn.branchType.exclusive":"排他",
    "bpmn.bpmnDefinition":"BPMN定义",
    "bpmn.import":"导入",
    "bpmn.export":"导出",
    "bpmn.svg":"SVG",
    "bpmn.zoomIn":"缩小",
    "bpmn.zoomOut":"放大",
    "bpmn.fullScreen":"全屏",
    "bpmn.node":"节点",
    "bpmn.infoPanel":"信息面板",
    "bpmn.flowBpmnDefinition":"流程BPMN定义",
    "bpmn.renderFlowDiagram":"渲染流程图",
    "bpmn.viewFlowDefinition":"查看EasyFlow定义",
    "bpmn.oldNewBpmnCompare":"新旧BPMN定义比对",
    "bpmn.oldNewEasyFlowCompare":"新旧EasyFlow定义比对",
    "bpmn.cancel":"取消",
    "bpmn.flowPreHandler":"流程前置处理器",
    "bpmn.flowPreHandlerTooltip":"流程前置处理器",
    "bpmn.flowPostHandler":"流程后置处理器",
    "bpmn.flowPostHandlerTooltip":"流程后置处理器",
    "bpmn.calledElement":"子流程ID",
    "bpmn.calledElementTooltip":"子流程ID",
    "bpmn.flow":"流程信息",
    "bpmn.flowTooltip":"流程信息,至少需配置id",
    "bpmn.calledElement":"调用ID",
    "bpmn.logFlag":"打印日志开关",
    "bpmn.logFlagTooltip":"打印日志开关，缺省开",
    "bpmn.default":"缺省",
    "bpmn.true":"是",
    "bpmn.false":"否",
    "bpmn.switchRowMode":"切换行模式",
    "bpmn.switchJsonMode":"切换JSON模式",
    "bpmn.property.propertyKey":"属性键",    
    "bpmn.property.propertyValue":"属性值",    
    "bpmn.property.typeString":"字符",    
    "bpmn.property.typeNumber":"数值",    
    "bpmn.property.typeBoolean":"真假",    
    "bpmn.property.typeObject":"对象",    
    "bpmn.property.typeArray":"数组",    
    "bpmn.property.typeNull":"空值",   
    "bpmn.property.valueFormatInvalid":"值格式不正确!"  
	
});


    J.bpmnIOTranslations = {
        "Add Lane above": "添加到通道之上",
        "Add Lane below": "添加到通道之下",
        "Append compensation activity": "追加补偿活动",
        "Append {type}": "追加 {type}",
        "Append EndEvent": "追加结束事件",
        "Append Gateway": "追加网关",
        "Append Task": "追加任务",
        "Append Intermediate/Boundary Event": "追加消息接收事件",
        "Append TextAnnotation": "追加文本标注",
        "Business Rule Task": "规则任务",
        "Call Activity": "调用流程",
        "Cancel Boundary Event": "取消边界事件",
        "Cancel End Event": "结束取消事件",
        "Change type": "更改类型",
        "Collapsed Pool": "折叠池",
        "Compensation Boundary Event": "补偿边界事件",
        "Compensation End Event": "结束补偿事件",
        "Compensation Intermediate Throw Event": "中间补偿抛出事件",
        "Compensation Start Event": "补偿启动事件",
        "Complex Gateway": "复杂网关",
        "Conditional Boundary Event (non-interrupting)": "条件边界事件 (非中断)",
        "Conditional Boundary Event": "条件边界事件",
        "Conditional Flow": "条件流转",        
        "Conditional Intermediate Catch Event": "中间条件捕获事件",
        "Conditional Start Event (non-interrupting)": "条件启动事件 (非中断)",
        "Conditional Start Event": "条件启动事件",
        "Connect using Association": "文本关联",
        "Connect using DataInputAssociation": "数据关联",
        "Connect using Sequence/MessageFlow or Association": "连接",
        "Create DataStoreReference": "创建数据存储(仅用于展示)",
        "Create IntermediateThrowEvent/BoundaryEvent": "创建中间抛出/边界事件",
        "Create Pool/Participant": "创建池/参与者",
        "Create expanded SubProcess": "创建可折叠子流程",
        "Create {type}": "创建 {type}",
        "Create StartEvent": "创建事件",
        "Create ScriptTask": "创建任务",
        "Create Gateway": "创建网关",
        "Create Group": "创建分组",
        "Default Flow": "缺省流转",
        "Data Store Reference": "数据存储",
        "Data Object Reference": "数据对象",
        "Divide into three Lanes": "分成三条通道",
        "Divide into two Lanes": "分成两条通道",
        "Empty Pool": "空池",
        "Empty Pool (removes content)":"空池(清空内容)",
        "End Event": "结束事件",
        "Error Boundary Event": "错误边界事件",
        "Error End Event": "结束错误事件",
        "Error Start Event": "错误启动事件",
        "Escalation Boundary Event (non-interrupting)": "升级边界事件 (非中断)",
        "Escalation Boundary Event": "升级边界事件",
        "Escalation End Event": "结束升级事件",
        "Escalation Intermediate Throw Event": "中间升级抛出事件",
        "Escalation Start Event (non-interrupting)": "升级启动事件 (非中断)",
        "Escalation Start Event": "升级启动事件",
        "Event Sub Process": "事件子流程",
        "Event based Gateway": "事件网关",
        "Exclusive Gateway": "排他网关",
        "Expanded Pool": "展开池",
        "Inclusive Gateway": "包容网关",
        "Intermediate Throw Event": "中间抛出事件",
        "Link Intermediate Catch Event": "中间链接捕获事件",
        "Link Intermediate Throw Event": "中间链接抛出事件",
        "Loop": "循环",
        "Manual Task": "手动任务",
        "Message Boundary Event (non-interrupting)": "消息边界事件 (非中断)",
        "Message Boundary Event": "消息边界事件",
        "Message End Event": "结束消息事件",
        "Message Intermediate Catch Event": "中间消息捕获事件",
        "Message Intermediate Throw Event": "中间消息抛出事件",
        "Message Start Event (non-interrupting)": "消息启动事件 (非中断)",
        "Message Start Event": "消息启动事件",
        "Parallel Gateway": "并行网关",
        "Parallel Multi Instance": "并行多实例",
        "Receive Task": "接收消息任务",
        "Remove": "移除",
        "Script Task": "脚本任务",
        "Send Task": "发送任务",
        "Sequence Flow": "顺序流转",
        "Sequential Multi Instance": "串行多实例",
        "Service Task": "服务任务",
        "Signal Boundary Event (non-interrupting)": "信号边界事件 (非中断)",
        "Signal Boundary Event": "信号边界事件",
        "Signal End Event": "结束信号事件",
        "Signal Intermediate Catch Event": "中间信号捕获事件",
        "Signal Intermediate Throw Event": "中间信号抛出事件",
        "Signal Start Event (non-interrupting)": "信号启动事件 (非中断)",
        "Signal Start Event": "信号启动事件",
        "Start Event": "开始事件",
        "Sub Process (collapsed)": "折叠子流程",
        "Sub Process (expanded)": "展开子流程",
        "Sub Process": "子流程",
        "Task": "任务",
        "TextAnnotation": "文本标注",
        "Terminate End Event": "终止边界事件",
        "Timer Boundary Event (non-interrupting)": "定时边界事件 (非中断)",
        "Timer Boundary Event": "定时边界事件",
        "Timer Intermediate Catch Event": "中间定时捕获事件",
        "Timer Start Event (non-interrupting)": "定时启动事件 (非中断)",
        "Timer Start Event": "定时启动事件",
        "Transaction": "事务",
        "User Task": "用户任务",
        "already rendered {element}": "{element} 已呈现",
        "diagram not part of bpmn:Definitions": "图表不是 bpmn:Definitions 的一部分",
        "element required": "需要元素",
        "element {element} referenced by {referenced}#{property} not yet drawn": "元素 {element} 的引用 {referenced}#{property} 尚未绘制",
        "failed to import {element}": "{element} 导入失败",
        "flow elements must be children of pools/participants": "元素必须是池/参与者的子级",
        "more than {count} child lanes": "超过 {count} 条通道",
        "no diagram to display": "没有要显示的图表",
        "no parent for {element} in {parent}": "在 {element} 中没有父元素 {parent}",
        "no process or collaboration to display": "没有可显示的流程或协作",
        "no shape type specified": "未指定形状类型",
        "out of bounds release": "越界释放",
        "Change element":"更换元素",
        "Activate the lasso tool":"区域选择"
    };

	$.extend(J.msg, {
		"common.view":"查看",
		"dataview.add":"新增",
		"dataview.dataInfo":"数据信息",
		"dataview.originalData":"原始数据",
		"dataviewcfg.collectPolicy":"收集策略",
		"dataviewcfg.keep":"保留",
		"dataviewcfg.ignore":"忽略",
		"dataviewcfg.topHtml":"顶部HTML",
		"dataviewcfg.bottomHtml":"底部HTML",
		"dataviewcfg.pageBeforeRenderScript":"页面前处理脚本",
		"dataviewcfg.pageAfterRenderScript":"页面后处理脚本",
		"dataviewcfg.pageBeforeCollectScript":"收集前处理脚本",
		"dataviewcfg.pageAfterCollectScript":"收集后处理脚本",
		"dataviewcfg.nullPolicyTooltip":"配置收集时的null值处理方式,一般全量模板选择忽略，其他选择空或保留",
		"dataviewcfg.tabName":"选项卡名称",
		"dataviewcfg.siderBar":"侧边栏",
		"dataviewcfg.siderBarLevel":"侧边栏层级",
		"dataviewcfg.panelName":"面板名称",
		"dataviewcfg.show":"显示",
		"dataviewcfg.cardName":"卡片名称",
		"dataviewcfg.elementPerRow":"每行元素数",
		"dataviewcfg.configName":"配置名称",
		"dataviewcfg.configType":"配置类型",
		"dataviewcfg.selectType":"请选择",
		"dataviewcfg.textType":"文本",
		"dataviewcfg.selectType":"下拉",
		"dataviewcfg.areaType":"区域",
		"dataviewcfg.fixTextType":"固定文本",
		"dataviewcfg.tableSelectType":"表格选择",
		"dataviewcfg.listSelectType":"列表选择",
		"dataviewcfg.selfType":"自定义",
		"dataviewcfg.flowType":"流程",
		"dataviewcfg.fsmType":"状态机",
		"dataviewcfg.dateType":"日期",
		"dataviewcfg.fileType":"文件",
		"dataviewcfg.cardListType":"卡片列表",
		"dataviewcfg.tableType":"表格",
		"dataviewcfg.maskInputType":"掩码输入",
		"dataviewcfg.gridColNum":"栅格列数",
		"dataviewcfg.gridColAuto":"自动",
		"dataviewcfg.newLine":"换行",
		"dataviewcfg.yes":"是",
		"dataviewcfg.no":"否",
		"dataviewcfg.colsTooltip":"对应bootstrap中的col-{栅格列数}，自动代表为col自适应",
		"dataviewcfg.newLineTooltip":"下一个元素换行显示",
		"dataviewcfg.paramConfig":"参数配置",
		"dataviewcfg.config":"配置",
		"dataviewcfg.confirm":"确定",
		"dataviewcfg.required":"必填",
		"dataviewcfg.configDesc":"配置说明",
		"dataviewcfg.descTooltip":"提示信息，一般说明此配置项的使用场景、使用方式、如何配置等",
		"dataviewcfg.modifyOnAdd":"新增可修改",
		"dataviewcfg.modifyOnAddTooltip":"新增页面中，此配置项是否可修改，如不可修改则建议设置一个默认值",
		"dataviewcfg.modifyOnEdit":"编辑可修改",
		"dataviewcfg.modifyOnEditTooltip":"修改页面中，此配置项是否可修改",
		"dataviewcfg.valType":"值类型",
		"dataviewcfg.valTypeTooltip":"string类型：元素值获取到后直接使用；json类型：元素值转换为json对象后使用",
		"dataviewcfg.defaultVal":"默认值",
		"dataviewcfg.defaultValTooltip":"新增时的默认填写值",
		"dataviewcfg.validateRule":"校验规则",
		"dataviewcfg.validateRuleTooltip":'校验规则，基于jquery validate；参考格式为：[{"type":"maxlength","value":"5","message":"最大长度为5"}]',
		"dataviewcfg.source":"需求来源",
		"dataviewcfg.sourcePm":"产品",
		"dataviewcfg.sourceDev":"研发",
		"dataviewcfg.sourceTooltip":"配置参数需求来源，暂无作用",
		"dataviewcfg.valProcess":"值处理",
		"dataviewcfg.key":"键值",
		"dataviewcfg.keyPath":"键值路径",
		"dataviewcfg.self":"自定义",
		"dataviewcfg.valProcessTooltip":"如何将元素值与json配置映射，键值：映射到json的一级元素；键值路径：映射到json的一级元素中的某个jsonpath，如$.custNo；自定义：其他方式",
		"dataviewcfg.path":"路径",
		"dataviewcfg.script":"脚本",
		"dataviewcfg.beforeRender":"渲染前",
		"dataviewcfg.beforeRenderTooltip":"高级自定义用法，元素渲染前的处理方法，javascript代码",
		"dataviewcfg.afterRender":"渲染后",
		"dataviewcfg.afterRenderTooltip":"高级自定义用法，元素渲染后的处理方法，javascript代码",
		"dataviewcfg.extConfig":"扩展配置",
		"dataviewcfg.extConfigTooltip":"高级自定义用法，javascript对象，对象中的属性会覆盖或追加到配置数据中，key值不能为ext",
		"dataviewcfg.selfScript":"自定义脚本",
		"dataviewcfg.selectType":"类型",
		"dataviewcfg.single":"单选",
		"dataviewcfg.multiple":"多选",
		"dataviewcfg.seqMultipe":"顺序多选",
		"dataviewcfg.selectValType":"选择值类型",
		"dataviewcfg.default":"默认",
		"dataviewcfg.commaSep":"逗号隔开的字符串",
		"dataviewcfg.optionName":"选项名称",
		"dataviewcfg.optionValue":"选项值",
		"dataviewcfg.addConfigItem":"新增配置项",
		"dataviewcfg.add":"增加",
		"dataviewcfg.fileCount":"文件个数",
		"dataviewcfg.singleFile":"单文件",
		"dataviewcfg.multipleFile":"多文件",
		
		"dataviewapp.viewChange":"视图切换",
		"dataviewapp.dataView":"数据查看",
		"dataviewapp.dataMap":"映射开关",
		"dataviewapp.dataMapTtitle":"映射开关(点击文本展示数据路径)",
		"dataviewapp.viewChangeTitle":"视图模板切换",
		"dataviewapp.dataView":"数据查看",
		"dataviewapp.currentData":"当前数据",
		"dataviewapp.oldData":"旧数据",
		"dataviewapp.dataCompare":"数据比对",
		"dataviewapp.dataRender":"数据渲染",
		"dataviewapp.save":"提交保存",
		"dataviewapp.close":"关闭",
		"dataviewapp.newOldDataCompare":"新旧数据比较",
		"dataviewapp.dataItem":"数据项",
		"dataviewapp.originalVal":"原始值",
		"dataviewapp.afterVal":"修改后值",
		"dataviewapp.dataKey":"数据键值",
		"dataviewapp.keyPath":"键值路径",
		"dataviewapp.selectCheck1":"下拉选项不全,请到视图配置模板中添加. 名称",
		"dataviewapp.selectCheck2":"键值",
		"dataviewapp.selectCheck3":"选项列表",
		"dataviewapp.selectCheck4":"当前值",
		"dataviewapp.notJsonFormat":"数据非JSON格式",
		"dataviewapp.enable":"启用",
		"dataviewapp.diable":"禁用",
		
		});
}
})(window.J);