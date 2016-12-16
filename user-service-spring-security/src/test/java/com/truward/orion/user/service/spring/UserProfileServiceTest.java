package com.truward.orion.user.service.spring;

import com.truward.orion.user.service.model.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import static com.truward.orion.user.service.model.UserModelV1.*;

/**
 * Unit tests for {@link UserProfileService}.
 *
 * @author Alexander Shabanov
 */
@RunWith(MockitoJUnitRunner.class)
public final class UserProfileServiceTest {
  @Mock UserRestService userRestServiceMock;

  final String username = "username";

  @Test
  public void shouldLookupUser() {
    // Given:
    final long id = 123000L;
    final UserProfileService profileService = new UserProfileService(userRestServiceMock);

    final UserAccount account = UserAccount.newBuilder()
        .setId(id)
        .setPasswordHash("password-hash")
        .setNickname(username)
        .setActive(false)
        .addAllAuthorities(Collections.singletonList("ROLE_USER"))
        .setCreated(1234000000000L)
        .build();

    when(userRestServiceMock.lookupAccount(AccountLookupRequest.newBuilder()
        .setUsername(username)
        .setIncludeContacts(false)
        .build()))
        .thenReturn(AccountLookupResponse.newBuilder().setAccount(account).build());

    // When:
    final UserDetails userDetails = profileService.loadUserByUsername(username);

    // Then:
    assertEquals(username, userDetails.getUsername());
    assertEquals(account.getPasswordHash(), userDetails.getPassword());
    assertEquals(account.getActive(), userDetails.isEnabled());
    assertEquals(
        new HashSet<>(Arrays.asList(new SimpleGrantedAuthority(UserIdRoleUtil.getUserIdRoleName(id)),
            new SimpleGrantedAuthority("ROLE_USER"))),
        userDetails.getAuthorities());
    assertTrue(userDetails.isAccountNonExpired());
    assertTrue(userDetails.isCredentialsNonExpired());
    assertTrue(userDetails.isAccountNonLocked());
  }

  @Test(expected = UsernameNotFoundException.class)
  public void shouldFailToFindUser() {
    // Given:
    final UserProfileService profileService = new UserProfileService(userRestServiceMock);
    when(userRestServiceMock.lookupAccount(AccountLookupRequest.newBuilder()
        .setUsername(username)
        .setIncludeContacts(false)
        .build()))
        .thenReturn(AccountLookupResponse.newBuilder().build());

    // When:
    profileService.loadUserByUsername(username);
  }
}
