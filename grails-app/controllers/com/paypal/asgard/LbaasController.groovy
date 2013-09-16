package com.paypal.asgard

import com.paypal.asgard.exception.RestClientRequestException
import com.paypal.asgard.model.*
import com.paypal.asgard.util.ConstraintsProcessor
import grails.converters.JSON
import grails.converters.XML

import javax.servlet.http.HttpServletResponse

class LbaasController {

    final static allowedMethods = [saveVip : ['GET', 'POST'], listPolicies: 'GET', listPools: ['GET', 'POST'], showPool: 'GET',listMethods: 'GET', listMonitors: 'GET', savePool: ['GET', 'POST'], updatePool: ['GET', 'POST'], delete: 'POST', saveService: 'POST', enableService: 'POST',disableService: 'POST',deleteService: ['GET', 'POST'], listJobs: ['GET', 'POST'], showJob: ['GET', 'POST'],listVips: ['GET', 'POST'], showVip: ['GET', 'POST'], createVip: ['GET', 'POST'],deleteVip: ['GET', 'POST'], savePolicy: ['GET', 'POST'], updatePolicy: ['GET', 'POST'], deletePolicy: ['POST'], addPool: 'GET', editPool: 'GET', addService: 'GET', getInstancesByPoolName: 'GET']

    def lbaasService
    def instanceService
    def tenantService
    def networkService
    def sessionStorageService

    def index = { redirect(action: 'listPools', params: params) }

    def listPools = {
        try {
            List <Pool> listPools = lbaasService.allPools
            withFormat {
                html { ['pools': listPools] }
                xml { new XML([pools : listPools]).render(response) }
                json { new JSON([pools : listPools]).render(response) }
            }
        } catch (RestClientRequestException e) {
            def error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            withFormat {
                html { ['pools' : [], flash: [message: error]]}
                xml { new XML([errors : error]).render(response)}
                json { new JSON([errors : error]).render(response)}
            }
        }
    }


