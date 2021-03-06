package com.bawer.tasks.revolut.ewallet.model.request

import com.bawer.tasks.revolut.ewallet.model.Currency
import java.math.BigDecimal

class AccountRequest (
        val holderName: String,
        val holderSurname: String,
        val currency: Currency,
        val balance: BigDecimal = BigDecimal.ZERO
)