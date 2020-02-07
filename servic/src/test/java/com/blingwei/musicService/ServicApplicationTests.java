package com.blingwei.musicService;

import com.blingwei.musicService.dao.CommentMapper;
import com.blingwei.musicService.dao.redisService.LikeRedisService;
import com.blingwei.musicService.dao.redisService.impl.LikeRedisServiceImpl;
import com.blingwei.musicService.enums.PickStatusEnum;
import com.blingwei.musicService.enums.TypeEnum;
import com.blingwei.musicService.manage.UserPickManage;
import com.blingwei.musicService.pojo.Comment;
import com.blingwei.musicService.pojo.UserPick;
import net.sf.json.JSON;
import org.apache.shiro.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;


@SpringBootTest
class ServicApplicationTests {

    @Autowired
    private CommentMapper commitMapper;

    @Autowired
    private LikeRedisServiceImpl likeRedisService;

    @Autowired
    private UserPickManage userPickManage;

    @Test
    void contextLoads() {
        Comment comment = new Comment();
        comment.setUserId(1);
        comment.setMatterId(1);
        comment.setType(TypeEnum.ESSAY_WITH_SONG);
        comment.setPid(0);
        comment.setContent("你的心");
        commitMapper.addCommit(comment);
    }

    @Test
    void findCommentByMatterIdTest(){
        List<Comment> comments = commitMapper.findEssayWithSongCommentByMatterId(1);
        comments.size();
    }


    @Test
    void redisTest(){
//        likeRedisService.pickEssayWithSong("13","13");
//        likeRedisService.getPickEssayWithSongNum("13");
//        System.out.println(likeRedisService.getPickEssayWithSongNum("13"));
//        likeRedisService.cancelPickEssayWithSong("12","13");
        String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        System.out.println(path);
    }

    @Test
    void scheduleTeskTest(){
        Map<String, Integer> essayWithSongPickMap = likeRedisService.getAllEssayWithSongPick();
        List<UserPick> userPicks = new ArrayList();
        List<UserPick> userExistPicks = new ArrayList<>();
        for(Map.Entry<String, Integer> entry:essayWithSongPickMap.entrySet()){
            UserPick userPick = new UserPick();
            String[] k = entry.getKey().split(":");
            userPick.setUserId(Integer.parseInt(k[0]));
            userPick.setMatterId(Integer.parseInt(k[1]));
            userPick.setStatus(PickStatusEnum.valueOf(entry.getValue()));
            userPick.setType(TypeEnum.ESSAY_WITH_SONG);
            if(userPickManage.isExist(userPick)){
                userExistPicks.add(userPick);
            }else{
                userPicks.add(userPick);
            }

        }

        Map<String, Integer> commentPickMap = likeRedisService.getAllCommentPick();
        for(Map.Entry<String, Integer> entry:commentPickMap.entrySet()){
            UserPick userPick = new UserPick();
            String[] k = entry.getKey().split(":");
            userPick.setUserId(Integer.parseInt(k[0]));
            userPick.setMatterId(Integer.parseInt(k[1]));
            userPick.setStatus(PickStatusEnum.valueOf(entry.getValue()));
            userPick.setType(TypeEnum.COMMENT);
            if(userPickManage.isExist(userPick)){
                userExistPicks.add(userPick);
            }else{
                userPicks.add(userPick);
            }
        }
        if(!CollectionUtils.isEmpty(userPicks)){
            userPickManage.insertUserPicks(userPicks);
        }
        if(!CollectionUtils.isEmpty(userExistPicks)){
            userPickManage.insertExistUserPicks(userExistPicks);
        }
        System.out.println();
    }

//    @Test
//    void test(){
//        SecurityUtils.getSubject().getPrincipal();
//        System.out.println();
//    }



}
