import org.apache.ivy.plugins.resolver.FileSystemResolver
import org.apache.ivy.plugins.resolver.URLResolver

grails.project.work.dir = 'work'
grails.project.class.dir = 'target/classes'
grails.project.test.class.dir = 'target/test-classes'
grails.project.test.reports.dir = 'target/test-reports'
grails.project.war.file = "target/${appName}.war"

codenarc {
    reports = {
        AuroraXmlReport('xml') {
            outputFile = 'CodeNarc-Report.xml'
            title = 'Aurora CodeNarc Report'
        }
        AuroraHtmlReport('html') {
            outputFile = 'CodeNarc-Report.html'
            title = 'Aurora CodeNarc Report'
        }
    }
    ruleSetFiles = 'file:grails-app/conf/CodeNarcRuleSet.groovy'
    maxPriority1Violations = 1
}

coverage {
    // list of directories to search for source to include in coverage reports
    exclusions = ['**/*Controller*', '**/*$*', '**/model/**', '**/auth/**', '**/exception/**', '**/format/**', '**/joke/**',
                  '**/listeners/**', '**/util/**', '**/*Filter*', '**/*Command*', '**/*TagLib*', '**/*Realm*', '**/*Utils*',
                  '**/Requests*', '**/OpenStackRESTService*', '**/EntityType*', '**/ConfigService*', '**/EventSubscribeService*',
                  '**/JokeService*', '**/OpenStackService*', '**/SessionStorageService*', '**/RestApiHandlerService*',
                  '**/Styler*', '**/FastProperty*', '**/CodeNarcRuleSet*', '**/Occasion*', '**/ServiceInitLoggingBeanPostProcessor*',
                  '**/Constant*', '**/*gsp_*']
}

grails.project.dependency.resolution = {
    // Inherit Grails' default dependencies
    inherits('global') {}

    log 'warn'

    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
        mavenRepo "http://repository.codehaus.org"
        // Optional custom repository for dependencies.
        Closure internalRepo = {
            String repoUrl = 'http://artifacts/ext-releases-local'
            String artifactPattern = '[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]'
            String ivyPattern = '[organisation]/[module]/[revision]/[module]-[revision]-ivy.[ext]'
            URLResolver urlLibResolver = new URLResolver()
            urlLibResolver.with {
                name = repoUrl
                addArtifactPattern("${repoUrl}/${artifactPattern}")
                addIvyPattern("${repoUrl}/${ivyPattern}")
                m2compatible = true
            }
            resolver urlLibResolver

            String localDir = System.getenv('IVY_LOCAL_REPO') ?: "${System.getProperty('user.home')}/ivy2-local"
            FileSystemResolver localLibResolver = new FileSystemResolver()
            localLibResolver.with {
                name = localDir
                addArtifactPattern("${localDir}/${artifactPattern}")
                addIvyPattern("${localDir}/${ivyPattern}")
            }
            resolver localLibResolver
        }
        // Comment or uncomment the next line to toggle the use of an internal artifacts repository.
        //internalRepo()
    }

    dependencies {

        compile(
                // Transitive dependencies of aws-java-sdk, but also used directly
                'org.apache.httpcomponents:httpcore:4.1',
                'org.apache.httpcomponents:httpclient:4.1.1',

                // Explicitly including aws-java-sdk transitive dependencies
                'org.codehaus.jackson:jackson-core-asl:1.8.9',
                'org.codehaus.jackson:jackson-mapper-asl:1.8.9',

                // Extra collection types and utilities
                'commons-collections:commons-collections:3.2.1',

                // Easier Java from of the Apache Foundation
                'commons-lang:commons-lang:2.4',

                // Easier Java from Joshua Bloch and Google
                'com.google.guava:guava:12.0',

                // SSH calls to retrieve secret keys from remote servers
                'com.jcraft:jsch:0.1.45',

                // Send emails about system errors and task completions
                'javax.mail:mail:1.4.1',

                // Better date API
                'joda-time:joda-time:1.6.2',

                // Delete when Amazon provides a proper instance type API. Web scraping API to parse poorly formed HTML.
                'org.jsoup:jsoup:1.6.1',

                // Static analysis for Groovy code.
                'org.codenarc:CodeNarc:0.19',

                // This fixes ivy resolution issues we had with our transitive dependency on 1.4.
                'commons-codec:commons-codec:1.5',

                // Call Perforce in process. Delete when user data no longer come from Perforce at deployment time.
                'com.perforce:p4java:2010.1.269249',

                // Groovy concurrency framework.
                'org.codehaus.gpars:gpars:1.0.0',

                // For REST client
                'org.codehaus.groovy.modules.http-builder:http-builder:0.6'

        ) { // Exclude superfluous and dangerous transitive dependencies
            excludes(
                    // Some libraries bring older versions of JUnit as a transitive dependency and that can interfere
                    // with Grails' built in JUnit
                    'junit',

                    'mockito-core',
            )
        }

        // Optional dependency for Spock to support mocking objects without a parameterless constructor.
        test 'org.objenesis:objenesis:1.2'
        test "org.spockframework:spock-grails-support:0.7-groovy-1.8"
        test 'org.gmock:gmock:0.8.3'
    }

    plugins {
        compile ":hibernate:$grailsVersion"
        compile ":compress:0.4"
        compile ":context-param:1.0"
        compile ':shiro:1.1.4'
        compile ":standalone:1.1.1"

        test ':spock:0.7'

        test ':code-coverage:1.2.5'

        build ":tomcat:$grailsVersion"
    }

}
