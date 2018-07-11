package org.whale.cbc.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.whale.cbc.kafka.handler.ProducerHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @Author kevin
 * @DATE 2017/5/16
 * @DESCRIPTION :
 */
@Component
public class KafkaProducerService<T> {
    private Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private KafkaProducer producer;
    private ProducerHandler producerHandler;
    public KafkaProducerService(String propertisePath, ProducerHandler producerHandler){
        Assert.notNull(propertisePath,"producer.properties路径为空");
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(propertisePath);
        InputStream in=null;
        //InputStream in = Object.class.getResourceAsStream(propertisePath);
        try {
            in =resource.getInputStream();
       }catch (Exception e){
            throw new RuntimeException("读取异常:"+propertisePath+",异常:"+e.getMessage());
        }
        Assert.notNull(in,propertisePath+"文件不存在");
        Properties prop = new Properties();
        try{
            prop.load(in);
            this.producer = new KafkaProducer(prop);
            if(producerHandler!=null)this.producerHandler=producerHandler;
        }catch (IOException e){
            e.printStackTrace();
            logger.error("创建生成者失败："+e.getMessage());
            Assert.state(false,"创建生成者失败："+e.getMessage());
        }
    }

    /**
     * 发送消息异步
     * @param topic
     * @param value
     */
//    public void produce(String topic, T value) {
//        this.producer.send(new ProducerRecord(topic, value), new KafkaProducerService.ProduceCallback(topic, value, System.currentTimeMillis()));
//    }


    /**
     * 发送消息异步
     * @param topic
     * @param partition
     * @param key
     * @param value
     */

//    public void produce(String topic, Integer partition, String key, T value) {
//        this.producer.send(new ProducerRecord(topic, partition, key, value), new KafkaProducerService.ProduceCallback(topic, value, System.currentTimeMillis()));
//        logger.info("");
//    }
//
//    public void produce(String topic, Integer partition, T value) {
//        this.producer.send(new ProducerRecord(topic, partition, "", value), new KafkaProducerService.ProduceCallback(topic, value, System.currentTimeMillis()));
//    }

    public void produce(String topic, String key, T value) {
        this.producer.send(new ProducerRecord(topic, key, value), new KafkaProducerService.ProduceCallback(topic, value, System.currentTimeMillis()));
        logger.info("生产报文--->topic:{},key:{},msg:{}",topic, key, value);
    }

    /**
     * 发送消息同步(无回调)
     * @param topic
     * @param value
     */
//    public void produceSyn(String topic, T value) {
//        try {
//            this.producer.send(new ProducerRecord(topic, value)).get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            throw new RuntimeException("报文发送异常:"+e.getMessage());
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//            throw new RuntimeException("报文发送异常:"+e.getMessage());
//        }
//    }


    /**
     * 发送消息同步(无回调)
     * @param topic
     * @param partition
     * @param key
     * @param value
     */

//    public void produceSyn(String topic, Integer partition, String key, T value) {
//        try {
//            this.producer.send(new ProducerRecord(topic, partition, key, value)).get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            throw new RuntimeException("报文发送异常:"+e.getMessage());
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//            throw new RuntimeException("报文发送异常:"+e.getMessage());
//        }
//    }
//
//    public void produceSyn(String topic, Integer partition, T value) {
//        try {
//            this.producer.send(new ProducerRecord(topic, partition,"", value)).get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            throw new RuntimeException("报文发送异常:"+e.getMessage());
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//            throw new RuntimeException("报文发送异常:"+e.getMessage());
//        }
//    }

    public void produceSyn(String topic,String key, T value) {
        try {
            this.producer.send(new ProducerRecord(topic, key, value)).get();
            logger.info("生产报文--->topic:{},key:{},msg:{}",topic, key, value);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("报文发送异常:"+e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("报文发送异常:"+e.getMessage());
        }
    }


    /**
     * 消息发送回调函数
     */
    class ProduceCallback implements Callback {
        private String topic;
        private T value;
        private long startTime;

        public ProduceCallback(String topic, T value, long startTime) {
            this.topic = topic;
            this.value = value;
            this.startTime = startTime;
        }

        public void onCompletion(RecordMetadata metadata, Exception exception) {
            if(metadata != null) {
                KafkaProducerService.this.logger.debug(">>>topic {}, message({}) sent to partition[{}],offset({}) in {} ms", new Object[]{this.topic, this.value, Integer.valueOf(metadata.partition()), Long.valueOf(metadata.offset()), Long.valueOf(System.currentTimeMillis() - this.startTime)});
                if(KafkaProducerService.this.producerHandler!=null)KafkaProducerService.this.producerHandler.whenProduceSucceed(this.topic,this.value);
            } else {
                exception.printStackTrace();
                if(KafkaProducerService.this.producerHandler!=null)KafkaProducerService.this.producerHandler.whenProducerFailed(this.topic,this.value,exception);
            }

        }
    }

}
