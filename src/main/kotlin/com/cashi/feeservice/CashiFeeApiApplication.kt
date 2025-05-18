package com.cashi.feeservice

import dev.restate.sdk.springboot.EnableRestate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableRestate
class FeeserviceApplication

fun main(args: Array<String>) {
	runApplication<FeeserviceApplication>(*args)
}
