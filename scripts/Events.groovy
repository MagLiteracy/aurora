/**
 * Generate a property file to be sucked in via Config.groovy, which specifies certain build-time properties.
 * This block is run after every compile, so that the resultant file is available before the tests are run.
 * Ideally this would only run with kind=='compile' and not test too.
 */
eventCompileEnd = { kind ->
    String sourceVersionFile = "${classesDirPath}/sourceVersion.properties"
    ant.propertyfile(file: sourceVersionFile) {
        entry(key: 'scm.commit', value: System.getenv('GIT_COMMIT') ?: '')
        entry(key: 'build.id', value: System.getenv('BUILD_ID') ?: '')
        entry(key: 'build.number', value: System.getenv('BUILD_NUMBER') ?: '')
    }
}
