package com.jd.easyflow.process.adapter.export;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import com.jd.easyflow.action.Action;
import com.jd.easyflow.common.adapter.export.dto.ExportRequest;
import com.jd.easyflow.common.adapter.export.dto.ExportResponse;
import com.jd.easyflow.common.adapter.export.dto.ExportResponseCode;
import com.jd.easyflow.common.adapter.export.dto.pager.PagerCondition;
import com.jd.easyflow.common.adapter.export.dto.pager.PagerResult;
import com.jd.easyflow.process.adapter.export.converter.PagerConverter;
import com.jd.easyflow.process.adapter.export.converter.ProcessInstanceConverter;
import com.jd.easyflow.process.adapter.export.dto.instance.CanCancelProcessInstanceReq;
import com.jd.easyflow.process.adapter.export.dto.instance.CanCancelProcessInstanceRes;
import com.jd.easyflow.process.adapter.export.dto.instance.CancelProcessInstanceReq;
import com.jd.easyflow.process.adapter.export.dto.instance.CancelProcessInstanceRes;
import com.jd.easyflow.process.adapter.export.dto.instance.CreateProcessInstanceReq;
import com.jd.easyflow.process.adapter.export.dto.instance.CreateProcessInstanceRes;
import com.jd.easyflow.process.adapter.export.dto.instance.LockProcessInstanceReq;
import com.jd.easyflow.process.adapter.export.dto.instance.ProcessInstanceDTO;
import com.jd.easyflow.process.adapter.export.dto.instance.ProcessNodeExecutionDTO;
import com.jd.easyflow.process.adapter.export.dto.instance.ProcessNodeInstanceDTO;
import com.jd.easyflow.process.adapter.export.dto.instance.QueryOpenNodeInstanceReq;
import com.jd.easyflow.process.adapter.export.dto.instance.QueryProcessInstanceReq;
import com.jd.easyflow.process.adapter.export.dto.instance.QueryProcessNodeReqDTO;
import com.jd.easyflow.process.adapter.export.dto.instance.UnlockProcessInstanceReq;
import com.jd.easyflow.process.domain.model.entity.ProcessInstanceEntity;
import com.jd.easyflow.process.domain.model.entity.ProcessNodeExecutionEntity;
import com.jd.easyflow.process.domain.model.entity.ProcessNodeInstanceEntity;
import com.jd.easyflow.process.domain.model.vo.CreateProcessInstanceReqVO;
import com.jd.easyflow.process.domain.model.vo.CreateProcessInstanceResVO;
import com.jd.easyflow.process.domain.model.vo.QueryProcessNodeReq;
import com.jd.easyflow.process.domain.repository.ProcessRepository;
import com.jd.easyflow.process.domain.service.ProcessInstanceDomainService;

/**
 * @author liyuliang5
 */
public class ProcessInstanceExportImpl implements ProcessInstanceExport {
    
    private static final Logger log = LoggerFactory.getLogger(ProcessInstanceExportImpl.class);


    @Autowired
    private ProcessInstanceDomainService processInstanceDomainService;

    @Autowired
    private ProcessRepository processRepository;

    @Action(code = "easyflow-process-0201", name = "createProcessInstance")
    public ExportResponse<CreateProcessInstanceRes> createProcessInstance(ExportRequest<CreateProcessInstanceReq> req) {
        log.info("Start create process instance , req:{}", req);
        CreateProcessInstanceReqVO vo = ProcessInstanceConverter.INSTANCE.convert(req.getData());
        CreateProcessInstanceResVO resVo = processInstanceDomainService.createProcessInstance(vo);
        log.info("End create process instance");
        CreateProcessInstanceRes res = new CreateProcessInstanceRes();
        res.setProcessInstanceNo(resVo.getProcessInstanceNo());
        return ExportResponse.build4Success(res);
    }


    @Action(code = "easyflow-process-0202", name = "getProcessInstance")
    @Override
    public ExportResponse<ProcessInstanceDTO> getProcessInstance(ExportRequest<String> req) {
        ProcessInstanceEntity entity = getTransactionTemplate().execute(status -> {
            return processInstanceDomainService.queryProcessInstance(req.getData());
        });
        ProcessInstanceDTO dto = ProcessInstanceConverter.INSTANCE.convert(entity);
        return ExportResponse.build4Success(dto);
    }

