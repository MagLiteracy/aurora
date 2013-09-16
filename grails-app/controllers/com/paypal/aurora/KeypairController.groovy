package com.paypal.aurora

import com.paypal.aurora.exception.RestClientRequestException
import com.paypal.aurora.model.Keypair
import com.paypal.aurora.util.ConstraintsProcessor
import grails.converters.JSON
import grails.converters.XML

import javax.servlet.http.HttpServletResponse

class KeypairController {

    def keypairService
    def static allowedMethods = [list: ['GET','POST'], save: ['GET','POST'], delete: ['POST'], insertKeypair: 'POST', download: 'POST']

    def index = { redirect(action: 'list', params: params) }

    def list = {
        List<Keypair> keypairs = []
        try{
            keypairs = keypairService.listAll()
            def model = [keypairs: keypairs]
            withFormat {
                html { model }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [keypairs: keypairs, errors : error]
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            withFormat {
                html { model }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        }
    }

    def delete = {
        List<String> keypairNames = Requests.ensureList(params.selectedKeypairs ?: params.keypairName)
        def resp = []
        def not_deleted = []
        def error = [:]
        for (name in keypairNames) {
            try{
                resp << keypairService.delete(name)
            } catch(RestClientRequestException e){
                not_deleted << name
                error[name] = ExceptionUtils.getExceptionMessage(e)
            }
        }
        def model = [resp : resp, not_deleted : not_deleted, errors : error]
        withFormat {
            html { redirect(action: 'list') }
            xml { new XML(model).render(response) }
            json { new JSON(model).render(response) }
        }
    }

    def create = {
        [parent:"/keypair", constraints: ConstraintsProcessor.getConstraints(KeypairCreateCommand.class) ]
    }

    def insert = {
        [parent:"/keypair", constraints: ConstraintsProcessor.getConstraints(KeypairInsertCommand.class) ]
    }

    def save = { KeypairCreateCommand cmd ->
        Keypair keypair
        if (cmd.hasErrors()) {
            withFormat {
                html { chain(action: 'create', model: [cmd: cmd], params: params) }
                xml { new XML([errors:cmd.errors]).render(response) }
                json { new JSON([errors:cmd.errors]).render(response) }
            }
        } else {
            try {
                keypair = keypairService.create(params.name)
                def model = [keypair: keypair]
                withFormat {
                    html { [keypair:keypair,parent: "/keypair",] }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            } catch (RestClientRequestException e){
                def error = ExceptionUtils.getExceptionMessage(e)
                def model = [errors : error]
                withFormat {
                    html { flash.message = error; redirect(action : 'list')}
                    xml {new XML(model).render(response)}
                    json {new JSON(model).render(response)}
                }
            }
        }
    }
    def download = {
        response.setContentType("application/octet-stream")
        response.setContentLength(params.key.length())
        response.setHeader("Content-disposition", "attachment; filename=" +
                params.name + ".pem")
        response.outputStream << params.key.bytes
    }

    def insertKeypair = { KeypairInsertCommand cmd ->
        if (cmd.hasErrors()) {
            withFormat {
                html { chain(action: 'insert', model: [cmd: cmd], params: params) }
                xml { new XML([errors:cmd.errors]).render(response) }
                json { new JSON([errors:cmd.errors]).render(response) }
            }
        } else {
            try{
                def resp = keypairService.insert(params.name, params.publicKey)
                def model = [resp : resp]
                withFormat {
                    html { redirect(action: 'list') }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            } catch (RestClientRequestException e){
                def error = ExceptionUtils.getExceptionMessage(e)
                def model = [errors : error]
                withFormat {
                    html { flash.message = error; redirect(action : 'list')}
                    xml { new XML(model).render(response)}
                    json { new JSON(model).render(response)}
                }
            }
        }
    }

}
class KeypairCreateCommand {

    def keypairService


    String name

    static constraints = {
        name(nullable: false, blank: false, matches: /[\w-]+/, validator: { value, command ->
            if (command.keypairService.exists(value)){
                return "keypairCreateCommand.name.exists"
            }
        })
    }
}
class KeypairInsertCommand {

    def keypairService

    String name
    String publicKey

    //,

    static constraints = {
        name(nullable: false, blank: false, matches: /[\w-]+/, validator: { value, command ->
            if (command.keypairService.exists(value)){
                return "keypairInsertCommand.name.exists"
            }
        })
        publicKey(nullable: false, blank: false, matches: /ssh-rsa AAAA[0-9A-Za-z+\/]+[=]{0,3} ([^@]+@[^@]+)/)
    }
}