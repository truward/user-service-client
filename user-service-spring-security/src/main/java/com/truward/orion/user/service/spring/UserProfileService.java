package com.truward.orion.user.service.spring;

import com.truward.orion.user.service.model.UserRestService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.truward.orion.user.service.model.UserModelV1.*;

/**
 * Implementation of Spring Security's {@link UserDetailsService} using user-service client.
 *
 * @author Alexander Shabanov
 */
public final class UserProfileService implements UserDetailsService {
  private final UserRestService userRestService;
  private final Map<String, SimpleGrantedAuthority> authorityCacheMap = new ConcurrentHashMap<>(100);

  public UserProfileService(@Nonnull UserRestService userRestService) {
    this.userRestService = Objects.requireNonNull(userRestService, "userRestService");
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    final AccountLookupResponse r = userRestService.lookupAccount(AccountLookupRequest.newBuilder()
        .setUsername(username)
        .setIncludeContacts(false)
        .build());
    if (!r.hasAccount()) {
      throw new UsernameNotFoundException(username);
    }

    final UserAccount acc = r.getAccount();

    return new User(acc.getNickname(), acc.getPasswordHash(), acc.getActive(),
        true, true, true,
        toAuthorities(acc.getId(), acc.getAuthoritiesList()));
  }

  //
  // Private
  //

  @Nonnull
  private List<SimpleGrantedAuthority> toAuthorities(long userId, @Nonnull List<String> roles) {
    final List<SimpleGrantedAuthority> result = new ArrayList<>(roles.size() + 1);

    // add user ID-encoded role
    result.add(new SimpleGrantedAuthority(UserIdRoleUtil.getUserIdRoleName(userId)));

    // add standard assigned roles
    for (final String r : roles) {
      result.add(getOrInsert(r));
    }

    return result;
  }

  @Nonnull
  private SimpleGrantedAuthority getOrInsert(@Nonnull String role) {
    // NOTE: synchronization is absolutely required needed here
    return authorityCacheMap.computeIfAbsent(role, k -> new SimpleGrantedAuthority(role));
  }
}
