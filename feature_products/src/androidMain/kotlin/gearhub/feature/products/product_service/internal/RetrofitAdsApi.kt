package gearhub.feature.products.product_service.internal

import com.gear.hub.network.config.HostProvider
import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.data.models.CreateAdRequestDTO
import gearhub.feature.products.product_feature.internal.data.models.CreateAdResponseDTO
import gearhub.feature.products.product_feature.internal.data.models.UpdateAdRequestDTO
import gearhub.feature.products.product_service.api.AdsApi
import retrofit2.http.Body
import retrofit2.http.Header
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

    override suspend fun createAd(
        request: CreateAdRequestDTO,
        authHeader: String?,
    ): ApiResponse<CreateAdResponseDTO> =
        try {
            val response = service.createAd(authHeader, request)
            ApiResponse.Success(response)
        } catch (http: retrofit2.HttpException) {
            val message = http.response()?.errorBody()?.string()
            ApiResponse.HttpError(http.code(), message)
        } catch (_: java.io.IOException) {
            ApiResponse.NetworkError
        } catch (throwable: Throwable) {
            ApiResponse.UnknownError(throwable)
        }

    override suspend fun updateAd(
        id: String,
        request: UpdateAdRequestDTO,
        authHeader: String?,
    ): ApiResponse<CreateAdResponseDTO> =
        try {
            val response = service.updateAd(id, authHeader, request)
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
    suspend fun createAd(
        @Header("Authorization") authHeader: String?,
        @Body body: CreateAdRequestDTO,
    ): CreateAdResponseDTO

    @PATCH("api/v1/ads/{id}")
    suspend fun updateAd(
        @Path("id") id: String,
        @Header("Authorization") authHeader: String?,
        @Body body: UpdateAdRequestDTO,
    ): CreateAdResponseDTO
}
