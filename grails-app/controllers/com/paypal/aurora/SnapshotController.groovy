package com.paypal.aurora

import com.paypal.aurora.exception.RestClientRequestException
import com.paypal.aurora.util.ConstraintsProcessor
import grails.converters.JSON
import grails.converters.XML

import javax.servlet.http.HttpServletResponse

class SnapshotController {

    def static allowedMethods = [list: ['GET', 'POST'], show: ['GET', 'POST'], save: ['GET', 'POST'], delete: ['GET', 'POST']]

    def index = { redirect(action: 'list', params: params) }

    def snapshotService

    def list = {
        def snapshots
        def error
        try{
            snapshots = snapshotService.getAllSnapshots()
        } catch (RestClientRequestException e){
            error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        }
        def model = [snapshots : snapshots, errors : error]
        withFormat {
            html { model }
            xml { new XML(model).render(response) }
            json { new JSON(model).render(response) }
        }
    }

    def _snapshots = {
        def snapshots = []
        def error
        try{
            snapshots = snapshotService.getAllSnapshots()
        } catch (RestClientRequestException e){
            error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        }
        def model = [snapshots : snapshots, errors : error]
        withFormat {
            html { model }
            xml { new XML(model).render(response) }
            json { new JSON(model).render(response) }
        }
    }

    def show = {
        try{
            def snapshot = snapshotService.getSnapshotById(params.id)
            def model = [snapshot : snapshot]
            withFormat {
                html { [parent:"/snapshot",snapshot: snapshot] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def errors = ExceptionUtils.getExceptionMessage(e)
            withFormat {
                html { flash.message = errors; redirect([parent:"/snapshot", action: 'list'])}
                xml { new XML([errors: errors]).render(response) }
                json { new JSON([errors: errors]).render(response) }
            }
        }
    }

    def delete = {
        List<String> snapshotIds = Requests.ensureList(params.selectedSnapshots ?: params.id)
        List<String> notRemovedSnapshotIds = []
        def deleted = []
        def error = [:]
        for (snapshotId in snapshotIds) {
            try {
                deleted << snapshotService.deleteSnapshotById(snapshotId)
            } catch (RestClientRequestException e) {
                log.error "Could not delete shapshot: ${e}"
                notRemovedSnapshotIds << snapshotIds
                error[snapshotId] = ExceptionUtils.getExceptionMessage(e)
            }
        }
        def flashMessage = null
        if (notRemovedSnapshotIds) {
            def ids = notRemovedSnapshotIds.join(',')
            flashMessage = "Could not delete snapshots with id: ${ids}"
        }
        def view = [deleted: deleted,not_deleted_ids:notRemovedSnapshotIds, errors : error]
        withFormat {
            html { flash.message = flashMessage; redirect(action: 'list')}
            xml { new XML(view).render(response) }
            json { new JSON(view).render(response) }
        }
    }

    def create = {
        withFormat {
            html {[parent:"/snapshot", constraints: ConstraintsProcessor.getConstraints(SnapshotCreateCommand.class)]}
        }
    }

    def save = { SnapshotCreateCommand cmd ->

        if (cmd.hasErrors()) {
            response.status = 400
            withFormat {
                html { chain(action: 'create', model: [cmd : cmd], params: params) }
                xml {new XML([errors : cmd.errors]).render(response)}
                json {new JSON([errors : cmd.errors]).render(response)}
            }
        } else {
            try {
                def resp = snapshotService.createSnapshot(params);
                def model = [resp : resp]
                withFormat {
                    html { redirect(action: 'list') }
                    xml { new XML(resp).render(response) }
                    json { new JSON(resp).render(response) }
                }
            } catch (RestClientRequestException e) {
                def errors = ExceptionUtils.getExceptionMessage(e)
                withFormat {
                    html { flash.message = errors; chain(action: 'create', params: params)}
                    xml { new XML([errors: errors]).render(response) }
                    json { new JSON([errors: errors]).render(response) }
                }
            }
        }
    }

}
class SnapshotCreateCommand {

    String name
    String description

    static constraints = {
        name(nullable: false, blank: false)
        description(nullable: false, blank: false)
    }
}
