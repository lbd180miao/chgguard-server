package org.chgthink.system.service;

import org.chgthink.system.domain.vo.SysClientVo;


/**
 * 登录认证客户端查询接口
 *
 * @author Michelle.Chung
 * @date 2023-06-18
 */
public interface ISysClientService {

    /**
     * 查询客户端信息基于客户端id
     */
    SysClientVo queryByClientId(String clientId);
}
