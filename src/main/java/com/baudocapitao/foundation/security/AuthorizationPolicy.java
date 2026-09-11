package com.baudocapitao.foundation.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

public class AuthorizationPolicy {

    private final FoundationUserLookup userLookup;
    private final String masterRole;

    public AuthorizationPolicy(FoundationUserLookup userLookup, String masterRole) {
        this.userLookup = userLookup;
        this.masterRole = masterRole;
    }

    public FoundationUser currentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Usuário não autenticado");
        }
        return userLookup.findByUsername(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("Usuário autenticado não encontrado"));
    }

    public boolean isMaster(Authentication authentication) {
        return masterRole.equals(currentUser(authentication).getRole());
    }

    public String scopedUserId(Authentication authentication, String requestedUserId) {
        FoundationUser user = currentUser(authentication);
        if (isMaster(authentication) && requestedUserId != null && !requestedUserId.isBlank()) {
            return requestedUserId;
        }
        if (requestedUserId != null && !requestedUserId.isBlank()
                && !user.getId().equals(requestedUserId)) {
            throw new AccessDeniedException("Acesso negado ao usuário informado");
        }
        return user.getId();
    }

    public void requireOwnerOrMaster(Authentication authentication, String ownerId) {
        if (!isMaster(authentication) && !currentUser(authentication).getId().equals(ownerId)) {
            throw new AccessDeniedException("Acesso negado ao recurso informado");
        }
    }

    public void requireMaster(Authentication authentication) {
        if (!isMaster(authentication)) {
            throw new AccessDeniedException("Acesso restrito ao usuário master");
        }
    }
}
