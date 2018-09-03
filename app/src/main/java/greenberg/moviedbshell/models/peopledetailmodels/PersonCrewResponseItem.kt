package greenberg.moviedbshell.models.peopledetailmodels

import com.google.gson.annotations.SerializedName

data class PersonCrewResponseItem(

	@field:SerializedName("overview")
	val overview: String? = null,

	@field:SerializedName("original_language")
	val originalLanguage: String? = null,

	@field:SerializedName("original_title")
	val originalTitle: String? = null,

	@field:SerializedName("video")
	val video: Boolean? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("genre_ids")
	val genreIds: List<Int?>? = null,

	@field:SerializedName("poster_path")
	val posterPath: String? = null,

	@field:SerializedName("backdrop_path")
	val backdropPath: String? = null,

	@field:SerializedName("media_type")
	val mediaType: String? = null,

	@field:SerializedName("credit_id")
	val creditId: String? = null,

	@field:SerializedName("release_date")
	val releaseDate: String? = null,

	@field:SerializedName("popularity")
	val popularity: Double? = null,

	@field:SerializedName("vote_average")
	val voteAverage: Double? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("department")
	val department: String? = null,

	@field:SerializedName("job")
	val job: String? = null,

	@field:SerializedName("adult")
	val adult: Boolean? = null,

	@field:SerializedName("vote_count")
	val voteCount: Int? = null,

	@field:SerializedName("first_air_date")
	val firstAirDate: String? = null,

	@field:SerializedName("episode_count")
	val episodeCount: Int? = null,

	@field:SerializedName("origin_country")
	val originCountry: List<String?>? = null,

	@field:SerializedName("original_name")
	val originalName: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)