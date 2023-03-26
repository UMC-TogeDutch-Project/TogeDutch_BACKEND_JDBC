package com.proj.togedutch.service;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.config.BaseResponseStatus;
import com.proj.togedutch.dao.ApplicationDao;
import com.proj.togedutch.dao.ChatMessageDao;
import com.proj.togedutch.dao.ChatRoomDao;
import com.proj.togedutch.dao.PostDao;
import com.proj.togedutch.entity.ChatRoom;
import com.proj.togedutch.entity.ChatRoomUser;
import com.proj.togedutch.entity.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

import static com.proj.togedutch.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class ChatRoomService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ChatRoomDao chatRoomDao;
    @Autowired
    private ChatMessageDao chatMessageDao;
    @Autowired
    private PostDao postDao;
    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    public ChatRoomService(ChatRoomDao chatRoomDao) {
        this.chatRoomDao = chatRoomDao;
    }

    public ChatRoom createChatRoom() throws BaseException {
        try {
            int chatRoomIdx = chatRoomDao.createChatRoom();

            return chatRoomDao.getChatRoomById(chatRoomIdx);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public ChatRoom getChatRoomById(int chatRoomIdx) throws BaseException {
        try {
            return chatRoomDao.getChatRoomById(chatRoomIdx);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }

    public List<ChatRoom> getAllChatRooms() throws BaseException {
        try {
            return chatRoomDao.getAllChatRooms();
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int deleteChatRoom(int chatRoomIdx) throws BaseException {
        try {
            chatMessageDao.deleteChat(chatRoomIdx);
            chatMessageDao.deleteChatPhoto(chatRoomIdx);
            postDao.modifyPostByChatRoomId(chatRoomIdx);
            applicationDao.modifyApplicationByChatRoomId(chatRoomIdx);
            return chatRoomDao.deleteChatRoom(chatRoomIdx);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public ChatRoomUser inviteUser(int chatRoomIdx, int userId) throws BaseException {
        try {
            return chatRoomDao.inviteUser(chatRoomIdx, userId);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof SQLException) {
                SQLException sqlException = (SQLException) e.getCause();
                if (sqlException.getErrorCode() == 1062) {
                    throw new BaseException(BaseResponseStatus.DUPLICATE_KEY_ORROR);
                } else if (sqlException.getErrorCode() == 1452) {
                    throw new BaseException(BaseResponseStatus.FOREIGN_KEY_ORROR);
                } else throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
            } else throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<ChatRoomUser> getChatRoomUsers(int chatRoomIdx) throws BaseException{
        try{
            return chatRoomDao.getChatRoomUsers(chatRoomIdx);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public ChatRoomUser inChatRoomUser(int chatRoomIdx, int userId) throws BaseException{
        try {
            chatRoomDao.inChatRoomUser(chatRoomIdx,userId);
            return chatRoomDao.getChatRoomUser(chatRoomIdx,userId);
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public ChatRoomUser outChatRoomUser(int chatRoomIdx, int userId) throws BaseException{
        try {
            chatRoomDao.outChatRoomUser(chatRoomIdx,userId);
            return chatRoomDao.getChatRoomUser(chatRoomIdx,userId);
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int leaveChatRoomUser(int chatRoomIdx, int userId) throws BaseException{
        try {
            return chatRoomDao.leaveChatRoomUser(chatRoomIdx,userId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public ChatRoomUser getChatRoomUser(int chatRoomIdx, int userId) throws BaseException {
        try {
            return chatRoomDao.getChatRoomUser(chatRoomIdx,userId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
