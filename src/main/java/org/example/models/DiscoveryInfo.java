package org.example.models;

import java.util.Map;

public class DiscoveryInfo {
    private String version;
    private String adminContact;
    private Map<String, String> resources;

    // Empty constructor needed for JSON mapping
    public DiscoveryInfo() {}

    public DiscoveryInfo(String version, String adminContact, Map<String, String> resources) {
        this.version = version;
        this.adminContact = adminContact;
        this.resources = resources;
    }

    // Getters and Setters are MANDATORY for JAX-RS to convert this to JSON
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getAdminContact() { return adminContact; }
    public void setAdminContact(String adminContact) { this.adminContact = adminContact; }

    public Map<String, String> getResources() { return resources; }
    public void setResources(Map<String, String> resources) { this.resources = resources; }
}