package com.team1.appang.dto.address;

import com.team1.appang.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;

//배송지 하나를 표현하는 DTO. 등록/수정/목록 조회 응답에서 공통으로 사용
public record AddressData(
        @Schema(description = "배송지 id", example = "1")
        Long addressId,
        @Schema(description = "배송지 별칭", example = "집")
        String alias,
        @Schema(description = "수령인 이름", example = "홍길동")
        String recipientName,
        @Schema(description = "수령인 연락처", example = "01012345678")
        String recipientPhone,
        @Schema(description = "기본 주소", example = "인천광역시 연수구 아카데미로 119")
        String mainAddress,
        @Schema(description = "상세 주소", example = "정보통신공학과 401호")
        String detailAddress,
        @Schema(description = "배송 요청사항", example = "부재시 경비실에 맡겨주세요")
        String deliveryMessage,
        @Schema(description = "기본 배송지 여부", example = "true")
        boolean isDefault
) {
    public static AddressData from(Address address) {
        return new AddressData(
                address.getId(),
                address.getAlias(),
                address.getRecipientName(),
                address.getRecipientPhone(),
                address.getMainAddress(),
                address.getDetailAddress(),
                address.getDeliveryMessage(),
                address.isDefault()
        );
    }
}
