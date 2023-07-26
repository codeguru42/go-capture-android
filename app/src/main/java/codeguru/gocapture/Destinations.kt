package codeguru.gocapture

interface Destination {
    val route: String
}

object Main : Destination {
    override val route = "main"
}

object Image : Destination {
    override val route = "image"
}
