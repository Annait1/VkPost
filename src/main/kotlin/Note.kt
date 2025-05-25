import java.lang.RuntimeException

data class Note(
    val id: Int,
    val title: String,
    val text: String,
    val isDeleted: Boolean = false
)

data class NoteComment(
    val id: Int,
    val noteId: Int,
    val message: String,
    val isDeleted: Boolean = false
)

class NoteStorage<T> {
    private var items = mutableListOf<T>()
    fun add(item: T): T {
        items += item
        return item
    }

    fun getAll(): List<T> = items

    fun update(index: Int, item: T) {
        items[index] = item
    }

    fun withIndex(): List<IndexedValue<T>> = items.withIndex().toList()

    fun clear() {
        items.clear()
    }

    operator fun iterator(): Iterator<T> = items.iterator()
}


class NoteNotFoundException(message: String) : RuntimeException(message)
object NoteService {
    private val notes = NoteStorage<Note>()
    private val noteComment = NoteStorage<NoteComment>()
    private var nextId = 1


    fun add(note: Note): Note {
        val newNote = note.copy(id = nextId++)
        /* notes += newNote
         return newNote*/
        /*    поменяла на*/
        return notes.add(newNote)
    }

    fun deleteNote(noteId: Int): Boolean {
        for ((index, note) in notes.withIndex()) {
            if (note.id == noteId) {
                if (note.isDeleted) {
                    return false
                }
                notes.update(index, note.copy(isDeleted = true))
                return true
            }
            /*val updateNote = note.copy(isDeleted = true)
                notes[index] = updateNote
                return true*/
            /*поменяла на
                        return false
                notes.update(index, note.copy(isDeleted = true))*/
        }
        return false

    }


fun editNote(note: Note, title: String, text: String): Boolean {
    for ((index, createNote) in notes.withIndex()) {
        if (createNote.id == note.id) {
            notes.update(index, createNote.copy(title=title, text = text))

            return true
        }

    }
    return false
}

fun getById(noteId: Int): Note? {
    for (note in notes) {
        if (note.id == noteId && !note.isDeleted)
            return note
    }
    return null

}


fun get(): List<Note> {
    val result = mutableListOf<Note>()
    for (note in notes.getAll()) {
        if (!note.isDeleted) {
            result += note
        }
    }
    return result
    /*было*/
    /*for (note in notes) {
        if (!note.isDeleted) {
            result += note
        }
    }
    return result*/
}

fun createComment(noteId: Int, comment: NoteComment): NoteComment {
    var noteExists = false
    for (note in notes.getAll()) {
        if (note.id == noteId && !note.isDeleted) {
            noteExists = true
            break
        }
    }
    if (!noteExists) {
        throw NoteNotFoundException("Заметка с id=$noteId не найдена")
    }

    val newComment = comment.copy(id = nextId++)
    /*this.noteComment += newComment
    return newComment*/
    return noteComment.add(newComment)
}

fun deleteComment(commentId: Int): Boolean {
    for ((index, comment) in noteComment.withIndex()) {
        if (comment.id == commentId) {
            if (comment.isDeleted) {
                return false
            }
            val updateComment = comment.copy(isDeleted = true)
            noteComment.update(index, updateComment)
            return true
        }

    }
    return false
}

fun restoreComment(commentId: Int): Boolean {
    for ((index, comment) in noteComment.withIndex()) {
        if (comment.id == commentId) {
            if (!comment.isDeleted) {
                return false
            }
            val restored = comment.copy(isDeleted = false)
            noteComment.update(index, restored)
            return true
        }
    }
    return false
}

fun editComment(fixComment: NoteComment, message: String): Boolean {
    for ((index, createNoteComment) in noteComment.withIndex()) {
        if (createNoteComment.id == fixComment.id) {
            noteComment.update(index,createNoteComment.copy(message = message))

            return true

        }

    }
    return false
}

fun getComments(noteId: Int): List<NoteComment> {
    val result = mutableListOf<NoteComment>()
    for (comment in noteComment) {
        if (comment.noteId == noteId && !comment.isDeleted) {
            result += comment
        }
    }
    return result
}

fun readComment(commentId: Int): NoteComment? {
    for (comment in noteComment) {
        if (comment.id == commentId && !comment.isDeleted)
            return comment
    }
    return null
}

fun restore(commentId: Int): Boolean {
    for ((index, comment) in noteComment.withIndex()) {
        if (commentId == comment.id) {
            if (!comment.isDeleted) {
                return false
            }

            val restored = comment.copy(isDeleted = false)
            noteComment.update(index, restored)

            return true
        }
    }
    return false
}

fun clear() {
    notes.clear()
    noteComment.clear()
    nextId = 1
}
}



fun main() {
    NoteService.clear()

    val note1 =
        NoteService.add(Note(0, "Главное правило домашки", "Если решается легко, значит, ты решаешь неправильно"))

    val note2 = NoteService.add(
        Note(
            0,
            "Топ достопримечательностей Волгограда", "Мыс Фиолент, Храм Воскресения Христова"
        )
        /*опечатка, вместо Крыма - Волгоград*/
    )
    val note3 = NoteService.add(
        Note(
            0,
            "Секрет блинчиков", "Добавьте горячее молоко в тесто, перемешивая венчиком или вилкой"
        )
    )
    val comment1 = NoteService.createComment(note1.id, NoteComment(0, note1.id, "жизя"))
    val comment2 = NoteService.createComment(note2.id, NoteComment(0, note2.id, "красота"))
    val comment3 = NoteService.createComment(note3.id, NoteComment(0, note3.id, "запомню"))

    /*   val noteTestId = note1.id
       println(noteTestId)*/


    val deletedNote = NoteService.deleteNote(note1.id)
    println("Удаляем заметку, результат : $deletedNote")

    val noteDeletedTwo = NoteService.deleteNote(note1.id)
    println("Пробуем еще раз удалить заметку, результат : $noteDeletedTwo ")

    val edNote = NoteService.editNote(
        note2,
        "Достопримечательности Крыма", "Мыс Фиолент, Храм Воскресения Христова, Воронцовский дворец"
    )

    val activeNotes = NoteService.get()
    println("Активные заметки:")
    for (note in activeNotes) {
        println("${note.id}: ${note.title} - ${note.text}")
    }


    println("-----------------------------------")



    val deletedComment = NoteService.deleteComment(comment1.id)

    println("Удаляем комментарий, результат : $deletedComment ")

    val commentDeletedTwo = NoteService.deleteComment(comment1.id)
    println("Пробуем еще раз удалить комментарий, результат : $commentDeletedTwo ")

    val restored = NoteService.restoreComment(comment1.id)
    println("Восстановливаем комментарий, результат : $restored")


    println("\nКомментарии к заметке ${note2.id}:")
    val comments = NoteService.getComments(note2.id)
    for (com in comments) {
        println("${com.id} : ${com.message}")
    }

    println("\nКомментарии к заметке ${note3.id}:")
    val comments2 = NoteService.getComments(note3.id)
    for (com in comments2) {
        println("${com.id} : ${com.message}")
    }



    println("\n Читаем комментарий с id = ${comment2.id}")
    val foundComment = NoteService.readComment(comment2.id)

    if (foundComment != null) {
        println("Комментарий найден: ${foundComment.id} — ${foundComment.message}")
    } else {
        println("Комментарий не найден или удалён.")
    }

}