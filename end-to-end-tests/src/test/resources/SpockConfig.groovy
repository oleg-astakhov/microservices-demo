runner {
    parallel {
        def doEnable = System.properties['runInParallel'] == "true"
        println "Parallel test execution mode enabled: " + doEnable
        enabled doEnable
    }
}