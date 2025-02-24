import com.example.shiqone.model.Post


typealias PostsCallback = (List<Post>) -> Unit
typealias EmptyCallback = () -> Unit

object Constants {

    object Collections {
        const val POSTS = "posts"
    }
}