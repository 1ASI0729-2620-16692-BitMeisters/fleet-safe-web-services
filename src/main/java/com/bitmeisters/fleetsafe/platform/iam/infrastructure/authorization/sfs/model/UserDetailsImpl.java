package com.bitmeisters.fleetsafe.platform.iam.infrastructure.authorization.sfs.model;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {

    private final UUID id;
    private final UUID companyId;
    private final String username;
    @JsonIgnore
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(UUID id, UUID companyId, String username, String password, boolean enabled,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.companyId = companyId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getId(),
                user.getCompanyId().value(),
                user.getId().toString(),
                user.getPasswordHash(),
                user.isActive(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
}