    @Action(code = "easyflow-process-0203", name = "pagerQueryProcessInstance")
    @Override
    public ExportResponse<PagerResult> pagerQueryProcessInstance(ExportRequest<PagerCondition> req) {
        com.jd.easyflow.common.dto.pager.PagerCondition condition = PagerConverter.INSTANCE.convert(req.getData());
        com.jd.easyflow.common.dto.pager.PagerResult result = getTransactionTemplate().execute(status -> {
            return processInstanceDomainService.pageQueryProcessInstance(condition);
        });
        PagerResult exportResult = new PagerResult<>();
        exportResult.setCount(result.getCount());
        exportResult.setList(ProcessInstanceConverter.INSTANCE.convert(result.getList()));
        return ExportResponse.build4Success(exportResult);
    }

    @Action(code = "easyflow-process-0204", name = "queryProcessNodeInstanceByInstanceNo")
    @Override
    public ExportResponse<List<ProcessNodeInstanceDTO>> queryProcessNodeInstanceByInstanceNo(
            ExportRequest<String> req) {
        ExportResponse response;
        String instanceNo = req.getData();
        if (StringUtils.isBlank(instanceNo)) {
            response = ExportResponse.build4Failed(ExportResponseCode.INVALID);
            return response;
        }
        List<ProcessNodeInstanceEntity> processNodeInstanceList = getTransactionTemplate().execute(status -> {
            return processInstanceDomainService.queryProcessInstanceNode(instanceNo);
        });
        return ExportResponse.build4Success(ProcessInstanceConverter.INSTANCE.convertDTOList(processNodeInstanceList));
    }

    @Action(code = "easyflow-process-0205", name = "queryProcessInstanceByProcessTypeAndBizNo")
    @Override
    public ExportResponse<ProcessInstanceDTO> queryProcessInstanceByProcessTypeAndBizNo(
            ExportRequest<QueryProcessInstanceReq> req) {
        QueryProcessInstanceReq query = req.getData();
        String processType = query.getProcessType();
        String bizNo = query.getBizNo();
        ExportResponse response;
        if (StringUtils.isAnyBlank(processType, bizNo)) {
            response = ExportResponse.build4Failed(ExportResponseCode.INVALID);
            return response;
        }
        ProcessInstanceEntity processInstance = getTransactionTemplate().execute(status -> {
            return processRepository.getProcessInstanceByProcessTypeAndBizNo(processType, bizNo);
        });
        if (processInstance == null) {
            return ExportResponse.build4Success(null);
        }
        return ExportResponse.build4Success(ProcessInstanceConverter.INSTANCE.convert(processInstance));
    }

    @Action(code = "easyflow-process-0206", name = "lockProcessInstance")
    @Override
    public ExportResponse<String> lockProcessInstance(ExportRequest<LockProcessInstanceReq> req) {
        String requestId = processInstanceDomainService.lockProcessInstance(req.getData().getProcessType(),
                req.getData().getBizNo());
        return ExportResponse.build4Success(requestId);
    }

    @Action(code = "easyflow-process-0207", name = "unLockProcessInstance")
    @Override
    public ExportResponse<Boolean> unLockProcessInstance(ExportRequest<UnlockProcessInstanceReq> req) {
        Boolean result = processInstanceDomainService.unLockProcessInstance(req.getData().getProcessType(),
                req.getData().getBizNo(), req.getData().getLockRequestId());
        return ExportResponse.build4Success(result);
    }

