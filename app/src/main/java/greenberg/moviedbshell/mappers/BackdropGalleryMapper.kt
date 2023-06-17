package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.imagegallerymodels.ImageGalleryResponse
import greenberg.moviedbshell.models.ui.PosterItem
import javax.inject.Inject

class BackdropGalleryMapper
@Inject constructor() : Mapper<ImageGalleryResponse, List<PosterItem>> {
    override fun mapToEntity(item: ImageGalleryResponse?): List<PosterItem> {
        return item?.backdrops?.mapNotNull { backdrop ->
            PosterItem(
                aspectRatio = backdrop?.aspectRatio ?: 0.0,
                filePath = backdrop?.filePath.orEmpty(),
                voteAverage = backdrop?.voteAverage ?: 0.0,
                voteCount = backdrop?.voteCount ?: 0,
                width = backdrop?.width ?: 0,
                height = backdrop?.height ?: 0,
            )
        } ?: listOf()
    }
}
