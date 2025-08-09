(function (J) {
    /**
     * Define panel control
     */
    J.FlowControl = function (cfg) {
        /**
         *Init
         */
        this.init = function (cfg) {
            this.cfg = cfg;
            this.data = cfg.data ? cfg.data : {};
            this.originData = JSON.parse(JSON.stringify(this.data));
            this.$container = cfg.$container;
            var _self = this;
            this.bpmnControl = new J.BpmnControl({
                data: {bpmnXmlData: this.data.bpmnXmlData},
                mode: cfg.mode,
                openDiagramCallBack: function (processElements) {
                    if (_self.$flowContainer.find("[name='format']").val() != 'FLOW-bpmn') {
                        return;
                    }
                    if (processElements.length==0) {
                        return;
                    }
                    _self.$flowContainer.find("[name='defId']").val(processElements[0].businessObject.id);
                    _self.$flowContainer.find("[name='defName']").val(processElements[0].businessObject.name);
                },
                onBpmnDefinitionChange: function (bo, key, newVal, oldVal) {
                    if (_self.$flowContainer.find("[name='format']").val()!='FLOW-bpmn') {
                        return;
                    }
                    if (bo.$type == 'bpmn:Process') {
						if (key == 'id' || key == 'name') {
							var processElements = this.findProcessElements();
							if (bo.id == processElements[0].businessObject.id) {
		                        if (key == 'id') {
		                            _self.$flowContainer.find("[name='defId']").val(newVal);
		                        } else if (key == 'name') {
		                            _self.$flowContainer.find("[name='defName']").val(newVal);
		                        }
							}
						}
                    }
                }
            });
        }
        /**
         * Render
         */
        this.render = async function () {
            var $flowContainer = this.$flowContainer = $(_html()).appendTo(this.$container);
            $flowContainer.find("[name='defId']").val(this.data.defId);
            $flowContainer.find("[name='defName']").val(this.data.defName);
            this.data.format && $flowContainer.find("[name='format']").val(this.data.format);
            this.data.bizType && $flowContainer.find("[name='bizType']").val(this.data.bizType);
            this.data.category && $flowContainer.find("[name='category']").val(this.data.category);
            if (this.cfg.mode == 'view') {
                $flowContainer.find("[name='format']").attr("disabled", true);
                $flowContainer.find("[name='bizType']").attr("disabled", true);
                $flowContainer.find("[name='category']").attr("disabled", true);
            }

            var _self = this;
            // Define type select
            $flowContainer.find('[name="format"]').change(function () {
                _self._renderTabs();
            });

            // Format select
            $flowContainer.find('[name="format"]').change(function() {_self._renderTabs();});
            // Show tab
            this._renderTabs();
            // Render BPMN panel
            await this._renderBpmnPanel();
            $(function(){
            $flowContainer.find(".bpmnContainer").addClass(" tab-pane");
            });
            // Render JSON Panel
            this._renderJsonPanel();
            return this;
        }
        this.init(cfg);
        return this;
    }

    /**
     * Render BPMN Panel
     */
    J.FlowControl.prototype._renderBpmnPanel = function () {
        var $bpmnContainer = this.$flowContainer.find(".bpmnContainer");
        this.bpmnControl.render($bpmnContainer);
    }

    /**
     * Render JSON Panel
     */
    J.FlowControl.prototype._renderJsonPanel = function () {
        this._renderJsonDataContainer();
        var _self = this;
        this.$flowContainer.find(".jsonContainer").find(".j-json-def").click(function () {
            var elementHtml = _self._jsonViewHtml();
            $.jDialog({
                title: J.msg['flow.jsonDefTitle'],
                size: "modal-lg",
                element: elementHtml,
                action: function ($dialog) {
                    $dialog.find(".jsonDef").val(_self.data.jsonData);
                    // Render buttons
                    $dialog.find(".j-btn-json-render").click(function () {
                        var dataStr = $dialog.find(".jsonDef").val();
                        var data;
                        try {
                           data =  JSON.parse(dataStr);
                        } catch (e) {
                            $.jMessage({msg:J.msg['jqueryValidate.json']});
                            return;
                        }
                        
                        _self.data.jsonData = dataStr;
                        _self._renderJsonDataContainer();
                        if (data instanceof Array) {
                            data = data[0];
                        }
                        _self.$flowContainer.find("[name='defId']").val(data.id);
                        _self.$flowContainer.find("[name='defName']").val(data.name);
                        $dialog.modal("hide");
                    });
                    // Compare
                    $dialog.find(".j-btn-json-compare").click(function () {
                        var newData = $dialog.find(".jsonDef").val();
                        var oldData = _self.originData.jsonData;
                        $.jDiffDialog({
                            left: {title: J.msg['flow.newFlowDef'] + ":", content: newData},
                            right: {title: J.msg['flow.oldFlowDef'] + ":", content: oldData}
                        });
                    });
                }
            });
        });

    }

    J.FlowControl.prototype._renderJsonDataContainer = function () {
        var $jsonContainer = this.$jsonContainer = this.$flowContainer.find(".jsonContainer");
        $jsonContainer.find(".jsonDataContainer").empty();
        var jsonData = this.data.jsonData;
        if (!jsonData) {
            $jsonContainer.find(".jsonDataContainer").append("<div>" + J.msg['flow.noJsonDataTip'] + "</div>");
            return;
        }
        var def = JSON.parse(jsonData);
        // Node panel
        var table = '<table class="table table-striped table-bordered"><thead><tr><th>' + J.msg['flow.id'] + '</th><th>' + J.msg['flow.nodeId'] + '</th><th>' + J.msg['flow.nodeName'] + '</th></tr></thead>';
        var format = this.$flowContainer.find("[name='format']").val();
        ;
        if (format == 'FSM-easy') {
			if (def.states) {
	            for (var i = 0; i < def.states.length; i++) {
	                table += "<tr><td>" + def.id + "</td><td>" + def.states[i].id + "</td><td>" + (def.states[i].name ? def.states[i].name : '') + "</td></tr>";
	            }
			}
        } else {
            if (def instanceof Array) {
             for (var j in def) {
				if (def[j].nodes) {
	              for (var i = 0; i < def[j].nodes.length; i++) {
	                table += "<tr><td>" + def[j].id + "</td><td>" + def[j].nodes[i].id + "</td><td>" + (def[j].nodes[i].name ? def[j].nodes[i].name : '') + "</td></tr>";
	              }
			  }
                }
            } else {
				if (def.nodes) {
	            for (var i = 0; i < def.nodes.length; i++) {
	                table += "<tr><td>" + def.id + "</td><td>" + def.nodes[i].id + "</td><td>" + (def.nodes[i].name ? def.nodes[i].name : '') + "</td></tr>";
	            }
			}
            }
        }
        table += "</table>";
        $jsonContainer.find(".jsonDataContainer").append(table);
    }


    /**
     * Render tab
     */
    J.FlowControl.prototype._renderTabs = function () {
        var format = this.$flowContainer.find('[name="format"]').val();
        if (format == 'FLOW-bpmn') {
            this.$flowContainer.find(".nav-bpmnContainer").show();
            this.$flowContainer.find(".nav-jsonContainer,.nav-plantUmlContainer").hide();
            this.$flowContainer.find(".nav-bpmnContainer").tab("show");
        } else {
            this.$flowContainer.find(".nav-bpmnContainer,.nav-jsonContainer").show();
            this.$flowContainer.find(".nav-plantUmlContainer").hide();
            this.$flowContainer.find(".nav-jsonContainer").tab("show");
        }
    };

    /**
     * Collect data
     */
    J.FlowControl.prototype.collect = async function () {
        this.data.defId = this.$flowContainer.find("[name='defId']").val();
        this.data.defName = this.$flowContainer.find("[name='defName']").val();
        this.data.bizType = this.$flowContainer.find("[name='bizType']").val();
        this.data.category = this.$flowContainer.find("[name='category']").val();
        this.data.format = this.$flowContainer.find("[name='format']").val();
        // JSON Data has been set
        this.data.bpmnXmlData = await this.bpmnControl.collect();
        // hack! judge by length
        if (this.data.bpmnXmlData.length < 1000) {
            this.data.bpmnXmlData = null;
        }
        return this.data;
    }


    var _html = function () {
        var bpmnContainerId = "bpmnContainer" + $.jSequence.next();
        var jsonContainerId = "jsonContainer" + $.jSequence.next();
        var bizTypeHtml = "";
        if (window.flowBizType) {
            for (var field in window.flowBizType) {
                bizTypeHtml += "<option value='" + field + "'>" + window.flowBizType[field] + "</option>";
            }
        }
        var categoryHtml = "";
        if (window.flowCategory) {
            for (var field in window.flowCategory) {
                categoryHtml += "<option value='" + field + "'>" + window.flowCategory[field] + "</option>";
            }
        }
            return  '    <div class=" container-fluid">                                                                   ' +
                '        <div class="row"> ' +
                '            <div class="form-group col">                                                             ' +
                '                <label>' + J.msg['flow.flowDefId'] + ':</label><input type="text" name="defId"                            ' +
                '                    class="form-control" readonly="readonly"/>                                                          ' +
                '            </div>                                                                                   ' +
                '            <div class="form-group col">                                                             ' +
                '                <label>' + J.msg['flow.flowDefName'] + ':</label><input type="text" name="defName"                            ' +
                '                    class="form-control" readonly="readonly"/>                                                          ' +
                '            </div>                                                                                   ' +
                '            <div class="form-group col">                                                             ' +
                '                <label>' + J.msg['flow.flowDefType'] + ':</label> <select class="form-control" name="format"' +
                '                         class="form-control"> ' +
                '                         <option value="FLOW-bpmn">' + J.msg['flow.flowDefType.flowBpmn'] + '</option>          ' +
                '                         <option value="FLOW-easy">' + J.msg['flow.flowDefType.flowJson'] + '</option>          ' +
                '                         <option value="FSM-easy">' + J.msg['flow.flowDefType.fsmJson'] + '</option>             ' +
                '                         </select>              ' +
                '            </div>                                                                                   ' +
                '            <div class="form-group col">                                                             ' +
                '                <label>' + J.msg['flow.bizType'] + ':</label> <select class="form-control" name="bizType"' +
                '                         class="form-control"/>                ' + bizTypeHtml +
                '                         </select>              ' +
                '            </div>                                                                                   ' +
                '            <div class="form-group col">                                                             ' +
                '                <label>' + J.msg['flow.category'] + ':</label> <select class="form-control" name="category"' +
                '                         class="form-control"/>                ' + categoryHtml +
                '                         </select>              ' +
                '            </div>                                                                                   ' +
                '        </div>                                                                                        '+
                '        <div>                                                                                        ' +
                '            <ul class="nav nav-tabs">                                                                ' +
                '                <li class="nav-item"><a class="nav-link nav-jsonContainer" data-toggle="tab"  data-target="#' + jsonContainerId + '">' + J.msg['flow.flowDefinition'] + '</a></li>          ' +
                '                <li class="nav-item"><a class="nav-link nav-bpmnContainer" data-toggle="tab" data-target="#' + bpmnContainerId + '">' + J.msg['flow.flowDiagram'] + '</a></li>                                        ' +
                '            </ul>                                                                                    ' +
                '            <div class="tab-content">                                                                ' +
                // bpmnContainer
                '                <div id="' + bpmnContainerId + '" class="bpmnContainer">                                       ' +
                '                </div>                                                                               ' +
                // jsonContainer
                '        <div id="' + jsonContainerId + '" class="jsonContainer tab-pane">                                                ' +
                '            <div class="row jsonDataContainer">                                                                         ' +
                '            </div>                                                                                    ' +
                '            <div class="row">                                                                         ' +
                '                <button type="button" class="btn btn-primary j-json-def">'+J.msg['flow.jsonDefinition'] + '</button>          ' +
                '            </div>                                                                                    ' +
                '        </div>                                                                                        ' +

                '            </div>                                                                                   ' +
                '</div>                                                                                                ' +
                '';
    }

    J.FlowControl.prototype._jsonViewHtml = function () {
        return '<div class="jsonDefContainer">                                                                                 ' +
            '    <div class="row">                                                                                          ' +
            '        <div class="form-group col">                                                                           ' +
            '            <label><span class="j-require">*</span>'+J.msg['flow.flowJsonDefinition'] + ':</label>                                       ' +
            '            <textarea class="jsonDef form-control" rows="30"></textarea>                                       ' +
            '        </div>                                                                                                 ' +
            '    </div>                                                                                                     ' +
            '    <div class="row">                                                                                          ' +
            '        <div class="col text-center">                                                                          ' +
            (this.cfg.mode == 'view' ? '' : '            <button type="button" class="btn btn-primary j-btn-json-render">'+J.msg['flow.update'] + '</button>              ') +
            (this.cfg.mode != 'edit' ? '' : '            <button type="button" class="btn btn-secondary j-btn-json-compare">'+J.msg['flow.oldNewCompare'] + '</button>              ') +
            '            <button type="button" class="btn btn-secondary j-btn-cancel">'+J.msg['flow.cancel'] + '</button>                         ' +
            '        </div>                                                                                                 ' +
            '    </div>                                                                                                     ' +
            '</div>                                                                                                         ' +
            '';
    }
})(window.J);    