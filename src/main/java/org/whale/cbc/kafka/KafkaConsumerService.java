package org.whale.cbc.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.whale.cbc.kafka.handler.ConsumerHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author kevin
 * @DATE 2017/5/16
 * @DESCRIPTION :消费者服务，初始化是有初始topicHandlerMap则自动启动消费，否则设定为生产模式
 */
@Component
public class KafkaConsumerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, ConsumerHandler> topicHandlerMap;
    private int threadNums;
    private Boolean autoCommit;
    private ExecutorService executor;
    private Boolean serviceisRun = false;
    private Properties prop;
    public static ConcurrentHashMap<String, List<ConsumerThread>> consumerThreadMap = new ConcurrentHashMap<>();
    @Autowired
    private KafkaProducerService kafkaProducerService;

    /**
     * 消费者线程
     */
    public class ConsumerThread implements Runnable{
        private ConsumerHandler handler;
        private KafkaConsumer<String,byte[]> consumer;
        private Boolean threadIsRun = false;
        private String topic;
        private Boolean redo = false;
        private String name;

        public ConsumerThread(String topic, ConsumerHandler handler) {
            this.handler = handler;
            this.consumer =new KafkaConsumer<String, byte[]>(KafkaConsumerService.this.prop);
            this.topic = topic;
            consumer.subscribe(Arrays.asList(topic));
        }

        public ConsumerThread(String topic, ConsumerHandler handler,Properties prop) {
            this.handler = handler;
            this.consumer =new KafkaConsumer<String, byte[]>(prop);
            this.topic = topic;
            consumer.subscribe(Arrays.asList(topic));
        }

        public void run() {
            threadIsRun = true;
            while (serviceisRun&&threadIsRun) {
                ConsumerRecords<String, byte[]> records = consumer.poll(200);
                if(records.count()>0){
                    logger.info(">>>拉取到{}条信息",records.count());
                    if (!autoCommit) {
                        consumer.commitAsync();
                    }
                    //List<TopicPartition> errorTopic = new ArrayList<TopicPartition>();
                    for (final ConsumerRecord record : records) {
//                        TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
//                        if(redo&&errorTopic.contains(topicPartition)){
//                            logger.debug(">>>kafka消息队列消费异常,暂停消费准备重试，topic:{},partition:{}", new Object[]{record.topic(),Integer.valueOf(record.partition())});
//                            continue;
//                        }
                        logger.info(">>>准备消费topic:{},partition:{},offset:{},value:{}", new Object[]{record.topic(), Integer.valueOf(record.partition()), Long.valueOf(record.offset()),record.value()});
                        try {
                            this.handler.whenGetRecord(record);
                        } catch (Exception e) {
                            logger.error(">>>kafka消息队列消费异常，topic:{},message:{},offset:{},partition:{}", new Object[]{record.topic(), record.value(), Long.valueOf(record.offset()), Integer.valueOf(record.partition())});
                            logger.error(">>>kafka异常信息", e);
                            Boolean redo =this.handler.whenRunFailed(record, e);
                            if(redo!=null&&redo){
                                logger.info(">>>kafka消息队列消费异常,消息放至队尾准备重试，topic:{},partition:{},offset:{}", new Object[]{record.topic(),Integer.valueOf(record.partition())}, Long.valueOf(record.offset()));
//                                consumer.seek(topicPartition,record.offset());
//                                errorTopic.add(topicPartition);
//                                redo = true;
                                kafkaProducerService.produce(record.topic(),record.key().toString(),record.value());
                            }
                        }
                        logger.info(">>>结束消费topic-partition-offset : {}-{}-{}", new Object[]{record.topic(), Integer.valueOf(record.partition()), Long.valueOf(record.offset())});
                    }
                }
            }
        }

        public void stop(){
            this.threadIsRun=false;
            logger.info("{},消费者停止",this.topic);
            consumerThreadMap.get(this.topic).remove(this);
        }

        public KafkaConsumer<String, byte[]> getConsumer() {
            return consumer;
        }

        public String getTopic() {
            return topic;
        }

        public Boolean isRun(){
            return this.threadIsRun;
        }
    }

    /**
     * 手动提交offset回调函数
     */
    class KafkaOffsetCommitCallback implements OffsetCommitCallback{

        private String topic;

        public KafkaOffsetCommitCallback(String topic){
            this.topic=topic;
        }

        @Override
        public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
            //处理异常
            if (null != exception) {
                // 如果异步提交发生了异常
                logger.error(">>>kafka提交offset异常 {}, 错误: {}", topic, exception);
                topicHandlerMap.get(topic).whenCommitOffsetFailed(offsets,exception);
            }
        }
    }

    /**
     * 初始化函数，当topicHandlerMap为空时，设定为生产模式
     * @param propertisePath
     * @param topicHandlerMap
     */
    public KafkaConsumerService(String propertisePath, Map<String, ConsumerHandler> topicHandlerMap,int threadNums){
        Assert.notNull(propertisePath,"consumer.properties路径为空");
        //InputStream in = Object.class.getResourceAsStream(propertisePath);
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(propertisePath);
        InputStream in=null;
        //InputStream in = Object.class.getResourceAsStream(propertisePath);
        try {
            in =resource.getInputStream();
        }catch (Exception e){
            throw new RuntimeException("读取异常:"+propertisePath);
        }
        Assert.notNull(in,propertisePath+"文件不存在");
        Properties prop = new Properties();
        try{
            prop.load(in);
            this.autoCommit = prop.getProperty("enable.auto.commit").equals("true")?true:false;
            this.threadNums = threadNums==0?100:threadNums;
            this.topicHandlerMap= topicHandlerMap;
            this.prop = prop;
        }catch (IOException e){
            e.printStackTrace();
            logger.error("创建消费者失败："+e.getMessage());
            Assert.state(false,"创建消费者失败："+e.getMessage());
        }
    }

    /**
     * 生产模式下生产并启动消费者
     * @param topic
     * @param consumerHandler
     * @return
     */
    public ConsumerThread createConsumerThread(String topic,ConsumerHandler consumerHandler){
        ConsumerThread consumerThread = new ConsumerThread(topic,consumerHandler);
        executor.submit(consumerThread);
        if(consumerThreadMap.get(topic)==null){
            consumerThreadMap.put(topic,new ArrayList<ConsumerThread>());
        }
        consumerThreadMap.get(topic).add(consumerThread);
        return consumerThread;
    }

    public ConsumerThread createConsumerThread(String topic,String groupId,ConsumerHandler consumerHandler){
        Properties _prop = (Properties)this.prop.clone();
        _prop.setProperty("group.id",groupId);
        ConsumerThread consumerThread = new ConsumerThread(topic,consumerHandler,_prop);
        executor.submit(consumerThread);
        if(consumerThreadMap.get(topic)==null){
            consumerThreadMap.put(topic,new ArrayList<ConsumerThread>());
        }
        consumerThreadMap.get(topic).add(consumerThread);
        return consumerThread;
    }
    /**
     * 启动消费者
     */
    public void run() {
        if(this.serviceisRun){
            logger.info("消费者已经启动");
            return;
        }
        this.logger.debug(">>>KafkaConsumerService.start！");
        this.serviceisRun = true;
        this.executor = Executors.newFixedThreadPool(threadNums);
        if(null == this.topicHandlerMap||this.topicHandlerMap.size()==0){
            return;
        }
        Iterator consumerMap = this.topicHandlerMap.keySet().iterator();
        List<String> topics=new ArrayList<String>();
        while(consumerMap.hasNext()) {
            String topic = (String)consumerMap.next();
            logger.info("订阅topic:{}",topic);
            ConsumerThread consumerThread = new ConsumerThread(topic,topicHandlerMap.get(topic));
            executor.submit(consumerThread);
            if(consumerThreadMap.get(topic)==null){
                consumerThreadMap.put(topic,Arrays.asList(consumerThread));
            }else{
                consumerThreadMap.get(topic).add(consumerThread);
            }
        }
    }

    /**
     * 关闭消费者
     */
    public void shutdown() {
        logger.info("开始停止消费者");
        this.serviceisRun=false;
         if (executor!= null) {
             executor.shutdown();
         }
         try {
             if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    logger.debug("线程池超时关闭，强制结束线程");
                 }
         } catch (InterruptedException ignored) {
             logger.error("线程意外终止");
             Thread.currentThread().interrupt();
         }

    }

    /**
     * 消费者是否启动
     * @return
     */
    public Boolean isRun() {
        return serviceisRun;
    }
}
