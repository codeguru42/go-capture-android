package codeguru.gocapture

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface GoCaptureService {
    @Multipart
    @POST("capture/")
    suspend fun captureImage(@Part image: MultipartBody.Part): Response<ResponseBody>

    @Multipart
    @POST("capture_async/")
    suspend fun captureImageAsync(@Part image: MultipartBody.Part, @Part("fcm_registration_token") fcmRegistrationToken: String): Response<ResponseBody>
}
