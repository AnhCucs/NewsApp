package anhcucs.ninhgiang_hd.newsapp.utils

sealed class Resources<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T?) : Resources<T>(data)
    class Error<T>( message: String?,data: T? = null,) : Resources<T>(message = message, data = data)
    class Loading<T> : Resources<T>()
}