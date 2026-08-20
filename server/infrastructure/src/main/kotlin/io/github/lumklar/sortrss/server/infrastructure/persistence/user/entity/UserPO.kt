package io.github.lumklar.sortrss.server.infrastructure.persistence.user.entity;

import io.github.lumklar.sortrss.common.domain.model.user.RegistrationSource
import io.github.lumklar.sortrss.server.infrastructure.persistence.user.convert.RegistrationSourceConverter
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "users",
    uniqueConstraints = [UniqueConstraint(columnNames = ["username"])]
)
class UserPO(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Column(name = "username", nullable = false, length = 255)
    var username: String? = null,

    @Column(name = "password", nullable = true, length = 255)
    var password: String? = null,

    // 注册来源：使用枚举 + 转换器
    @Convert(converter = RegistrationSourceConverter::class)
    @Column(name = "registration_source", nullable = false, length = 32)
    var registrationSource: RegistrationSource? = null,

    @CreationTimestamp
    @Column(name = "gmt_create", nullable = false, updatable = false)
    var gmtCreate: Instant? = null,

    @UpdateTimestamp
    @Column(name = "gmt_modify", nullable = false)
    var gmtModify: Instant? = null
)