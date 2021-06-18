package au.com.imed.portal.referrer.referrerportal.rest.account.controller.v2;

import au.com.imed.portal.referrer.referrerportal.ReferrerPortalApplication;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReferrerPortalApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-local.properties")
public class AccountDetailsControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReferrerAccountService referrerAccountService;

    @InjectMocks
    private AccountDetailsController accountDetailsController = new AccountDetailsController();

    @Test
    public void shouldRequestAccountDetailsAndSucceed() throws Exception {
        var mockReturn = new AccountDetail();
        mockReturn.setMobile("jackson");
        mockReturn.setEmail("jackson@palo-it.com");
        mockReturn.setName("jackson");
        mockReturn.setUid("0404040404");
        Mockito.when(referrerAccountService.getReferrerAccountDetail(Mockito.any(String.class))).thenReturn(mockReturn);

        this.mockMvc
                .perform(
                        get("/account/details").contentType(MediaType.APPLICATION_JSON).content("{}").header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json("{}"));
    }

    @Test
    public void shouldRequestAccountDetailsAndFail() throws Exception {
        var mockReturn = new AccountDetail();
        mockReturn.setMobile("");
        mockReturn.setEmail("");
        mockReturn.setName("");
        mockReturn.setUid("");
        Mockito.when(referrerAccountService.getReferrerAccountDetail(Mockito.any(String.class))).thenReturn(mockReturn);
        this.mockMvc
                .perform(
                        get("/account/details").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().is4xxClientError());
    }
}
