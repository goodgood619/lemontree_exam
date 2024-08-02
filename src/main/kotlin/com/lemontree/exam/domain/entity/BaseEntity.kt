package com.lemontree.exam.domain.entity

import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
open class BaseEntity(
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)