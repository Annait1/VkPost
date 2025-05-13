
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WallServiceTest {
    @Before
    fun clearBeforeTest() {
        WallService.clear()
    }

    @Test
    fun addZeroId() {
        val post = Post(0, 123, 1, "text", Likes(0), Comments(0))
        val result = WallService.add(post)
        assertNotEquals(0, result.id)
    }

    @Test
    fun updateReturnTrue() {
        WallService.add(Post(0, 111, 1, "one", Likes(0), Comments(0)))
        val added = WallService.add(Post(0, 222, 2, "two", Likes(1), Comments(1)))

        val updated = added.copy(text = "Обновлённый текст")
        val result = WallService.update(updated)

        assertTrue(result)
    }

    @Test
    fun updateReturnFalse() {
        WallService.add(Post(0, 111, 1, "тест", Likes(0), Comments(0)))

        val fakePost = Post(999, 222, 2, "несуществующий", Likes(0), Comments(0))
        val result = WallService.update(fakePost)

        assertFalse(result)
    }
}
