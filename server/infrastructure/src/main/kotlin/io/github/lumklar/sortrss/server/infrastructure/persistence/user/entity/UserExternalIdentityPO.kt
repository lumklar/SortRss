package io.github.lumklar.sortrss.server.infrastructure.persistence.user.entity

import io.github.lumklar.sortrss.common.domain.model.user.ExternalProvider
import io.github.lumklar.sortrss.server.infrastructure.persistence.user.convert.ExternalProviderConverter
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "user_external_identities",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_provider_subject", columnNames = ["provider", "subject"]),
        UniqueConstraint(name = "uk_user_provider", columnNames = ["user_id", "provider"])
    ]
)
class UserExternalIdentityPO(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Column(name = "user_id", nullable = false)
    var userId: UUID? = null,

    @Convert(converter = ExternalProviderConverter::class)
    @Column(name = "provider", nullable = false, length = 32)
    var provider: ExternalProvider? = null,

    @Column(name = "subject", nullable = false, length = 255)
    var subject: String? = null,

    @CreationTimestamp
    @Column(name = "gmt_create", nullable = false, updatable = false)
    var gmtCreate: Instant? = null
)