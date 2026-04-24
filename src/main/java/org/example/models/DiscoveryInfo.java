package org.example.models;

import java.util.Map;

/**
 * POJO for the Discovery (HATEOAS) endpoint response.
 * Includes versioning, contact info, academic year, and resource links.
 */
public class DiscoveryInfo {
    private String version;
    private String description;
    private String adminContact;
    private String academicYear;
    private Map<String, String> resources;

    public DiscoveryInfo() {}

    public DiscoveryInfo(String version, String description, String adminContact,
                         String academicYear, Map<String, String> resources) {
        this.version = version;
        this.description = description;
        this.adminContact = adminContact;
        this.academicYear = academicYear;
        this.resources = resources;
    }

    public String getVersion()                       { return version; }
    public void setVersion(String version)           { this.version = version; }

    public String getDescription()                   { return description; }
    public void setDescription(String description)   { this.description = description; }

    public String getAdminContact()                  { return adminContact; }
    public void setAdminContact(String c)            { this.adminContact = c; }

    public String getAcademicYear()                  { return academicYear; }
    public void setAcademicYear(String y)            { this.academicYear = y; }

    public Map<String, String> getResources()        { return resources; }
    public void setResources(Map<String, String> r)  { this.resources = r; }
}
