package gearhub.feature.menu_feature.internal.data.mappers

import gearhub.feature.menu_feature.api.model.MenuCategoryRecord
import gearhub.feature.menu_feature.api.models.MenuCategoryInfo
import gearhub.feature.menu_feature.internal.domain.models.MenuCategoryDomain

internal fun MenuCategoryRecord.toDomain(): MenuCategoryDomain = MenuCategoryDomain(
    id = id,
    title = name,
    slug = slug.orEmpty(),
)

internal fun MenuCategoryDomain.toUI(): MenuCategoryInfo = MenuCategoryInfo(
    id = id,
    title = title,
    slug = slug,
)
