package com.proj.togedutch.service;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.dao.ApplicationDao;
import com.proj.togedutch.dao.PostDao;
import com.proj.togedutch.dao.UserDao;
import com.proj.togedutch.entity.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proj.togedutch.utils.JwtService;

import java.util.List;

import static com.proj.togedutch.config.BaseResponseStatus.*;

@Service
public class ApplicationService {
    final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    ApplicationDao applicationDao;
    @Autowired
    JwtService jwtService;
    @Autowired
    PostDao postdao;
    @Autowired
    UserDao userDao;


    //공고 신청
    public Application applyPost(int postIdx) throws BaseException {
        int userIdx;
        Post getPost;
        Application checkDuplicated;

        try {
            getPost = postdao.getPostById(postIdx);
            userIdx = jwtService.getUserIdx();
        } catch(Exception e){
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

        if(userIdx == getPost.getUser_id())
            throw new BaseException(POST_UPLOAD_MINE);

        // userIdx랑 post_id가 같은 application이 있는지 체크 이미 있으면 이미 신청한 공고임
        try{
            checkDuplicated = applicationDao.getApplication(userIdx, postIdx);
        } catch(Exception e){
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

        if(checkDuplicated != null)
            throw new BaseException(DUPLICATED_APPLICATION);

        try {
            Application application = new Application();
            application.setPost_id(postIdx); // entity에있는 setter사용
            application.setUser_id(userIdx);
            application.setChatRoom_id(getPost.getChatRoom_id());

            int applicationIdx = applicationDao.applyPost(application, getPost.getUser_id());
            Application createApplication = getApplication(applicationIdx);
            return createApplication;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //신청 수락
    public Application modifyStatus(int applicationIdx) throws BaseException{
        try{
            return applicationDao.modifyStatus(applicationIdx);
        }catch(Exception e){
            throw new BaseException(MODIFY_FAIL_USER);
        }
    }


    //신청 거절
    public Application modifyStatus_deny(int applicationIdx) throws BaseException{
        try{
            return applicationDao.modifyStatus_deny(applicationIdx);
        }catch(Exception e){
            throw new BaseException(MODIFY_FAIL_USER);
        }
    }



    //공고 전체 조회
    public Application getApplication(int postIdx) throws BaseException{
        try {
            Application application = applicationDao.getApplication(postIdx);
            return application;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //신청 상태 전체 조회 (내가 참여한 공고)
    public List<Application> getApplicationByJoinUserId(int userIdx) throws BaseException {
        try{
            List<Application> joinApplication = applicationDao.getApplicationByJoinUserId(userIdx);
            return joinApplication;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    //신청 상태 전체 조회 (내가 업로드)
    public List<Application> getApplicationByUploadUserId(int userIdx) throws BaseException {
        try{
            List<Application> UploadApplication = applicationDao.getApplicationByUploadUserId(userIdx);
            return UploadApplication;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //신청 상태 전체 조회 (내가업로드-수락대기 상태)
    public List<ApplicationStandby> getApplicationBuUploadStandby(int userIdx) throws BaseException {
        try{

            Application applicationGet= new Application();
            int postIdx= applicationGet.getPost_id();
            Post getPost=postdao.getPostById(postIdx);
            String postTitle= getPost.getTitle(); //<-post 제목 가져옴




            int userIdx1=applicationGet.getUser_id();
            User getUser1= userDao.getUser(userIdx1);






            List<ApplicationStandby> UploadApplicationStandby = applicationDao.getApplicationBuUploadStandby(userIdx);
            return UploadApplicationStandby;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }









    //채팅방 전체 조회 (내가 참여)
    public List<ChatRoom> getChatRoomByJoinUserId(int userIdx) throws BaseException {
        try{
            List<ChatRoom> joinChatRoom = applicationDao.getChatRoomByJoinUserId(userIdx);
            return joinChatRoom;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //공고 상태 변경
    public Post modifyPostStatusById(int postIdx) throws BaseException{
        try{
            Post modifyPostStatusById = applicationDao.modifyPostStatusById(postIdx);
            return modifyPostStatusById;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }



    // 채팅방 삭제 후 Application의 chatRoom_id로 null로 변경
    public int modifyApplicationByChatRoomId(int chatRoomIdx) throws BaseException {
        try{
            int result = applicationDao.modifyApplicationByChatRoomId(chatRoomIdx);
            return result;
        } catch(BaseException e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}