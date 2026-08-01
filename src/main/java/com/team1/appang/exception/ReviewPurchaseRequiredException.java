package com.team1.appang.exception;

//구매 이력이 없는 상품에 리뷰를 작성하려 할 때
public class ReviewPurchaseRequiredException extends RuntimeException {
    public ReviewPurchaseRequiredException(){
        super("구매한 상품만 리뷰를 작성할 수 있습니다.");
    }

    public ReviewPurchaseRequiredException(String message) {
        super(message);
    }
}
