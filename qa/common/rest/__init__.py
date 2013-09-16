"""
If you are going to rename some key make sure the logic of utils.get_list() method is still correct
because the method was written following to the naming in this dictionary and keys in the REST responses
to list requests.
"""

urls = {
    # AUTHENTICATION
    'login': 'auth/signIn',
    'logout': 'auth/signOut',
    # TENANT / DATACENTER
    'change_tenant': 'userState/changeTenant',
    'change_datacenter': 'userState/changeDataCenter',
    'datacenters': 'userState/getAllDataCenters',
    'get_user_state': 'userState/getCurrentUserState',
    # INSTANCES
    'instances': '',
    'create_instance': 'instance/save',
    'show_instance': 'instance/show',
    'terminate_instances': 'instance/terminate',
    'pause_instance': 'instance/pause',
    'unpause_instance': 'instance/unpause',
    'suspend_instance': 'instance/suspend',
    'resume_instance': 'instance/resume',
    'update_instance': 'instance/update',
    'make_snapshot': 'instance/makeSnapshot',
    'show_log': 'instance/log',
    'reboot_instance': 'instance/reboot',
    # IMAGES
    'images': 'image/list',
    # url is the same as for images. unique key is needed for convenience (see get_list)
    'instance_snapshots': 'image/list',
    'create_image': 'image/save',
    'show_image': 'image/show',
    'update_image': 'image/update',
    'delete_image': 'image/delete',
    # FLAVORS
    'flavors': 'flavor/list',
    'create_flavor': 'flavor/save',
    'delete_flavor': 'flavor/delete',
    # SECURITY
    'securitygroups': 'securityGroup/list',
    'show_security_group': 'securityGroup/show',
    'create_security_group': 'securityGroup/save',
    'delete_security_group': 'securityGroup/delete',
    'add_rule': 'securityGroup/addRule',
    'delete_rule': 'securityGroup/deleteRule',
    # KEYPAIRS
    'keypairs': 'keypair/list',
    'create_keypair': 'keypair/save',
    'delete_keypair': 'keypair/delete',
    'import_keypair': 'keypair/insertKeypair',
    # VOLUMES
    'volumes': 'volume/list',
    'create_volume': 'volume/save',
    'show_volume': 'volume/show',
    'delete_volume': 'volume/delete',
    'create_volume_snapshot': 'snapshot/save',
    'attach': 'volume/attach',
    'detach': 'volume/detach',
    # url is the same as for volumes. unique key is needed for convenience (see get_list)
    'volumetypes': 'volume/list',
    'create_volume_type': 'volume/saveType',
    'show_volume_type': 'volume/showType',
    'delete_volume_type': 'volume/deleteType',
    # VOLUME SNAPSHOTS
    'snapshots': 'snapshot/list',
    'show_snapshot': 'snapshot/show',
    'create_snapshot': 'snapshot/save',
    'delete_snapshot': 'snapshot/delete',
    # SETTINGS.QUOTAS
    'quotas': 'quota/list',
    # SETTINGS.SERVICES
    'services': 'openStackService/list',
    # SETTINGS.TENANTS
    'tenants': 'tenant/list',
    'show_tenant': 'tenant/show',
    'create_tenant': 'tenant/save',
    'update_tenant': 'tenant/update',
    'delete_tenant': 'tenant/delete',
    'tenant_quotas': 'tenant/quotas',
    'update_tenant_quotas': 'tenant/saveQuotas',
    'tenant_users': 'tenant/users',
    'update_tenant_users': 'tenant/usersSave',
    'tenant_policies': 'lbaas/listPolicies',
    # SETTINGS.USERS
    'users': 'openStackUser/list',
    'show_user': 'openStackUser/show',
    'create_user': 'openStackUser/save',
    'update_user': 'openStackUser/update',
    'delete_user': 'openStackUser/delete',
    # Heat
    'stacks': 'heat/list',
    'create_stack': 'heat/createStack',
    'show_stack': 'heat/show',
    'delete_stack': 'heat/delete',
    'upload': 'heat/upload',
    # LBaaS Pools and Services
    'pools': 'lbaas/listPools',
    'create_pool': 'lbaas/savePool',
    'show_pool': 'lbaas/showPool',
    'delete_pool': 'lbaas/delete',
    'create_service': 'lbaas/saveService',
    'enable_service': 'lbaas/enableService',
    'disable_service': 'lbaas/disableService',
    'delete_service': 'lbaas/deleteService',
    'monitors': 'lbaas/listMonitors',
    'methods': 'lbaas/listMethods',
    # LBaaS Jobs
    'jobs': 'lbaas/listJobs',
    # LBaaS VIPs
    'vips': 'lbaas/listVips',
    'create_vip': 'lbaas/saveVip',
    'show_vip': 'lbaas/showVip',
    'update_vip': 'lbaas/updateVip',
    'delete_vip': 'lbaas/deleteVip',
    # LBaaS Policies
    'policies': 'lbaas/listPolicies',
    'create_policy': 'lbaas/savePolicy',
    'update_policy': 'lbaas/updatePolicy',
    'delete_policy': 'lbaas/deletePolicy',
    # Networking
    'floating_ip': 'network/floatingIpList',
    'allocate_ip': 'network/allocateFloatingIp',
    'networks': 'network/list',
    # 'show_floating_ip': 'network/show',
    'create_network': 'network/create',
    'show_network': 'network/show',
    'delete_network': 'network/delete',
    'routers': 'router/list',
    'create_router': 'router/create',
    'show_router': 'router/show',
    'delete_router': 'router/delete',
}
