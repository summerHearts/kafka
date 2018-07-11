package org.whale.cbc.kafka.handler;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Map;

/**
 * @Author kevin
 * @DATE 2017/5/16
 * @DESCRIPTION :消费者方法接口
 */
public interface ConsumerHandler<T> {
    /**
     * 收到消息时方法
     * @param record
     */
    void whenGetRecord(ConsumerRecord<String,T> record) throws Exception ;

    /**
     * 消费异常时方法,返回true时该record的重做，返回false时作废
     * @param record
     * @param exception
     */
    Boolean whenRunFailed(ConsumerRecord<String,T> record,Exception exception);

    /**
     * 手动提交时,offset提交异常时方法
     * @param offsets
     * @param exception
     */
    void whenCommitOffsetFailed(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception);
}
