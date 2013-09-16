import org.apache.log4j.DailyRollingFileAppender

// http://grails.org/doc/latest/guide/3.%20Configuration.html#3.1.2 Logging
log4j = {

    appenders {
        def logDirectory = System.properties.getProperty('logger.dir') ?: '.'

        appender new DailyRollingFileAppender(
                name: 'aurorarolling',
                file: "$logDirectory/aurora.log",
                layout: pattern(conversionPattern: '[%d{ISO8601}] [%t] %c{4}    %m%n'),
                datePattern: "'.'yyyy-MM-dd")
        rollingFile(
                name: "stacktrace",
                file: "$logDirectory/stacktrace.log",
                maxFileSize : '500MB'
        )
    }
    def logLevel = System.properties.getProperty('logger.level') ?: 'info'
    logLevel = logLevel.toLowerCase()

    // Suppress most noise from libraries
    error 'grails.spring', 'net.sf.ehcache', 'org.springframework', 'org.hibernate',
            'org.apache.catalina', 'org.apache.commons', 'org.apache.coyote', 'org.apache.jasper', 'org.apache.tomcat',
            'org.codehaus.groovy.grails'

    debug 'org.apache.http.wire'

    environments {
        development {
            console name: 'stdout', layout: pattern(conversionPattern: '[%d{ISO8601}] %c{4}    %m%n')
            root {
                "$logLevel" 'stdout'
            }
        }
        production {
            root {
                "$logLevel" 'aurorarolling'
            }
        }
    }
}

auroraHome = System.getenv('AURORA_HOME') ?: System.getProperty('AURORA_HOME') ?: '/etc/aurora'

println "Using ${auroraHome} as AURORA_HOME"


appConfigured = new File(auroraHome, 'Config.json').exists()

grails.app.context = '/'
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.types = [
        html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml'],
        json: ['application/json', 'text/json'],
        js: 'text/javascript',
        rss: 'application/rss+xml',
        atom: 'application/atom+xml',
        text: 'text-plain',
        css: 'text/css',
        csv: 'text/csv',
        all: '*/*',
        form: 'application/x-www-form-urlencoded',
        multipartForm: 'multipart/form-data'
]

// The default codec used to encode data with ${}
grails.views.default.codec = 'none' // none, html, base64
grails.views.gsp.encoding = 'UTF-8'

// Configuration for JSON and XML converters
grails.converters.encoding = 'UTF-8'
grails.converters.default.pretty.print = true
grails.converters.json.default.deep = false
grails.converters.xml.default.deep = false

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

grails.exceptionresolver.params.exclude = ['password', 'j_password']

grails.spring.bean.packages = ['com.paypal.aurora.subscriber', 'com.paypal.aurora.listeners']

security.shiro.annotationdriven.enabled = true


thread {
    useJitter = true
}

plugin {
    taskFinishedListeners = ['snsTaskFinishedListener']
}

promote {
    imageTags = false
}

ticket {
    label = 'Ticket'
}

server {
    online = true

}

environments {
    development {
        server.online = !System.getProperty('offline')
        if (!server.online) { println 'Config: working offline' }
        plugin {
            refreshDelay = 5000
        }
    }
    test {
        server.online = false
    }
    production {
        cloud {
            envStyle = 'prod'
        }
    }
}
