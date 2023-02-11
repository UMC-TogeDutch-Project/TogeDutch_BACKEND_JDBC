package com.proj.togedutch.dao;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.entity.Application;
import com.proj.togedutch.entity.ChatRoom;
import com.proj.togedutch.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

import static com.proj.togedutch.config.BaseResponseStatus.DATABASE_ERROR;

@Repository
public class ApplicationDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostDao postdao;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //공고 신청
    @Transactional(rollbackFor=Exception.class)
    public int applyPost(Application application, int Uploader_userIdx) throws BaseException {
        String createApplicationQuery="insert into Application(Post_post_id, User_user_id, ChatRoom_chatRoom_id, Post_User_user_id) VALUES(?,?,?,?)";
        Object[] createApplicationParams = new Object[]{ application.getPost_id(), application.getUser_id(), application.getChatRoom_id(), Uploader_userIdx};
        this.jdbcTemplate.update(createApplicationQuery, createApplicationParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    //신청 수락
    @Transactional(rollbackFor=Exception.class)
    public Application modifyStatus(int applicationIdx) throws BaseException {
        String modifyStatusQuery="update Application set status =? where application_id= ?";
        Object[] modifyStatusParams = new Object[]{"수락완료",applicationIdx};
        if(this.jdbcTemplate.update(modifyStatusQuery,modifyStatusParams)==1)
            return getApplication(applicationIdx);
        else
            throw new BaseException(DATABASE_ERROR);
    }
    //신청 거절
    @Transactional(rollbackFor=Exception.class)
    public Application modifyStatus_deny(int applicationIdx) throws BaseException {
        String modifyStatusQuery="update Application set status =? where application_id= ?";
        Object[] modifyStatusParams = new Object[]{"수락거절",applicationIdx};
        if(this.jdbcTemplate.update(modifyStatusQuery,modifyStatusParams)==1)
            return getApplication(applicationIdx);
        else
            throw new BaseException(DATABASE_ERROR);
    }



    //공고 전체 조회
    public Application getApplication(int applicationIdx){
        String getApplicationQuery = "select * from Application where application_id = ?";
        int getApplicationParams = applicationIdx;
        return this.jdbcTemplate.queryForObject(getApplicationQuery,
                (rs, rowNum) -> new Application(
                        rs.getInt("application_id"),
                        rs.getString("status"),
                        rs.getInt("Post_post_id"),
                        rs.getInt("User_user_id"),
                        rs.getInt("ChatRoom_chatRoom_id")),
                getApplicationParams
        );
    }


    //신청 상태 전체 조회 (내가 업로드)
    public List<Application> getApplicationByUploadUserId(int userIdx) throws BaseException {

        String getApplicationQuery = "select * from Application where Post_User_user_id = ? ";
        return this.jdbcTemplate.query(getApplicationQuery,
                (rs, rowNum) -> new Application(
                        rs.getInt("application_id"),
                        rs.getString("status"),
                        rs.getInt("Post_post_id"),
                        rs.getInt("User_user_id"),
                        rs.getInt("ChatRoom_chatRoom_id")
                ), userIdx);
    }


    //신청 상태 전체 조회 (내가 참여한 공고)
    public List<Application> getApplicationByJoinUserId(int userIdx) throws BaseException {
        String getApplicationQuery = "select * from Application where User_user_id = ? ";


        return this.jdbcTemplate.query(getApplicationQuery,
                (rs, rowNum) -> new Application(
                        rs.getInt("application_id"),
                        rs.getString("status"),
                        rs.getInt("Post_post_id"),
                        rs.getInt("User_user_id"),
                        rs.getInt("ChatRoom_chatRoom_id")
                ), userIdx);
    }


    //채팅방 전체 조회 (내가 참여)
    public List<ChatRoom> getChatRoomByJoinUserId(int userIdx) throws BaseException {
        String getChatRoomQuery = "select * From ChatRoom where chatRoom_id In( select ChatRoom_chatRoom_id from Application where User_user_id = ? and status = ?)";

        return this.jdbcTemplate.query(getChatRoomQuery,
                (rs, rowNum) -> new ChatRoom(
                        rs.getInt("chatRoom_id"),
                        rs.getTimestamp("created_at")
                ), userIdx, "수락완료");
    }

    //공고 상태 변경
    @Transactional(rollbackFor = Exception.class)
    public Post modifyPostStatusById(int postIdx) throws BaseException {


        String modifyPostQuery = "update Post \n" + "set status = \"모집완료\"\n" + "where num_of_recruits = recruited_num and post_id=?";

        Object[] modifyPostParams = new Object[]{postIdx};
        if (this.jdbcTemplate.update(modifyPostQuery, modifyPostParams) == 1){
            Post modifyPost=postdao.getPostById(postIdx);
            return modifyPost;
        }
        else{
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor=Exception.class)
    public int modifyApplicationByChatRoomId(int chatRoomIdx) throws BaseException {
        String modifyChatRoomQuery="update Application set ChatRoom_chatRoom_id=null where ChatRoom_chatRoom_id = ?";
        Object[] modifyChatRoomParams = new Object[]{chatRoomIdx};

        return this.jdbcTemplate.update(modifyChatRoomQuery, modifyChatRoomParams);
    }

    public Application getApplication(int userIdx, int postIdx){
        String getApplicationQuery = "select * from Application where User_user_id=? and Post_post_id=?";
        Object[] getApplicationParams = new Object[]{ userIdx, postIdx };

        try{
            return this.jdbcTemplate.queryForObject(getApplicationQuery,
                    (rs, rowNum) -> new Application(
                            rs.getInt("application_id"),
                            rs.getString("status"),
                            rs.getInt("Post_post_id"),
                            rs.getInt("User_user_id"),
                            rs.getInt("ChatRoom_chatRoom_id")),
                    getApplicationParams
            );
        } catch(EmptyResultDataAccessException e){
            return null;
        }
    }
}