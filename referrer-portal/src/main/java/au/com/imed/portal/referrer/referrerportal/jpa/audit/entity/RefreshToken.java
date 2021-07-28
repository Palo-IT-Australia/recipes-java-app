package au.com.imed.portal.referrer.referrerportal.jpa.audit.entity;


import javax.persistence.*;

@Entity
@Table(name = "REFRESH_TOKEN", catalog = "dbo", indexes = {@Index(name="user_id_idx", columnList = "user_id", unique = false)})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "valid")
    private boolean valid;

    public RefreshToken(){}

    public RefreshToken(String userId, String refreshToken) {
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.valid = true;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
