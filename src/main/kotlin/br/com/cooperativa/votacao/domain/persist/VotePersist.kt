package br.com.cooperativa.votacao.domain.persist

import br.com.cooperativa.votacao.domain.dto.VoteType
import br.com.cooperativa.votacao.util.cleanCodeText
import br.com.cooperativa.votacao.util.createId
import org.springframework.data.annotation.Id

class VotePersist(
    id: String = createId(), // CPF
    val vote: VoteType = VoteType.NOT_SELECTED
) {

    @Id
    val id = cleanCodeText(id)

    override fun equals(other: Any?) = other is VotePersist && id == other.id

    override fun hashCode() = id.hashCode()

    override fun toString() = """{"id": "$id", "vote": "$vote"}"""

    companion object {
        fun build(
            id: String,
            vote: VoteType
        ): VotePersist {
            return VotePersist(
                id = id,
                vote = vote
            )
        }
    }
}
