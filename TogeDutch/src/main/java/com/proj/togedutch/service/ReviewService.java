package com.proj.togedutch.service;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.dao.ReviewDao;
import com.proj.togedutch.dao.UserDao;
import com.proj.togedutch.entity.Keyword;
import com.proj.togedutch.entity.Review;
import com.proj.togedutch.entity.ReviewEmotion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.proj.togedutch.config.BaseResponseStatus.DATABASE_ERROR;
import static com.proj.togedutch.config.BaseResponseStatus.DUPLICATED_EMAIL;

@Service
public class ReviewService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    ReviewDao reviewDao;

    public int createReview(int applicationId,Review review) throws BaseException {
        try {
            return reviewDao.createReview(applicationId,review);
        } catch (Exception e) {
            throw new BaseException(DUPLICATED_EMAIL);
        }

    }
    //키워드 조회(키워드 id)
    public List<Review> getTextReview(int postId) throws BaseException{
        try {
            return reviewDao.getTextReview(postId);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public ReviewEmotion getEmotionReview(int postId) throws BaseException{
        try {
            return reviewDao.getEmotionReview(postId);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
