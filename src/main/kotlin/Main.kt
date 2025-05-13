import kotlin.coroutines.coroutineContext

data class Likes(
    val count: Int,
    val userLikes: Boolean = false,
    val canLike: Boolean = true,
    val canPublish: Boolean = true
)

data class Comments(
    val count: Int,
    val canPost: Boolean = true,
    val groupsCanPost: Boolean = true,
    val canClose: Boolean = false,
    val canOpen: Boolean = false
)

data class Post(
    val id: Int,
    val date: Int,
    val fromId: Int,
    val text: String,
    val likes: Likes,
    val comments: Comments

)

object WallService {
    private var posts = emptyArray<Post>()
    private var nextId = 1
    fun add(post: Post): Post {
        val newPost = post.copy(id = nextId++)
        posts += newPost
        return newPost
    }

    fun clear() {
        posts = emptyArray()
        nextId = 1
    }


    fun update(post: Post): Boolean {
        /* !Заметка-напоминалка!

        ! index
        0,1,2

        ! withIndex - оборачивает в масив пара
        0(Post(id = 1,date = 123, text = "Наш"))
        1(Post(id = 2,date = 123, text = "пост"))
        2(Post(id = 3,date = 123, text = "Наш пост"))

        ! storedPost
        Post(id = 1,date = 123, text = "Наш")
        Post(id = 2,date = 123, text = "пост"))*/

        for ((index, storedPost) in posts.withIndex()) {
            if (storedPost.id == post.id) {
                posts[index] = post.copy(id = storedPost.id) /*post.copy(id = storedPost.id) - взять всё у post,
                кроме id.
                .copy все берет из объекта, у которого вызывается сopy, то есть в данном случчае
                у post.copy и заменяется только то, что указано в скобках*/
                return true
            }
        }

        return false
    }
}


fun main() {
    WallService.clear()

    val likes = Likes(0)
    val comments = Comments(0)

    val post1 = Post(0, 123456, 45, "Наш первый пост!", likes, comments)
    val added1 = WallService.add(post1)

    val post2 = Post(0, 124, 46, "Второй пост", likes, comments)
    val added2 = WallService.add(post2)

    println("Добавленные посты:")
    println(added1)
    println(added2)
    println("---------------")

    val updatedPost2 = added2.copy(text = "Обновлённый второй пост", likes = Likes(100))
    val updateResult = WallService.update(updatedPost2)

    println("Результат обновления второго поста: $updateResult")


}
