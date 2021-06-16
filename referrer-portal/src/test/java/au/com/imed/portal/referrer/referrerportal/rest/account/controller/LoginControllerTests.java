package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import au.com.imed.portal.referrer.referrerportal.ReferrerPortalApplication;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_ERROR_MSG;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_SUCCESS_MSG;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReferrerPortalApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-local.properties")
public class LoginControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReferrerCreateAccountService referrerCreateAccountService;

    @InjectMocks
    private LoginController loginController = new LoginController();

    @Test
    public void shouldTryRegisterUserAndSucceed() throws Exception {
        var mockReturn = new HashMap<String, String>();
        mockReturn.put(MODEL_KEY_SUCCESS_MSG, "data");
        Mockito.when(referrerCreateAccountService.createAccount(Mockito.any(ExternalUser.class))).thenReturn(mockReturn);

        this.mockMvc
                .perform(
                        post("/portal/register").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json("{}"));
    }

    @Test
    public void shouldTryRegisterUserAndFail() throws Exception {
        var mockReturn = new HashMap<String, String>();
        mockReturn.put(MODEL_KEY_ERROR_MSG, "data");
        Mockito.when(referrerCreateAccountService.createAccount(Mockito.any(ExternalUser.class))).thenReturn(mockReturn);

        this.mockMvc
                .perform(
                        post("/portal/register").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().is4xxClientError());
    }
}