    @Action(code = "easyflow-process-0209", name = "queryOpenNodeInstance")
    @Override
    public ExportResponse<ProcessNodeInstanceDTO> queryOpenNodeInstance(
            ExportRequest<QueryOpenNodeInstanceReq> request) {
        QueryOpenNodeInstanceReq queryReq = request.getData();
        if (StringUtils.isAnyBlank(queryReq.getInstanceNo(), queryReq.getNodeId())) {
            return ExportResponse.build4Failed(ExportResponseCode.FIELD_EMPTY);
        }
        ProcessNodeInstanceEntity nodeInstance = getTransactionTemplate().execute(status -> {
            return processRepository.getOpenNodeInstance(queryReq.getInstanceNo(), queryReq.getNodeId());
        });
        ProcessNodeInstanceDTO nodeInstanceDTO = ProcessInstanceConverter.INSTANCE.convert(nodeInstance);
        return ExportResponse.build4Success(nodeInstanceDTO);
    }

    @Action(code = "easyflow-process-0210", name = "findNodeInstances")
    @Override
    public ExportResponse<List<ProcessNodeInstanceDTO>> findNodeInstances(
            ExportRequest<QueryProcessNodeReqDTO> queryReq) {
        QueryProcessNodeReqDTO reqDTO = queryReq.getData();
        if (StringUtils.isAnyBlank(reqDTO.getProcessInstanceNo())) {
            return ExportResponse.build4Failed(ExportResponseCode.FIELD_EMPTY);
        }
        QueryProcessNodeReq queryProcessNodeReq = ProcessInstanceConverter.INSTANCE.convert(reqDTO);
        List<ProcessNodeInstanceEntity> nodeInstances = getTransactionTemplate().execute(status -> {
            return processRepository.findNodeInstances(queryProcessNodeReq);
        });
        List<ProcessNodeInstanceDTO> instanceDTOList = ProcessInstanceConverter.INSTANCE.convertDTOList(nodeInstances);
        return ExportResponse.build4Success(instanceDTOList);
    }

    @Action(code = "easyflow-process-0212", name = "queryNodeInstanceByNo")
    @Override
    public ExportResponse<ProcessNodeInstanceDTO> queryNodeInstanceByNo(ExportRequest<String> request) {
        if (StringUtils.isBlank(request.getData())) {
            return ExportResponse.build4Failed(ExportResponseCode.FIELD_EMPTY);
        }
        ProcessNodeInstanceEntity nodeInstanceEntity = getTransactionTemplate().execute(status -> {
            return processRepository.getByNodeInstanceNo(request.getData());
        });
        ProcessNodeInstanceDTO nodeInstanceDTO = ProcessInstanceConverter.INSTANCE.convert(nodeInstanceEntity);
        return ExportResponse.build4Success(nodeInstanceDTO);
    }

    @Action(code = "easyflow-process-0213", name = "queryNodeExecutionByNo")
    @Override
    public ExportResponse<ProcessNodeExecutionDTO> queryNodeExecutionByNo(ExportRequest<String> request) {
        if (StringUtils.isBlank(request.getData())) {
            return ExportResponse.build4Failed(ExportResponseCode.FIELD_EMPTY);
        }
        ProcessNodeExecutionEntity executionEntity = getTransactionTemplate().execute(status -> {
            return processRepository.getByNodeExecutionNo(request.getData());
        });
        ProcessNodeExecutionDTO executionDTO = ProcessInstanceConverter.INSTANCE.convert(executionEntity);
        return ExportResponse.build4Success(executionDTO);
    }

    @Action(code = "easyflow-process-0214", name = "updateProcessInstance")
    @Override
    public ExportResponse<Object> updateProcessInstance(ExportRequest<ProcessInstanceDTO> request) {
        ProcessInstanceEntity entity = ProcessInstanceConverter.INSTANCE.convert(request.getData());
        getTransactionTemplate().executeWithoutResult(status -> {
            processInstanceDomainService.updateProcessInstanceExtData(entity);
        });
        return ExportResponse.build4Success();
    }

    @Action(code = "easyflow-process-0215", name = "queryActiveProcessInstanceByProcessTypeAndBizNo")
    @Override
    public ExportResponse<ProcessInstanceDTO> queryActiveProcessInstanceByProcessTypeAndBizNo(
            ExportRequest<QueryProcessInstanceReq> req) {
        QueryProcessInstanceReq query = req.getData();
        String processType = query.getProcessType();
        String bizNo = query.getBizNo();
        ExportResponse response;
        if (StringUtils.isAnyBlank(processType, bizNo)) {
            response = ExportResponse.build4Failed(ExportResponseCode.INVALID);
            return response;
        }
        ProcessInstanceEntity processInstance = getTransactionTemplate().execute(status ->{
            return processRepository.getActiveProcessInstanceByProcessTypeAndBizNo(processType, bizNo);
        });
        if (processInstance == null) {
            return ExportResponse.build4Success(null);
        }
        return ExportResponse.build4Success(ProcessInstanceConverter.INSTANCE.convert(processInstance));
    }

