package greenberg.moviedbshell.models.tvdetailmodels

import greenberg.moviedbshell.models.sharedmodels.CreditsResponse

data class TvDetailResponseItem(val tvDetailResponse: TvDetailResponse,
                                val creditsResponse: CreditsResponse)