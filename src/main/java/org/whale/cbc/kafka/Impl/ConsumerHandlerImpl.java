package org.whale.cbc.kafka.Impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.assertj.core.util.DateUtil;
import org.springframework.stereotype.Component;
import org.whale.cbc.kafka.handler.ConsumerHandler;

import java.util.Date;
import java.util.Map;

/**
 * Created by yakik on 2017/5/18.
 */
@Component
public class ConsumerHandlerImpl implements ConsumerHandler<String> {
    int i=0;
    @Override
    public void whenGetRecord(ConsumerRecord<String,String> record) throws Exception {
        System.out.println(new Date().toString()+">>>consumer>>>whenGetRecord>>>topic:"+record.topic()+">>>value:"+new String(record.value())+">>>>time:"+new Date(record.timestamp()).toString());
        i++;
        try{
            Thread.sleep(1000);//等待消费完成
        }catch (Exception e){

        }
//        if(i%5==0){
//            throw new Exception("处理错误");
//        }
    }

    @Override
    public Boolean whenRunFailed(ConsumerRecord<String,String> record, Exception exception) {
        System.out.println(">>>consumer>>>whenRunFailed>>>topic:"+record.topic()+">>>value:"+new String(record.value())+">>>>exception:"+exception);
        return false;
    }

    @Override
    public void whenCommitOffsetFailed(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
        System.out.println(">>>consumer>>>whenCommitOffsetFailed>>>topic:"+offsets.toString()+">>>>exception:"+exception);
    }
}
