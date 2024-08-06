package com.lemontree.exam.domain.entity

import com.lemontree.exam.domain.request.SignUpRequest
import com.lemontree.exam.util.Utils
import com.lemontree.exam.util.Utils.generateRandomUserMaximumBalanceLimit
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val email: String,
    val nickname: String,
    val password: String,
    var balance: BigDecimal = BigDecimal.ZERO,
    val limitBalance: BigDecimal = generateRandomUserMaximumBalanceLimit()
) : BaseEntity() {
    companion object {
        fun of(signUpRequest: SignUpRequest) =
            User(
                email = signUpRequest.email,
                nickname = signUpRequest.nickName,
                password = Utils.sha256Hash(signUpRequest.password),
            )
    }
}