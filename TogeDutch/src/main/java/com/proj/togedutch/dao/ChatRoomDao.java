package com.proj.togedutch.dao;

import com.proj.togedutch.entity.ChatRoom;
import com.proj.togedutch.entity.ChatRoomUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChatRoomDao {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createChatRoom(){
        String createChatRoomQuery = "insert into ChatRoom (chatRoom_id, created_at) values (DEFAULT, DEFAULT)";
        this.jdbcTemplate.update(createChatRoomQuery);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public ChatRoom getChatRoomById(int chatRoomIdx) {
        String getChatRoomQuery = "select * from ChatRoom where chatRoom_id = ?";
        return this.jdbcTemplate.queryForObject(getChatRoomQuery,
                (rs, rowNum) -> new ChatRoom(
                        rs.getInt("chatRoom_id"),
                        rs.getTimestamp("created_at")
                ), chatRoomIdx);
    }

    public List<ChatRoom> getAllChatRooms() {
        String getChatRoomsQuery = "select * from ChatRoom";
        return this.jdbcTemplate.query(getChatRoomsQuery,
                (rs, rowNum) -> new ChatRoom(
                        rs.getInt("chatRoom_id"),
                        rs.getTimestamp("created_at")
        ));
    }

    public int deleteChatRoom(int chatRoomIdx) {
        String deleteChatRoomQuery = "delete from ChatRoom where chatRoom_id = ?";
        Object[] deleteChatRoomParams = new Object[]{chatRoomIdx};
        return this.jdbcTemplate.update(deleteChatRoomQuery, deleteChatRoomParams);
    }

    // 채팅방에 유저 초대
    public ChatRoomUser inviteUser(int chatRoomIdx, int userId){
        this.jdbcTemplate.update("set time_zone = 'Asia/Seoul'");
        String inviteUserQuery = "INSERT INTO ChatRoomOfUser (`chatRoom_id`, `user_id`) VALUES (?, ?);";
        this.jdbcTemplate.update(inviteUserQuery,chatRoomIdx,userId);
        String lastInsertUserQuery = "SELECT * FROM ChatRoomOfUser where chatRoom_id = ? and user_id =?";
        return this.jdbcTemplate.queryForObject(lastInsertUserQuery,
                (rs, rowNum) -> new ChatRoomUser(
                        rs.getInt("chatRoom_id"),
                        rs.getInt("user_id"),
                        rs.getByte("status"),
                        rs.getInt("is_read"),
                        rs.getTimestamp("updated_at")
                ), chatRoomIdx,userId);
    }

    public List<ChatRoomUser> getChatRoomUsers(int chatRoomIdx) {
        String getChatRoomUsersQuery = "SELECT ChatRoomOfUser.*, User.name FROM mydb.ChatRoomOfUser join mydb.User on User.user_id = ChatRoomOfUser.user_id where chatRoom_id = ?;";
        return this.jdbcTemplate.query(getChatRoomUsersQuery,
                (rs, rowNum) -> new ChatRoomUser(
                        rs.getInt("chatRoom_id"),
                        rs.getInt("user_id"),
                        rs.getByte("status"),
                        rs.getInt("is_read"),
                        rs.getTimestamp("updated_at"),
                        rs.getString("name")
                ), chatRoomIdx);
    }

    public ChatRoomUser getChatRoomUser(int chatRoomIdx, int userId){
        this.jdbcTemplate.update("set time_zone = 'Asia/Seoul'");
        String getChatRoomUsersQuery = "SELECT ChatRoomOfUser.*, User.name FROM mydb.ChatRoomOfUser " +
                "join mydb.User on User.user_id = ChatRoomOfUser.user_id where chatRoom_id = ? and ChatRoomOfUser.user_id =?;";
        return this.jdbcTemplate.queryForObject(getChatRoomUsersQuery,
                (rs, rowNum) -> new ChatRoomUser(
                        rs.getInt("chatRoom_id"),
                        rs.getInt("user_id"),
                        rs.getByte("status"),
                        rs.getInt("is_read"),
                        rs.getTimestamp("updated_at"),
                        rs.getString("name")
                ), chatRoomIdx,userId);
    }

    public void inChatRoomUser(int chatRoomIdx, int userId) {
        this.jdbcTemplate.update("set time_zone = 'Asia/Seoul'");
        String updateChatRoomUsersQuery = "UPDATE `mydb`.`ChatRoomOfUser` SET `status` = 1, `updated_at` = CURRENT_TIMESTAMP, `is_read` = 0 " +
                "WHERE (`chatRoom_id` = ?) and (`user_id` = ?);";
        this.jdbcTemplate.update(updateChatRoomUsersQuery,chatRoomIdx,userId);
    }

    public void outChatRoomUser(int chatRoomIdx, int userId) {
        this.jdbcTemplate.update("set time_zone = 'Asia/Seoul'");
        String updateChatRoomUsersQuery = "UPDATE `mydb`.`ChatRoomOfUser` SET `status` = 00, `updated_at` = CURRENT_TIMESTAMP " +
                "WHERE (`chatRoom_id` = ?) and (`user_id` = ?);";
        this.jdbcTemplate.update(updateChatRoomUsersQuery,chatRoomIdx,userId);
    }
}
