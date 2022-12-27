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
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/19 5:03 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "department")
@ApiModel(description = "部门实体")
public class Department implements Serializable {
    /**
     * 部门Id
     */
    @Id
    @ApiModelProperty("部门Id")
    private String departmentId;

    /**
     * 部门名称
     */
    @ApiModelProperty("部门名称")
    private String departmentName;

    /**
     * 部门描述
     */
    @ApiModelProperty("部门描述")
    private String description;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer sort;

    /**
     * 上级Id
     */
    @ApiModelProperty("上级Id")
    private String superiorId;

    /**
     * 子节点信息
     */
    @ApiModelProperty("子节点信息")
    private List<Department> children;

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
