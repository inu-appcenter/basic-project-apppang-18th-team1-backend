package com.team1.appang.exception;

//본인이 작성하지 않은 리뷰를 수정하려 할 때
public class ReviewAccessDeniedException extends RuntimeException {
    public ReviewAccessDeniedException(){
        super("본인이 작성한 리뷰만 수정할 수 있습니다.");
    }

    public ReviewAccessDeniedException(String message) {
        super(message);
    }
}
