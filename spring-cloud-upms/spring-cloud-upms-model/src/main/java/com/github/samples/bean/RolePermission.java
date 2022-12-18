package com.github.samples.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ChengWei.Fu
 * @date 2022/12/12
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RolePermission implements Serializable {
    /**
     * 主键id
     */
    private String id;

    /**
     * 角色 id
     */
    private String roleId;

    /**
     * 权限 id
     */
    private String permissionId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
