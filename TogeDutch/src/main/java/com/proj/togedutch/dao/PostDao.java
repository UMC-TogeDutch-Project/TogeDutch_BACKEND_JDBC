package com.proj.togedutch.dao;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.entity.CategoryRequest;
import com.proj.togedutch.entity.Post;
import com.proj.togedutch.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.ion.Decimal;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.proj.togedutch.config.BaseResponseStatus.DATABASE_ERROR;
import static com.proj.togedutch.config.BaseResponseStatus.FAILED_TO_FIND_BY_CATEGORY;


@Repository
public class PostDao {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 공고 생성 -> 채팅방 생성
    @Transactional(rollbackFor = Exception.class)
    public int createPost(Post post, int userIdx) {
        String createPostQuery
                = "insert into Post (title, url, delivery_tips, minimum, order_time, num_of_recruits, User_user_id, image, latitude, longitude, category) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] createPostParams = new Object[]{post.getTitle(), post.getUrl(), post.getDelivery_tips(), post.getMinimum(), post.getOrder_time(), post.getNum_of_recruits(), userIdx, post.getImage(), post.getLatitude(), post.getLongitude(), post.getCategory()};
        this.jdbcTemplate.update(createPostQuery, createPostParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // post_id 반환
    }

    public Post getPostById(int postIdx) {
        String getPostQuery = "select * from Post where post_id = ?";
        return this.jdbcTemplate.queryForObject(getPostQuery,
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
    }


    // 공고 전체 조회
    public List<Post> getAllPosts() {
        String getPostQuery = "select * from Post where status != \"공고사용불가\" ";
        return this.jdbcTemplate.query(getPostQuery,
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
                ));
    }

    // 공고 전체 조회 (최신순 / 주문 임박)
    public List<Post> getSortingPosts(String sort) {
        String getPostQuery;

        this.jdbcTemplate.update("set time_zone = 'Asia/Seoul'");
        if (sort.equals("latest"))   // 최신순
            getPostQuery = "select * from Post where status!=\"모집완료\" and status!=\"시간만료\" and status != \"공고사용불가\" order by created_at desc";
        else                        // 주문 임박
            getPostQuery = "select * from Post where order_time between now() and DATE_ADD(NOW(), INTERVAL 10 MINUTE) and status!=\"모집완료\" and status!=\"시간만료\" and status != \"공고사용불가\" order by order_time asc";

        return this.jdbcTemplate.query(getPostQuery,
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
                ));
    }

    public String getImageUrl(int postIdx) {
        String getImageUrlQuery = "select image from Post where post_id = ?";
        return this.jdbcTemplate.queryForObject(getImageUrlQuery, String.class, postIdx);
    }

    // 공고 수정
    @Transactional(rollbackFor = Exception.class)
    public Post modifyPost(int postIdx, Post post, int userIdx, String fileUrl) throws BaseException {
        String modifyPostQuery = "update Post " +
                "set title = ?, url = ?, delivery_tips = ?, minimum = ?, order_time = ?, num_of_recruits = ?, User_user_id = ?, image = ?, updated_at = ? , latitude = ?, longitude = ?, category = ?" +
                "where post_id = ?";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());

        Object[] modifyPostParams = new Object[]{post.getTitle(), post.getUrl(), post.getDelivery_tips(), post.getMinimum(), post.getOrder_time(), post.getNum_of_recruits(), userIdx, fileUrl, sdf.format(timeStamp), post.getLatitude(), post.getLongitude(), post.getCategory(), postIdx};

        if (this.jdbcTemplate.update(modifyPostQuery, modifyPostParams) == 1)
            return getPostById(postIdx);
        else {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public Post getPostByUserId(int postIdx, int userIdx) throws BaseException {
        String getPostQuery = "select * from Post where post_id = ? and User_user_id = ?";

        return this.jdbcTemplate.queryForObject(getPostQuery,
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
                ), postIdx, userIdx);
    }

    @Transactional(rollbackFor = Exception.class)
    public int deletePost(int postIdx) {
        String deletePostQuery
                = "UPDATE Post SET status = \"공고사용불가\" WHERE post_id = ?";
        //Object[] deletePostParams = new Object[]{postIdx};
        return this.jdbcTemplate.update(deletePostQuery, postIdx);

    }

