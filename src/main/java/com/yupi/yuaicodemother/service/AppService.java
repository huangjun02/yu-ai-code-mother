package com.yupi.yuaicodemother.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.yupi.yuaicodemother.model.dto.app.AppQueryRequest;
import com.yupi.yuaicodemother.model.entity.App;
import com.yupi.yuaicodemother.model.vo.AppVO;

import java.util.List;

/**
 * 应用 服务层。
 *
 */
public interface AppService extends IService<App> {

    /**
     * 获取脱敏的应用信息
     *
     * @param app 应用
     * @return 脱敏后的应用视图
     */
    AppVO getAppVO(App app);

    /**
     * 获取脱敏的应用信息列表
     *
     * @param appList 应用列表
     * @return 脱敏后的应用视图列表
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 获取查询条件
     *
     * @param appQueryRequest 查询请求
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);
}
