package br.com.cooperativa.votacao.domain.persist

import br.com.cooperativa.votacao.domain.dto.VoteType
import br.com.cooperativa.votacao.util.cleanCodeText
import br.com.cooperativa.votacao.util.createId
import br.com.cooperativa.votacao.util.now
import org.hibernate.validator.constraints.br.CPF
import org.springframework.data.annotation.Id
import java.time.LocalDateTime

class VotePersist(
    id: String = createId(), // CPF
    val vote: VoteType = VoteType.NOT_SELECTED,
    val createdAt: LocalDateTime = now()
) {

    @Id
    @CPF(message = "CPF inválido")
    val id = cleanCodeText(id)

    override fun equals(other: Any?) = other is VotePersist && id == other.id

    override fun hashCode() = id.hashCode()

    override fun toString() = """{"id": "$id", "vote": "$vote"}"""

    companion object {
        fun build(
            id: String,
            vote: VoteType,
            createdAt: LocalDateTime = now()
        ): VotePersist {
            return VotePersist(
                id = id,
                vote = vote,
                createdAt = createdAt
            )
        }
    }
}
