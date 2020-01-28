package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.imagegallerymodels.ImageGalleryResponse
import greenberg.moviedbshell.models.ui.BackdropPosterItem
import javax.inject.Inject

class BackdropImageMapper
@Inject constructor() : Mapper<ImageGalleryResponse, List<BackdropPosterItem>> {
    override fun mapToEntity(item: ImageGalleryResponse?): List<BackdropPosterItem> {
        return item?.backdrops?.mapNotNull { backdrop ->
            BackdropPosterItem(
                    aspectRatio = backdrop?.aspectRatio ?: 0.0,
                    filePath = backdrop?.filePath.orEmpty(),
                    voteAverage = backdrop?.voteAverage ?: 0.0,
                    voteCount = backdrop?.voteCount ?: 0,
                    width = backdrop?.width ?: 0,
                    height = backdrop?.height ?: 0
            )
        } ?: listOf()
    }
}