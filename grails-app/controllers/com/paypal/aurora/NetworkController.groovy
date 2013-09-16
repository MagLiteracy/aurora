package com.paypal.aurora

import static org.apache.commons.lang.StringUtils.isNotEmpty

import com.paypal.aurora.exception.RestClientRequestException
import com.paypal.aurora.model.*
import com.paypal.aurora.util.ConstraintsProcessor
import grails.converters.JSON
import grails.converters.XML
import org.apache.shiro.grails.annotations.RoleRequired

class NetworkController {

    def static allowedMethods = [list : ['GET'], save: ['POST'], show: ['GET', 'POST'], savePort: ['POST'],
            saveSubnet: ['POST'], saveEdition: ['POST'],
            savePortEdition: ['POST'], saveSubnetEdition: ['POST'], delete: ['POST'],
            deletePort: ['POST'], deleteSubnet: ['POST'], showSubnet: ['GET', 'POST'], showPort: ['GET', 'POST'],
            allocateIp: ['POST'],
            associateFloatingIp: ['GET', 'POST'], disassociateIp: ['POST'], associateIp: ['GET', 'POST']]

    def sessionStorageService
    def quantumService
    def tenantService
    def networkService
    def instanceService
    def openStackRESTService
    def quantumDNSService

    def index() {
        if (openStackRESTService.isServiceEnabled(OpenStackRESTService.QUANTUM)) {
            redirect(action: 'list', params: params)
        } else {
            redirect(action: 'floatingIpList', params: params)
        }
    }


