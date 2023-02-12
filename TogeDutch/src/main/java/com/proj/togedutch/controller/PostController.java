package com.proj.togedutch.controller;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.config.BaseResponse;
import com.proj.togedutch.config.BaseResponseStatus;
import com.proj.togedutch.entity.*;
import com.proj.togedutch.service.AWSS3Service;
import com.proj.togedutch.service.ChatRoomService;
import com.proj.togedutch.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PostService postService;
    @Autowired
    AWSS3Service awsS3Service;
    @Autowired
    ChatRoomService chatRoomService;

    @Value("${cloud.aws.url}")
    private String url;

    // 공고 등록
    @ResponseBody
    @PostMapping("")
    public BaseResponse<Post> createPost(@RequestParam int user, @RequestPart Post post, @RequestPart(value="file", required = false)  MultipartFile file) throws IOException, NullPointerException{
        if (post.getTitle() == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_POST_EMPTY_TITLE);
        }
        if (post.getUrl() == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_POST_EMPTY_URL);
        }
        if (Integer.valueOf(post.getDelivery_tips()) == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_POST_EMPTY_TIP);
        }
        if (Integer.valueOf(post.getMinimum()) == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_POST_EMPTY_MINIMUM);
        }
        if (Integer.valueOf(post.getNum_of_recruits()) == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_POST_EMPTY_RECRUIT);
        }
        if (post.getLatitude() == null || post.getLongitude() == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_POST_EMPTY_LOCATION);
        }
        if(post.getCategory() == null){
            return new BaseResponse<>(BaseResponseStatus.POST_POST_EMPTY_CATEGORY);
        }

        String fileUrl = null;

        // 파일 없는 경우 오류 처리 다시 하기 (01.24 : 파일 없어도 uploadFile 메소드 실행됨)
        if(file != null && !file.isEmpty())
            fileUrl = url + awsS3Service.uploadFile(file, post, user);

        try {
            logger.info("fileUrl은 " + fileUrl);
            post.setImage(fileUrl);

            Post newPost = postService.createPost(post, user);
            ChatRoom newChatRoom = chatRoomService.createChatRoom();
            Post modifyPost = postService.insertChatRoom(newPost.getPost_id(), newChatRoom.getChatRoomIdx());
            return new BaseResponse<>(modifyPost);
        } catch (BaseException e) {
            logger.debug("에러로그 : " + e.getCause());
            System.out.println("에러로그 : " + e.getCause());
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 공고 전체 조회
    @GetMapping("")
    public BaseResponse<List<Post>> getAllPosts() throws BaseException {
        try{
            List<Post> getPostsRes = postService.getAllPosts();
            return new BaseResponse<>(getPostsRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 공고 전체 조회 (최신순 / 주문임박)
    @GetMapping("/")
    public BaseResponse<List<Post>> getSortingPosts(@RequestParam String sort) throws BaseException {
        try{
            List<Post> getPostsRes = postService.getSortingPosts(sort);
            return new BaseResponse<>(getPostsRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 공고 수정
    @PutMapping("/{postIdx}")
    public BaseResponse<Post> modifyPost(@PathVariable("postIdx") int postIdx, @RequestPart Post post,
                                         @RequestParam int user, @RequestPart MultipartFile file) throws Exception {

        /* 23.01.16 로그인한 유저와 Post_User_id가 같은 경우만 수정 가능하도록 검사하는 코드 추가 필요*/

        if (post.getTitle() == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_POST_EMPTY_TITLE);
        }
        if (post.getUrl() == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_POST_EMPTY_URL);
        }
        if (Integer.valueOf(post.getDelivery_tips()) == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_POST_EMPTY_TIP);
        }
        if (Integer.valueOf(post.getMinimum()) == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_POST_EMPTY_MINIMUM);
        }
        if (Integer.valueOf(post.getNum_of_recruits()) == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_POST_EMPTY_RECRUIT);
        }
        if (post.getLatitude() == null || post.getLongitude() == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_POST_EMPTY_LOCATION);
        }
        if(post.getCategory() == null){
            return new BaseResponse<>(BaseResponseStatus.POST_POST_EMPTY_CATEGORY);
        }

        String fileUrl = postService.getImageUrl(postIdx);
        if(fileUrl != null) {           // 기존에 서버에 등록된 이미지 삭제
            String[] url = fileUrl.split("/");
            logger.info("Delete Image start");
            awsS3Service.deleteImage(url[3]); // https:~ 경로 빼고 파일명으로 삭제
        }
        
        // 이미지 파일이 있으면 서버에 등록
        if(!file.isEmpty())
            fileUrl = url + awsS3Service.uploadFile(file, post, user);

        // 공고 내용 수정
        try {
            Post modifyPost = postService.modifyPost(postIdx, post, user, fileUrl);
            logger.info("Modify success");
            return new BaseResponse<>(modifyPost);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 공고 특정 조회 (로그인 전과 로그인 후의 화면 버튼 달라짐 -> 근데 굳이 11번과 안나눠도 될 것 같음)
    @GetMapping("/{postIdx}")
    public BaseResponse<Post> getPostByUserId(@PathVariable("postIdx") int postIdx, @RequestParam int user) throws BaseException {
        try {
            Post getPost = postService.getPostByUserId(postIdx, user);
            return new BaseResponse<>(getPost);
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    //공고삭제
    @ResponseBody
    @DeleteMapping("/delete/{postIdx}")
    public BaseResponse<Integer> deletePost(@PathVariable("postIdx") int postIdx) throws Exception {

            int deletePost = postService.deletePost(postIdx);
            logger.info("Delete success");

            return new BaseResponse<>(deletePost);

    }

    //공고 내가 참여 조회
    @ResponseBody
    @GetMapping("/join/{userIdx}")
    public BaseResponse<List<Post>> getPostByJoinUserId(@PathVariable("userIdx") int userIdx) throws BaseException {
        try {
            List<Post> getPost = postService.getPostByJoinUserId(userIdx);
            return new BaseResponse<>(getPost);
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/postId/{postIdx}")
    public BaseResponse <Post> getPostByPostId(@PathVariable("postIdx") int postIdx) throws BaseException {
        try {
            Post getPost = postService.getPostByPostId(postIdx);
            return new BaseResponse<>(getPost);
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    //공고 내가 업로드
    @ResponseBody
    @GetMapping("/all/{userIdx}")
    public BaseResponse<List<Post>> getPostBuUploadUserId(@PathVariable("userIdx") int userIdx) throws BaseException {
        try {
            List<Post> getPost = postService.getPostByUploadUserId(userIdx);
            return new BaseResponse<>(getPost);
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    //공고 검색어
    @ResponseBody
    @GetMapping("/search")
    public BaseResponse<List<Post>> getPostByTitleUserId(@RequestParam String keyword) throws BaseException {
        if(keyword.isEmpty())
            return new BaseResponse<>(BaseResponseStatus.POST_EMPTY_KEYWORD);
        try {
            System.out.println("뭐가?찍힘?" + keyword);
            List<Post> getPost = postService.getPostByTitleUserId(keyword);
            return new BaseResponse<>(getPost);
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 공고 상태 변경
    @PutMapping("/status/{postIdx}")
    public BaseResponse<Post> modifyPostStatus(@PathVariable("postIdx") int postIdx) throws BaseException {
        try{
            Post modifyPost = postService.modifyPostStatus(postIdx);
            return new BaseResponse<>(modifyPost);
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /*
    * 카테고리랑 거리(위도 경도) 주면 일정 거리 한 1km정도 안에 공고들 중에 order time 아직 안지난 공고 리스트 넘겨줌
    * */
    @PostMapping("/category")
    public BaseResponse<List<Post>> getPostsByCategory(@RequestBody CategoryRequest postReq) throws BaseException {
        try{
            List<Post> getPostsByCategory = postService.getPostsByCategory(postReq);
            return new BaseResponse<>(getPostsByCategory);
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 해당 공고에 참여중인 유저 전체 조회
    @GetMapping("/users/{postIdx}")
    public BaseResponse<List<User>> getUsersInPost(@PathVariable("postIdx") int postIdx) throws BaseException {
        try{
            List<User> getUsersInPost = postService.getUsersInPost(postIdx);
            return new BaseResponse<>(getUsersInPost);
        } catch(BaseException e){
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 채팅방 아이디로 공고 조회
    @GetMapping("/chatRoom/{chatRoomIdx}")
    public BaseResponse<Post> getPostByChatRoomId(@PathVariable int chatRoomIdx) throws BaseException {
        try{
            Post getPost = postService.getPostByChatRoomId(chatRoomIdx);
            return new BaseResponse<>(getPost);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}