package org.chgthink.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chgthink.common.core.constant.SystemConstants;
import org.chgthink.common.core.domain.R;
import org.chgthink.common.core.domain.model.LoginBody;
import org.chgthink.common.core.enums.PushSourceEnum;
import org.chgthink.common.core.enums.PushTypeEnum;
import org.chgthink.common.core.utils.DateUtils;
import org.chgthink.common.core.utils.MessageUtils;
import org.chgthink.common.core.utils.StringUtils;
import org.chgthink.common.core.utils.ValidatorUtils;
import org.chgthink.common.encrypt.annotation.ApiEncrypt;
import org.chgthink.common.json.utils.JsonUtils;
import org.chgthink.common.satoken.utils.LoginHelper;
import org.chgthink.system.api.MessageService;
import org.chgthink.system.api.domain.PushPayloadDTO;
import org.chgthink.system.api.model.RegisterBody;
import org.chgthink.system.domain.vo.SysClientVo;
import org.chgthink.system.service.ISysClientService;
import org.chgthink.system.service.ISysConfigService;
import org.chgthink.web.domain.vo.LoginVo;
import org.chgthink.web.service.IAuthStrategy;
import org.chgthink.web.service.SysLoginService;
import org.chgthink.web.service.SysRegisterService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 认证控制器，提供登录、注册和退出能力。
 *
 * @author Lion Li
 */
@Slf4j
@SaIgnore
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SysLoginService loginService;
    private final SysRegisterService registerService;
    private final ISysConfigService configService;
    private final ISysClientService clientService;
    private final ScheduledExecutorService scheduledExecutorService;
    private final MessageService messageService;


    /**
     * 登录方法
     *
     * @param body 登录信息
     * @return 结果
     */
    @ApiEncrypt
    @PostMapping("/login")
    public R<LoginVo> login(@RequestBody String body) {
        LoginBody loginBody = JsonUtils.parseObject(body, LoginBody.class);
        ValidatorUtils.validate(loginBody);
        // 授权类型和客户端id
        String clientId = loginBody.getClientId();
        String grantType = loginBody.getGrantType();
        SysClientVo client = clientService.queryByClientId(clientId);
        // 查询不到 client 或 client 内不包含 grantType
        if (ObjectUtil.isNull(client) || !StringUtils.contains(client.getGrantType(), grantType)) {
            log.info("客户端id: {} 认证类型：{} 异常!.", clientId, grantType);
            return R.fail(MessageUtils.message("auth.grant.type.error"));
        } else if (!SystemConstants.NORMAL.equals(client.getStatus())) {
            return R.fail(MessageUtils.message("auth.grant.type.blocked"));
        }
        // 登录
        LoginVo loginVo = IAuthStrategy.login(body, client, grantType);

        Long userId = LoginHelper.getUserId();
        scheduledExecutorService.schedule(() -> {
            messageService.publishMessage(
                List.of(userId),
                PushPayloadDTO.of(
                    PushTypeEnum.MESSAGE,
                    PushSourceEnum.BACKEND,
                    DateUtils.getTodayHour(new Date()) + "好，欢迎登录 ChgGuard 安全管控平台",
                    null
                )
            );
        }, 5, TimeUnit.SECONDS);
        return R.ok(loginVo);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        loginService.logout();
        return R.ok("退出成功");
    }

    /**
     * 用户注册。
     *
     * @param user 注册信息
     * @return 操作结果
     */
    @ApiEncrypt
    @PostMapping("/register")
    public R<Void> register(@Validated @RequestBody RegisterBody user) {
        if (!configService.selectRegisterEnabled()) {
            return R.fail("当前系统没有开启注册功能！");
        }
        registerService.register(user);
        return R.ok();
    }

}
