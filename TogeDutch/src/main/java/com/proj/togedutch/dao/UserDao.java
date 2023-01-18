package com.proj.togedutch.dao;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.entity.Keyword;
import com.proj.togedutch.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

import static com.proj.togedutch.config.BaseResponseStatus.*;

@Repository
public class UserDao {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery, int.class, checkEmailParams);
    }
    @Transactional(rollbackFor = Exception.class)
    public int createUser(User user) {
        String createUserQuery = "insert into User (Keyword_keyword_id, name, role, email, password, phone, location, status, FileProvider_file_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] createUserParams = new Object[]{user.getKeywordIdx(), user.getName(), user.getRole(), user.getEmail(), user.getPassword(), user.getPhone(), user.getLocation(), user.getStatus(), user.getImage()};
        int row = this.jdbcTemplate.update(createUserQuery, createUserParams);
        logger.debug(String.valueOf(row));
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }
    @Transactional(rollbackFor = Exception.class)
    public int createKeyword(Keyword keyword) {
        String createKeywordQuery = "insert into Keyword (word1, word2, word3, word4, word5, word6) VALUES (?, ?, ?, ?, ?, ?)";
        Object[] createKeywordParams = new Object[]{keyword.getWord1(), keyword.getWord2(), keyword.getWord3(),
                keyword.getWord4(), keyword.getWord5(), keyword.getWord6()};
        this.jdbcTemplate.update(createKeywordQuery, createKeywordParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public User getUser(int userIdx) {
        String getUserQuery = "select * from User where user_id = ?";
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("user_id"),
                        rs.getInt("Keyword_keyword_id"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("location"),
                        rs.getString("status"),
                        rs.getString("image")),
                        getUserParams
                );
    }
    public Keyword getKeyword(int keywordIdx) {
        String getKeywordQuery = "select * from Keyword where keyword_id = ?";
        int getKeywordParams = keywordIdx;
        return this.jdbcTemplate.queryForObject(getKeywordQuery,
                (rs, rowNum) -> new Keyword(
                        rs.getInt("keyword_id"),
                        rs.getString("word1"),
                        rs.getString("word2"),
                        rs.getString("word3"),
                        rs.getString("word4"),
                        rs.getString("word5"),
                        rs.getString("word6")),
                getKeywordParams);
    }

    public List<User> getUsers() {
        String getUsersQuery = "select * from User";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("user_id"),
                        rs.getInt("Keyword_keyword_id"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("location"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getString("jwt"),
                        rs.getString("image"))
                );
    }

    public User getPwd(User user) {
        String getPwdQuery = "select * from User where email = ?";
        String getPwdParams = user.getEmail();
        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("user_id"),
                        rs.getInt("Keyword_keyword_id"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("location"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getString("jwt"),
                        rs.getString("image")),
                getPwdParams
                );
    }

    @Transactional(rollbackFor = Exception.class)
    public User modifyUser(User user) throws BaseException {
        String modifyUserQuery = "update User set name = ?, phone = ?, location = ? where user_id = ?";
        Object[] modifyUserParams = new Object[]{user.getName(), user.getPhone(), user.getLocation(), user.getUserIdx()};
        if (this.jdbcTemplate.update(modifyUserQuery, modifyUserParams) == 1)
            return getUser(user.getUserIdx());
        else {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public Keyword modifyKeyword(Keyword keyword) throws BaseException {
        String modifyKeywordQuery = "update Keyword set word1 = ?, word2 = ?, word3 = ?, word4 = ?, word5 = ? where keyword_id = ?";
        Object[] modifyKeywordParams = new Object[]{keyword.getWord1(), keyword.getWord2(), keyword.getWord3(), keyword.getWord4(), keyword.getWord5(), keyword.getKeywordIdx()};
        if (this.jdbcTemplate.update(modifyKeywordQuery, modifyKeywordParams) == 1)
            return getKeyword(keyword.getKeywordIdx());
        else
            throw new BaseException(DATABASE_ERROR);
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteUser(int userIdx) throws BaseException {
        String deleteUserQuery = "delete from User where user_id = ?";
        Object[] deleteUserParams = new Object[]{userIdx};
        if (this.jdbcTemplate.update(deleteUserQuery, deleteUserParams) == 1)
            return 1;
        else throw new BaseException(DATABASE_ERROR);
    }

    @Transactional(rollbackFor = Exception.class)
    public User modifyStatus(int userIdx, String status) throws BaseException {
        String modifyStatusQuery = "update User set status = ? where user_id = ?";
        Object[] modifyStatusParams = new Object[]{status, userIdx};
        if (this.jdbcTemplate.update(modifyStatusQuery, modifyStatusParams) == 1)
            return getUser(userIdx);
        else
            throw new BaseException(DATABASE_ERROR);
    }

}
