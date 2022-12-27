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
 * @date 2021/4/26 3:09 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "common_process")
@ApiModel(description = "常用流程")
public class CommonProcess implements Serializable {
    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("id")
    private Integer id;

    /**
     * 用户Id
     */
    @ApiModelProperty("用户Id")
    private Integer userId;

    /**
     * 流程Key
     */
    @ApiModelProperty("流程Key")
    private String flowKey;
}