    public List<Post> getPostByJoinUserId(int userIdx) throws BaseException {
        String getPostQuery = "select * From Post where post_id In( select Post_post_id from Application where User_user_id = ? )";

        return this.jdbcTemplate.query(getPostQuery,
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
                ), userIdx);
    }

    public List<Post> getPostByUploadUserId(int userIdx) throws BaseException {
        String getPostQuery = "select * from Post where User_user_id = ? ";

        return this.jdbcTemplate.query(getPostQuery,
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
                ), userIdx);
    }

    public List<Post> getPostByTitleUserId(String title) throws BaseException {
        String getPostQuery = "select * from Post where title like ? and status != \"공고사용불가\" ";
        String titleKeyword= "%" + title + "%";
        return this.jdbcTemplate.query(getPostQuery,
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
                ), titleKeyword);
    }

    @Transactional(rollbackFor = Exception.class)
    public Post insertChatRoom(int postIdx, int chatRoomIdx) throws BaseException {
        String modifyPostQuery = "update Post set ChatRoom_chatRoom_id = ? where post_id = ?";

        Object[] modifyPostParams = new Object[]{chatRoomIdx, postIdx};

        if (this.jdbcTemplate.update(modifyPostQuery, modifyPostParams) == 1)
            return getPostById(postIdx);
        else {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int modifyPostByChatRoomId(int chatRoomIdx) throws BaseException {
        String modifyPostQuery = "update Post set ChatRoom_chatRoom_id=null where ChatRoom_chatRoom_id = ?";
        Object[] modifyPostParams = new Object[]{chatRoomIdx};

        return this.jdbcTemplate.update(modifyPostQuery, modifyPostParams);
    }

    @Transactional(rollbackFor = Exception.class)
    public Post modifyPostStatus(int postIdx) throws BaseException {
        String modifyPostQuery = "update Post set status=\"시간만료\" where post_id = ? and order_time < current_timestamp and num_of_recruits != recruited_num";

        Object[] modifyPostParams = new Object[]{postIdx};

        if (this.jdbcTemplate.update(modifyPostQuery, modifyPostParams) == 1)
            return getPostById(postIdx);
        else {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 카테고리로 공고 조회
    public List<Post> getPostsByCategory(CategoryRequest postReq) throws BaseException {
        this.jdbcTemplate.update("set time_zone = 'Asia/Seoul'");

        String getPostQuery = "SELECT\n" +
                "    * , (\n" +
                "       6371 * acos ( cos ( radians(?) ) * cos( radians(latitude) ) * cos( radians(longitude) - radians(?) )\n" +
                "          + sin ( radians(?) ) * sin( radians(`latitude`) )\n" +
                "       )\n" +
                "   ) AS distance\n" +
                "FROM Post\n" +
                "where order_time > now() and (category = ? or category = ? or category = ? or category = ? or category = ? or category = ?) and status = ? \n" +
                "HAVING distance <= 1\n" +
                "ORDER BY distance;";

        Object[] getPostParams = new Object[]{postReq.getLatitude(), postReq.getLongitude(), postReq.getLatitude(), postReq.getCategory1(), postReq.getCategory2(), postReq.getCategory3(), postReq.getCategory4(), postReq.getCategory5(), postReq.getCategory6(), "모집중"};

        return this.jdbcTemplate.query(getPostQuery,
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
                ), getPostParams);
    }

    public List<User> getUsersInPost(int postIdx) throws BaseException {
        String getUsersQuery = "select * from User where user_id in ( select User_user_id from Application where Post_post_id = ? and status = \"수락완료\");";

        return this.jdbcTemplate.query(getUsersQuery,
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
                        rs.getDouble("longitude"))
                , postIdx);
    }

    public Post getPostByChatRoomId(int chatRoomIdx) throws BaseException {
        String getPostQuery = "select * from Post where ChatRoom_chatRoom_id = ?";

        return this.jdbcTemplate.queryForObject(getPostQuery,
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
                ), chatRoomIdx);
    }
}
