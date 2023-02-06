package com.proj.togedutch.service;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.dao.MatchingDao;
import com.proj.togedutch.entity.Post;
import com.proj.togedutch.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        try{
            User getMatching = MatchingDao.getReMatching(postIdx);
            return getMatching;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
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

}
