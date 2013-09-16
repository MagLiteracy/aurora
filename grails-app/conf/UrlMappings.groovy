class UrlMappings {
    static mappings = {

        "/externalImage/$name**"(controller: 'externalImage')

        "/$controller/$action?/$id?" {
            constraints {
                // apply constraints here
            }
        }

        // Optionally allow the region in the URL to the left of the controller.
        "/$region/$controller/$action?/$id?" {
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: 'instance', action: 'list')

        // http://en.wikipedia.org/wiki/List_of_HTTP_status_codes
        "400"(view: '/error')
        "401"(view: '/error')
        "403"(view: '/error')
        "404"(controller : 'error', action : 'formattedError')
        "405"(controller: 'error', action: 'formattedError')
        "406"(view: '/error')
        "409"(view: '/error')
        "414"(view: '/error')
        "415"(view: '/error')
        "500"(controller: 'error', action: 'handle')
        "501"(view: '/error')
        "502"(view: '/error')
        "503"(view: '/error')
        "504"(view: '/error')
        "505"(view: '/error')
        "509"(view: '/error')
        "510"(view: '/error')

    }
}
