package gearhub.feature.products.service.internal

import com.gear.hub.network.config.HostProvider
import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.internal.data.model.CreateAdRequestDto
import gearhub.feature.products.internal.data.model.CreateAdResponseDto
import gearhub.feature.products.internal.data.model.UpdateAdRequestDto
import gearhub.feature.products.service.api.AdsApi
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Android-реализация AdsApi на базе общего Retrofit-клиента.
 */
internal class RetrofitAdsApi(
    private val service: AdsRetrofitService,
    @Suppress("UNUSED_PARAMETER") private val hostProvider: HostProvider,
) : AdsApi {

    override suspend fun createAd(request: CreateAdRequestDto): ApiResponse<CreateAdResponseDto> =
        try {
            val response = service.createAd(request)
            ApiResponse.Success(response)
        } catch (http: retrofit2.HttpException) {
            val message = http.response()?.errorBody()?.string()
            ApiResponse.HttpError(http.code(), message)
        } catch (_: java.io.IOException) {
            ApiResponse.NetworkError
        } catch (throwable: Throwable) {
            ApiResponse.UnknownError(throwable)
        }

    override suspend fun updateAd(id: String, request: UpdateAdRequestDto): ApiResponse<CreateAdResponseDto> =
        try {
            val response = service.updateAd(id, request)
            ApiResponse.Success(response)
        } catch (http: retrofit2.HttpException) {
            val message = http.response()?.errorBody()?.string()
            ApiResponse.HttpError(http.code(), message)
        } catch (_: java.io.IOException) {
            ApiResponse.NetworkError
        } catch (throwable: Throwable) {
            ApiResponse.UnknownError(throwable)
        }
}

/**
 * Retrofit-интерфейс объявлений.
 */
internal interface AdsRetrofitService {
    @POST("api/v1/ads")
    suspend fun createAd(@Body body: CreateAdRequestDto): CreateAdResponseDto

    @PATCH("api/v1/ads/{id}")
    suspend fun updateAd(
        @Path("id") id: String,
        @Body body: UpdateAdRequestDto,
    ): CreateAdResponseDto
}
