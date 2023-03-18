package com.proj.togedutch.dao;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

@Repository
public class ChatMessageDao {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Resource(name = "redisTemplate")
//    private HashOperations<String, String, ChatRoom> hashOpsChatRoom;
//    @Resource(name = "redisTemplate")
//    private HashOperations<String, String, String> hashOpsEnterInfo;
//    @Resource(name = "redisTemplate")
//    private ValueOperations<String, String> valueOps;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 채팅 메세지 조회
    public ChatMessage getChatMessage(int chatRoomId, int chatId) {
        this.jdbcTemplate.update("set time_zone = 'Asia/Seoul'");
        String sql = "select Chat.*,User.name from Chat join User on Chat.User_user_id = User.user_id where ChatRoom_chatRoom_id = ? and chat_id = ?";
        return this.jdbcTemplate.queryForObject(sql,(rs, rowNum) -> new ChatMessage(
                rs.getInt("chat_id"),
                rs.getInt("ChatRoom_chatRoom_id"),
                rs.getInt("User_user_id"),
                rs.getTimestamp("created_at"),
                rs.getString("content"),
                rs.getString("name")
        ),chatRoomId,chatId);
    }
    public List<ChatMessage> getChatMessages(int chatRoom_id) throws BaseException {
        String getMessagesQuery = "select Chat.*,User.name from Chat join User on Chat.User_user_id = User.user_id where ChatRoom_chatRoom_id = ?";

        return this.jdbcTemplate.query(getMessagesQuery,(rs, rowNum) -> new ChatMessage(
                rs.getInt("chat_id"),
                rs.getInt("ChatRoom_chatRoom_id"),
                rs.getInt("User_user_id"),
                rs.getTimestamp("created_at"),



                rs.getString("content"),
                rs.getString("name")
        ),chatRoom_id);
    }
    public int createChatMessage(int chatRoomId, int user ,ChatMessage message) {
        this.jdbcTemplate.update("set time_zone = 'Asia/Seoul'");
        String sql = "INSERT INTO Chat (`ChatRoom_chatRoom_id`, `User_user_id`, `content`) VALUES (?,?,?)";
        Object[] createChatMessage = new Object[]{chatRoomId,user,message.getContent()};
        this.jdbcTemplate.update(sql, createChatMessage);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public void saveMessage(ChatMessage message){
        this.jdbcTemplate.update("set time_zone = 'Asia/Seoul'");
        String roomIdName = Integer.toString(message.getChatRoomId());
        String sql = "INSERT INTO Chat (`ChatRoom_chatRoom_id`, `User_user_id`, `content`) VALUES (?,?,?)";
        Object[] createMessageParams = new Object[]{message.getChatRoomId(),message.getUserId(),message.getContent()};
        this.jdbcTemplate.update(sql, createMessageParams);
    }

    public int deleteChat(int chatRoomIdx) throws BaseException {
        String deleteChatQuery
                = "delete from Chat where ChatRoom_chatRoom_id = ?";
        Object[] deleteChatParams = new Object[]{chatRoomIdx};
        return this.jdbcTemplate.update(deleteChatQuery, deleteChatParams);
    }

    public int deleteChatPhoto(int chatRoomIdx) throws BaseException {
        String deleteChatPhotoQuery
                = "delete from ChatPhoto where ChatRoom_chatRoom_id = ?";
        Object[] deleteChatPhotoParams = new Object[]{chatRoomIdx};
        return this.jdbcTemplate.update(deleteChatPhotoQuery, deleteChatPhotoParams);
    }

    public ChatPhoto getChatPhoto(int chatRoomId,int chatPhotoId){
        String sql = "select * from ChatPhoto where chatPhoto_id = ? and ChatRoom_chatRoom_id = ?";
        return this.jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new ChatPhoto(
                        rs.getInt("chatPhoto_id"),
                        rs.getTimestamp("created_at"),
                        rs.getInt("ChatRoom_chatRoom_id"),
                        rs.getInt("User_user_id"),
                        rs.getString("image")
                ),chatPhotoId,chatRoomId);
    }

    public List<ChatPhoto> getChatPhotos (int chatRoomId) {
        String sql = "select * from ChatPhoto where ChatRoom_chatRoom_id = ?";
        return this.jdbcTemplate.query(sql,(rs,row) -> new ChatPhoto(
                rs.getInt("chatPhoto_id"),
                rs.getTimestamp("created_at"),
                rs.getInt("ChatRoom_chatRoom_id"),
                rs.getInt("User_user_id"),
                rs.getString("image")
        ),chatRoomId);
    }


