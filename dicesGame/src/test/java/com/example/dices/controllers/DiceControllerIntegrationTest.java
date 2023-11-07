package com.example.dices.controllers;


import com.example.dices.models.DiceModel;
import com.example.dices.models.JsonModel;
import com.example.dices.repositories.IDiceRepository;
import com.example.dices.services.DiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class DiceControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IDiceRepository diceRepository;

    @Autowired
    private DiceService diceService;

    @Autowired
    private ObjectMapper objectMapper;

    public static class TestConstants {
        public static final int DEFAULT_DICE_ID1 = 1;
        public static final int DEFAULT_DICE_SIZE1 = 6;
        public static final int DEFAULT_DICE_ID2 = 5;
        public static final int DEFAULT_DICE_SIZE2 = 9;
        public static final int DEFAULT_DICE_NOT_EXIST = 5;
        public static final int DEFAULT_DICE_INVALID_SIZE = 1001;
        public static final String ERROR_MESSAGE_1 = "There is no information to display";
        public static final String ERROR_MESSAGE_2 = "Dice not found with ID: ";
        public static final String ERROR_MESSAGE_3 = "The dice size must be between 1 and 1000.";

    }

    @BeforeEach
    public void setUp() {
        // Initialize Databases

        DiceModel diceModel = new DiceModel();
        diceModel.setDiceId(TestConstants.DEFAULT_DICE_ID1);
        diceModel.setDiceSize(TestConstants.DEFAULT_DICE_SIZE1);
        diceModel.setDiceId(TestConstants.DEFAULT_DICE_ID2);
        diceModel.setDiceSize(TestConstants.DEFAULT_DICE_SIZE2);
        diceRepository.save(diceModel);
    }

    @AfterEach
    public void cleanDatabase() {
        // Clean repository after each test.
        diceRepository.deleteAll();
    }


    @Test
    void testGetDicesNoEmpty() throws Exception {
        // makes the GET request to the /api/v1/dices endpoint
        // Verify that the response has a status 200 (OK)
        MvcResult result = mockMvc.perform(get("/api/v1/dices"))
                .andExpect(status().isOk())
                .andReturn();

        // Get the response as a JSON string and perform the necessary checks
        String contentType = result.getResponse().getContentType();
        assertEquals("application/json", contentType);
    }


    @Test
    void testGetDicesEmpty() throws Exception {
        // Make the GET request to the /api/v1/dices endpoint
        diceRepository.deleteAll();
        mockMvc.perform(get("/api/v1/dices"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(TestConstants.ERROR_MESSAGE_1));
    }


    @Test
    void testSaveDiceWithValidSize() throws Exception {

        // create a DiceModel object to send in the POST request
        DiceModel diceModel = new DiceModel();
        diceModel.setDiceSize(TestConstants.DEFAULT_DICE_SIZE2);

        // convert the object to JSON
        String diceJson = objectMapper.writeValueAsString(diceModel);

        // make the POST request to the endpoint /api/v1/dices
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/dices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(diceJson))
                .andExpect(status().isOk()) // verify that the response is 200 OK
                .andReturn();

        String contentType = result.getResponse().getContentType();
        assertEquals("application/json", contentType);

        // Get the response as a DiceModel object
        String responseBody = result.getResponse().getContentAsString();
        DiceModel responseDice = objectMapper.readValue(responseBody, DiceModel.class);

        // verify that the response is not null and that it has an assigned ID
        assertNotNull(responseDice);
        assertNotNull(responseDice.getDiceId());
    }


    @Test
    void testSaveDiceWithInvalidSize() throws Exception {

        // create a DiceModel object to send in the POST request
        DiceModel invalidDiceSize = new DiceModel();
        invalidDiceSize.setDiceSize(TestConstants.DEFAULT_DICE_INVALID_SIZE);

        // convert the object to JSON
        String requestBody = objectMapper.writeValueAsString(invalidDiceSize);

        // make the POST request to the endpoint /api/v1/dices
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/dices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        // display message error
        assertEquals(TestConstants.ERROR_MESSAGE_3, responseContent);
    }

    @Test
    public void testGetDiceByIdExist() throws Exception {

        // ARRANGE
        DiceModel diceExist = new DiceModel();
        diceExist.setDiceSize(TestConstants.DEFAULT_DICE_ID2);
        diceExist.setDiceSize(TestConstants.DEFAULT_DICE_SIZE2);
        diceRepository.save(diceExist);
        int idToTest = TestConstants.DEFAULT_DICE_ID2;

        // ACT
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/dices/{id}", idToTest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        DiceModel responseDice = objectMapper.readValue(responseBody, DiceModel.class);
        assertEquals(idToTest, responseDice.getDiceId());

        // ASSERT
        assertEquals(200, result.getResponse().getStatus());
        assertEquals(idToTest, responseDice.getDiceId());

    }


    @Test
    public void testGetDiceByIdNotExist() throws Exception {

        // ARRANGE
        // makes the GET request to the endpoint /api/v1/dices/{id}

        int idToTest = TestConstants.DEFAULT_DICE_NOT_EXIST;

        MvcResult result = mockMvc.perform(get("/api/v1/dices/{id}", idToTest))
                .andExpect(status().isNotFound())
                .andReturn();

        // ACT
        String responseBody = result.getResponse().getContentAsString();

        // ASSERT
        assertEquals(404, result.getResponse().getStatus());
        assertEquals((TestConstants.ERROR_MESSAGE_2 + idToTest), responseBody);

    }


    @Test
    void testCalculateRandomNumberWithDiceIsPresent() throws Exception {

        // makes the POST request to the endpoint /api/v1/dices/{id}/rolls
        // ARRANGE

        int idToRoll = TestConstants.DEFAULT_DICE_ID1;
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/dices/{id}/rolls", idToRoll))
                .andExpect(status().isOk())
                .andReturn();

        // ACT
        String contentType = result.getResponse().getContentType();
        assertEquals("application/json", contentType);

        // Get the response as a DiceModel object
        String responseBody = result.getResponse().getContentAsString();
        JsonModel responseDice = objectMapper.readValue(responseBody, JsonModel.class);
        int roll = responseDice.getRoll();

        // ASSERTS
        assertNotNull(responseDice); // verify that the response is not null and that it has an assigned ID
        assertEquals(idToRoll, responseDice.getId());
        assertTrue(roll > 0);

    }

    @Test
    void testCalculateRandomNumberWithDiceIsNotPresent() throws Exception {

        // makes the POST request to the endpoint /api/v1/dices/{id}/rolls
        // ARRANGE

        int idToRoll = TestConstants.DEFAULT_DICE_NOT_EXIST;
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/dices/{id}/rolls", idToRoll))
                .andExpect(status().isNotFound())
                .andReturn();

        // ACT
        // Get the response as a DiceModel object
        String responseBody = result.getResponse().getContentAsString();

        // ASSERT
        if (result.getResponse().getStatus() == HttpStatus.NOT_FOUND.value())
            assertEquals((TestConstants.ERROR_MESSAGE_2 + idToRoll), responseBody);

    }


    @Test
    void testDeleteDiceById() throws Exception {
        // make a DELETE request to the endpoint /api/v1/dices/{id}
        int diceIdToDelete = 1; // ID to delete

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/dices/{id}", diceIdToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // Verify HTTP 200 OK

        // Verify that ID deleted not exist.
        assertTrue(diceRepository.findById(diceIdToDelete).isEmpty());

    }
}