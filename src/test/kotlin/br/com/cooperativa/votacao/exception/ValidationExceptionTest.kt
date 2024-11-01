package br.com.cooperativa.votacao.exception

import br.com.cooperativa.votacao.util.validateList
import br.com.cooperativa.votacao.utils.buildAgenda
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ValidationExceptionTest {

    @Test
    fun `should build exception`() {
        val obj = buildAgenda(id="", topic="", description = "")
        val constrains = validateList(obj)
        val ex = ValidationException(constraints = constrains)
        val expectedErrorMessage = constrains.joinToString()

        assertEquals(expectedErrorMessage, ex.message)
    }
}