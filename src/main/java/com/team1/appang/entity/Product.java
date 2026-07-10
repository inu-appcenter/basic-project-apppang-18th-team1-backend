package com.team1.appang.entity;

import jakarta.persistence.*;
import org.w3c.dom.Text;

public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String brand_name;
    private int discount_rate;
    private String unit_price_text; //단위당 가격
    private Text detail_images; //상세 이미지

    //nullable 특징
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int origin_price;

    @Column(nullable = false)
    private int sale_price;

    @Column(nullable = false)
    private String main_image_url;

    //외래키. JPA 규칙에 맞춰 객체로 생성
    @ManyToOne // 일대다 관계이므로
    @JoinColumn(name = "category_id")
    private Category category;
}
