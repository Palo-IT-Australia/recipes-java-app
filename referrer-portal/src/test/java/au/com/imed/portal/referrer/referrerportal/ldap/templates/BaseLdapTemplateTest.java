package au.com.imed.portal.referrer.referrerportal.ldap.templates;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ldap.core.LdapTemplate;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BaseLdapTemplateTest {

    private final BaseLdapTemplate baseLdapTemplate = new BaseLdapTemplateMock();

    @Test
    public void shouldLoginUser() {
        var templateMock = Mockito.mock(LdapTemplate.class);
        ((BaseLdapTemplateMock) baseLdapTemplate).setMock(templateMock);

        baseLdapTemplate.authenticate("username", "password");

        verify(templateMock).authenticate("", "uid=username", "password");
    }
}

class BaseLdapTemplateMock extends BaseLdapTemplate {

    LdapTemplate mock;

    @Override
    String getBaseDomain() {
        return "basedomain";
    }

    @Override
    String getUserDn() {
        return "userdn";
    }

    @Override
    String getLdapPassword() {
        return "password";
    }

    @Override
    String getLdapUrl() {
        return "localhost";
    }

    @Override
    public String getSearchQuery(String uid) {
        return "uid=" + uid;
    }

    @Override
    public LdapTemplate getLdapTemplate() {
        return mock;
    }

    public void setMock(LdapTemplate mock) {
        this.mock = mock;
    }
}