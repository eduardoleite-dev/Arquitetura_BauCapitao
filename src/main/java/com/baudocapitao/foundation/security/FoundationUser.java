package com.baudocapitao.foundation.security;

public interface FoundationUser {

    String getId();

    String getUsername();

    String getRole();

    boolean isBlocked();
}
