package gearhub.feature.menu_service.internal

import com.gear.hub.network.config.HostProvider
import com.gear.hub.network.model.ApiResponse
import gearhub.feature.menu_feature.internal.data.models.MenuCategoryDTO
import gearhub.feature.menu_service.api.MenuApi
import retrofit2.http.GET

/**
 * Android-реализация MenuApi на базе Retrofit.
 */
internal class RetrofitMenuApi(
    private val service: MenuRetrofitService,
    @Suppress("UNUSED_PARAMETER") private val hostProvider: HostProvider,
) : MenuApi {

    override suspend fun getCategories(): ApiResponse<List<MenuCategoryDTO>> =
        try {
            val response = service.getCategories()
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
 * Retrofit-интерфейс эндпоинта категорий.
 */
internal interface MenuRetrofitService {
    @GET("api/v1/categories")
    suspend fun getCategories(): List<MenuCategoryDTO>
}
