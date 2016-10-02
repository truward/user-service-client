package com.truward.orion.user.service.spring;

import org.junit.Test;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Nullable;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link SecurityControllerMixin}
 *
 * @author Alexander Shabanov
 */
public final class SecurityControllerMixinTest {

  @Test
  public void shouldNotHaveUserAccount() {
    // Given:
    final SecurityControllerMixin securityController = new NullAuthenticationSecurityController();

    // When:
    final UserDetails userDetails = securityController.getUserAccount();
    final boolean hasAccount = securityController.hasUserAccount();

    // Then:
    assertNull(userDetails);
    assertFalse(hasAccount);
  }

  @Test(expected = InsufficientAuthenticationException.class)
  public void shouldFailToProvideUserId() {
    // Given:
    final SecurityControllerMixin securityController = new NullAuthenticationSecurityController();

    // When:
    securityController.getUserId();

    // Then: (exception)
  }

  @Test
  public void shouldFailToProvideUserAccountMap() {
    // Given:
    final SecurityControllerMixin securityController = new NullAuthenticationSecurityController();

    // When:
    final Map<String, ?> map = securityController.newMapWithAccount();

    // Then:
    assertEquals(1, map.size());
    assertNull(map.get(SecurityControllerMixin.USER_ACCOUNT_KEY));
  }

  @Test
  public void shouldAlwaysHaveStrictAccountNameDefinition() {
    // make sure this field will never be changed accidentally as views will likely refer to this name
    // without referring to the constant.
    assertEquals("userAccount", SecurityControllerMixin.USER_ACCOUNT_KEY);
  }

  //
  // Private
  //

  private static final class NullAuthenticationSecurityController implements SecurityControllerMixin {
    @Nullable
    @Override
    public Authentication getAuthentication() {
      return null;
    }
  }
}
