import java.lang.RuntimeException

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

sealed class Attachment(val type: String)

data class Photo(
    val id: Int,
    val text: String,
    val date: Int,
    val width: Int,
    val height: Int
)

data class Audio(
    val id: Int,
    val artist: String,
    val title: String,
    val duration: Int /*продолжительность*/

)

data class Video(
    val id: Int,
    val title: String,
    val duration: Int,
    val views: Int
)


data class File(
    val id: Int,
    val title: String,
    val size: Int,
    val ext: String,
    val type: Int
) {
    fun getDocumentType(): String {
        return when (type) {
            1 -> "текстовые документы"
            2 -> "архивы"
            3 -> "gif"
            4 -> "изображение"
            5 -> "аудио"
            6 -> "видео"
            7 -> "электронные книги"
            else -> "неизвестно"
        }
    }
}

data class Sticker(
    val innerType: String,
    val productInt: Int,
    val isAllowed: Boolean = false

)

data class PhotoAttachment(val photo: Photo) : Attachment("photo")

data class AudioAttachment(val audio: Audio) : Attachment("audio")
data class VideoAttachment(val video: Video) : Attachment("video")


data class FileAttachment(val file: File) : Attachment("file")

data class StickerAttachment(val sticker: Sticker) : Attachment("sticker")

data class Comment(
    val id: Int,
    val fromId: Int,
    val date: Int,
    val text: String,
    val replyToUser: Int? = null,
    val replyToComment: Int? = null
)

class PostNotFoundException(message: String) : RuntimeException()

/*
data class GenericPair<A, B>(var first: A, var second: B)

var GenericPair
*/

data class Post(
    val id: Int,
    val date: Int,
    val fromId: Int?,
    val text: String?,
    val likes: Likes,
    val comments: Comments,
    val attachments: Array<Attachment> = emptyArray()

)

object WallService {
    private var posts = emptyArray<Post>()
    private var nextId = 1
    private var comments = emptyArray<Comment>()
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

    fun createComment(postId: Int, comment: Comment): Comment {
        var postExists = false

        for (post in posts) {
            if ((post.id == postId)) {
                postExists = true
                break
            }

        }
        if (!postExists) {
            throw PostNotFoundException(" Пост с id=$postId не найден")
        }
        comments += comment
        return comment
    }

}


fun printPost(post: Post) {
    val text = post.text ?: "[без текста]"
    val author = post.fromId ?: 0
    println("Post(id=${post.id}, fromId=$author, text=$text, likes=${post.likes.count})")

    if (post.attachments.isNotEmpty()) {
        println("Вложения:")
        post.attachments.forEach { attachment ->
            println("— ${attachment.type}")
        }
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

    val post3 = Post(0, 124, null, null, likes, comments)
    val added3 = WallService.add(post3)

    println("Добавленные посты:")
    printPost(added1)
    printPost(added2)
    printPost(added3)

    val updatedPost2 = added2.copy(text = "Обновлённый второй пост", likes = Likes(100))
    val updateResult = WallService.update(updatedPost2)
    println("Результат обновления второго поста: $updateResult")


    val photo = Photo(1, "Всем приветик", 123, 800, 600)
    val photoAttachment = PhotoAttachment(photo)

    val postWithAttachment = Post(
        id = 0,
        date = 127,
        fromId = 20,
        text = "Пост с вложением",
        likes = likes,
        comments = comments,
        attachments = arrayOf(photoAttachment)
    )

    val added4 = WallService.add(postWithAttachment)
    printPost(added4)

    val comment = Comment(
        id = 1,
        fromId = 42,
        date = 1680000000,
        text = "Супер!",
        replyToUser = null,
        replyToComment = null
    )

    println("---------------------")

    val newComment = WallService.createComment(added2.id, comment)
    println("Комментарий: ${newComment.text}")


}
