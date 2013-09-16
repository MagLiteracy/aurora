class JsonFilters {

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                if (request.JSON != null) {
                    request.JSON.entrySet().each {entry ->
                        params.put(entry.key, entry.value)
                    }
                }
            }
        }
    }
}
