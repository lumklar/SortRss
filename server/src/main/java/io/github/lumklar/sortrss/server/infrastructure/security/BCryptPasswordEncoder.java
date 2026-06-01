package io.github.lumklar.sortrss.server.infrastructure.security;

import io.github.lumklar.sortrss.common.domain.service.PasswordEncoder;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoder implements PasswordEncoder {

    /**
     * 对原始密码进行 BCrypt 哈希编码，自动生成随机盐并包含在结果中。
     *
     * @param rawPassword 明文密码，不能为空
     * @return 格式为 $2a$10$... 的 BCrypt 哈希字符串
     */
    @Override
    @NotNull
    public String encode(@NotNull String rawPassword) {
        // 生成随机盐，默认强度 10（可配置，但保持与 matches 一致即可）
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(rawPassword, salt);
    }

    /**
     * 校验明文密码与已编码的 BCrypt 哈希值是否匹配。
     *
     * @param rawPassword     明文密码
     * @param encodedPassword BCrypt 哈希字符串（应包含盐和版本信息）
     * @return 匹配返回 true，否则 false
     */
    @Override
    public boolean matches(@NotNull String rawPassword, @NotNull String encodedPassword) {
        // BCrypt.checkpw 会从 encodedPassword 中提取盐并完成校验
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
