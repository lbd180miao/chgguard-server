package org.chgthink.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.chgthink.system.domain.SysClient;

import java.io.Serial;
import java.io.Serializable;


/**
 * 登录认证客户端配置 sys_client
 *
 * @author Michelle.Chung
 * @date 2023-05-15
 */
@Data
@AutoMapper(target = SysClient.class)
public class SysClientVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 客户端key
     */
    private String clientKey;

    /**
     * 客户端秘钥
     */
    private String clientSecret;

    /**
     * 授权类型
     */
    private String grantType;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 允许访问路径
     */
    private String accessPath;

    /**
     * IP白名单
     */
    private String ipWhitelist;

    /**
     * token活跃超时时间
     */
    private Long activeTimeout;

    /**
     * token固定超时时间
     */
    private Long timeout;

    /**
     * 状态（0正常 1停用）
     */
    private String status;


}
