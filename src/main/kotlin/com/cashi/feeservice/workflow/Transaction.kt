package com.cashi.feeservice.workflow

import com.cashi.feeservice.dto.TransactionRequest
import com.cashi.feeservice.dto.TransactionResponse
import com.cashi.feeservice.service.FeeCalculatorService
import dev.restate.sdk.annotation.Handler
import dev.restate.sdk.kotlin.Context
import dev.restate.sdk.kotlin.runBlock
import dev.restate.sdk.springboot.RestateService
import org.slf4j.LoggerFactory


/**
 * Restate service that handles fee-related transactions.
 *
 * Delegates fee calculation and persistence to the [FeeCalculatorService],
 * ensuring both steps are run inside the Restate workflow context.
 *
 * @property service performs the actual fee computation and persistence logic.
 */
@RestateService
class Transaction(private val service: FeeCalculatorService) {

    private val logger = LoggerFactory.getLogger(Transaction::class.java)

    /**
     * Workflow handler for the "fee" event.
     *
     * 1. Calculates the fee for the given transaction request.
     * 2. Persists the calculated fee.
     *
     * @param ctx the Restate [Context] that allows running blocking service calls.
     * @param request the incoming transaction data to process.
     * @return a [TransactionResponse] containing the calculated fee and related info.
     */
    @Handler
    suspend fun fee(ctx: Context, request: TransactionRequest): TransactionResponse {

        logger.info("Processing transaction: ${request.transactionId}")
        // 1. Calculate and record fee
        val response = ctx.runBlock { service.calculateFee(request) }

        logger.info("Persisting fee of ${response.fee} for transaction ${response.transactionId}")
        ctx.runBlock { service.recordFeeCalculation(request, response) }
        return response;

    }

}
