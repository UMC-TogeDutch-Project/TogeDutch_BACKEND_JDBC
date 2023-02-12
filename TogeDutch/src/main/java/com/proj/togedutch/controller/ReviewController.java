package com.proj.togedutch.controller;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.config.BaseResponse;
import com.proj.togedutch.config.BaseResponseStatus;
import com.proj.togedutch.entity.*;
import com.proj.togedutch.service.AWSS3Service;
import com.proj.togedutch.service.PostService;
import com.proj.togedutch.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.sql.In;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AWSS3Service awsS3Service;

    @Autowired
    ReviewService reviewService;

    @Value("${cloud.aws.url}")
    private String url;

    @ResponseBody
    @PostMapping("/{applicationId}")
    public BaseResponse<Integer> createReview(@RequestBody Review review, @PathVariable("applicationId") int applicationId) throws BaseException {

        try {
            int a = reviewService.createReview(applicationId,review);
            System.out.println(a);
            return new BaseResponse<>(a);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }


    @ResponseBody
    @GetMapping("/{postId}")
    public BaseResponse<List<Review>> getTextReview(@PathVariable("postId") int postId) {
        try {
            List<Review> getTextReview = reviewService.getTextReview(postId);
            return new BaseResponse<>(getTextReview);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/emotion/{postId}")
    public BaseResponse<ReviewEmotion> getEmotionReview(@PathVariable("postId") int postId) {
        try {
            ReviewEmotion getEmotion = reviewService.getEmotionReview(postId);
            return new BaseResponse<>(getEmotion);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


}
