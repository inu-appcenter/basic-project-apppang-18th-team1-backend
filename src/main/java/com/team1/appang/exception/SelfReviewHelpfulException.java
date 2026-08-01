package com.team1.appang.exception;

//본인이 작성한 리뷰에 도움돼요를 누르려 할 때
public class SelfReviewHelpfulException extends RuntimeException {
    public SelfReviewHelpfulException(){
        super("자신이 작성한 리뷰에는 도움돼요! 를 누를 수 없습니다");
    }

    public SelfReviewHelpfulException(String message) {
        super(message);
    }
}
