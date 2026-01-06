package me.tom.cascade.network.handlers;

public enum LoginState {
    WAITING_FOR_LOGIN_START,
    WAITING_FOR_COOKIE_RESPONSE,
    WAITING_FOR_ENCRYPTION_RESPONSE,
    WAITING_FOR_LOGIN_ACK,
    COMPLETED
}