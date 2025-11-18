package com.wsn.cow.mapper;

import com.wsn.cow.entity.AlarmRecord;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

/**
 * 告警记录Mapper接口
 */
public interface AlarmRecordMapper {
    
    /**
     * 插入告警记录
     */
    int insert(AlarmRecord alarmRecord);
    
    /**
     * 根据ID查询
     */
    AlarmRecord selectById(Long id);
    
    /**
     * 分页查询告警记录
     */
    List<AlarmRecord> selectPage(@Param("nodeId") String nodeId,
                                 @Param("alarmType") String alarmType,
                                 @Param("alarmLevel") Integer alarmLevel,
                                 @Param("handleStatus") Integer handleStatus,
                                 @Param("startTime") Date startTime,
                                 @Param("endTime") Date endTime,
                                 @Param("offset") int offset,
                                 @Param("limit") int limit);
    
    /**
     * 查询总数
     */
    int countByCondition(@Param("nodeId") String nodeId,
                        @Param("alarmType") String alarmType,
                        @Param("alarmLevel") Integer alarmLevel,
                        @Param("handleStatus") Integer handleStatus,
                        @Param("startTime") Date startTime,
                        @Param("endTime") Date endTime);
    
    /**
     * 查询未处理告警数量
     */
    int countUnhandled();
    
    /**
     * 更新处理状态
     */
    int updateHandleStatus(@Param("id") Long id,
                          @Param("handleStatus") Integer handleStatus,
                          @Param("handler") String handler,
                          @Param("handleRemark") String handleRemark);
    
    /**
     * 删除指定时间之前的告警
     */
    int deleteBeforeTime(Date time);
    
    /**
     * 根据ID删除告警记录
     */
    int deleteById(Long id);
}
