from selenium.webdriver.common.by import By


class UIMap(object):

    # Common
    msg_error = (By.ID, "error_message")
    bt_up = (By.ID, "upButton")
    buttons = (By.CLASS_NAME, "buttons")
    bt_create = (By.ID, "create")
    bt_edit = (By.ID, "edit")
    bt_enable = (By.ID, "enable")
    bt_disable = (By.ID, "disable")
    bt_delete = (By.ID, "delete")
    bt_confirm = (By.ID, "btn-confirm")
    bt_rename = (By.ID, "rename")
    bt_submit = (By.ID, "submit")
    bt_download = (By.ID, "download")
    bt_insert = (By.ID, "insert")
    ed_name = (By.ID, "name")
    ed_description = (By.ID, "description")
    ed_location = (By.ID, "location")
    ed_size = (By.ID, "size")
    ed_email = (By.ID, "email")
    ed_pass = (By.ID, "password")
    ed_confirm_pass = (By.ID, "confirm_password")
    tbl_head = (By.TAG_NAME, "thead")
    tbl_body = (By.TAG_NAME, "tbody")
    ed_filter = (By.ID, "filter")
    ed_filter2 = (By.ID, "filter_1")  # filter for the 2nd table on the page (if the table exists)
    counter = (By.ID, "counter")
    counter1 = (By.ID, "counter_1")

    dlg_confirm = (By.ID, "confirmationDialog")

    # Login
    environment = (By.ID, "select_environment")
    username_input = (By.ID, "username")
    password_input = (By.ID, "password")
    login_button = (By.ID, "submit")
    logged_as = (By.ID, "authentication")
    logout_button = (By.ID, "signOut")

    # Main Menu
    menu_top_nav = (By.ID, "topNav")

    menu_compute = (By.ID, "nav-instance-list-root")
    menu_instances = (By.ID, "nav-instance-list")
    menu_images = (By.ID, "nav-image-list")
    menu_flavors = (By.ID, "nav-flavor-list")

    menu_storage = (By.ID, "nav-volume-list-root")
    menu_volumes = (By.ID, "nav-volume-list")
    menu_snapshots = (By.ID, "nav-snapshot-list")

    menu_lbaas = (By.ID, "nav-lbaas-listPools-root")
    menu_pools = (By.ID, "nav-lbaas-listPools")
    menu_vips = (By.ID, "nav-lbaas-listVips")
    menu_policies = (By.ID, "nav-lbaas-listPolicies")
    menu_jobs = (By.ID, "nav-lbaas-listJobs")

    menu_networking = (By.ID, "nav-network-index-root")
    menu_floating_ip = (By.ID, "nav-network-floatingIpList")
    menu_networks = (By.ID, "nav-networks-list")
    menu_routers = (By.ID, "nav-routers-list")

    menu_heat = (By.ID, "nav-heat-list-root")

    menu_security = (By.ID, "nav-securityGroup-list-root")
    menu_security_groups = (By.ID, "nav-securityGroup-list")
    menu_keypairs = (By.ID, "nav-keypair-list")

    menu_settings = (By.ID, "nav-quota-list-root")
    menu_quota_usage = (By.ID, "nav-quotaUsage-list")
    menu_quotas = (By.ID, "nav-quota-list")
    menu_services = (By.ID, "nav-openStackService-list")
    menu_tenants = (By.ID, "nav-tenant-list")
    menu_users = (By.ID, "nav-openStackUser-list")

    # Menu Instances
    bt_launch_instance = (By.ID, "launchInstance")
    bt_terminate_instance = (By.ID, "terminate")
    bt_details = (By.ID, "details")
    bt_access_and_security = (By.ID, "accessAndSecurity")
    bt_volume_options = (By.ID, "volumeOptions")
    bt_post_creation = (By.ID, "postCreation")

    tbl_instances = (By.ID, "table_listInstance")
    tbl_snapshots = (By.ID, "snapshots")

    edit_inst_name = (By.ID, "input_instCreate_name")
    chk_security_groups = (By.NAME, "securityGroups")
    chk_security_groups_default = (By.ID, "checkbox_default")
    chk_select_instance = (By.ID, "selectedInstances")
    cb_image = (By.ID, "select_cb_image")
    cb_flavor = (By.ID, "select_cb_flavor")
    cb_keypair = (By.ID, "select_cb_keypair")
    cb_volume_options = (By.ID, "select_cb_options")
    # cb_volume_snapshot = (By.ID, "select_cb_volumeSnapshot")
    cb_volume_snapshot = (By.ID, "select_cb_vol_type")

    bt_create_snapshot = (By.ID, "snapshot")
    bt_pause = (By.ID, "pause")
    bt_unpause = (By.ID, "unpause")
    bt_suspend = (By.ID, "suspend")
    bt_resume = (By.ID, "resume")
    bt_reboot = (By.ID, "reboot")
    bt_log = (By.ID, "log")
    text_log = (By.ID, "textarea_instLog_log")
    bt_vnc = (By.ID, "vnc")
    vnc_status = (By.ID, "noVNC_status")
    vnc_canvas = (By.ID, "noVNC_canvas")

    #Menu Images
    tbl_images = (By.ID, "images")
    ed_image_location = (By.ID, "location")
    ed_min_disk = (By.ID, "minDisk")
    ed_min_ram = (By.ID, "minRam")
    cb_format = (By.ID, "select_diskFormat")
    select_format_img = (By.ID, "select_img")
    select_format_iso = (By.ID, "select_iso")
    select_format_ami = (By.ID, "select_diskFormat_item_ami")
    chk_public = (By.ID, "shared")
    bt_edit_image_attributes = (By.ID, "edit")
    bt_delete_image_attributes = (By.ID, "delete")
    tbl_image_details = (By.ID, 'table_showImage')

    # Menu Flavors
    tbl_flavors = (By.ID, "table_listFlavor")
    ed_ram = (By.ID, "ram")
    ed_disk = (By.ID, "disk")
    ed_vcpus = (By.ID, "vcpus")
    chk_is_public = (By.ID, "isPublic")
    ed_ephemeralSpaceSize = (By.ID, "ephemeral")
    ed_swap = (By.ID, "swap")
    ed_rxtx_factor = (By.ID, "rxtxFactor")

    # LBaaS Pools
    tbl_pools = (By.ID, "table_lbassListPools")
    tbl_lbaas_services = (By.ID, "table_lbassServices")
    bt_add_new_pool = (By.ID, "addPool")
    bt_edit_pool = (By.ID, "editPool")
    ed_port = (By.ID, "port")
    cb_instance = (By.ID, "select_instanceId")
    cb_interface = (By.ID, "select_netInterface")
    ed_weight = (By.ID, "weight")
    chk_enabled = (By.ID, "enabled")
    cb_lb_method = (By.ID, "select_lbMethod")
    # select_peeping = (By.ID, "select_peeping")
    chk_monitors_http = (By.ID, "monitors-http")
    # chk_monitor_thirst = (By.ID, "monitors-thirst")
    # chk_monitor_hunger = (By.ID, "monitors-hunger")
    bt_add_service = (By.ID, "addService")

    # Menu LBaaS Vips
    tbl_vips = (By.ID, "table_lbassListVips")
    ed_ip = (By.ID, "ip")
    cb_protocol = (By.ID, 'select_protocol')
    bt_edit_vip = (By.ID, 'editVip')

    # Menu LBaaS Policies
    tbl_policies = (By.ID, "policies")
    ed_rule = (By.ID, 'rule')
    bt_delete_policy = (By.ID, 'deletePolicy')

    # Menu LBaaS Jobs
    tbl_lbaas_jobs = (By.ID, "table_lbassJobs")
    bt_init_filter = (By.ID, "initListFilter")

    # Menu Networking
    bt_allocate = (By.ID, "allocate")
    tbl_floating_ip = (By.ID, "table_floatingIpList")
    cb_select_pool = (By.ID, "select_pool")
    ed_hostname = (By.ID, "hostname")
    cb_select_zone = (By.ID, "select_zone")
    tbl_networks = (By.ID, "networks")
    tbl_show_network = (By.ID, "table_showNetwork")
    tbl_show_router = (By.ID, "table_showRouter")
    bt_set_gw = (By.ID, "setGateway")
    cb_select_network = (By.ID, "select_network")
    cb_select_project = (By.ID, "select_tenant")
    chk_admin_state = (By.ID, "adminState")
    chk_shared = (By.ID, "shared")
    chk_external = (By.ID, "external")
    tbl_routers = (By.ID, "routers")

    #Menu Keypairs
    tbl_keypair = (By.ID, "table_keypairList")
    ed_public_key = (By.ID, "publicKey")

    # Menu Volumes and Volume Types
    tbl_volumes = (By.ID, "table_volumeList")
    tbl_volume_snapshots = (By.ID, 'table_snapshotList')
    tbl_volume_types = (By.ID, "table_volumeTypes")
    bt_edit_attach = (By.ID, "editAttach")
    bt_attach = (By.ID, "attach")
    bt_detach = (By.ID, "detach")
    cb_attach_to_instance = (By.ID, "select_instanceId")
    ed_device = (By.ID, "device")
    bt_create_volume_type = (By.ID, 'createType')
    bt_delete_volume_type = (By.ID, 'deleteType')

    # Menu Security Groups
    tbl_sg_rules = (By.ID, "table_securityGroupShow")
    tbl_security_groups = (By.ID, "table_securityGroupList")
    tbl_security_group_edit = (By.ID, "table_securityGroupEditRules")
    bt_edit_rules = (By.ID, "editRules")
    cb_ip_protocol = (By.ID, "select_select_sgShow_ipProtocol")
    select_tcp = (By.ID, "select_TCP")
    select_udp = (By.ID, "select_UDP")
    select_icmp = (By.ID, "select_ICMP")
    cb_source_group = (By.ID, "select_select_sgShow_sourceGroup")
    select_cidr = (By.ID, "select_CIDR")
    ed_sg_name = (By.ID, "input_sgCreate_name")
    ed_sg_description = (By.ID, "input_sgCreate_description")
    ed_cidr = (By.ID, "input_sgShow_cidr")
    ed_from_port = (By.ID, "input_sgShow_fromPort")
    ed_to_port = (By.ID, "input_sgShow_toPort")

    # Menu Settings
    tbl_quota_usage = (By.ID, "quotaContainer")
    tbl_tenant_quotas = (By.ID, "table_tenantQuotas")
    tbl_quotas = (By.ID, "table_quotaList")
    tbl_services = (By.ID, "table_ossList")
    tbl_tenants = (By.ID, "table_tenantList")
    tbl_tenant_quotas = (By.ID, "table_tenantQuotas")
    tbl_tenant_users = (By.ID, "table_OSUserList")
    ed_quota_cores = (By.ID, "quota-cores")
    ed_quota_floating_ips = (By.ID, "quota-floating_ips")
    ed_quota_gigabytes = (By.ID, "quota-gigabytes")
    ed_quota_injected_file_content_bytes = (By.ID, "quota-injected_file_content_bytes")
    ed_quota_injected_file_path_bytes = (By.ID, "quota-injected_file_path_bytes")
    ed_quota_injected_files = (By.ID, "quota-injected_files")
    ed_quota_instances = (By.ID, "quota-instances")
    ed_quota_key_pairs = (By.ID, "quota-key_pairs")
    ed_quota_metadata_items = (By.ID, "quota-metadata_items")
    ed_quota_ram = (By.ID, "quota-ram")
    ed_quota_security_group_rules = (By.ID, "quota-security_group_rules")
    ed_quota_security_groups = (By.ID, "quota-security_groups")
    ed_quota_volumes = (By.ID, "quota-volumes")
    quota = {
        "Cores": ed_quota_cores,
        "Floating Ips": ed_quota_floating_ips,
        "Gigabytes": ed_quota_gigabytes,
        "Injected File Content Bytes": ed_quota_injected_file_content_bytes,
        "Injected File Path Bytes": ed_quota_injected_file_path_bytes,
        "Injected Files": ed_quota_injected_files,
        "Instances": ed_quota_instances,
        "Key Pairs": ed_quota_key_pairs,
        "Metadata Items": ed_quota_metadata_items,
        "Ram": ed_quota_ram,
        "Security Group Rules": ed_quota_security_group_rules,
        "Security Groups": ed_quota_security_groups,
        "Volumes": ed_quota_volumes,
    }
    bt_show_users = (By.ID, "ui-accordion-2-header-0")
    bt_edit_users = (By.ID, "editUsers")
    bt_show_quotas = (By.ID, "ui-accordion-1-header-0")
    bt_edit_quotas = (By.ID, "editQuotas")
    tbl_left_side_list_container = (By.ID, "leftSideListContainer")
    leftSideList = (By.ID, "leftSideList")
    tbl_right_side_list_container = (By.ID, "rightSideListContainer")
    rightSideList = (By.ID, "rightSideList")
    bt_users_roles_submit = (By.ID, "usersRolesSubmit")
    bt_users_roles_reset = (By.ID, "usersRolesReset")

    # Menu Users
    tbl_users = (By.ID, "table_OSUserList")
