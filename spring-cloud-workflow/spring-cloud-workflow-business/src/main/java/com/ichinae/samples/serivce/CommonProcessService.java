package com.ichinae.samples.serivce;

import com.ichinae.samples.bean.CommonProcess;

import java.util.List;

/**
 * @author fuchengwei
 * @date 2021/4/26 3:16 下午
 */
public interface CommonProcessService {
    /**
     * 获取指定用户Id的常用流程
     *
     * @param userId 用户Id
     * @return List<CommonProcess>
     */
    List<CommonProcess> getCommonProcesses(Integer userId);

    /**
     * 添加常用流程
     *
     * @param userId 用户Id
     * @param keys   流程keys
     */
    void insertCommonProcess(Integer userId, String[] keys);
}