    public int createChatPhoto(int chatRoomId,int userId,String file) {
        this.jdbcTemplate.update("set time_zone = 'Asia/Seoul'");
        String ChatPhotoQuery = "insert into ChatPhoto (ChatRoom_chatRoom_id,User_user_id,image) Values (?,?,?)";

        Object[] createChatPhoto = new Object[]{chatRoomId,userId,file};
        this.jdbcTemplate.update(ChatPhotoQuery, createChatPhoto);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public int createChatMeetTime(int chatRoom_id, int user, String time) {
        this.jdbcTemplate.update("set time_zone = 'Asia/Seoul'");
        String chatMeetTimeQuery = "insert into ChatMeetTime (ChatRoom_chatRoom_id,User_user_id,meetTime) values (?,?,?)";
        Object[] MeetTimeParams = new Object[]{chatRoom_id,user,time};
        this.jdbcTemplate.update(chatMeetTimeQuery,MeetTimeParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public ChatMeetTime getChatMeetTime(int chatRoom_id, int chatMeetTime_id) {
        String sql = "select * from ChatMeetTime where chatMeetTime_id = ? and ChatRoom_chatRoom_id = ?";
        return this.jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new ChatMeetTime(
                        rs.getInt("chatMeetTime_id"),
                        rs.getInt("ChatRoom_chatRoom_id"),
                        rs.getInt("User_user_id"),
                        rs.getString("meetTime"),
                        rs.getTimestamp("created_at")
                ),chatMeetTime_id,chatRoom_id);
    }

    public void putChatMeetTime(int chatRoom_id,int chatMeetTime_id,String time){
        this.jdbcTemplate.update("set time_zone = 'Asia/Seoul'");
        String updateMeetTimeQuery = "update ChatMeetTime set meetTime=? where chatMeetTime_id = ? and ChatRoom_chatRoom_id = ?";
        Object[] putMeetTimeParams = new Object[]{time,chatMeetTime_id,chatRoom_id};
        this.jdbcTemplate.update(updateMeetTimeQuery,putMeetTimeParams);
    }

    public int createChatLocation(int chatRoom_id, int user, BigDecimal latitude, BigDecimal longitude) {
        this.jdbcTemplate.update("set time_zone = 'Asia/Seoul'");
        String createChatLocationQuery = "insert into ChatLocation (ChatRoom_chatRoom_id,User_user_id, latitude, longitude) values (?,?,?,?)";
        Object[] createChatLocationParams = new Object[]{chatRoom_id,user,latitude, longitude};
        this.jdbcTemplate.update(createChatLocationQuery,createChatLocationParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public ChatLocation getChatLocation(int chatRoom_id, int chatLocationIdx) {
        String sql = "select * from ChatLocation where chatLocation_id = ? and ChatRoom_chatRoom_id = ?";
        return this.jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new ChatLocation(
                        rs.getInt("chatLocation_id"),
                        rs.getInt("ChatRoom_chatRoom_id"),
                        rs.getInt("User_user_id"),
                        rs.getBigDecimal("latitude"),
                        rs.getBigDecimal("longitude"),
                        rs.getTimestamp("created_at")
                ),chatLocationIdx, chatRoom_id);
    }

    public void putChatLocation(int chatRoom_id, int chatLocationIdx, BigDecimal latitude, BigDecimal longitude) {
        this.jdbcTemplate.update("set time_zone = 'Asia/Seoul'");
        String updateCLQuery = "update ChatLocation set latitude=?, longitude=? where chatLocation_id = ? and ChatRoom_chatRoom_id = ?";
        Object[] putChatLocationParams = new Object[]{latitude,longitude,chatLocationIdx,chatRoom_id};
        this.jdbcTemplate.update(updateCLQuery,putChatLocationParams);
    }

//    public ChatRoomUser getChatRoomMember(int chatRoom_id) {
//        String sql = "SELECT count(distinct User_user_id) FROM Chat where ChatRoom_chatRoom_id = ?";
//        //Object[] getChatMemberParams = new Object[]{chatRoom_id};
//        return this.jdbcTemplate.queryForObject(sql,(rs, rowNum) -> new ChatRoomUser(
//                rs.getInt("count(distinct User_user_id)")
//        ),chatRoom_id);
//    }

//    public ChatRoomUser getUserName(int chatRoom_id, int user_id) {
//        String sql = "SELECT distinct User.name, (SELECT count(distinct User_user_id) FROM Chat  \n" +
//                "where ChatRoom_chatRoom_id=?) as countMem \n" +
//                "FROM Chat join User on Chat.User_user_id = User.user_id\n" +
//                "where ChatRoom_chatRoom_id= ? and Chat.User_user_id=?;";
//        return this.jdbcTemplate.queryForObject(sql,(rs, rowNum) -> new ChatRoomUser(
//                rs.getString("name"),
//                rs.getInt("countMem")),chatRoom_id,chatRoom_id,user_id);
//    }
}
