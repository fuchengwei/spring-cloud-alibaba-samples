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

/**
 * @author fuchengwei
 * @date 2021/4/27 9:55 上午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "business")
@ApiModel(description = "业务表")
public class Business implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("业务Id")
    private Integer businessId;

    /**
     * 表单内容
     */
    @ApiModelProperty("表单内容")
    private String content;

    /**
     * 表单值
     */
    @ApiModelProperty("表单值")
    private String value;
}
