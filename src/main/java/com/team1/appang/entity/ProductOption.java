package com.team1.appang.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class ProductOption {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
}
