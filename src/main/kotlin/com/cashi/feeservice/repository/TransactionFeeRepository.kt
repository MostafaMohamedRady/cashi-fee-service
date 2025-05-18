package com.cashi.feeservice.repository

import com.cashi.feeservice.entity.TransactionFeeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionFeeRepository : JpaRepository<TransactionFeeEntity, String>