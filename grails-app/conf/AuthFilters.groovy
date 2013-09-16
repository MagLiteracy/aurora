class AuthFilters {

    def filters = {
        all(uriExclude: "/init/index", uri: "/**") {
            before = {
                // Ignore direct views (e.g. the default main index page).
                if (!controllerName) return true

                // Access control by convention.
                accessControl()
            }

        }
    }
}