    def listMethods = {
        try{
            List <String> listMethods = lbaasService.getMethods();
            withFormat {
                xml { new XML([Methods : listMethods]).render(response) }
                json { new JSON([Methods : listMethods]).render(response) }
            }
        }catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            withFormat {
                xml {new XML([errors : error]).render(response)}
                json { new JSON([errors : error]).render(response)}
            }
        }
    }

    def listMonitors = {
        try{
            List <String> listMonitors = lbaasService.getMonitors()
            withFormat {
                xml { new XML([Monitors : listMonitors]).render(response) }
                json { new JSON([Monitors : listMonitors]).render(response) }
            }
        }catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            withFormat {
                xml { new XML([errors : error]).render(response)}
                json { new JSON([errors : error]).render(response)}
            }
        }
    }


    def showPool = {
        try{
            Pool pool = lbaasService.getPool(params.id)
            List<LBService> services = lbaasService.getServices(params.id)
            def model = [pool : pool, services: services]
            withFormat {
                html { [parent: "/lbaas/listPools", pool: pool, services: services] }
                xml { new XML(model).render(response)}
                json { new JSON(model).render(response)}
            }
        }catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            withFormat {
                html { flash.message = error; redirect(action : 'listPools')}
                xml { new XML([errors : error]).render(response) }
                json { new JSON([errors : error]).render(response) }
            }
        }
    }

    def listJobs = {
        try{
            List<Job> listJobs = lbaasService.getJobs()?.reverse()
            withFormat {
                html { model: ['jobs': listJobs] }
                xml { new XML([listJobs : listJobs]).render(response) }
                json { new JSON([listJobs : listJobs]).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            withFormat {
                html { model :['jobs' : [], flash : [message : error]] }
                xml { new XML([errors : error]).render(response) }
                json { new JSON([errors : error]).render(response) }
            }
        }
    }

    def _jobs = {
        try{
            List<Job> listJobs = lbaasService.getJobs()?.reverse()
            withFormat {
                html { model: ['jobs': listJobs] }
                xml { new XML([listJobs : listJobs]).render(response) }
                json { new JSON([listJobs : listJobs]).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            withFormat {
                html { model :['jobs' : [], flash : [message : error]] }
                xml { new XML([errors : error]).render(response) }
                json { new JSON([errors : error]).render(response) }
            }
        }
    }

    def showJob = {
        Job job = lbaasService.getJobById(params.id)
        withFormat {
            html {[parent: '/lbaas/listJobs',job: job]}
            xml {new XML([job: job]).render(response)}
            json {new JSON([job: job]).render(response)}
        }
    }

    def listVips = {
        try{
            List<Vip> listVips = lbaasService.getVips()
            withFormat {
                html { [vips : listVips] }
                xml { new XML([vips : listVips]).render(response) }
                json { new JSON([vips : listVips]).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            withFormat {
                html {[vips : [], flash: [message: error]]}
                xml { new XML([errors : error]).render(response) }
                json { new JSON([errors : error]).render(response) }
            }
        }
    }


    def createVip = {
        params.allowedProtocols = allowedProtocols
        [parent: "/lbaas/listVips", constraints: ConstraintsProcessor.getConstraints(VipValidationCommand.class),
                params: params]
    }

    def saveVip = { VipValidationCommand cmd ->
        if (cmd.hasErrors()) {
            withFormat {
                html { chain(action: 'createVip', model: [cmd: cmd], params: params) }
                xml { new XML([errors: cmd.errors]).render(response) }
                json { new JSON([errors: cmd.errors]).render(response) }
            }
        } else {
            try {
                def resp = lbaasService.createVip(params)
                def model = [resp : resp]
                withFormat {
                    html { redirect(action: 'listVips') }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            } catch (RestClientRequestException e) {
                def errors = ExceptionUtils.getExceptionMessage(e)
                response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                withFormat {
                    html { flash.message = errors; chain(action: 'createVip', params: params) }
                    xml { new XML([errors: errors]).render(response) }
                    json { new JSON([errors: errors]).render(response) }
                }
            }

        }
    }

    def showVip = {
        try {
            def vip = lbaasService.getVip(params.id)
            def model = [vip : vip]
            withFormat {
                html { [parent: "/lbaas/listVips", vip: vip] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e) {
            def errors = ExceptionUtils.getExceptionMessage(e)
            withFormat {
                html { flash.message = errors; redirect(action: 'listVips')}
                xml { new XML([errors: errors]).render(response) }
                json { new JSON([errors: errors]).render(response) }
            }
        }
    }

    def deleteVip = {
        List<String> vipNames = Requests.ensureList(params.selectedVips ?: params.id)
        List<String> notRemovedVips = []
        def deleted = []
        def error = [:]
        for (vipName in vipNames) {
            try {
                lbaasService.deleteVip(vipName)
                deleted << vipName
            } catch (RestClientRequestException e) {
                log.error(e)
                notRemovedVips << vipName
                error[vipName] = ExceptionUtils.getExceptionMessage(e)
            }
        }
        def flashMessage = null
        if (notRemovedVips) {
            def names = notRemovedVips.join(',')
            flashMessage = "Could not delete vips with name: ${names}"
            response.status = 400
        }
        def view = [deleted: deleted, not_deleted_ids : notRemovedVips, errors : error]
        withFormat {
            html { flash.message = flashMessage; redirect(action: 'listVips')}
            xml { new XML(view).render(response) }
            json { new JSON(view).render(response) }
        }
    }

    def addService = {
        def pool = lbaasService.getPool(params.id)
        def instances = instanceService.getAllActiveInstances()
        params.weight = 10
        [pool: pool, instances: instances, parent: "/lbaas/showPool/${params.id}",
                constraints: ConstraintsProcessor.getConstraints(ServiceCreateCommand.class),
                isUseQuantumFLIP: networkService.isUseExternalFLIP()]
    }

    def savePool = {PoolCreateCommand cmd ->
        if (params.monitors && !cmd.monitors){
            cmd.monitors = params.monitors
            cmd.validate()
        }
        if (cmd.hasErrors()) {
            withFormat {
                html { chain(action: 'addPool', model: [cmd: cmd], params: params) }
                xml { new XML([errors: cmd.errors]).render(response) }
                json { new JSON([errors: cmd.errors]).render(response) }
            }
        } else {
            try {
                params.monitors = Requests.ensureList(params.monitors)
                lbaasService.createPool(params)
                def resp = lbaasService.addServices(Requests.ensureList(params.instances),
                        (String)params.name,
                        (String)null,
                        (String)params.netInterface,
                        (String)params.servicePort,
                        (String)params.serviceWeight,
                        (boolean)(params.serviceEnabled == 'on')
                )
                def model = [resp : resp]
                withFormat {
                    html { redirect(action: 'listPools') }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            } catch (RestClientRequestException e) {
                def errors = ExceptionUtils.getExceptionMessage(e)
                withFormat {
                    html { flash.message = errors; chain(action: 'addPool', params: params)}
                    xml { new XML([errors: errors]).render(response) }
                    json { new JSON([errors: errors]).render(response) }
                }
            }
        }
    }

    def delete = {
        List<String> poolsNames = Requests.ensureList(params.selectedPools)
        List<String> notRemovedPools = []
        def deleted = []
        def error = [:]
        for (pool in poolsNames){
            try{
                lbaasService.deletePool(pool)
                deleted << pool
            }catch (RestClientRequestException e){
                notRemovedPools << pool
                error[pool] = ExceptionUtils.getExceptionMessage(e)
            }
        }
        def model = [deleted: deleted, not_deleted_ids : notRemovedPools, errors : error]
        withFormat {
            html { redirect(action: 'listPools') }
            xml { new XML(model).render(response) }
            json { new JSON(model).render(response) }
        }
    }

    def deleteService = {
        List<String> servicesNames = Requests.ensureList(params.selectedServices)
        List<String> notRemovedServices = []
        def deleted = []
        def error = [:]
        for (service in servicesNames){
            try{
                lbaasService.deleteService(params.pool, service)
                deleted << service
            } catch (RestClientRequestException e){
                notRemovedServices << service
                error[service] = ExceptionUtils.getExceptionMessage(e)
            }
        }
        def model = [deleted : deleted, not_deleted_ids : notRemovedServices, errors : error]
        withFormat {
            html { redirect(action: 'showPool', params: [id: params.pool]) }
            xml { new XML(model).render(response)}
            json { new JSON(model).render(response) }
        }
    }

    def enableService = {
        try{
            List<String> servicesNames = Requests.ensureList(params.selectedServices)
            lbaasService.changeEnabled(params.pool, servicesNames, true)
            withFormat {
                html { redirect(action: 'showPool', params: [id: params.pool]) }
                xml { new XML([status: 'OK']).render(response) }
                json { new JSON([status: 'OK']).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            withFormat {
                html { flash.message = error; redirect(action: 'showPool', params: [id: params.pool])}
                xml {new XML([errors : error]).render(response)}
                json {new JSON([errors : error]).render(response)}
            }
        }
    }

    def disableService = {
        try{
            List<String> servicesNames = Requests.ensureList(params.selectedServices)
            lbaasService.changeEnabled(params.pool, servicesNames, false)
            withFormat {
                html { redirect(action: 'showPool', params: [id: params.pool]) }
                xml {new XML([status : 'OK']).render(response)}
                json { new JSON([status: 'OK']).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html { flash.message = error; redirect(action: 'showPool', params: [id: params.pool])}
                xml {new XML(model).render(response)}
                json { new JSON(model).render(response) }
            }
        }
    }

    def saveService = { ServiceCreateCommand cmd ->
        if (cmd.hasErrors()) {
            withFormat {
                html { chain(action: "addService", model: [cmd: cmd], params: params) }
                xml { new XML([errors: cmd.errors]).render(response) }
                json { new JSON([errors: cmd.errors]).render(response) }
            }
        } else {
            try{
                lbaasService.addServices(Requests.ensureList(params.instanceId),
                        params.id,
                        params.name,
                        params.netInterface,
                        params.port,
                        params.weight,
                        params.enabled == 'on'
                )
                withFormat {
                    html { redirect(action: 'showPool', params: [id: params.id]) }
                    xml {new XML([status : 'OK']).render(response)}
                    json { new JSON([status: 'OK']).render(response) }
                }
            } catch (RestClientRequestException e){
                def error = ExceptionUtils.getExceptionMessage(e)
                def model = [errors : error]
                withFormat {
                    html { flash.message = error; redirect(action: 'showPool', params: [id: params.id])}
                    xml {new XML(model).render(response)}
                    json {new JSON(model).render(response)}
                }
            }
        }
    }

    def addPool = {
        [methods: getMethods(),
                monitors: getMonitors(),
                instances: instanceService.getAllActiveInstances(),
                parent: "/lbaas",
                constraints: ConstraintsProcessor.getConstraints(PoolCreateCommand.class),
                isUseQuantumFLIP: networkService.isUseExternalFLIP()]
    }

    def editPool = {
        try{
            Pool pool = lbaasService.getPool(params.id)
            def model = [name: params.name != null ? params.name: pool.name,
                    enabled: params.enabled ?: pool.enabled,
                    lbMethod: params.lbMethod ?: pool.method,
                    monitors: params.monitors ?: pool.monitors,
                    methods: getMethods(),
                    allMonitors: getMonitors(),
                    parent: "/lbaas/showPool/$params.id",
                    id: params.id,
                    ]
            withFormat {
                html { [parent: "/lbaas/showPool/$params.id", params: model, constraints: ConstraintsProcessor.getConstraints(PoolCreateCommand.class)] }
                xml { new XML(model).render(response)}
                json { new JSON(model).render(response)}
            }
        }catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            withFormat {
                html { flash.message = error; redirect(action : 'showPool', params: [id: params.id])}
                xml { new XML([errors : error]).render(response) }
                json { new JSON([errors : error]).render(response) }
            }
        }
    }

    def updatePool = { PoolCreateCommand cmd ->
        if (params.monitors && !cmd.monitors){
            cmd.monitors = params.monitors
            cmd.validate()
        }
        if (cmd.hasErrors()) {
            withFormat {
                html { chain(action: 'editPool', model: [cmd: cmd], params: params) }
                xml { new XML([errors: cmd.errors]).render(response) }
                json { new JSON([errors: cmd.errors]).render(response) }
            }
        } else {
            try {
                params.monitors = Requests.ensureList(params.monitors)
                def model = lbaasService.updatePool(params)
                withFormat {
                    html { chain(action: 'showPool', params: [id: params.name])}
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            } catch (RestClientRequestException e) {
                def errors = ExceptionUtils.getExceptionMessage(e)
                withFormat {
                    html { flash.message = errors; chain(action: 'editPool', params: params)}
                    xml { new XML([errors: errors]).render(response) }
                    json { new JSON([errors: errors]).render(response) }
                }
            }
        }
    }

    def getMonitors() {
        sessionStorageService.customServices[0].find{it.type == 'lbms'}?.monitors?:lbaasService.monitors
    }

    def getMethods() {
        sessionStorageService.customServices[0].find{it.type == 'lbms'}?.methods?:lbaasService.methods
    }

    def getAllowedProtocols() {
        sessionStorageService.customServices[0].find{it.type == 'lbms'}?.allowedProtocols?:[]
    }

    def listPolicies = {
        def error
        if (params.tenantName) {
            def tenant = tenantService.getTenantByName(params.tenantName)
            List<Policy> policies
            try {
               policies = lbaasService.getPolicies(params.tenantName)
            } catch (RestClientRequestException e) {
                error = ExceptionUtils.getExceptionMessage(e)
                response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            }
            def map = [parent: "/tenant/show/${tenant.id}", policies: policies, tenantName: params.tenantName]
            withFormat {
                html { flash.message = error; map }
                xml { new XML([policies: policies, errors : error]).render(response) }
                json { new JSON([policies: policies, errors : error]).render(response) }
            }
        } else {
            List<Policy> policies
            try{
                policies = lbaasService.getPolicies()
            } catch (RestClientRequestException e){
                error = ExceptionUtils.getExceptionMessage(e)
            }
            def map = [policies: policies, errors : error,  flash: [message: error]]
            withFormat {
                html { map }
                xml { new XML(map).render(response) }
                json { new JSON(map).render(response) }
            }
        }
    }

    def createPolicy = {
        if (params.tenantName) {
            [parent: "/lbaas/listPolicies?tenantName=${params.tenantName}", constraints: ConstraintsProcessor.getConstraints(PolicyValidationCommand.class)]
        } else {
            [parent: '/lbaas/listPolicies', constraints: ConstraintsProcessor.getConstraints(PolicyValidationCommand.class)]
        }

    }

    def deletePolicy = {
        List<String> policyNames = Requests.ensureList(params.selectedPolicies ?: params.id)
        List<String> notRemovedPolicyNames = []
        def deleted = []
        def error = [:]
        for (name in policyNames) {
            try{
                lbaasService.deletePolicy(name, params.tenantName)
                deleted << name
            } catch (RestClientRequestException e) {
                response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                notRemovedPolicyNames << name
                error[name] = ExceptionUtils.getExceptionMessage(e)
            }
        }
        def view = [deleted : deleted, not_deleted_ids: notRemovedPolicyNames, error : error]
        withFormat {
            html { defineRedirectParams(params) }
            xml { new XML(view).render(response) }
            json { new JSON(view).render(response) }
        }
    }

    def editPolicy = {
        Policy policy = lbaasService.getPolicy(params.id, params.tenantName)
        if (params.tenantName) {
            [parent: "/lbaas/listPolicies?tenantName=${params.tenantName}", policy: policy, constraints: ConstraintsProcessor.getConstraints(PolicyValidationCommand.class)]
        } else {
            [parent: '/lbaas/listPolicies', policy: policy, constraints: ConstraintsProcessor.getConstraints(PolicyValidationCommand.class)]
        }
    }

    def updatePolicy = { PolicyValidationCommand cmd ->
        if (cmd.hasErrors()) {
            withFormat {
                html { chain(action: 'editPolicy', model: [cmd: cmd], params: params) }
                xml { new XML([errors: cmd.errors]).render(response) }
                json { new JSON([errors: cmd.errors]).render(response) }
            }
        } else {
            try {
                def resp = lbaasService.updatePolicy(params, params.tenantName)
                def model = [resp : resp]
                withFormat {
                    html { defineRedirectParams(params) }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            } catch (RestClientRequestException e) {
                def errors = ExceptionUtils.getExceptionMessage(e)
                withFormat {
                    html { flash.message = errors; chain(action: 'editPolicy', params: params)}
                    xml { new XML([errors: errors]).render(response) }
                    json { new JSON([errors: errors]).render(response) }
                }
            }
        }
    }

    def savePolicy = { PolicyValidationCommand cmd ->
        if (cmd.hasErrors()) {
            withFormat {
                html { chain(action: 'createPolicy', model: [cmd: cmd], params: params) }
                xml { new XML([errors: cmd.errors]).render(response) }
                json { new JSON([errors: cmd.errors]).render(response) }
            }
        } else {
            try {
                def resp = lbaasService.createPolicy(params, params.tenantName)
                def model = [resp : resp]
                withFormat {
                    html { defineRedirectParams(params) }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            } catch (RestClientRequestException e) {
                def errors = ExceptionUtils.getExceptionMessage(e)
                withFormat {
                    html { flash.message = errors; chain(action: 'createPolicy', params: params)}
                    xml { new XML([errors: errors]).render(response) }
                    json { new JSON([errors: errors]).render(response) }
                }
            }
        }
    }

    def defineRedirectParams(def params) {
        if (params.tenantName) {
            redirect(action: 'listPolicies', params: [tenantName: params.tenantName])
        } else {
            redirect(action: 'listPolicies')
        }
    }

}

class PoolCreateCommand {
    String name
    String lbMethod
    def monitors
    String enabled
    static constraints = {
        name(nullable: false, blank: false)
        lbMethod(nullable: false, blank: false)
        monitors(nullable: false)
        enabled(nullable: true, blank: true, matches: /on/)
    }
}

class ServiceCreateCommand {

    String name
    String instanceId
    String netInterface
    String port
    String weight
    String enabled

    static constraints = {
        name(nullable: false, blank: false, validator: {name, obj ->
            if (!name.endsWith(":" + obj.port)) {
                return "serviceCreateCommand.name.validator.error"
            }
        })
        instanceId(nullable: false, blank: false)
        netInterface(nullable: false, blank: false)
        port(nullable: false, blank: false, matches: /\d+/)
        weight(nullable: false, blank: false, matches: /\d+/)
        enabled(nullable: true, blank: true, matches: /on/)
    }
}


class VipValidationCommand {
    String ip
    String name
    String port
    String protocol
    String enabled

    static constraints = {
        ip(nullable: false, blank: false, matches: /\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}/)
        name(nullable: false, blank: false)
        port(nullable: false, blank: false, validator: {port, obj ->
            return ValidatorUtils.checkInteger(port)
        })
        enabled(nullable: true, blank: true, matches: /on/)
    }
}

class PolicyValidationCommand {

    String name
    String rule

    static constraints = {
        name(nullable: false, blank: false, matches: /\w[\w\-]*/)
        rule(nullable: false, blank: false)
    }
}

