package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.tvdetailmodels.NetworksItem
import greenberg.moviedbshell.models.ui.NetworkItem
import javax.inject.Inject

class NetworksMapper
@Inject constructor() : Mapper<List<NetworksItem?>, List<NetworkItem>> {
    override fun mapToEntity(item: List<NetworksItem?>?): List<NetworkItem> {
        return item?.map { NetworkItem(it?.name.orEmpty()) } ?: listOf()
    }
}
