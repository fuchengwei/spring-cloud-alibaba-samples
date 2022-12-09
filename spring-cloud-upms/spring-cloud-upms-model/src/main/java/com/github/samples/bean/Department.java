package com.github.samples.bean;

import java.util.List;

/**
 * @author ChengWei.Fu
 * @date 2022/12/8
 */
public class Department extends Base {
    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 上级id
     */
    private Integer parentId;

    /**
     * 子节点信息
     */
    private List<Department> children;
}
