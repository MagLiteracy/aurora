package com.paypal.aurora.model

class OpenStackService implements Serializable {

    private static final long serialVersionUID = 6688749953705040470L

    String name
    String type
    String publicURL
    String adminURL
    String host

    //hacks for PP
    String user
    String password
    String tokenId
    String tenant
    boolean disabled = false

    OpenStackService(String name, String type, String publicURL, String adminURL) {
        this.name = name
        this.type = type
        setPublicURL(publicURL)
        this.adminURL = adminURL
    }

    OpenStackService(String name, String type, String publicURL, String adminURL, String user, String password, String tenant, boolean disabled) {
        this(name, type, publicURL, adminURL)
        this.user = user
        this.password = password
        this.disabled = disabled
        this.tenant = tenant
    }

    def setPublicURL(String publicURL) {
        this.publicURL = publicURL
        this.host = publicURL ? formatHost(publicURL) : null
    }

    private static String formatHost(String host) {
        String result = host;
        if (result.indexOf("://") > 0) {
            result = result.substring(result.indexOf("://") + 3, result.length());
        }

        if (result.indexOf(':') > 0) {
            result = result.substring(0, result.indexOf(':'));
        }

        if (result.indexOf('/') > 0) {
            result = result.substring(0, result.indexOf('/'));
        }

        return result;
    }

    @Override
    public String toString() {
        return "OpenStackService{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", publicURL='" + publicURL + '\'' +
                ", adminURL='" + adminURL + '\'' +
                ", host='" + host + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", tokenId='" + tokenId + '\'' +
                ", tenant='" + tenant + '\'' +
                ", disabled=" + disabled +
                '}';
    }
}
