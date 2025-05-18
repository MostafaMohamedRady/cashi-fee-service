package com.cashi.feeservice.service

import com.cashi.feeservice.dto.TransactionRequest
import com.cashi.feeservice.dto.TransactionResponse
import com.cashi.feeservice.entity.TransactionFeeEntity
import com.cashi.feeservice.enum.TransactionType
import com.cashi.feeservice.repository.TransactionFeeRepository
import com.cashi.feeservice.validator.TransactionValidator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service for calculating and persisting transaction fees.
 *
 * This service:
 * 1. Validates incoming transaction requests.
 * 2. Computes the fee based on the transaction type and amount.
 * 3. Constructs a [TransactionResponse] with the fee details.
 * 4. Persists the fee calculation result into the database.
 *
 * @property repo Repository used to save [TransactionFeeEntity] records.
 * @property transactionValidator Validator for business rules on the request.
 */
@Service
class FeeCalculatorService(
    private val repo: TransactionFeeRepository,
    private val transactionValidator: TransactionValidator
) {

    private val logger = LoggerFactory.getLogger(FeeCalculatorService::class.java)

    /**
     * Validates the request, determines the applicable rate, calculates the fee,
     * and returns a [TransactionResponse].
     *
     * @param request the transaction details submitted by the client.
     * @return a [TransactionResponse] containing the original data plus fee, rate, and description.
     * @throws IllegalArgumentException if the amount or state is invalid.
     */
    fun calculateFee(request: TransactionRequest): TransactionResponse {
        // 1. Validate the transaction amount
        logger.info("transaction=${request.transactionId} validating amount=${request.amount}")
        transactionValidator.validateAmount(request.amount)

        // 2. Validate the transaction state
        logger.info("transaction=${request.transactionId} validating state=${request.state}")
        transactionValidator.validateState(request.state)

        // 3. Determine the fee rate based on transaction type
        val rate = extractRate(request)

        // 4. Calculate the fee and build the description
        val fee = request.amount * rate
        val description = "Standard fee rate of ${rate * 100}%"

        // 5. Return the response DTO
        return TransactionResponse(
            transactionId = request.transactionId,
            amount        = request.amount,
            asset         = request.asset,
            type          = request.type,
            fee           = fee,
            rate          = rate,
            description   = description
        )
    }

    /**
     * Determines the fee rate for the given transaction type.
     *
     * @param request the incoming transaction request.
     * @return the fee rate as a double (e.g., 0.0015 for 0.15%).
     */
    private fun extractRate(request: TransactionRequest): Double =
        when (request.type) {
            TransactionType.MOBILE_TOP_UP.toString()          -> 0.0015  // 0.15%
            TransactionType.INTERNATIONAL_TRANSFER.toString() -> 0.0025  // 0.25%
            TransactionType.BILL_PAYMENT.toString()           -> 0.0010  // 0.10%
            TransactionType.CRYPTO_TRANSFER.toString()        -> 0.0050  // 0.50%
            else                                              -> 0.0015  // default rate
        }

    /**
     * Persists a [TransactionFeeEntity] representing the calculated fee.
     *
     * @param request  the original transaction request.
     * @param response the response containing the calculated fee details.
     */
    fun recordFeeCalculation(
        request: TransactionRequest,
        response: TransactionResponse
    ) {
        logger.info("recording fee for transaction=${request.transactionId}, fee=${response.fee}")
        val entity = TransactionFeeEntity(
            id              = null,
            transactionId   = request.transactionId,
            amount          = response.amount,
            asset           = response.asset,
            type            = TransactionType.fromDisplayName(request.type),
            fee             = response.fee,
            rate            = response.rate,
            description     = response.description,
            createdAt       = request.createdAt
        )
        repo.save(entity)
    }
}
