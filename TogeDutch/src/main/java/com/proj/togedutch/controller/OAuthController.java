package com.proj.togedutch.controller;

import com.proj.togedutch.service.OAuthService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@AllArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private OAuthService oAuthService;

    // 카카오 로그인
    @ResponseBody
    @GetMapping("/kakao")
    public int kakaoLogin(@RequestParam String code){
        String accessToken = oAuthService.getKakaoAccessToken(code);
        System.out.println(accessToken);        // accessToken 출력

        HashMap<String, Object> userInfo = oAuthService.getUserInfo(accessToken);
        String email = String.valueOf(userInfo.get("email"));

        /*
            # return 타입 정리 #

            0 : 일반 로그인 페이지로 redirect(카카오 로그인 이메일 동의 선택 안 한 경우)
            1 : 로그인 완료 (메인 페이지로 이동)
            -1 : 회원가입 페이지로 이동 (회원가입 페이지에 해당 email 값 띄워주기) -> return 값 변경 필요
         */

        logger.info("email : " + email);      // email null이냐???
        logger.info("userInfo.get(email) : " + userInfo.get(email));

        if(userInfo.get("email") == null) {  // 카카오 로그인 이메일 동의 선택 안한 경우
            logger.info("일반 로그인 페이지로 redirect");
            return 0;
        }

        int loginResult = oAuthService.checkUserInfo(email);
        return loginResult;
    }

}