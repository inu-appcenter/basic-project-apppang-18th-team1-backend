package com.team1.appang.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class CartItem {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
}
