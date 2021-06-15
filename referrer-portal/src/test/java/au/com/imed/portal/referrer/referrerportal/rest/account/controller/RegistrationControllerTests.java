package au.com.imed.portal.referrer.referrerportal.rest.account.controller;
import au.com.imed.portal.referrer.referrerportal.ReferrerPortalApplication;
import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_ERROR_MSG;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_SUCCESS_MSG;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReferrerPortalApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-local.properties")
public class RegistrationControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReferrerCreateAccountService referrerCreateAccountService;

    @InjectMocks
    private RegistrationController registrationController = new RegistrationController();

    @Test
    public void shouldTryRegisterUserAndSucceed() throws Exception {
        var mockReturn = new HashMap<String, String>();
        mockReturn.put(MODEL_KEY_SUCCESS_MSG, "data");
        Mockito.when(referrerCreateAccountService.createAccount(Mockito.any(ExternalUser.class))).thenReturn(mockReturn);

        this.mockMvc
                .perform(
                        post("/registration/apply").contentType(MediaType.APPLICATION_JSON).content("{}"))
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
                        post("/registration/apply").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().is4xxClientError());
    }
}


