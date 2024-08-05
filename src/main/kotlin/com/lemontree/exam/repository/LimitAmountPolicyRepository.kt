package com.lemontree.exam.repository

import com.lemontree.exam.domain.entity.LimitAmountPolicy
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LimitAmountPolicyRepository : JpaRepository<LimitAmountPolicy, Long> {
}