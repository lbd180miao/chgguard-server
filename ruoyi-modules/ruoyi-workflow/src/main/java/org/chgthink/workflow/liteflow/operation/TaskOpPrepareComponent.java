package org.chgthink.workflow.liteflow.operation;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import org.chgthink.common.core.exception.ServiceException;
import org.chgthink.common.core.utils.ValidatorUtils;
import org.chgthink.common.core.validate.AddGroup;
import org.chgthink.common.core.validate.EditGroup;
import org.chgthink.common.satoken.utils.LoginHelper;
import org.dromara.warm.flow.core.workflow.context.OperatorContext;
import org.chgthink.workflow.common.ConditionalOnEnable;
import org.chgthink.workflow.common.enums.TaskOperationEnum;
import org.chgthink.workflow.domain.bo.TaskOperationBo;
import org.chgthink.workflow.domain.context.TaskOperationContext;

/**
 * 准备任务操作参数。
 *
 * @author may
 */
@ConditionalOnEnable
@Slf4j
@LiteflowComponent("taskOpPrepare")
public class TaskOpPrepareComponent extends NodeComponent {

    @Override
    public void process() {
        TaskOperationContext context = getContextBean(TaskOperationContext.class);
        TaskOperationEnum op = TaskOperationEnum.getByCode(context.getTaskOperation());
        if (op == null) {
            log.error("Invalid operation type:{} ", context.getTaskOperation());
            throw new ServiceException("Invalid operation type " + context.getTaskOperation());
        }
        context.setOperation(op);

        TaskOperationBo bo = context.getTaskOperationBo();
        switch (op) {
            case DELEGATE_TASK, TRANSFER_TASK -> ValidatorUtils.validate(bo, AddGroup.class);
            case ADD_SIGNATURE, REDUCTION_SIGNATURE -> ValidatorUtils.validate(bo, EditGroup.class);
        }

        OperatorContext operator = new OperatorContext(LoginHelper.getUserIdStr());
        operator.setIgnore(LoginHelper.isSuperAdmin());
        context.setOperator(operator);
        context.setMessage(bo.getMessage());
    }

}
