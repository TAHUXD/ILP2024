package uk.ac.ed.inf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RestServiceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testValidateOrder_Valid() throws Exception {
        String validOrderJson = "{ \"orderNo\": \"12345\", \"orderDate\": \"2024-11-18\", \"priceTotalInPence\":1100,"
                + "\"pizzasInOrder\":[{\"name\":\"R1: Margarita\",\"priceInPence\":1000}],"
                + "\"creditCardInformation\":{\"creditCardNumber\":\"4485959141852684\",\"creditCardExpiry\":\"12/25\",\"cvv\":\"123\"}}";

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("VALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("NO_ERROR"));
    }

    @Test
    void testValidateOrder_Invalid() throws Exception {
        String invalidOrderJson = "{ \"orderNo\": \"12346\", \"orderDate\": \"2024-11-18\", \"priceTotalInPence\":100,"
                + "\"pizzasInOrder\":[],"
                + "\"creditCardInformation\":{\"creditCardNumber\":\"4485959141852684\",\"creditCardExpiry\":\"12/25\",\"cvv\":\"123\"}}";

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidOrderJson))
                .andExpect(status().isOk()) // Still returns 200 with error code
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("EMPTY_ORDER"));
    }

    @Test
    void testCalcDeliveryPath_Valid() throws Exception {
        String validOrderJson = "{ \"orderNo\": \"12345\", \"orderDate\": \"2024-11-18\", \"priceTotalInPence\":1100,"
                + "\"pizzasInOrder\":[{\"name\":\"R1: Margarita\",\"priceInPence\":1000}],"
                + "\"creditCardInformation\":{\"creditCardNumber\":\"4485959141852684\",\"creditCardExpiry\":\"12/25\",\"cvv\":\"123\"}}";

        mockMvc.perform(post("/calcDeliveryPath")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrderJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCalcDeliveryPathGeoJSON_Valid() throws Exception {
        String validOrderJson = "{ \"orderNo\": \"12345\", \"orderDate\": \"2024-11-18\", \"priceTotalInPence\":1100,"
                + "\"pizzasInOrder\":[{\"name\":\"R1: Margarita\",\"priceInPence\":1000}],"
                + "\"creditCardInformation\":{\"creditCardNumber\":\"4485959141852684\",\"creditCardExpiry\":\"12/25\",\"cvv\":\"123\"}}";

        mockMvc.perform(post("/calcDeliveryPathGeoJSON")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Feature"))
                .andExpect(jsonPath("$.geometry.type").value("LineString"))
                .andExpect(jsonPath("$.properties.name").value("Delivery Path"));
    }
}

