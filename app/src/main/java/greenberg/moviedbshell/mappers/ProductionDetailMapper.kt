package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.ui.MovieDetailItem
import greenberg.moviedbshell.models.ui.ProductionDetailItem
import javax.inject.Inject

class ProductionDetailMapper
@Inject() constructor() : Mapper<MovieDetailItem, ProductionDetailItem> {
    override fun mapToEntity(item: MovieDetailItem?): ProductionDetailItem {
        return ProductionDetailItem(
            movieTitle = item?.movieTitle.orEmpty(),
            originalTitle = item?.originalTitle.orEmpty(),
            releaseDate = item?.releaseDate.orEmpty(),
            budget = item?.budget ?: 0,
            runtime = item?.runtime ?: 0,
            status = item?.status.orEmpty(),
            revenue = item?.revenue ?: 0,
            crewMembers = item?.crewMembers.orEmpty(),
            productionCompanies = item?.productionCompanies.orEmpty(),
            productionCountries = item?.productionCountries.orEmpty()
        )
    }
}