package com.qa.api.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class GETAPICall {

    private Playwright playwright;
    private APIRequest request;
    private ObjectMapper objectMapper;

    @BeforeClass
    public void setUp() {
        playwright = Playwright.create();
        request = playwright.request();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testRandomNumberAPIWithRange() throws IOException {
        String url = "http://www.randomnumberapi.com/api/v1.0/random?min=100&max=1000&count=5";
        List<Integer> values = sendAndValidateRequest(url, new TypeReference<List<Integer>>() {});
        Assert.assertEquals(values.size(), 5, "Expected 5 values in the response but found " + values.size());
    }

    @Test
    public void testRandomNumberAPI() throws IOException {
        String url = "http://www.randomnumberapi.com/api/v1.0/random";
        sendAndValidateRequest(url, new TypeReference<List<Integer>>() {});
    }

    @Test
    public void testRandomString() throws IOException {
        String url = "http://www.randomnumberapi.com/api/v1.0/randomstring";
        List<String> responseBody = sendAndValidateRequest(url, new TypeReference<List<String>>() {});

        // Validate if the response body is of type List<String> (array with a single string element)
        Assert.assertTrue(responseBody.size() == 1, "Array should contain only one element.");

        String randomString = responseBody.get(0);

        // Validate if the string is of expected length
        int expectedmaxLength = 1000; // Example expected length
        int stringlength = randomString.length();

        if (stringlength < expectedmaxLength) {

            System.out.println("String length is within the expected maximum length.");
        } else {
            // Action when the string length is exceeds the expected limit
            System.out.println("String length exceeds the expected maximum length.");
        }



        // Optional: Check if the string is not empty
        Assert.assertTrue(randomString.length() > 0, "String should not be empty.");
    }


    @Test
    public void testUid() throws IOException {
        String url = "http://www.randomnumberapi.com/api/v1.0/uuid?count=2";
        List<String> values = sendAndValidateRequest(url, new TypeReference<List<String>>() {});
        Assert.assertEquals(values.size(), 2, "Expected 2 UUID values in the response but found " + values.size());
    }

    // Generic method to handle sending the request and validating the response
    private <T> T sendAndValidateRequest(String url, TypeReference<T> valueTypeRef) throws IOException {
        APIResponse response = sendGetRequest(url);
        validateResponse(response.status(), response.statusText(), 200, "OK");
        return parseJsonResponse(response.body(), valueTypeRef);
    }


    private <T> T sendAndValidateRequest(String url, Class<T> valueType) throws IOException {
        APIResponse response = sendGetRequest(url);
        validateResponse(response.status(), response.statusText(), 200, "OK");
        return parseJsonResponse(response.body(), valueType);
    }

    // Send GET request
    private APIResponse sendGetRequest(String url) {
        APIRequestContext context = request.newContext();
        return context.get(url);
    }

    // Validate status code and status text
    private void validateResponse(int statusCode, String statusText, int expectedStatusCode, String expectedStatusText) {
        Assert.assertEquals(statusCode, expectedStatusCode, "Unexpected status code: " + statusCode);
        Assert.assertEquals(statusText, expectedStatusText, "Unexpected status text: " + statusText);
    }

    // Parse JSON response into specific type
    private <T> T parseJsonResponse(byte[] responseBody, TypeReference<T> valueTypeRef) throws IOException {
        String responseBodyStr = new String(responseBody);
        return objectMapper.readValue(responseBodyStr, valueTypeRef);
    }

    // Overloaded parse method for simpler types
    private <T> T parseJsonResponse(byte[] responseBody, Class<T> valueType) throws IOException {
        String responseBodyStr = new String(responseBody);
        return objectMapper.readValue(responseBodyStr, valueType);
    }

    @AfterClass
    public void tearDown() {
        if (playwright != null) {
            playwright.close();
        }
    }
}
