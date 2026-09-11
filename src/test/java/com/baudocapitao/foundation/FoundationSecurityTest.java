package com.baudocapitao.foundation;

import com.baudocapitao.foundation.crypto.AesGcmCipher;
import com.baudocapitao.foundation.security.AuthorizationPolicy;
import com.baudocapitao.foundation.security.FoundationUser;
import com.baudocapitao.foundation.security.FoundationUserLookup;
import com.baudocapitao.foundation.security.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FoundationSecurityTest {

    @Test
    void encryptsAndDecryptsWithAuthenticatedCiphertext() {
        AesGcmCipher cipher = new AesGcmCipher("0123456789abcdef0123456789abcdef".getBytes());
        String first = cipher.encrypt("segredo");
        String second = cipher.encrypt("segredo");

        assertEquals("segredo", cipher.decrypt(first));
        assertNotEquals(first, second);
        assertThrows(IllegalStateException.class, () -> cipher.decrypt("invalid"));
    }

    @Test
    void authorizationPolicyDeniesCrossUserAccess() {
        FoundationUser user = new TestUser("user-1", "user@example.com", "USER", false);
        FoundationUserLookup lookup = username -> Optional.of(user);
        AuthorizationPolicy policy = new AuthorizationPolicy(lookup, "MASTER");
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("user@example.com", null);

        assertEquals("user-1", policy.scopedUserId(authentication, null));
        assertThrows(AccessDeniedException.class,
                () -> policy.scopedUserId(authentication, "other-user"));
        assertFalse(policy.isMaster(authentication));
    }

    @Test
    void jwtRejectsTamperedToken() {
        JwtTokenService jwt = new JwtTokenService(
                "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef", 60_000);
        String token = jwt.generateToken("user-1", "user@example.com", "User");

        assertTrue(jwt.isValid(token));
        assertEquals("user@example.com", jwt.getUsername(token));
        assertEquals("user-1", jwt.getUserId(token));
        assertFalse(jwt.isValid(token + "tampered"));
    }

    private record TestUser(String id, String username, String role, boolean blocked)
            implements FoundationUser {
        @Override
        public String getId() { return id; }

        @Override
        public String getUsername() { return username; }

        @Override
        public String getRole() { return role; }

        @Override
        public boolean isBlocked() { return blocked; }
    }
}
