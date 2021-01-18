package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.sharedmodels.ProductionCountriesItem
import greenberg.moviedbshell.models.ui.ProductionCountryItem
import javax.inject.Inject

class ProductionCountryMapper
@Inject constructor() : Mapper<List<ProductionCountriesItem?>?, List<ProductionCountryItem>> {
    override fun mapToEntity(item: List<ProductionCountriesItem?>?): List<ProductionCountryItem> {
        val mappedItems = item?.map { productionCountry ->
            ProductionCountryItem(
                name = productionCountry?.name.orEmpty()
            )
        }
        return mappedItems.orEmpty()
    }
}