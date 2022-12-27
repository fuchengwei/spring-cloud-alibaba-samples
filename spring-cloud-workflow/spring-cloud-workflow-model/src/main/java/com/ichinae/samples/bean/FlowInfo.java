package com.ichinae.samples.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author fuchengwei
 * @date 2021/4/23 5:01 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "flow_info")
@ApiModel(description = "流程信息")
public class FlowInfo implements Serializable {
    /**
     * 流程key
     */
    @Id
    @ApiModelProperty("流程key")
    private String flowKey;

    /**
     * 流程类型Id
     */
    @ApiModelProperty("流程类型Id")
    private Integer typeId;

    /**
     * 表单Id
     */
    @ApiModelProperty("表单Id")
    private Integer formId;

    /**
     * 图标名称
     */
    @ApiModelProperty("图标名称")
    private String icon;

    /**
     * 类型信息
     */
    private FlowType flowType;

    /**
     * 表单信息
     */
    private Form form;
}
