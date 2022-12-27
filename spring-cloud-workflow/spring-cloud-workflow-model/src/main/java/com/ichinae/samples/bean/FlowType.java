package com.ichinae.samples.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author fuchengwei
 * @date 2021/4/22 2:24 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "flow_type")
@ApiModel(description = "流程类型对象")
public class FlowType implements Serializable {
    /**
     * 流程类型Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("流程类型Id")
    private Integer typeId;

    /**
     * 流程类型名称
     */
    @ApiModelProperty("流程类型名称")
    private String typeName;

    /**
     * 流程类型描述
     */
    @ApiModelProperty("流程类型描述")
    private String description;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
