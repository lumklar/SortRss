package io.github.lumklar.sortrss.common.domain.model.user

enum class ExternalProvider {
    QQ,
    WECHAT,
    WEIBO,
    GOOGLE
}

fun ExternalProvider.toRegistrationSource(): RegistrationSource = RegistrationSource.EXTERNAL
//    when (this) {
//        ExternalProvider.QQ -> RegistrationSource.EXTERNAL
//        ExternalProvider.WECHAT -> RegistrationSource.EXTERNAL
//        ExternalProvider.WEIBO -> RegistrationSource.EXTERNAL
//        ExternalProvider.GOOGLE -> RegistrationSource.EXTERNAL
//    }