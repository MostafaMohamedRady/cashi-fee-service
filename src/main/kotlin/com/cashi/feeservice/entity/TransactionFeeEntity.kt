package com.cashi.feeservice.entity

import com.cashi.feeservice.enum.TransactionType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "transaction_fees")
data class TransactionFeeEntity(
    @Id @GeneratedValue val id: Long?,
    val transactionId: String,

    val amount: Double,
    val asset: String,

    @Enumerated(EnumType.STRING)
    val type: TransactionType,
    val fee: Double,
    val rate: Double,
    val description: String,
    val createdAt: LocalDateTime
)