    def list = {
        try{
            List<Network> networks = quantumService.networkList
            Map details = [networks: networks]
            withFormat {
                html { details }
                xml { new XML(details).render(response) }
                json { new JSON(details).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error, flash: [message: error]]
            withFormat {
                html {model: model}
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        }
    }

    @RoleRequired('admin')
    def delete = {
        List<String> networkIds = Requests.ensureList(params.selectedNetworks ?: params.networkId)
        def deleted = []
        def not_deleted = []
        def errors = [:]
        for (id in networkIds) {
            try{
                quantumService.deleteNetwork(id)
                deleted << id
            }catch (RestClientRequestException e){
                def error = ExceptionUtils.getExceptionMessage(e)
                not_deleted << id
                errors[id] = error
            }
        }

        def model = [deleted : deleted, not_deleted: not_deleted, errors : errors]

        withFormat {
            html{
                redirect(action : 'list');
                if (not_deleted) {
                    def ids = not_deleted.join(',')
                    flash.message = "Could not delete network with id: ${ids}"
                }
            }
            xml {new XML(model).render(response)}
            json {new JSON(model).render(response)}
        }

    }



    def show = {
        try{
            String id = params.id;
            Network network = quantumService.getNetworkById(id)
            List<Port> ports = quantumService.getPortsByNetworkId(id)
            def model = [network: network, ports: ports]
            withFormat {
                html { [parent: '/network', network: network, ports: ports] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        }catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html {
                    flash.message = error
                    chain(action: 'list')
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        }
    }

    def showSubnet = {
        try{
            String id = params.id;
            Subnet subnet = quantumService.getSubnetById(id)
            def model = [subnet : subnet]
            withFormat {
                html { [parent: "/network/show/$subnet.networkId", subnet: subnet] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html{
                    flash.message = error
                    chain(action : 'show', params : [id : params.id])
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        }
    }

    def showPort = {
        try{
            String id = params.id;
            Port port = quantumService.getPortById(id)
            def model = [port : port]
            withFormat {
                html { [parent: "/network/show/$port.networkId", port: port] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch(RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html {
                    flash.message = error
                    redirect(action: 'show', params: [id: params.id])
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        }
    }

    @RoleRequired('admin')
    def create = {
        params.tenants = tenantService.getAllTenants()
        [parent: "/network"]
    }

    def createSubnet = {
        [parent: "/network/show/$params.networkId", constraints: ConstraintsProcessor.getConstraints(SubnetValidateCommand.class)]
    }

    def createPort = {
        [parent: "/network/show/$params.networkId", constraints: ConstraintsProcessor.getConstraints(PortValidateCommand.class)]
    }

    def saveSubnet = { SubnetValidateCommand cmd ->
        if (cmd.hasErrors()) {
            def model = cmd.errors
            withFormat {
                html {
                    chain(action: 'createSubnet', model: [cmd: cmd], params: params);
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        } else {
            try{
                List<String> dnsList = parseDNSList(params.dnsName)
                List<HostRoute> hostRouteList = parseHostRoutes(params.hostRoutes)
                List<Range> rangeList = parseAllocationPools(params.allocationPools)

                Subnet subnet = new Subnet()
                subnet.name = params.name
                subnet.enableDhcp = "on".equals(params.enableDHCP)
                subnet.networkId = params.networkId
                subnet.dnsNameservers = dnsList
                subnet.allocationPools = rangeList
                subnet.gatewayIp = params.gatewayIp == '' ? null : params.gatewayIp
                subnet.ipVersion = Integer.valueOf(params.ipVersion)
                subnet.hostRoutes = hostRouteList
                subnet.tenantId = params.tenantId
                subnet.cidr = params.networkAddress

                def model = quantumService.createSubnet(subnet)

                withFormat {
                    html {redirect(action: 'show', params: [id: params.networkId])}
                    xml {new XML(model).render(response)}
                    json {new JSON(model).render(response)}
                }
            } catch (RestClientRequestException e){
                def error = ExceptionUtils.getExceptionMessage(e)
                def model = [errors : error]
                withFormat {
                    html {
                        flash.message = error
                        chain(action: 'createSubnet', params: [id: params.networkId]);
                    }
                    xml {new XML(model).render(response)}
                    json {new JSON(model).render(response)}
                }
            }
        }
    }

    private static List<Range> parseAllocationPools(String rangeString) {
        List<Range> rangeList = []
        if (isNotEmpty(rangeString)) {
            for (String range : rangeString.split("\n")) {
                String[] temp = range.split(",")
                if (temp.size() >= 2) {
                    rangeList.add(new Range(temp[0], temp[1]))
                } else {
                    if (temp.size() == 1) {
                        rangeList.add(new Range(temp[0], ""))
                    }
                }
            }
        }
        return rangeList
    }

    def savePort = { PortValidateCommand cmd ->
        if (cmd.hasErrors()) {
            def model = cmd.errors
            withFormat {
                html {
                    chain(action: 'createPort', model: [cmd: cmd], params: params)
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        } else {
            try{
                Port port = new Port()
                port.name = params.name
                port.adminStateUp = "on".equals(params.adminState)
                port.deviceId = params.deviceId
                port.deviceOwner = params.deviceOwner
                port.networkId = params.networkId
                port.tenantId = params.tenantId
                def model = quantumService.createPort(port)
                withFormat {
                    html{
                        redirect(action: 'show', id: params.networkId)
                    }
                    xml {new XML(model).render(response)}
                    json {new JSON(model).render(response)}
                }
            } catch (RestClientRequestException e) {
                def error = ExceptionUtils.getExceptionMessage(e)
                def model = [errors : error]
                withFormat {
                    html{
                        flash.message = error
                        chain(action: 'createPort', params: params)
                    }
                    xml {new XML(model).render(response)}
                    json {new JSON(model).render(response)}
                }
            }
        }
    }

    def deleteSubnet = {
        List<String> subnetIds = Requests.ensureList(params.selectedSubnets ?: params.subnetId)
        def deleted = []
        def not_deleted = []
        def errors = [:]

        for (id in subnetIds){
            try{
                quantumService.deleteSubnet(id)
                deleted << id
            }catch(RestClientRequestException e){
                def error = ExceptionUtils.getExceptionMessage(e)
                not_deleted << id
                errors[id] = error
            }
        }

        if (not_deleted) {
            def ids = not_deleted.join(',')
            flash.message = "Could not delete subnet with id: ${ids}"
        }

        def model = [deleted : deleted, not_deleted : not_deleted, errors : errors]

        withFormat {
            html {
                if (not_deleted) {
                    def ids = not_deleted.join(',')
                    flash.message = "Could not delete subnet with id: ${ids}"
                }
                redirect(action: 'show', params: [id: params.networkId])
            }
            xml {new XML(model).render(response)}
            json {new JSON(model).render(response)}
        }
    }

    def deletePort = {
        List<String> portIds = Requests.ensureList(params.selectedPorts ?: params.portId)
        def deleted = []
        def not_deleted = []
        def errors = [:]

        for (id in portIds) {
            try{
                quantumService.deletePort(id)
                deleted << id
            } catch (RestClientRequestException e){
                def error = ExceptionUtils.getExceptionMessage(e)
                not_deleted << id
                errors[id] = error
            }
        }

        def model = [deleted : deleted, not_deleted: not_deleted, errors : errors]

        withFormat {
            html{
                if (not_deleted) {
                    def ids = not_deleted.join(',')
                    flash.message = "Could not delete port with id: ${ids}"
                }
                redirect(action : 'show', params: [id : params.networkId])
            }
            xml {new XML(model).render(response)}
            json {new JSON(model).render(response)}
        }
    }

    def save = {
        try{
            Network network = new Network()
            network.name = params.name
            network.shared = 'on'.equals(params.shared)
            network.external = 'on'.equals(params.external)
            network.projectId = params.tenant
            network.adminStateUp = 'on'.equals(params.adminState)
            def model = quantumService.createNetwork(network)
            withFormat {
                html { redirect(action: 'list') }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        } catch (RestClientRequestException e) {
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html {
                    flash.message = error
                    chain(action: 'create')
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        }
    }


    def edit = {
        try{
            String id = params.id;
            Network network = quantumService.getNetworkById(id)
            def model = [network : network]
            withFormat {
                html { [parent: "/network/show/$id", network: network] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html {
                    flash.message = error
                    redirect(action: 'show', params: params)
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        }
    }

    def saveEdition = {
        try{
            Network network = new Network()
            network.id = params.id
            network.name = params.name
            network.shared = 'on'.equals(params.shared)
            network.external = 'on'.equals(params.external)
            network.adminStateUp = 'on'.equals(params.adminState)
            def model = quantumService.updateNetwork(network)
            withFormat {
                html{ redirect(action: 'show', params: [id: params.id]) }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html{
                    flash.message = error
                    redirect(action: 'edit', params: params)
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        }
    }

    def editSubnet = {
        try{
            String id = params.id;
            Subnet subnet = quantumService.getSubnetById(id)
            params.dnsName = params.dnsName ?: getDNSNamesAsString(subnet.dnsNameservers)
            params.hostRoutes = params.hostRoutes ?: getHostRoutesAsString(subnet.hostRoutes)
            params.id = subnet.id
            params.networkAddress = params.networkAddress ?: subnet.cidr
            params.name = params.name ?: subnet.name
            params.enableDHCP = params.enableDHCP ?: subnet.enableDhcp;
            params.gatewayIp = params.gatewayIp ?: subnet.gatewayIp
            def model = params
            withFormat {
                html { [parent: "/network/showSubnet/$id", params: params] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html {
                    flash.message = error
                    chain(action : 'showSubnet', params: params)
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        }
    }

    def saveSubnetEdition = { SubnetValidateCommand cmd ->
        if (cmd.hasErrors()) {
            def model = cmd.errors
            withFormat {
                html { redirect(action: 'editSubnet', model: [cmd: cmd], params: params) }
                xml { new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        } else {
            try{
                List<String> dnsList = parseDNSList(params.dnsName)
                List<HostRoute> hostRouteList = parseHostRoutes(params.hostRoutes)
                Subnet subnet = new Subnet()
                subnet.id = params.id
                subnet.name = params.name
                subnet.enableDhcp = "on".equals(params.enableDHCP)
                subnet.dnsNameservers = dnsList
                subnet.gatewayIp = params.gatewayIp == '' ? null : params.gatewayIp
                subnet.hostRoutes = hostRouteList
                def resp = quantumService.updateSubnet(subnet)
                withFormat {
                    html { redirect(action: 'showSubnet', params: [id: params.id]) }
                    xml {new XML(resp).render(response)}
                    json {new JSON(resp).render(response)}
                }
            } catch (RestClientRequestException e){
                def error = ExceptionUtils.getExceptionMessage(e)
                def model = [errors : error]
                withFormat {
                    html {
                        flash.message = error
                        chain(action : 'editSubnet', params: params)
                    }
                    xml {new XML(model).render(response)}
                    json {new JSON(model).render(response)}
                }
            }
        }
    }

    def editPort = {
        try{
            String id = params.id;
            Port port = quantumService.getPortById(id)
            def model = [port : port]
            withFormat {
                html { [parent: "/network/showPort/$id", port: port, constraints: ConstraintsProcessor.getConstraints(PortValidateCommand.class)] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html{
                    flash.message = error
                    redirect(action: 'showPort', params: params)
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        }
    }

    def savePortEdition = { PortValidateCommand cmd ->
        if (cmd.hasErrors()) {
            def model = cmd.errors
            withFormat {
                html {
                    redirect(action: 'showPort', model: [cmd: cmd], params: params)
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        } else {
            try {
                Port port = new Port()
                port.id = params.id
                port.name = params.name
                port.adminStateUp = "on".equals(params.adminState)
                port.deviceId = params.deviceId
                port.deviceOwner = params.deviceOwner
                def model = quantumService.updatePort(port)
                withFormat {
                    html {redirect(action: 'showPort', params: [id: params.id])}
                    xml {new XML(model).render(response)}
                    json {new JSON(model).render(response)}
                }
            } catch (RestClientRequestException e){
                def error = ExceptionUtils.getExceptionMessage(e)
                def model = [errors : error]
                withFormat {
                    html{
                        flash.message = error
                        redirect(action: 'editPort', params : params)
                    }
                    xml {new XML(model).render(response)}
                    json {new JSON(model).render(response)}
                }
            }
        }
    }



    private static List<HostRoute> parseHostRoutes(String hostRoutesString) {
        List<HostRoute> hostRouteList = null
        if (isNotEmpty(hostRoutesString)) {
            hostRouteList = []
            for (String route : hostRoutesString.split("\n")) {
                String[] temp = route.split(",")
                if (temp.size() >= 2) {
                    hostRouteList.add(new HostRoute(temp[0], temp[1]))
                } else {
                    if (temp.size() == 1) {
                        hostRouteList.add(new HostRoute(temp[0], ""))
                    }
                }
            }
        }
        hostRouteList
    }

    private static List<String> parseDNSList(String dnsString) {
        List<String> dnsList = null
        if (isNotEmpty(dnsString)) {
            dnsList = dnsString.split("\n")
        }
        dnsList
    }

    private static String getDNSNamesAsString(List<String> dnsNames) {
        String dnsNamesAsString = ""
        for (String dnsName : dnsNames) {
            dnsNamesAsString += dnsName + "\n"
        }
        dnsNamesAsString
    }

    private static String getHostRoutesAsString(List<HostRoute> hostRoutes) {
        String hostRoutesAsString = ""
        for (HostRoute hostRoute : hostRoutes) {
            hostRoutesAsString += hostRoute.destination + "," + hostRoute.nexthop + "\n"
        }
        hostRoutesAsString
    }

    def floatingIpList = {
        try{
            def floatingIps = networkService.getFloatingIps()
            def instances = instanceService.listAll()
            boolean showFqdn = quantumDNSService.isEnabled()
            withFormat {
                html { [floatingIps: floatingIps, instances: instances, showFqdn: showFqdn] }
                xml { new XML(floatingIps).render(response) }
                json { new JSON(floatingIps).render(response) }
            }
        }catch(RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html { model :['floatingIps' : [], flash : [message : error]] }
                xml { new XML(model).render(response) }
                json { new JSON(model).render(response) }
            }
        }
    }

    def showFloatingIp = {
        try{
            def floatingIp = networkService.getFloatingIpById(params.id)
            def instances = instanceService.listAll()
            def model = [floatingIp : floatingIp, instances: instances]
            withFormat {
                html { [parent: '/network/floatingIpList', floatingIp: floatingIp, instances: instances] }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        } catch(RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html{
                    flash.message = error
                    redirect(action: 'floatingIpList')
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        }

    }

    def allocateFloatingIp = {
        try {
            def pools = networkService.getFloatingIpPools()
            def parent = '/network/floatingIpList'
            if (params?.fromInstance) {
                parent = "/network/associateFloatingIp?instanceId=${params.fromInstance}"
            }
            boolean isDns = quantumDNSService.isEnabled()
            String [] zones = sessionStorageService.tenant.zones
            def model = [pools : pools]
            withFormat {
                html { [parent: parent, pools: pools, constraints: ConstraintsProcessor.getConstraints(AllocateIpValidateCommand.class), isDns: isDns, zones: zones] }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html {
                    flash.message = error
                    redirect(action : 'floatingIpList')
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        }
    }


    def releaseFloatingIp = {
        List<String> ids = Requests.ensureList(params.selectedIps)
        List<String> notReleasedFipIds = []
        def released = []
        def errors = [:]
        for (fipId in ids) {
            try {
                released << networkService.releaseFloatingIp(fipId)
            } catch (RestClientRequestException e) {
                log.error "Could not release IP: ${e}"
                notReleasedFipIds << fipId
                def error = ExceptionUtils.getExceptionMessage(e)
                errors[fipId] = error
            }
        }
        def model = [released: released, not_released_ids: notReleasedFipIds, errors: errors]
        withFormat {
            html {
                if (notReleasedFipIds) {
                    def notReleased = notReleasedFipIds.join(',')
                    flash.message = "Could not release IP(s) with id: ${notReleased}"
                }
                redirect(action: 'floatingIpList')
            }
            xml { new XML(model).render(response) }
            json { new JSON(model).render(response) }
        }
    }
    def associateFloatingIp = {
        try{
            def floatingIps = networkService.getUnassignedFloatingIps()
            def instances = []
            if(networkService.isUseExternalFLIP()) {
                instances = instanceService.listAll(true).findAll {it.floatingIps.isEmpty()}
            } else {
                instances = instanceService.listAll()
            }
            def defaultIp = params.ip ?: floatingIps[0]?.ip
            def defaultInstance = params.instanceId ?: instances[0]?.instanceId
            def fromInstance
            def parent
            if (params.instanceId) {
                parent = "/instance/show/${params.instanceId}"
                fromInstance = params.instanceId
            } else {
                def ipId = floatingIps.find { it.ip == params.ip }?.id
                parent = "/network/showFloatingIp/${ipId}"
                fromInstance = null
            }
            def model = [parent: parent, floatingIps: floatingIps, instances: instances, defaultIp: defaultIp, defaultInstance: defaultInstance, fromInstance: fromInstance]
            withFormat {
                html { model }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            def redirectMap
            if (params.instanceId)
                redirectMap = [controller: 'instance', action: 'show', id: params.instanceId]
            else{
                redirectMap = [action: 'showFloatingIp', id : params.ip]
            }
            withFormat {
                html{
                    flash.message = error
                    redirect(redirectMap)
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        }
    }
    def associateIp = { AssociateIpValidateCommand cmd ->
        if (cmd.hasErrors()) {
            def chainParams = []
            if (params.fromInstance) {
                chainParams = [instanceId: params.fromInstance]
            }
            def model = cmd.errors
            withFormat {
                html {
                    redirect(action: 'associateFloatingIp', model: [cmd: cmd], params: chainParams)
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        } else {
            def redirectParams
            if (params.fromInstance) {
                redirectParams = [controller: 'instance', action: 'show', params: [id: params.fromInstance]]
            } else {
                redirectParams = [action: 'floatingIpList']
            }
            try {
                def resp = networkService.associateFloatingIp(params.instanceId, params.ip)
                withFormat {
                    html { redirect(redirectParams) }
                    xml { new XML(resp).render(response) }
                    json { new JSON(resp).render(response) }
                }
            } catch (RestClientRequestException e) {
                def error = ExceptionUtils.getExceptionMessage(e)
                def model = [errors : error]
                withFormat {
                    html {
                        flash.message = error
                        redirect(redirectParams)
                    }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            }
        }
    }
    def disassociateIp = {
        def redirectParams
        if (params.fromInstance) {
            redirectParams = [controller: 'instance', action: 'show', params: [id: params.instanceId]]
        } else {
            redirectParams = [action: 'floatingIpList']
        }
        try{
            def resp = networkService.disassociateFloatingIp(params.instanceId, params.ip)
            withFormat {
                html { redirect(redirectParams) }
                xml { new XML(resp).render(response) }
                json { new JSON(resp).render(response) }
            }
        } catch (RestClientRequestException e){
            def error = ExceptionUtils.getExceptionMessage(e)
            def model = [errors : error]
            withFormat {
                html{
                    flash.message = error
                    redirect(redirectParams)
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        }
    }
    def allocateIp = { AllocateIpValidateCommand cmd ->
        if (cmd.hasErrors()) {
            def chainParams = []
            if (params.fromInstance) {
                chainParams = [instanceId: params.fromInstance]
            }
            def model = cmd.errors
            withFormat {
                html {
                    redirect(action: 'allocateFloatingIp', model: [cmd: cmd], params: chainParams)
                }
                xml {new XML(model).render(response)}
                json {new JSON(model).render(response)}
            }
        } else {
            def redirectParams = [action: 'floatingIpList']
            if (params.parent) {
                redirectParams = [url: params.parent]
            }
            try {
                def resp = networkService.allocateFloatingIp(params.pool, params.hostname, params.zone)
                withFormat {
                    html { redirect(redirectParams) }
                    xml { new XML(resp).render(response) }
                    json { new JSON(resp).render(response) }
                }
            } catch (RestClientRequestException e) {
                def error = ExceptionUtils.getExceptionMessage(e)
                def model = [errors : error]
                withFormat {
                    html {
                        flash.message = error
                        redirect(redirectParams)
                    }
                    xml { new XML(model).render(response) }
                    json { new JSON(model).render(response) }
                }
            }
        }
    }


}

class SubnetValidateCommand {

    String networkAddress

    static constraints = {
        networkAddress(nullable: false, blank: false)
    }
}

class PortValidateCommand {

    String name

    static constraints = {
        name(nullable: false, blank: false)
    }
}

class AllocateIpValidateCommand {
    String pool

    static constraints = {
        pool(nullable: false, blank: false)
    }
}

class AssociateIpValidateCommand {

    def quantumDNSService

    String ip
    String instanceId
    String hostname
    String zone

    static constraints = {
        ip(nullable: false, blank: false)
        instanceId(nullable: false, blank: false)
        zone(nullable: true, blank: false)
        hostname(nullable: true, blank: false, validator: {hostname, obj ->
            if (hostname && obj.quantumDNSService.getIpByHostnameAndZone(hostname, obj.zone)) {
                return ['associateIpValidateCommand.hostname.alreadyUsed', obj.zone]
            }
        })
    }
}