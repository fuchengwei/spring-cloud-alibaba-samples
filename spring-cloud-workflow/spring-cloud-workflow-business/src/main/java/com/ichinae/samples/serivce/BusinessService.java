package com.ichinae.samples.serivce;

import com.ichinae.samples.bean.Business;

/**
 * @author fuchengwei
 * @date 2021/4/27 9:59 上午
 */
public interface BusinessService {
    /**
     * 查询业务信息
     *
     * @param businessId 业务Id
     * @return Business
     */
    Business getBusinessByKey(String businessId);

    /**
     * 添加业务
     *
     * @param business 业务对象
     */
    void insertBusiness(Business business);
}
