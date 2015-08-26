package com.truward.orion.user.service.spring;

import org.springframework.security.core.GrantedAuthority;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Utility class for operating with unique user role that contains user ID.
 *
 * @author Alexander Shabanov
 */
public final class UserIdRoleUtil {
  private UserIdRoleUtil() {}

  private static final String USER_ROLE_HEADING = "ROLE_ID_";

  @Nonnull
  public static String getUserIdRoleName(long userId) {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder builder = new StringBuilder(USER_ROLE_HEADING.length() + 16);

    builder.append(USER_ROLE_HEADING).append(userId);
    return builder.toString();
  }

  public static long getUserIdFromRoleName(@Nonnull String roleName) {
    if (!roleName.startsWith(roleName)) {
      throw new IllegalArgumentException("Given roleName=" + roleName +
          " does not start with heading=" + USER_ROLE_HEADING);
    }

    final String idStr = roleName.substring(USER_ROLE_HEADING.length());
    try {
      return Long.parseLong(idStr);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Given roleName " + roleName + " does not contain properly encoded ID", e);
    }
  }

  @Nullable
  public static Long tryGetUserId(@Nonnull Collection<? extends GrantedAuthority> authorities) {
    for (final GrantedAuthority authority : authorities) {
      final String roleName = authority.getAuthority();
      if (roleName.startsWith(USER_ROLE_HEADING)) {
        return getUserIdFromRoleName(roleName);
      }
    }
    return null;
  }
}