    @Action(code = "easyflow-process-0216", name = "canCancel")
    @Override
    public ExportResponse<CanCancelProcessInstanceRes> canCancel(ExportRequest<CanCancelProcessInstanceReq> request) {
        CanCancelProcessInstanceReq canCancelProcessInstanceReq = request.getData();
        if (StringUtils.isAnyBlank(canCancelProcessInstanceReq.getInstanceNo(),
                canCancelProcessInstanceReq.getCancelUser())) {
            return ExportResponse.build4Failed(ExportResponseCode.FIELD_EMPTY);
        }
        CanCancelProcessInstanceRes canCancelProcessInstanceRes = getTransactionTemplate().execute(status -> {
            return processInstanceDomainService.canCancel(canCancelProcessInstanceReq);
        });
        return ExportResponse.build4Success(canCancelProcessInstanceRes);
    }

    @Action(code = "easyflow-process-0217", name = "cancel")
    @Override
    public ExportResponse<CancelProcessInstanceRes> cancel(ExportRequest<CancelProcessInstanceReq> request) {
        CancelProcessInstanceReq cancelProcessInstanceReq = request.getData();
        if (StringUtils.isAnyBlank(cancelProcessInstanceReq.getInstanceNo(),
                cancelProcessInstanceReq.getCancelUser())) {
            return ExportResponse.build4Failed(ExportResponseCode.FIELD_EMPTY);
        }
        processInstanceDomainService.cancel(cancelProcessInstanceReq);
        return ExportResponse.build4Success(new CancelProcessInstanceRes());
    }

    @Override
    @Action(code = "easyflow-process-0218", name = "queryInstanceByInstanceNos")
    public ExportResponse<List<ProcessInstanceDTO>> queryInstanceByInstanceNos(ExportRequest<List<String>> request) {
        List<String> instanceNos = request.getData();
        if (ObjectUtils.isEmpty(instanceNos)) {
            return ExportResponse.build4Failed(ExportResponseCode.FIELD_EMPTY);
        }
        List<ProcessInstanceDTO> processInstanceDTOS = getTransactionTemplate().execute(status -> {
            return processRepository.queryProcessInstanceByInstanceNos(instanceNos);
        });
        return ExportResponse.build4Success(processInstanceDTOS);
    }

    @Action(code = "easyflow-process-0219", name = "queryNodeInstanceByNos")
    @Override
    public ExportResponse<List<ProcessNodeInstanceDTO>> queryNodeInstanceByNos(ExportRequest<List<String>> request) {
        if (request.getData() == null || request.getData().isEmpty()) {
            return ExportResponse.build4Success(new ArrayList<>());
        }
        List<ProcessNodeInstanceEntity> nodeInstanceEntityList = getTransactionTemplate().execute(status -> {
            return processRepository.queryNodeInstanceByNos(request.getData());
        });
        List<ProcessNodeInstanceDTO> nodeInstanceDTO = ProcessInstanceConverter.INSTANCE.convertNodeInstanceList(nodeInstanceEntityList);
        return ExportResponse.build4Success(nodeInstanceDTO);
    }

    private TransactionTemplate getTransactionTemplate() {
        return processInstanceDomainService.getTransactionTemplate();
    }

    public ProcessInstanceDomainService getProcessInstanceDomainService() {
        return processInstanceDomainService;
    }

    public void setProcessInstanceDomainService(ProcessInstanceDomainService processInstanceDomainService) {
        this.processInstanceDomainService = processInstanceDomainService;
    }

    public ProcessRepository getProcessRepository() {
        return processRepository;
    }

    public void setProcessRepository(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }
    
    

}
