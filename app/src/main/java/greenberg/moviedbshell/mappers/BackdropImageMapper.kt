package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.imagegallerymodels.BackdropsItem
import greenberg.moviedbshell.models.imagegallerymodels.ImageGalleryResponse
import javax.inject.Inject

class BackdropImageMapper
@Inject constructor(): Mapper<ImageGalleryResponse, List<BackdropsItem>> {
    override fun mapToEntity(item: ImageGalleryResponse): List<BackdropsItem> {
        return item.backdrops?.mapNotNull { backdrop ->
            BackdropsItem(
                    aspectRatio = backdrop?.aspectRatio ?: 0.0,
                    filePath = backdrop?.filePath.orEmpty(),
                    voteAverage = backdrop?.voteAverage ?: 0.0,
                    voteCount = backdrop?.voteCount ?: 0,
                    width = backdrop?.width ?: 0,
                    height = backdrop?.height ?: 0
            )
        } ?: mutableListOf()
    }
}