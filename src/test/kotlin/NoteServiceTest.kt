import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
class NoteServiceTest {
    @Before
    fun clearBeforeTest() {
        NoteService.clear()
    }
    @Test
    fun readComment_existingAndNotDeleted_returnsComment() {
        NoteService.clear()
        val note = NoteService.add(Note(0, "Заметка", "Текст"))
        val comment = NoteService.createComment(note.id, NoteComment(0, note.id, "Комментарий"))

        val result = NoteService.readComment(comment.id)

        assertNotNull(result)
        assertEquals(comment.id, result?.id)
    }



    @Test
    fun readComment_deletedComment_returnsNull() {
        NoteService.clear()
        val note = NoteService.add(Note(0, "Заметка", "Текст"))
        val comment = NoteService.createComment(note.id, NoteComment(0, note.id, "Комментарий"))

        NoteService.deleteComment(comment.id)

        val result = NoteService.readComment(comment.id)

        assertNull(result)
    }
}