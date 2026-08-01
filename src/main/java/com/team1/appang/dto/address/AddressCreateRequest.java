package com.team1.appang.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AddressCreateRequest(
        @Schema(description = "배송지 별칭", example = "집")
        String alias,
        @Schema(description = "수령인 이름", example = "홍길동")
        @NotBlank(message = "수령인 이름을 입력해주세요.")
        String recipientName,
        @Schema(description = "수령인 연락처", example = "01012345678")
        @NotBlank(message = "수령인 연락처를 입력해주세요.")
        String recipientPhone,
        @Schema(description = "기본 주소", example = "인천광역시 연수구 아카데미로 119")
        @NotBlank(message = "기본 주소를 입력해주세요.")
        String mainAddress,
        @Schema(description = "상세 주소", example = "정보통신공학과 401호")
        String detailAddress,
        @Schema(description = "배송 요청사항", example = "부재시 경비실에 맡겨주세요")
        String deliveryMessage,
        @Schema(description = "기본 배송지로 지정할지 여부", example = "false")
        boolean isDefault
) {
}
