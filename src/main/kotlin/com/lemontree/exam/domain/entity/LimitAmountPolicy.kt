package com.lemontree.exam.domain.entity

import com.lemontree.exam.domain.type.LimitAmountType
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.Currency

@Entity
@Table(name = "limit_amount_policy")
class LimitAmountPolicy(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    @Enumerated(value = EnumType.STRING)
    val type: LimitAmountType,
    val currency: Currency,
    val limitAmount: BigDecimal,
) : BaseEntity() {
}