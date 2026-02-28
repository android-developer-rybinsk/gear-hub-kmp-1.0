package gearhub.feature.products.product_service.internal

import com.gear.hub.network.config.HostProvider
import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.data.models.AdsPageResponseDTO
import gearhub.feature.products.product_feature.internal.data.models.AdsSaveRequestDataModel
import gearhub.feature.products.product_feature.internal.data.models.AdsSaveResponseDataModel
import gearhub.feature.products.product_feature.internal.data.models.AdsWizardRequestDataModel
import gearhub.feature.products.product_feature.internal.data.models.AdsWizardResponseDataModel
import gearhub.feature.products.product_feature.internal.data.models.CreateAdRequestDTO
import gearhub.feature.products.product_feature.internal.data.models.CreateAdResponseDTO
import gearhub.feature.products.product_feature.internal.data.models.UpdateAdRequestDTO
import gearhub.feature.products.product_feature.internal.data.models.ProductCategoryDTO
import gearhub.feature.products.product_service.api.AdsApi
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal class RetrofitAdsApi(
    private val service: AdsRetrofitService,
    @Suppress("UNUSED_PARAMETER") private val hostProvider: HostProvider,
) : AdsApi {

    override suspend fun getCategories(): ApiResponse<List<ProductCategoryDTO>> =
        safeCall { service.getCategories() }

    override suspend fun getAds(limit: Int, cursor: String?): ApiResponse<AdsPageResponseDTO> =
        safeCall { service.getAds(limit, cursor) }

    override suspend fun wizard(request: AdsWizardRequestDataModel): ApiResponse<AdsWizardResponseDataModel> =
        safeCall { service.wizard(request) }

    override suspend fun save(request: AdsSaveRequestDataModel): ApiResponse<AdsSaveResponseDataModel> =
        safeCall { service.save(request) }

    override suspend fun createAd(request: CreateAdRequestDTO): ApiResponse<CreateAdResponseDTO> =
        safeCall { service.createAd(request) }

    override suspend fun updateAd(id: String, request: UpdateAdRequestDTO): ApiResponse<CreateAdResponseDTO> =
        safeCall { service.updateAd(id, request) }

    private inline fun <T> safeCall(block: () -> T): ApiResponse<T> =
        try {
            ApiResponse.Success(block())
        } catch (http: retrofit2.HttpException) {
            val message = http.response()?.errorBody()?.string()
            ApiResponse.HttpError(http.code(), message)
        } catch (_: java.io.IOException) {
            ApiResponse.NetworkError
        } catch (throwable: Throwable) {
            ApiResponse.UnknownError(throwable)
        }
}

internal interface AdsRetrofitService {
    @GET("api/v1/categories")
    suspend fun getCategories(): List<ProductCategoryDTO>

    @GET("api/v1/ads")
    suspend fun getAds(
        @Query("limit") limit: Int,
        @Query("cursor") cursor: String?,
    ): AdsPageResponseDTO

    @POST("api/v1/ads/wizard")
    suspend fun wizard(@Body body: AdsWizardRequestDataModel): AdsWizardResponseDataModel

    @POST("api/v1/ads/save")
    suspend fun save(@Body body: AdsSaveRequestDataModel): AdsSaveResponseDataModel

    @POST("api/v1/ads")
    suspend fun createAd(@Body body: CreateAdRequestDTO): CreateAdResponseDTO

    @PATCH("api/v1/ads/{id}")
    suspend fun updateAd(
        @Path("id") id: String,
        @Body body: UpdateAdRequestDTO,
    ): CreateAdResponseDTO
}
