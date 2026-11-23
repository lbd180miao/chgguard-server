package org.chgthink.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.chgthink.common.core.constant.CacheNames;
import org.chgthink.system.domain.SysClient;
import org.chgthink.system.domain.vo.SysClientVo;
import org.chgthink.system.mapper.SysClientMapper;
import org.chgthink.system.service.ISysClientService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 登录认证客户端查询服务
 *
 * @author Michelle.Chung
 * @date 2023-06-18
 */
@RequiredArgsConstructor
@Service
public class SysClientServiceImpl implements ISysClientService {

    private final SysClientMapper clientMapper;

    /**
     * 按客户端标识查询登录认证配置。
     *
     * @param clientId 客户端标识
     * @return 客户端认证配置
     */
    @Cacheable(cacheNames = CacheNames.SYS_CLIENT, key = "#clientId")
    @Override
    public SysClientVo queryByClientId(String clientId) {
        return clientMapper.lambda().eq(SysClient::getClientId, clientId).voOne();
    }
}
