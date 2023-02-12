package com.proj.togedutch.controller;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.config.BaseResponse;
import com.proj.togedutch.entity.Post;
import com.proj.togedutch.entity.User;
import com.proj.togedutch.service.AWSS3Service;
import com.proj.togedutch.service.MatchingService;
import com.proj.togedutch.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matching")
public class MatchingController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AWSS3Service awsS3Service;

    @Autowired
    MatchingService matchingService;

    @Value("${cloud.aws.url}")
    private String url;

    @ResponseBody
    @GetMapping("/{postIdx}")
    public BaseResponse<User> getFirstMatching(@PathVariable("postIdx") int postIdx) throws BaseException {
        try {
            User getMatching = matchingService.getFirstMatching(postIdx);
            return new BaseResponse<>(getMatching);
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ResponseBody
    @GetMapping("/rematching/{postIdx}")
    public BaseResponse<User> getReMatching(@PathVariable("postIdx") int postIdx) throws BaseException {
        try {
            User getMatching = matchingService.getReMatching(postIdx);
            return new BaseResponse<>(getMatching);
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
    @ResponseBody
    @GetMapping("/accept/{userIdx}/{postIdx}")
    public BaseResponse<Integer> getAcceptUserId(@PathVariable("userIdx") int userIdx, @PathVariable("postIdx") int postIdx ) throws BaseException {
        try {
            int getMatching = matchingService.getAcceptUserId(userIdx,postIdx);
            return new BaseResponse<>(getMatching);
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
    @ResponseBody
    @GetMapping("/deny/{userIdx}/{postIdx}")
    public BaseResponse<Integer> getDenyUserId(@PathVariable("userIdx") int userIdx, @PathVariable("postIdx") int postIdx) throws BaseException {

        try {
            int getMatching = matchingService.getDenyUserId(userIdx,postIdx);
            return new BaseResponse<>(getMatching);
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
    @ResponseBody
    @GetMapping("/wait/{userIdx}/{postIdx}")
    public BaseResponse<Integer> getWaitUserId(@PathVariable("userIdx") int userIdx, @PathVariable("postIdx") int postIdx) throws BaseException {
        try {
        int getMatching = matchingService.getWaitApplicationId(userIdx,postIdx);
            return new BaseResponse<>(getMatching);
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
}
