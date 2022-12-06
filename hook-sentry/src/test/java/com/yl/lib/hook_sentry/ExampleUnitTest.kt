package com.yl.lib.hook_sentry

import com.yl.lib.sentry.hook.cache.DiskCache
import com.yl.lib.sentry.hook.cache.MemoryCache
import com.yl.lib.sentry.hook.cache.TimeLessMemoryCache
import com.yl.lib.sentry.hook.cache.TimeLessDiskCache
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testDiskCache() {
        var diskCache = DiskCache()
        diskCache.put("key1", "value1")
        var result = diskCache.get("key1", "default")
        assertEquals(true, result.first)
        assertEquals("value1", result.second)
    }

    @Test
    fun testMemoryCache() {
        var memoryCache = MemoryCache<Any>()
        memoryCache.put("key1", "value1")
        var result = memoryCache.get("key1", "default")
        assertEquals(true, result.first)
        assertEquals("value1", result.second)
    }

    @Test
    fun testTimeMemoryCache() {
        var memoryCache = TimeLessMemoryCache<Any>()
        memoryCache.put("key1", "value1", 1000 * 1)
        Thread.sleep(1000 * 2)
        var result = memoryCache.get("key1", "default")
        assertEquals(false, result.first)
        assertEquals("default", result.second)
    }

    @Test
    fun testTimeDiskCache() {
        var timeDiskCache = TimeLessDiskCache()
        timeDiskCache.put("key1", "value1",1000 * 1)
        Thread.sleep(1000 * 2)
        var result = timeDiskCache.get("key1", "default")
        assertEquals(false, result.first)
        assertEquals("default", result.second)

        timeDiskCache.put("key2", "value2",1000 * 2)
        var result2 = timeDiskCache.get("key2", "default")
        assertEquals(true, result2.first)
        assertEquals("value2", result2.second)
    }
}