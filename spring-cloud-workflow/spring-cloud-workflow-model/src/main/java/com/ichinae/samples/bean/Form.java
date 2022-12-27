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
 * @date 2021/4/21 1:41 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "form")
@ApiModel(description = "表单对象")
public class Form implements Serializable {
    /**
     * 表单Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("表单Id")
    private Integer formId;

    /**
     * 表单名称
     */
    @ApiModelProperty("表单名称")
    private String formName;

    /**
     * 表单描述
     */
    @ApiModelProperty("表单描述")
    private String description;

    /**
     * 表单内容
     */
    @ApiModelProperty("表单内容")
    private String content;

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
