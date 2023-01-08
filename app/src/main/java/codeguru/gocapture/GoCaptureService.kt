package codeguru.gocapture

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface GoCaptureService {
    @Multipart
    @POST("capture")
    fun captureImage(@Part image: MultipartBody.Part)
}
