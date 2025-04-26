package edu.ucsb.cs156.example.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UCSBOrganizationTests {

    @Test
    public void testBuilderAndGetters() {
        UCSBOrganization org = UCSBOrganization.builder()
            .orgCode("testCode")
            .orgTranslationShort("Test Short")
            .orgTranslation("Test Full Name")
            .inactive(true)
            .build();

        assertEquals("testCode", org.getOrgCode());
        assertEquals("Test Short", org.getOrgTranslationShort());
        assertEquals("Test Full Name", org.getOrgTranslation());
        assertTrue(org.isInactive());
    }

    @Test
    public void testSetterAndIsInactive() {
        UCSBOrganization org = new UCSBOrganization();
        org.setInactive(false);
        assertFalse(org.isInactive());
        org.setInactive(true);
        assertTrue(org.isInactive());
    }
}
