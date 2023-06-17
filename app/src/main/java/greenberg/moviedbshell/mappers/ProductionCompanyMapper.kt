package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.sharedmodels.ProductionCompaniesItem
import greenberg.moviedbshell.models.ui.ProductionCompanyItem
import javax.inject.Inject

class ProductionCompanyMapper
@Inject constructor() : Mapper<List<ProductionCompaniesItem?>?, List<ProductionCompanyItem>> {
    override fun mapToEntity(item: List<ProductionCompaniesItem?>?): List<ProductionCompanyItem> {
        val mappedItems = item?.map { productionCompanyItem ->
            ProductionCompanyItem(
                name = productionCompanyItem?.name.orEmpty(),
                logoPath = productionCompanyItem?.logoPath.orEmpty(),
                originCountry = productionCompanyItem?.originCountry.orEmpty(),
            )
        }
        return mappedItems.orEmpty()
    }
}
