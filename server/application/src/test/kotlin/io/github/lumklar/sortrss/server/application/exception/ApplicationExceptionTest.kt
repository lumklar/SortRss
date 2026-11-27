package io.github.lumklar.sortrss.server.application.exception

import io.github.lumklar.sortrss.common.api.dto.ErrorSource
import io.github.lumklar.sortrss.common.domain.model.user.UserErrorCode
import io.github.lumklar.sortrss.common.domain.shared.exception.DomainException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class ApplicationExceptionTest {

    @Test
    fun `from domain exception uses application code and keeps domain code for tracing`() {
        val domain = DomainException(UserErrorCode.USER_NOT_EXIST)

        val app = ApplicationException.from(domain)

        assertEquals(ApplicationErrorCode.DOMAIN_REJECTED.code, app.code)
        assertEquals(UserErrorCode.USER_NOT_EXIST.code, app.domainCodeValue)
        assertSame(UserErrorCode.USER_NOT_EXIST, app.domainCode)
        assertEquals(ExceptionType.DOMAIN, app.type)
        assertSame(domain, app.cause)
    }

    @Test
    fun `default exception carries unknown application code without domain code`() {
        val app = ApplicationException()

        assertEquals(ApplicationErrorCode.UNKNOWN.code, app.code)
        assertNull(app.domainCodeValue)
        assertEquals(ExceptionType.UNKNOWN, app.type)
    }

    @Test
    fun `exception type maps to error source`() {
        assertEquals(ErrorSource.DOMAIN, ExceptionType.DOMAIN.toErrorSource())
        assertEquals(ErrorSource.DOMAIN, ExceptionType.APPLICATION.toErrorSource())
        assertEquals(ErrorSource.SYSTEM, ExceptionType.INFRASTRUCTURE.toErrorSource())
        assertEquals(ErrorSource.SYSTEM, ExceptionType.UNKNOWN.toErrorSource())
    }
}
