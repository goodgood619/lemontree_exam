package com.lemontree.exam.domain.entity

import com.lemontree.exam.domain.type.LimitAmountType
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "limit_amount_policy")
class LimitAmountPolicy(
    @GeneratedValue
    @Id
    val id: Long,
    @Enumerated(value = EnumType.STRING)
    val type: LimitAmountType,
    val currency: String,
    val limitAmount: BigDecimal,
) : BaseEntity() {
}