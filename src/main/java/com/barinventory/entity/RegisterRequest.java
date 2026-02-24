package com.barinventory.entity;

public record RegisterRequest(String name, String email, String password, Role role,Long barId) {}