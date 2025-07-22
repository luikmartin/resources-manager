package com.martinluik.resourcesmanager.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public final class MockMvcTestUtils {

  public static ResultActions performAndExpect(
      MockMvc mockMvc,
      MockHttpServletRequestBuilder requestBuilder,
      int expectedStatus,
      MediaType expectedContentType)
      throws Exception {
    return mockMvc
        .perform(requestBuilder)
        .andExpect(status().is(expectedStatus))
        .andExpect(content().contentType(expectedContentType));
  }

  public static ResultActions performAndExpect(
      MockMvc mockMvc, MockHttpServletRequestBuilder requestBuilder, int expectedStatus)
      throws Exception {
    return mockMvc.perform(requestBuilder).andExpect(status().is(expectedStatus));
  }
}
