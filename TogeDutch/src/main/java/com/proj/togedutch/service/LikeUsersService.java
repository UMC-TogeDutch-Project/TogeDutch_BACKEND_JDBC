package com.proj.togedutch.service;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.config.BaseResponse;
import com.proj.togedutch.dao.LikeUsersDao;
import com.proj.togedutch.entity.LikeUsers;
import com.proj.togedutch.entity.Post;
import com.proj.togedutch.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.proj.togedutch.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class LikeUsersService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    LikeUsersDao likeUsersDao;

    @Autowired
    JwtService jwtService;

    public LikeUsers createLikePost(int userIdx, int postIdx) throws BaseException {
        try {
            int Uploader_user_id = likeUsersDao.findUploader(postIdx);
            logger.info("Uploader_id : " + Uploader_user_id);

            int likeIdx = likeUsersDao.createLikePost(userIdx, postIdx, Uploader_user_id);
            LikeUsers likeUsers = likeUsersDao.getLikePostById(likeIdx);
            return likeUsers;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int deleteLikePost(int userIdx, int postIdx) throws BaseException {
        try{
            int result = likeUsersDao.deleteLikePost(userIdx, postIdx);
            return result;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<Post> getLikePosts(int userIdx) throws BaseException {
        try {
            List<Post> likePosts = likeUsersDao.getLikePosts(userIdx);
            return likePosts;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int duplicateLikePost(int userIdx, int postIdx, int Uploader_userIdx) throws BaseException {
        try{
            int result = likeUsersDao.duplicateLikePost(userIdx, postIdx, Uploader_userIdx);
            return result;
        } catch(EmptyResultDataAccessException e1){
            return 0;
        } catch (Exception e2) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
