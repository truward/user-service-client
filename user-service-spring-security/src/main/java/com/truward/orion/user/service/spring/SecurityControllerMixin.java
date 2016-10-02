package com.truward.orion.user.service.spring;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * A mixin, that provides helper methods (intended to use by Spring MVC Controllers) for accessing user account data.
 * <p>
 * Implementation-wise, this mixin relies on thread local variables, set by Spring Security, that store authentication
 * information and role information, set by {@link UserProfileService}. This role information is required for accessing
 * user ID.
 * </p>
 *
 * @author Alexander Shabanov
 */
public interface SecurityControllerMixin {
  /**
   * A key, which is associated with UserDetails instance in the map, returned by {@link #newMapWithAccount()} method.
   * Should always equal to <code>userAccount</code>, so view code will never have to refer to this constant.
   */
  String USER_ACCOUNT_KEY = "userAccount";

  /**
   * @return Authentication object
   */
  @Nullable
  default Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  /**
   * Attempts to restore user details information based on session information.
   *
   * @return UserDetails instance of the authenticated user, null for anonymous users
   */
  @Nullable
  default UserDetails getUserAccount() {
    final Authentication auth = getAuthentication();
    if (auth == null) {
      return null;
    }
    final Object details = auth.getPrincipal();
    if (details instanceof UserDetails) {
      return (UserDetails) details;
    }
    throw new InsufficientAuthenticationException("Internal: unknown principal=" + details); // should not happen
  }

  /**
   * @return True, if user account information is accessible
   */
  default boolean hasUserAccount() {
    return getUserAccount() != null;
  }

  /**
   * Returns user ID.
   * Throws {@link InsufficientAuthenticationException} if it can't be done, i.e. for anonymous users.
   *
   * @return ID of the authenticated user
   */
  default long getUserId() {
    final UserDetails account = getUserAccount();
    final Long id = account != null ? UserIdRoleUtil.tryGetUserId(account.getAuthorities()) : null;
    if (id == null) {
      throw new InsufficientAuthenticationException("Internal: UserAccount has not been found or RoleId is not a " +
          "part of account=" + account);
    }
    return id;
  }

  /**
   * Returns new map with user account. This method is useful for controllers that have to provide user account
   * information to the view.
   * Throws {@link InsufficientAuthenticationException} if it can't be done, i.e. for anonymous users.
   *
   * @return Newly created map with {@link UserDetails} instance associated with {@link #USER_ACCOUNT_KEY} key
   */
  default Map<String, Object> newMapWithAccount() {
    final Map<String, Object> result = new HashMap<>();
    result.put(USER_ACCOUNT_KEY, getUserAccount());
    return result;
  }
}
