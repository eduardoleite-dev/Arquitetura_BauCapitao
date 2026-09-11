package com.baudocapitao.foundation.security;

import java.util.Optional;

public interface FoundationUserLookup {

    Optional<? extends FoundationUser> findByUsername(String username);
}
