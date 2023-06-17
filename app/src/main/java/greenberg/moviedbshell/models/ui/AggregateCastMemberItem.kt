package greenberg.moviedbshell.models.ui

import kotlinx.parcelize.Parcelize

@Parcelize
class AggregateCastMemberItem(
    val originalName: String,
    val order: Int,
    val totalEpisodeCount: Int,
    val roles: List<RoleItem>,
    override val name: String,
    override val posterUrl: String,
    override val id: Int?,
) : CastMemberItem(
    // Always assume that a person has a role. Maybe one day this will backfire, but I imagine not.
    roles[0].character,
    name,
    posterUrl,
    id,
)
