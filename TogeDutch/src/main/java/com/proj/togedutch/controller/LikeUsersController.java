package com.proj.togedutch.controller;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.config.BaseResponse;
import com.proj.togedutch.config.BaseResponseStatus;
import com.proj.togedutch.entity.LikeUsers;
import com.proj.togedutch.entity.Post;
import com.proj.togedutch.service.LikeUsersService;
import com.proj.togedutch.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/{userIdx}/likePost")
public class LikeUsersController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LikeUsersService likeUsersService;
    @Autowired
    PostService postService;

    // 공고 관심목록에 등록
    // Post_User_userIdx : 공고 업로드 한 유저의 user_id
    // Like_userIdx : 해당 공고를 관심목록에 담은 유저의 user_id
    @PostMapping("/{postIdx}")
    public BaseResponse<LikeUsers> createLikePost(@PathVariable("userIdx") int userIdx, @PathVariable("postIdx") int postIdx) throws BaseException {
        try {
            Post post = postService.getPostById(postIdx);
            if(userIdx == post.getUser_id())
                return new BaseResponse<>(BaseResponseStatus.LIKEPOST_IMPOSSIBLE);

            int result = likeUsersService.duplicateLikePost(userIdx, postIdx, post.getUser_id());
            if(result == 1)         // 중복
                return new BaseResponse<>(BaseResponseStatus.DUPLICATED_LIKEPOST);

            LikeUsers likePost = likeUsersService.createLikePost(userIdx, postIdx);
            return new BaseResponse<>(likePost);
        } catch (BaseException e) {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 공고 관심목록에서 삭제
    @DeleteMapping("/{postIdx}")
    public int deleteLikePost(@PathVariable("userIdx") int userIdx, @PathVariable("postIdx") int postIdx) throws BaseException {
        try{
            int result = likeUsersService.deleteLikePost(userIdx, postIdx);
            return result;
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus()).getCode();
        }
    }

    // 공고 관심목록 전체 조회
    @GetMapping("")
    public BaseResponse<List<Post>> getLikePosts(@PathVariable("userIdx") int userIdx) throws BaseException {
        try{
            List<Post> likePosts = likeUsersService.getLikePosts(userIdx);
            return new BaseResponse<>(likePosts);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
