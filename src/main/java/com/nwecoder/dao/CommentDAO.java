package com.nwecoder.dao;

import com.nwecoder.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Administrator on 2017/5/7 0007.
 */
@Mapper
public interface CommentDAO {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id, content, created_date, entity_id, entity_type, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;


    @Insert({"insert into ", TABLE_NAME,"(", INSERT_FIELDS,
            ") values(#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})"})
    int addComment(Comment comment);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME,"where entity_id=#{entityId} and entity_type=#{entityType} order by id desc"})
    List<Comment > selectCommentByEntity(@Param("entityId")int entityId, @Param("entityType")int entityType);


    @Select({"select  count(id) from ", TABLE_NAME," where entity_id=#{entityId} and entity_type=#{entityType} "})
    int getCommentCount(@Param("entityId")int entityId, @Param("entityType")int entityType);

    @Update({"update comment set status=#{status} where where entity_id=#{entityId} and entity_type=#{entityType}"})
    int updateStatus(@Param("entityId")int entityId, @Param("entityType")int entityType, @Param("status") int status);
}