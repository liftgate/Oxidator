package io.liftgate.oxidator.configuration.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException

/**
 * @author GrowlyX
 * @since 6/5/2024
 */
@RestControllerAdvice
class ControllerAdvise
{
    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun noHandlerFoundHandler(exception: NoHandlerFoundException, request: WebRequest) = mutableMapOf(
        "status" to "no handler found",
        "message" to exception.localizedMessage
    )

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun internalError(exception: Exception, request: WebRequest) = mutableMapOf(
        "status" to "internal error",
        "message" to exception.localizedMessage
    )
}
