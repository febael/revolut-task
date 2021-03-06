package com.bawer.tasks.revolut.ewallet.test

import com.bawer.tasks.revolut.ewallet.repository.cqengine.CQEngineAccountRepository
import com.bawer.tasks.revolut.ewallet.repository.cqengine.CQEngineTransferRepository


internal class CQEngineFunctionalTests : BaseFunctionalTests() {

    override val accountRepository = CQEngineAccountRepository()
    override val transferRepository = CQEngineTransferRepository()
}