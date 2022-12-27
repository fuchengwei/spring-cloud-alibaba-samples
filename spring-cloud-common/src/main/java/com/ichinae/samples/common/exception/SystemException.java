package com.ichinae.samples.common.exception;

import com.ichinae.samples.common.enums.AuthExceptionEnum;
import com.ichinae.samples.common.enums.SystemExceptionEnum;
import com.ichinae.samples.common.enums.UpmsExceptionEnum;
import com.ichinae.samples.common.enums.WorkflowExceptionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author fuchengwei
 * @date 2020/12/16 10:53 上午
 * @description 系统异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemException extends RuntimeException {
    private Integer code;
    private String message;

    public SystemException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public SystemException(SystemExceptionEnum systemExceptionEnum) {
        this.code = systemExceptionEnum.getCode();
        this.message = systemExceptionEnum.getMessage();
    }

    public SystemException(UpmsExceptionEnum upmsExceptionEnum) {
        this.code = upmsExceptionEnum.getCode();
        this.message = upmsExceptionEnum.getMessage();
    }

    public SystemException(AuthExceptionEnum authExceptionEnum) {
        this.code = authExceptionEnum.getCode();
        this.message = authExceptionEnum.getMessage();
    }

    public SystemException(WorkflowExceptionEnum workflowExceptionEnum) {
        this.code = workflowExceptionEnum.getCode();
        this.message = workflowExceptionEnum.getMessage();
    }
}
