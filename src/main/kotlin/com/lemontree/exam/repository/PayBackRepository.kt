package com.lemontree.exam.repository

import com.lemontree.exam.domain.entity.PayBack
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PayBackRepository : JpaRepository<PayBack, Long> {
}