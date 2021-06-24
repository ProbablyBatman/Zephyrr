package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.imagegallerymodels.ImageGalleryResponse
import greenberg.moviedbshell.models.ui.PosterItem
import javax.inject.Inject

class PosterGalleryMapper
@Inject constructor() : Mapper<ImageGalleryResponse, List<PosterItem>> {
    override fun mapToEntity(item: ImageGalleryResponse?): List<PosterItem> {
        return item?.posters?.mapNotNull { poster ->
            PosterItem(
                aspectRatio = poster?.aspectRatio ?: 0.0,
                filePath = poster?.filePath.orEmpty(),
                voteAverage = poster?.voteAverage ?: 0.0,
                voteCount = poster?.voteCount ?: 0,
                width = poster?.width ?: 0,
                height = poster?.height ?: 0
            )
        } ?: listOf()
    }
}
