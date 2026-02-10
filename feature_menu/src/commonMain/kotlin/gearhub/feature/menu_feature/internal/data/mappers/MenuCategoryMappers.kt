package gearhub.feature.menu_feature.internal.data.mappers

import gearhub.feature.menu_feature.api.models.MenuCategoryModel
import gearhub.feature.menu_feature.api.models.MenuCategoryInfoModel
import gearhub.feature.menu_feature.internal.domain.models.MenuCategoryDomain

internal fun MenuCategoryModel.toDomain(): MenuCategoryDomain = MenuCategoryDomain(
    id = id,
    title = name,
    slug = slug.orEmpty(),
)

internal fun MenuCategoryDomain.toUI(): MenuCategoryInfoModel = MenuCategoryInfoModel(
    id = id,
    title = title,
    slug = slug,
)
