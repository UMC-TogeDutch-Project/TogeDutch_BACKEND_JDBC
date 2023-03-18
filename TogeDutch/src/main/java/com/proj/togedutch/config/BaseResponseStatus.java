package com.proj.togedutch.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),
    USERS_EMPTY_PASSWORD(false, 2011, "비밀번호 값을 확인해주세요."),
    POST_USERS_INVALID_PASSWORD(false, 2012, "숫자, 문자, 특수문자를 모두 포함한 7글자 이상의 비밀번호를 입력해주세요."),
    POST_USERS_INVALID_PHONE(false, 2013, "숫자로만 구성된 전화번호를 입력해주세요"),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "정확한 이메일 주소를 입력해주세요"),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    POST_USERS_EMPTY_PASSWORD(false, 2018, "비밀번호를 입력해주세요."),


    // [POST] /post
    POST_POST_EMPTY_TITLE(false, 2019, "공고 제목을 입력해주세요."),
    POST_POST_EMPTY_URL(false, 2020, "URL을 입력해주세요."),
    POST_POST_EMPTY_TIP(false, 2021, "배달팁을 입력해주세요."),
    POST_POST_EMPTY_MINIMUM(false, 2022, "최소 주문 금액을 입력해주세요."),
    POST_POST_EMPTY_RECRUIT(false, 2023, "모집 인원을 선택해주세요."),
    POST_POST_EMPTY_LOCATION(false, 2024, "약속 장소를 설정해주세요."),
    POST_POST_EMPTY_CATEGORY(false, 2025, "카테고리를 설정해주세요."),

    // [POST] /Advertisement
    POST_AD_EMPTY_STORE(false, 2026, "가게 이름을 입력해주세요."),
    POST_AD_EMPTY_INFORMATION(false, 2027, "가게 정보를 입력해주세요."),
    POST_AD_EMPTY_MAINMENU(false, 2028, "메인 메뉴를 입력해주세요."),
    POST_AD_EMPTY_TIP(false, 2029, "배달팁을 입력해주세요."),
    POST_AD_EMPTY_LOCATION(false, 2030, "약속 장소를 설정해주세요."),

    // [POST] /notice
    POST_NOTICE_EMPTY_TITLE(false, 2031, "공지 제목을 설정해주세요."),
    POST_NOTICE_EMPTY_CONTENT(false, 2032, "공지 내용을 설정해주세요."),

    // [POST] /user/{userIdx}/likePost
    LIKEPOST_IMPOSSIBLE(false, 2033, "본인의 공고는 관심 목록에 담을 수 없습니다."),
    DUPLICATED_LIKEPOST(false, 2034, "이미 관심목록에 담은 공고입니다."),

    // [POST] /post/:postIdx/application
    POST_UPLOAD_MINE(false, 2035, "내가 업로드한 공고입니다."),
    DUPLICATED_APPLICATION(false, 2036, "이미 신청한 공고입니다."),
    APPLICATION_IMPOSSIBLE(false, 2037, "신청 불가능한 공고입니다."),

    // [POST] /oauth/kakao
    POST_EMPTY_KAKAO_EMAIL(false, 2038, "카카오 계정에 등록된 이메일이 없습니다."),
    FAILED_TO_KAKAO_LOGIN(false, 2039, "카카오 로그인에 실패했습니다."),

    // KakaoPay
    KAKAO_PAY_CANCLE(false, 2040, "카카오 결제를 취소했습니다."),
    KAKAO_PAY_FAIL(false, 2041, "카카오 결제에 실패했습니다."),

    POST_EMPTY_KEYWORD(false,2042, "키워드 값을 입력하세요"),
    POST_EMPTY_USER(false,2043,"해당 유저의 공고가 존재하지 않습니다"),

    // Chat
    CHAT_MEETTIME_ERROR(false,2044,"<만남시간>의 형식이 잘못되었습니다."),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),
    FAILED_TO_FIND_USER(false, 3015, "존재하지 않는 회원입니다. 회원가입이 필요합니다."),

    // [POST] /post
    FAILED_TO_FIND_BY_CATEGORY(false, 3016, "해당하는 카테고리에 공고가 없습니다."),
    NONE_INPUT_CATEGORY(false, 3017, "카테고리를 입력하지 않았습니다."),
    //Application
    NOT_FULL_NUM_OF_RECRUITS(false, 3020, "모집인원이 채워지지 않았습니다."),
    POST_NOT_ACCESSIBLE(false, 3021, "공고를 사용할 수 없으므로, 참여자를 조회할 수 없습니다."),

    // [GET] /application
    NOBODY_WAITING(false, 3022, "수락대기 중인 메이트 신청자가 없습니다."),

    //[POST] /application
    COMPLETED_STATUS(false,3031,"이미 모집이 완료된 공고입니다."),
    ACCEPT_STATUS(false,3032,"이미 수락된 공고입니다."),
    REJECTD_STATUS(false,3033,"이미 수락거절된 공고입니다."),



    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),
    MODIFY_FAIL_USER(false,4014,"유저 수정 실패"),
    //MODIFY_FAIL_STATUS(false,4021,"유저 상태 오류"),
    MODIFY_FAIL_KEYWORD(false,4015,"키워드 수정 실패"),
    DELETE_FAIL_USER(false,4020,"유저 삭제 실패"),

    DUPLICATE_KEY_ORROR (false,4021,"동일한 유저가 존재합니다."),
    FOREIGN_KEY_ORROR (false,4021,"유저가 없거나, 채팅방이 존재하지 않습니다."),


    /**
     * 5000 : 필요시 만들어서 쓰세요
     */
    // Post
    DELETE_POST_FAIL(false, 5000, "삭제에 실패했습니다."),
    NUM_Of_RECRUITS_EMPTY(false, 5001, "모집된 인원이 없습니다."),



    /**
     * 6000 : 필요시 만들어서 쓰세요
     */
    //matching
    COUNT_EXCEED(false, 6060,"이 공고의 카운트 횟수 초과.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}