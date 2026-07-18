package com.team1.appang.dto.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

//TEXT 컬럼에 JSON 배열 문자열로 저장된 이미지 목록을 파싱함
public class ImageUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    //mainImageUrl 하나와 subImages(JSON 배열)를 합쳐서 하나의 리스트로 만듦
    public static List<String> parseImages(String mainImageUrl, String subImagesJson) {
        List<String> images = new ArrayList<>();
        if (mainImageUrl != null) {
            images.add(mainImageUrl);
        }
        images.addAll(parseImageList(subImagesJson));
        return images;
    }

    //JSON 배열 문자열을 List<String>으로 변환. 비어있거나 파싱 실패 시 빈 리스트 반환
    public static List<String> parseImageList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of(); //파싱 실패해도 API 전체가 죽지 않도록 빈 리스트로 처리
        }
    }
}