package gearhub.feature.menu_feature.internal.presentation.filter

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SellerType(val label: String) {
    ALL("Все"),
    PRIVATE("Частные"),
    COMPANY("Компании")
}

enum class SortOption(val label: String) {
    DEFAULT("По умолчанию"),
    DATE("По дате"),
    CHEAP("Дешевле"),
    EXPENSIVE("Дороже")
}

enum class VehicleCondition(val label: String) {
    ANY("Любое"),
    NEW("Новое"),
    USED("Б/у")
}

enum class AutoType(val label: String) {
    ALL("Все"),
    USED("С пробегом"),
    NEW("Новые")
}

enum class Steering(val label: String) {
    ANY("Все"),
    LEFT("Левый"),
    RIGHT("Правый")
}

enum class OwnersCount(val label: String) {
    ANY("Не важно"),
    ONE("Один"),
    UP_TO_TWO("До двух"),
    UP_TO_THREE("До трёх")
}

enum class AutoCondition(val label: String) {
    ALL("Все"),
    NOT_DAMAGED("Кроме битых"),
    DAMAGED("Битые")
}

data class MenuFilterState(
    val selectedCategoryId: String? = null,
    val location: String = "",
    val priceFrom: String = "",
    val priceTo: String = "",
    val sellerType: SellerType = SellerType.ALL,
    val sortOption: SortOption = SortOption.DEFAULT,
    val query: String = "",
    val autoType: AutoType = AutoType.ALL,
    val autoBrand: String? = null,
    val autoBody: String? = null,
    val autoYearFrom: String = "",
    val autoYearTo: String = "",
    val autoDrive: String? = null,
    val autoSteering: Steering = Steering.ANY,
    val autoEngineFrom: String = "",
    val autoEngineTo: String = "",
    val autoPowerFrom: String = "",
    val autoPowerTo: String = "",
    val autoMileageFrom: String = "",
    val autoMileageTo: String = "",
    val autoOwners: OwnersCount = OwnersCount.ANY,
    val autoCondition: AutoCondition = AutoCondition.ALL,
    val autoGearbox: String? = null,
    val autoEngineType: String? = null,
    val autoColor: String? = null,
    val motoType: String? = null,
    val motoBrand: String? = null,
    val motoYearFrom: String = "",
    val motoYearTo: String = "",
    val motoEngineType: String? = null,
    val motoEngineFrom: String = "",
    val motoEngineTo: String = "",
    val motoPowerFrom: String = "",
    val motoPowerTo: String = "",
    val motoStrokes: String? = null,
    val motoGearbox: String? = null,
    val snowType: String? = null,
    val snowBrand: String? = null,
    val snowYearFrom: String = "",
    val snowYearTo: String = "",
    val snowEngineType: String? = null,
    val snowEngineFrom: String = "",
    val snowEngineTo: String = "",
    val snowPowerFrom: String = "",
    val snowPowerTo: String = "",
    val snowGearbox: String? = null,
    val waterType: String? = null,
    val waterCondition: VehicleCondition = VehicleCondition.ANY,
    val specType: String? = null,
    val specPowerFrom: String = "",
    val specPowerTo: String = "",
    val specCondition: String? = null,
    val partsGroup: String? = null,
    val partsSubgroup: String? = null
)

object MenuFilterStore {
    private val state = MutableStateFlow(MenuFilterState())

    fun state(): StateFlow<MenuFilterState> = state.asStateFlow()

    fun update(reducer: (MenuFilterState) -> MenuFilterState) {
        state.value = reducer(state.value)
    }

    fun reset() {
        state.value = MenuFilterState()
    }
}
