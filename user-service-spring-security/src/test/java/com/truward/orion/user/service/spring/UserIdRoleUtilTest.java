package com.truward.orion.user.service.spring;

import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link UserIdRoleUtil}.
 *
 * @author Alexander Shabanov
 */
public final class UserIdRoleUtilTest {

  @Test
  public void shouldEncodeAndDecodeId() {
    // Given:
    final long id = 12L;

    // When:
    final String role = UserIdRoleUtil.getUserIdRoleName(id);

    // Then:
    assertEquals(id, UserIdRoleUtil.getUserIdFromRoleName(role));
  }

  @Test
  public void shouldFindIdInGrantedAuthoritiesCollection() {
    // Given:
    final long id = 12L;
    final List<SimpleGrantedAuthority> authorityList = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"),
        new SimpleGrantedAuthority(UserIdRoleUtil.getUserIdRoleName(id)),
        new SimpleGrantedAuthority("ROLE_ADMIN"));

    // When:
    final Long parsedId = UserIdRoleUtil.tryGetUserId(authorityList);

    // Then:
    assertNotNull(parsedId);
    assertEquals(id, parsedId.longValue());
  }

  @Test
  public void shouldNotFindIdInEmptyGrantedAuthoritiesCollection() {
    assertNull(UserIdRoleUtil.tryGetUserId(Collections.emptyList()));
  }

  @Test
  public void shouldNotFindIdInGrantedAuthoritiesCollection() {
    assertNull(UserIdRoleUtil.tryGetUserId(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"),
        new SimpleGrantedAuthority("ROLE_ADMIN"))));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailToParseImproperlyFormattedRole() {
    UserIdRoleUtil.getUserIdFromRoleName("ROLE_USER");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailToParseImproperlyFormattedRoleWithoutId() {
    UserIdRoleUtil.getUserIdFromRoleName("ROLE_ID_");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailToParseImproperlyFormattedRoleWithoutIdInGrantedAuthorities() {
    UserIdRoleUtil.tryGetUserId(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"),
        new SimpleGrantedAuthority("ROLE_ID_"),
        new SimpleGrantedAuthority("ROLE_ADMIN")));
  }
}
