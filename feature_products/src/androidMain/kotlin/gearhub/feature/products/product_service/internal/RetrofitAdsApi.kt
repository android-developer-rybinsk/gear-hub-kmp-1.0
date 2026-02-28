package gearhub.feature.products.product_service.internal

import com.gear.hub.network.config.HostProvider
import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.data.models.AdsSaveRequestDataModel
import gearhub.feature.products.product_feature.internal.data.models.AdsSaveResponseDataModel
import gearhub.feature.products.product_feature.internal.data.models.AdsWizardRequestDataModel
import gearhub.feature.products.product_feature.internal.data.models.AdsWizardResponseDataModel
import gearhub.feature.products.product_service.api.AdsApi
import retrofit2.http.Body
import retrofit2.http.POST

internal class RetrofitAdsApi(
    private val service: AdsRetrofitService,
    @Suppress("UNUSED_PARAMETER") private val hostProvider: HostProvider,
) : AdsApi {

    override suspend fun wizard(request: AdsWizardRequestDataModel): ApiResponse<AdsWizardResponseDataModel> =
        safeCall { service.wizard(request) }

    override suspend fun save(request: AdsSaveRequestDataModel): ApiResponse<AdsSaveResponseDataModel> =
        safeCall { service.save(request) }

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
    @POST("api/v1/ads/wizard")
    suspend fun wizard(@Body body: AdsWizardRequestDataModel): AdsWizardResponseDataModel

    @POST("api/v1/ads/save")
    suspend fun save(@Body body: AdsSaveRequestDataModel): AdsSaveResponseDataModel
}
