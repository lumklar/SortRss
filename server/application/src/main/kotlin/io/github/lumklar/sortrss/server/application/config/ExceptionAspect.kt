package io.github.lumklar.sortrss.server.application.config

import io.github.lumklar.sortrss.common.domain.shared.exception.DomainException
import io.github.lumklar.sortrss.server.application.exception.ApplicationException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

@Aspect
@Component
class AspectConfig {

    // 1. 定义切点：拦截 service 包下的所有方法
    @Pointcut("execution(* io.github.lumklar.sortrss.server.application.service.*.*(..))")
    fun serviceMethods() {}

    /**
     * 3. 环绕通知：捕获 DomainException 并包装为 ApplicationException
     *    只有抛出 DomainException 时才会被包装，其他异常原样抛出
     */
    @Around("serviceMethods()")
    fun wrapDomainException(joinPoint: ProceedingJoinPoint): Any? {
        return try {
            joinPoint.proceed()
        } catch (e: DomainException) {
            throw ApplicationException.from(e)
        }
    }
}