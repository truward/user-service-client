package com.truward.orion.user.service.spring;

import com.truward.orion.user.service.model.UserModel;
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
    final UserModel.AccountLookupResponse r = userRestService.lookupAccount(UserModel.AccountLookupRequest.newBuilder()
        .setUsername(username)
        .setIncludeContacts(false)
        .build());
    if (!r.hasAccount()) {
      throw new UsernameNotFoundException(username);
    }

    final UserModel.UserAccount acc = r.getAccount();

    return new User(Long.toString(acc.getId()), acc.getPasswordHash(), acc.getActive(), true, true, true,
        toAuthorities(acc.getAuthoritiesList()));
  }

  //
  // Private
  //

  @Nonnull
  private List<SimpleGrantedAuthority> toAuthorities(@Nonnull List<String> roles) {
    final List<SimpleGrantedAuthority> result = new ArrayList<>(roles.size());
    for (final String r : roles) {
      result.add(getOrInsert(r));
    }
    return result;
  }

  @Nonnull
  private SimpleGrantedAuthority getOrInsert(@Nonnull String role) {
    SimpleGrantedAuthority result = authorityCacheMap.get(role);
    if (result == null) {
      // NOTE: synchronization is not actually needed here
      result = new SimpleGrantedAuthority(role);
      // it's ok even if two different authorities will be created simultaneously - they will be equal anyways
      authorityCacheMap.put(role, result);
    }
    return result;
  }
}
