package com.proj.togedutch.dao;

import com.proj.togedutch.entity.Matching;
import com.proj.togedutch.entity.Post;
import com.proj.togedutch.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Repository
public class MatchingDao {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public User getFirstMatching(int postIdx){
        String getFirstMatchingQuery = "Select * from Post where post_id = ?";
        Post post =this.jdbcTemplate.queryForObject(getFirstMatchingQuery,
                (rs, rowNum) -> new Post(
                        rs.getInt("post_id"),
                        rs.getString("title"),
                        rs.getString("url"),
                        rs.getInt("delivery_tips"),
                        rs.getInt("minimum"),
                        rs.getString("order_time"),
                        rs.getInt("num_of_recruits"),
                        rs.getInt("recruited_num"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getInt("User_user_id"),
                        rs.getString("image"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getInt("ChatRoom_chatRoom_id"),
                        rs.getString("category")

                ), postIdx);

        Object[] getDistance = new Object[]{post.getLatitude(), post.getLongitude(), post.getLatitude() };

        String getPostQuery = "SELECT *,\n" +
                "\t(6371*acos(cos(radians( ? ))*cos(radians(latitude))*cos(radians(longitude)\n" +
                "\t-radians( ? ))+sin(radians( ? ))*sin(radians(latitude))))\n" +
                "\tAS distance\n" +
                "FROM User\n" +
                "HAVING distance <= 0.3\n" +
                "ORDER BY distance asc limit 1";

         User user=this.jdbcTemplate.queryForObject(getPostQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("user_id"),
                        rs.getInt("Keyword_keyword_id"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("image"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude")),

                getDistance);

        String MatchingQuery = "Insert into Matching (user_first_id, count) values (?,?) ";
        Object[] getMatchingParams = new Object[]{user.getUserIdx(),1};
        this.jdbcTemplate.update(MatchingQuery, getMatchingParams);

        return user;

    }
    // 데이터 하나만 추출하기(asc limit 1)
    //매칭된 유저 기록하기


    //중복된 유저인지 고려할 것
    //고려된 유저를 제외하고 매칭 시켜줄 것 + 위치적
    @Transactional(rollbackFor = Exception.class)
    public User getReMatching(int postIdx){


        String getReMatching = "select * from Post where post_id = ?";
        Post post =this.jdbcTemplate.queryForObject(getReMatching,
                (rs, rowNum) -> new Post(
                        rs.getInt("post_id"),
                        rs.getString("title"),
                        rs.getString("url"),
                        rs.getInt("delivery_tips"),
                        rs.getInt("minimum"),
                        rs.getString("order_time"),
                        rs.getInt("num_of_recruits"),
                        rs.getInt("recruited_num"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getInt("User_user_id"),
                        rs.getString("image"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getInt("ChatRoom_chatRoom_id"),
                        rs.getString("category")
                ), postIdx);

        logger.info(String.valueOf(post.getStatus()));
        System.out.println(post);


        String getMatchingUser = "Select * From Matching Where Post_post_id = ? ";
        Matching match = this.jdbcTemplate.queryForObject(getMatchingUser,
                (rs, rowNum) -> new Matching(
                        rs.getInt("Matching_Id"),
                        rs.getInt("user_first_id"),
                        rs.getInt("user_second_id"),
                        rs.getInt("user_third_id"),
                        rs.getInt("count")), postIdx
        );

        logger.info(String.valueOf(match.getCount()));
        System.out.println(match);

        int first=-1;
        int second=-1;
        int third=-1;

        if(match.getCount() == 1){
            first = match.getUserFirstId();
        }
        else if (match.getCount() == 2) {
            first = match.getUserFirstId();
            second = match.getUserSecondId();
        }
        else if (match.getCount() == 3){
            return null;
        }

        System.out.println(post.getLongitude());

        String getdistanceQuery = "SELECT *, (6371*acos(cos(radians(?))*cos(radians(latitude))*cos(radians(longitude)-radians(?))+sin(radians(?))*sin(radians(latitude)))) AS distance "
        + "FROM User "
        + "WHERE user_id != ? or user_id != ? or user_id != ? "
        + "HAVING distance <= 0.3 "
        + "ORDER BY distance asc limit 1 ";

        Object[] getDistance = new Object[]{post.getLongitude() ,post.getLatitude(), post.getLongitude() , first ,second ,third};

        User user1 = this.jdbcTemplate.queryForObject(getdistanceQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("user_id"),
                        rs.getInt("Keyword_keyword_id"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("image"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude")),
                getDistance

                //
                );

        logger.info(String.valueOf(user1.getLongitude()));
        System.out.println(user1);

        if(match.getCount() == 0){
            //매칭 횟수 변경
            String MatchingQuery = "Insert into Matching (user_first_id, count, Post_post_id) values (?,?,?) ";
            Object[] getMatchingParams = new Object[]{user1.getUserIdx(),1,post.getPost_id()};
            int a = this.jdbcTemplate.update(MatchingQuery, getMatchingParams);
            System.out.println(a);

        }

        if(match.getCount() == 1){
            //매칭 횟수 변경
            String MatchingQuery = "UPDATE Matching SET user_second_id = ? , count = ? WHERE Post_post_id = ? ";
            Object[] getMatchingParams = new Object[]{user1.getUserIdx(),2,post.getPost_id()};
            int a = this.jdbcTemplate.update(MatchingQuery, getMatchingParams);
            System.out.println(a);

        }
        else if (match.getCount() == 2){
            //매칭 횟수 변경
            String MatchingQuery = "UPDATE Matching SET user_third_id = ?,  count = ? WHERE Post_post_id = ? ";
            Object[] getMatchingParams = new Object[]{user1.getUserIdx(),3,post.getPost_id()};
            int b = this.jdbcTemplate.update(MatchingQuery, getMatchingParams);
            System.out.println(b);
        }


        return user1;

    }
    public int getAcceptUserId(int userIdx, int postIdx){
        String getSelectAcceptQuery = "Select * from Post where post_id = ?";
        Post post =this.jdbcTemplate.queryForObject(getSelectAcceptQuery,
                (rs, rowNum) -> new Post(
                        rs.getInt("post_id"),
                        rs.getString("title"),
                        rs.getString("url"),
                        rs.getInt("delivery_tips"),
                        rs.getInt("minimum"),
                        rs.getString("order_time"),
                        rs.getInt("num_of_recruits"),
                        rs.getInt("recruited_num"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getInt("User_user_id"),
                        rs.getString("image"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getInt("ChatRoom_chatRoom_id"),
                        rs.getString("category")

                ), postIdx);

        String getAcceptQuery = "Insert into Application (status, Post_post_id ,Post_User_user_id ,ChatRoom_chatRoom_id ,User_user_id) values (?,?,?,?,?)";
        Object[] getAcceptParams = new Object[]{"매칭 성공", postIdx, post.getUser_id(),
                post.getChatRoom_id(), userIdx};
        this.jdbcTemplate.update(getAcceptQuery, getAcceptParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    //DB에 저장할 거 없이 그냥 매칭 실패 보여주기
    public int getDenyUserId(int userIdx, int postIdx){
        String getSelectAcceptQuery = "Select * from Post where post_id = ?";
        Post post =this.jdbcTemplate.queryForObject(getSelectAcceptQuery,
                (rs, rowNum) -> new Post(
                        rs.getInt("post_id"),
                        rs.getString("title"),
                        rs.getString("url"),
                        rs.getInt("delivery_tips"),
                        rs.getInt("minimum"),
                        rs.getString("order_time"),
                        rs.getInt("num_of_recruits"),
                        rs.getInt("recruited_num"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getInt("User_user_id"),
                        rs.getString("image"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getInt("ChatRoom_chatRoom_id"),
                        rs.getString("category")

                ), postIdx);
        String getAcceptQuery = "Insert into Application (status, Post_post_id ,Post_User_user_id ,ChatRoom_chatRoom_id ,User_user_id) values (?,?,?,?,?)";
        Object[] getAcceptParams = new Object[]{"매칭 실패", postIdx, post.getUser_id(),
                post.getChatRoom_id(), userIdx};
        return this.jdbcTemplate.update(getAcceptQuery, getAcceptParams);

    }


}
