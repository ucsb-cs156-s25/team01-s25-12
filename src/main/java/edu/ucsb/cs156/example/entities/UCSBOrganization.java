package edu.ucsb.cs156.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity(name = "ucsborganizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UCSBOrganization {

    @Id
    private String orgCode;

    private String orgTranslationShort;

    private String orgTranslation;

    private boolean inactive;

    public String getOrgCode() { return orgCode; }
    public void setOrgCode(String orgCode) { this.orgCode = orgCode; }

    public String getOrgTranslationShort() { return orgTranslationShort; }
    public void setOrgTranslationShort(String orgTranslationShort) { this.orgTranslationShort = orgTranslationShort; }

    public String getOrgTranslation() { return orgTranslation; }
    public void setOrgTranslation(String orgTranslation) { this.orgTranslation = orgTranslation; }

    public boolean isInactive() { return inactive; }
    public void setInactive(boolean inactive) { this.inactive = inactive; }
}
