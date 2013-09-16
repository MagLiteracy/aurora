package com.paypal.asgard

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

class ObjectLinkTagLib extends ApplicationTagLib {

    def linkObject = { attrs, body ->
        String objectId = attrs.remove('id')
        if (!objectId) { return }
        String objectType = attrs.remove('type')
        EntityType type = objectType ? EntityType.fromName(objectType) : EntityType.fromId(objectId)
        type.entitySpecificLinkGeneration(attrs, objectId)
        attrs['class'] = type.name()
        attrs.controller = type.name()
        attrs.action = attrs.action ?: 'show'
        attrs.params = attrs.params ?: [id: objectId]
        def compact = attrs.compact ? attrs.remove('compact') : null
        def title = attrs.remove('type')
        attrs.title = title?:type.linkPurpose
        String displayName = body() ?: attrs.remove('displayName') ?: objectId

        String linkText = compact ? '' : displayName
        def writer = getOut()
        writer << link(attrs, linkText)
    }


    /**
     * Creates a grails application link from a set of attributes. This
     * link can then be included in links, ajax calls etc. Generally used as a method call
     * rather than a tag eg.<br/>
     *
     * &lt;a href="${createLink(action:'list')}"&gt;List&lt;/a&gt;
     *
     * @attr controller The name of the controller to use in the link, if not specified the current controller will be linked
     * @attr action The name of the action to use in the link, if not specified the default action will be linked
     * @attr uri relative URI
     * @attr url A map containing the action,controller,id etc.
     * @attr base Sets the prefix to be added to the link target address, typically an absolute server URL. This overrides the behaviour of the absolute property, if both are specified.
     * @attr absolute If set to "true" will prefix the link target address with the value of the grails.serverURL property from Config, or http://localhost:&lt;port&gt; if no value in Config and not running in production.
     * @attr id The id to use in the link
     * @attr fragment The link fragment (often called anchor tag) to use
     * @attr params A map containing URL query parameters
     * @attr mapping The named URL mapping to use to rewrite the link
     * @attr event Webflow _eventId parameter
     */
    Closure<Object> createLink = { attrs ->
        super.createLink.call(attrs)
    }

}
