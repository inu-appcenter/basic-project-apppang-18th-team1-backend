package com.team1.appang.dto.product;

//찜하기 토글 결과 응답
public record WishlistToggleResponse(
        boolean isWishlist, //토글 후 최종 상태
        String message
) {
    public static WishlistToggleResponse added() {
        return new WishlistToggleResponse(true, "찜 목록에 추가되었습니다.");
    }

    public static WishlistToggleResponse removed() {
        return new WishlistToggleResponse(false, "찜 목록에서 삭제되었습니다.");
    }
}