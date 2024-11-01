package br.com.cooperativa.votacao.util

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConvertUtilsTest {

    @Test
    fun `should be true`() {
        assertTrue("Sim".toBool()!!)
        assertTrue("S".toBool()!!)
        assertTrue("Verdadeiro".toBool()!!)
        assertTrue("V".toBool()!!)
        assertTrue("True".toBool()!!)
        assertTrue("T".toBool()!!)
        assertTrue("yEs".toBool()!!)
        assertTrue("Y".toBool()!!)
    }

    @Test
    fun `should be false`() {
        assertFalse("não".toBool()!!)
        assertFalse("nao".toBool()!!)
        assertFalse("No".toBool()!!)
        assertFalse("n".toBool()!!)
        assertFalse("Falso".toBool()!!)
        assertFalse("False".toBool()!!)
        assertFalse("F".toBool()!!)
    }

    @Test
    fun `should be null`() {
        assertNull("".toBool())
        assertNull("dfbidfhb".toBool())
    }
}
