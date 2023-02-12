package com.proj.togedutch.service;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.entity.Matching;
import com.proj.togedutch.entity.Post;
import com.proj.togedutch.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import static com.proj.togedutch.config.BaseResponseStatus.COUNT_EXCEED;
import static com.proj.togedutch.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class MatchingService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    com.proj.togedutch.dao.MatchingDao MatchingDao;

    public User getFirstMatching(int postIdx) throws BaseException {
        try{
            User getMatching = MatchingDao.getFirstMatching(postIdx);
            return getMatching;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public User getReMatching(int postIdx) throws BaseException {
        int MatchingCount = 500;
        User user = null;
        try{
            Post post = MatchingDao.getReMatchingFirst(postIdx);
            Matching matching =MatchingDao.getReMatchingSecond(post.getPost_id());
            int first=-1;
            int second=-1;

            if(matching.getCount() == 1){
                first = matching.getUserFirstId();
                user=MatchingDao.getReMatchingThird(post,matching,matching.getCount(), first , second);
                MatchingCount=MatchingDao.getReMatching(matching.getCount(),user,post.getPost_id());
            }
            else if (matching.getCount() == 2) {
                first = matching.getUserFirstId();
                second = matching.getUserSecondId();
                user=MatchingDao.getReMatchingThird(post,matching,matching.getCount(), first , second);
                MatchingCount=MatchingDao.getReMatching(matching.getCount(),user,post.getPost_id());
            }
            else if (matching.getCount() == 3){
                MatchingCount=400; //횟수 초과
                user = new User("3번 횟수 초과");
                return user;
            }

            return user;
        } catch(EmptyResultDataAccessException e){
            MatchingCount=300;
            Post post = MatchingDao.getReMatchingFirst(postIdx);
            user = MatchingDao.getNoMatching(post.getLatitude(),post.getLongitude(),post.getPost_id(),post);

            return user;
            //throw new BaseException(DATABASE_ERROR);
        }


    }
    public int getAcceptUserId(int userIdx,int postIdx) throws BaseException {
        try{
            int getAccept = MatchingDao.getAcceptUserId(userIdx,postIdx);
            return getAccept;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int getDenyUserId(int userIdx, int postIdx) throws BaseException {
        try{
            int getDeny = MatchingDao.getDenyUserId(userIdx, postIdx);
            return getDeny;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int getWaitApplicationId(int userIdx, int postIdx) throws BaseException {
        try{
            int getWait = MatchingDao.getWaitApplicationId(userIdx,postIdx);
            return getWait;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
