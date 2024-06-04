package com.yadoms.myyadoms

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test
import java.time.LocalDateTime

class ServerTimeTest {
    @Test
    fun `Not initialized`() {
        val serverTime = ServerTime { LocalDateTime.of(2024, 5, 17, 14, 11) }
        assertNull(serverTime.now())
    }

    @Test
    fun `Client and server synchronized`() {
        val clientInstant = LocalDateTime.of(2024, 5, 17, 14, 11)
        val serverInstant = clientInstant

        val serverTime = ServerTime { clientInstant }
        serverTime.synchronize(serverInstant)
        assertEquals(serverTime.now(), serverInstant)
    }

    @Test
    fun `Client and server not synchronized`() {
        val clientInstant = LocalDateTime.of(2024, 5, 17, 14, 11)
        val serverInstant = LocalDateTime.of(2024, 5, 17, 13, 5)

        val serverTime = ServerTime { clientInstant }
        serverTime.synchronize(serverInstant)
        assertEquals(serverTime.now(), serverInstant)
    }
}