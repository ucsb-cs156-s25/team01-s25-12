package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItem;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBDiningCommonsMenuItemsController.class)
@Import(TestConfig.class)
public class UCSBDiningCommonsMenuItemsControllerTests extends ControllerTestCase {

    @MockBean
    UCSBDiningCommonsMenuItemRepository ucsbDiningCommonsMenuItemRepository;

    @MockBean
    UserRepository userRepository;

    // Tests for GET /api/ucsbdiningcommons/all

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_ucsbdiningcommonsmenuitems() throws Exception {
        UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItem1 = UCSBDiningCommonsMenuItem.builder()
                        .name("firstDiningCommonsMenuItem")
                        .diningCommonsCode("firstDiningCommonsCode")
                        .station("firstStation")
                        .build();

        UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItem2 = UCSBDiningCommonsMenuItem.builder()
                        .name("lastDiningCommonsMenuItem")
                        .diningCommonsCode("lastDiningCommonsCode")
                        .station("lastStation")
                        .build();

        ArrayList<UCSBDiningCommonsMenuItem> expectedDiningCommonsMenuItems = new ArrayList<>();
        expectedDiningCommonsMenuItems.addAll(Arrays.asList(ucsbDiningCommonsMenuItem1, ucsbDiningCommonsMenuItem2));

        when(ucsbDiningCommonsMenuItemRepository.findAll()).thenReturn(expectedDiningCommonsMenuItems);

        // act
        MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems/all"))
                        .andExpect(status().isOk()).andReturn();

        // assert

        verify(ucsbDiningCommonsMenuItemRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedDiningCommonsMenuItems);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    // Tests for for GET by id
    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
            mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems?id=7"))
                            .andExpect(status().is(403)); // logged out users can't get by id
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {
        UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItem = UCSBDiningCommonsMenuItem.builder()
            .name("firstDiningCommonsMenuItem")
            .diningCommonsCode("firstDiningCommonsCode")
            .station("firstStation")
            .build();

            when(ucsbDiningCommonsMenuItemRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbDiningCommonsMenuItem));

            // act
            MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems?id=7"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(eq(7L));
            String expectedJson = mapper.writeValueAsString(ucsbDiningCommonsMenuItem);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

            // arrange

            when(ucsbDiningCommonsMenuItemRepository.findById(eq(7L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems?id=7"))
                            .andExpect(status().isNotFound()).andReturn();

            // assert

            verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(eq(7L));
            Map<String, Object> json = responseToJson(response);
            assertEquals("EntityNotFoundException", json.get("type"));
            assertEquals("UCSBDiningCommonsMenuItem with id 7 not found", json.get("message"));
    }

    // Authorization tests for /api/ucsbdiningcommonsmenuitems/post
    // (Perhaps should also have these for put and delete)

    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsbdiningcommonsmenuitems/post"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsbdiningcommonsmenuitems/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_ucsbdiningcommonsmenuitem() throws Exception {
            // arrange

            UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItem1 = UCSBDiningCommonsMenuItem.builder()
            .name("firstDiningCommonsMenuItem")
            .diningCommonsCode("firstDiningCommonsCode")
            .station("firstStation")
            .build();

            when(ucsbDiningCommonsMenuItemRepository.save(eq(ucsbDiningCommonsMenuItem1))).thenReturn(ucsbDiningCommonsMenuItem1);

            // act
            MvcResult response = mockMvc.perform(
                            post("/api/ucsbdiningcommonsmenuitems/post?name=firstDiningCommonsMenuItem&diningCommonsCode=firstDiningCommonsCode&station=firstStation")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).save(ucsbDiningCommonsMenuItem1);
            String expectedJson = mapper.writeValueAsString(ucsbDiningCommonsMenuItem1);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }


    // tests for PUT by id

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_edit_an_existing_ucsbdiningcommonsmenuitem() throws Exception {
        // arrange
        UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItemOrig = UCSBDiningCommonsMenuItem.builder()
        .name("originalDiningCommonsMenuItem")
        .diningCommonsCode("originalDiningCommonsCode")
        .station("originalStation")
        .build();

        UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItemEdited = UCSBDiningCommonsMenuItem.builder()
        .name("editedDiningCommonsMenuItem")
        .diningCommonsCode("editedDiningCommonsCode")
        .station("editedStation")
        .build();

        String requestBody = mapper.writeValueAsString(ucsbDiningCommonsMenuItemEdited);

        when(ucsbDiningCommonsMenuItemRepository.findById(eq(67L))).thenReturn(Optional.of(ucsbDiningCommonsMenuItemOrig));

        // act
        MvcResult response = mockMvc.perform(
                        put("/api/ucsbdiningcommonsmenuitems?id=67")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .characterEncoding("utf-8")
                                        .content(requestBody)
                                        .with(csrf()))
                        .andExpect(status().isOk()).andReturn();

        // assert
        verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(67L);
        verify(ucsbDiningCommonsMenuItemRepository, times(1)).save(ucsbDiningCommonsMenuItemEdited); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(requestBody, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_cannot_edit_ucsbdiningcommonsmenuitem_that_does_not_exist() throws Exception {
        // arrange
        UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItemEdited = UCSBDiningCommonsMenuItem.builder()
        .name("editedDiningCommonsMenuItem")
        .diningCommonsCode("editedDiningCommonsCode")
        .station("editedStation")
        .build();

        String requestBody = mapper.writeValueAsString(ucsbDiningCommonsMenuItemEdited);

        when(ucsbDiningCommonsMenuItemRepository.findById(eq(67L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                        put("/api/ucsbdiningcommonsmenuitems?id=67")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .characterEncoding("utf-8")
                                        .content(requestBody)
                                        .with(csrf()))
                        .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(67L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("UCSBDiningCommonsMenuItem with id 67 not found", json.get("message"));

    }

    // tests for DELETE

    @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_diningcommonsmenuitem() throws Exception {
                // arrange

                UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItem1 = UCSBDiningCommonsMenuItem.builder()
                .name("firstDiningCommonsMenuItem")
                .diningCommonsCode("firstDiningCommonsCode")
                .station("firstStation")
                .build();

                when(ucsbDiningCommonsMenuItemRepository.findById(eq(15L))).thenReturn(Optional.of(ucsbDiningCommonsMenuItem1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsbdiningcommonsmenuitems?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(15L);
                verify(ucsbDiningCommonsMenuItemRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBDiningCommonsMenuItem with id 15 deleted", json.get("message"));
        }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_tries_to_delete_non_existant_ucsbdiningcommonsmenuitem_and_gets_right_error_message()
        throws Exception {
        // arrange

        when(ucsbDiningCommonsMenuItemRepository.findById(eq(15L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                        delete("/api/ucsbdiningcommonsmenuitems?id=15")
                                        .with(csrf()))
                        .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(15L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("UCSBDiningCommonsMenuItem with id 15 not found", json.get("message"));
    }
}
