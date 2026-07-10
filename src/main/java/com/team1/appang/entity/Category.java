package com.team1.appang.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

//상품의 카테고리를 담는 엔티티
public class Category {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    //카테고리는 하나만 존재해야하므로 umique 지정
    @Column(nullable = false, unique = true)
    private String name;

    private String icon_image_url;

    //정렬순서를 nullable로 할지는 논의 필요
    private Long sort_order;
}
