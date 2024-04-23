package com.dqt.security.constant;

public class Authority {
    public static final String ADMIN = "hasAuthority('ADMIN')";
    public static final String USER = "hasAuthority('USER')";
    public static final String ADMIN_OR_USER = "hasAnyAuthority('ADMIN') or hasAnyAuthority('USER')";
}
