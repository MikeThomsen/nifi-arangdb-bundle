package org.apache.nifi.processor

import com.arangodb.ArangoDB
import com.arangodb.entity.BaseDocument
import org.apache.nifi.controller.ArangoDBClientService
import org.apache.nifi.controller.ArangoDBClientServiceImpl
import org.apache.nifi.util.TestRunner
import org.apache.nifi.util.TestRunners
import org.junit.After

class AbstractArangoDBIT {
    TestRunner runner
    ArangoDBClientService clientService
    ArangoDB arangoDB

    void setup(Class clz) {
        clientService = new ArangoDBClientServiceImpl()
        runner = TestRunners.newTestRunner(clz)
        runner.addControllerService("clientService", clientService)
        runner.setProperty(clientService, ArangoDBClientServiceImpl.HOSTS, "localhost:8529")
        runner.setProperty(clientService, ArangoDBClientServiceImpl.LOAD_BALANCING_STRATEGY, ArangoDBClientServiceImpl.LOAD_BALANCE_RANDOM)
        runner.setProperty(clientService, ArangoDBClientServiceImpl.USERNAME, "root")
        runner.setProperty(clientService, ArangoDBClientServiceImpl.PASSWORD, "testing1234")
        runner.setProperty(AbstractArangoDBProcessor.CLIENT_SERVICE, "clientService")
        runner.setProperty(AbstractArangoDBProcessor.DATABASE_NAME, "nifi")
    }

    @After
    void tearDown() {
        if (arangoDB) {
            arangoDB.db("nifi").query("FOR message IN messages REMOVE message IN messages", BaseDocument.class)
            arangoDB.shutdown()
        }
    }
}
