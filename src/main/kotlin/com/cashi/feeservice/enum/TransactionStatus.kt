package com.cashi.feeservice.enum

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionStatus {
    FEE_PENDING ,
    FEE_CALCULATED,
    FEE_RECORDED,
    FEE_COMPLETED
}