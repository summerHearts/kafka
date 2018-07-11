import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.whale.cbc.kafka.Impl.ConsumerHandlerImpl;
import org.whale.cbc.kafka.KafkaConsumerService;
import org.whale.cbc.kafka.KafkaProducerService;
import org.whale.cbc.kafka.RunApplication;

import java.util.Date;

/**
 * Created by yakik on 2017/5/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"/spring/applicationContext.xml"})
@SpringBootTest(classes = RunApplication.class)
public class mainTest {
    @Autowired
    private KafkaProducerService<String> kafkaProducerService;
    @Autowired
    private KafkaConsumerService kafkaConsumerService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ConsumerHandlerImpl consumerHandler;
    private String topic_1="test_topic_1";
    private String topic_2="test_topic_2";
    private String topic_3="test_topic_3";
    private String topic_4="test_topic_4";
    private String topic_5="test_topic_5";
    private String topic_6="test_topic_6";
    private String topic_7="test_topic_7";
    private String topic_8="test_topic_8";
    @Test
    public void test1(){
        try {
            kafkaProducerService.produce("SYNC-REDIS-TO-ORACLE","100023","123321");
        }catch (Exception e){

        }

    }

    @Test
    public void testsend(){
        kafkaProducerService.produce(topic_1,"2","test_send_partition");
    }


    @Test
    public void test2(){
        //while(true)
        try {
            for(int i=0;i<500;i++) {
                kafkaProducerService.produce(topic_1,"11", ("hello topic_1_" + i+"_"+new Date().toString()));
//                kafkaProducerService.produce(topic_2, ("hello topic_2_" + i));
//                kafkaProducerService.produce(topic_3, ("hello topic_3_" + i));
//                kafkaProducerService.produce(topic_4, ("hello topic_4_" + i));
//                kafkaProducerService.produce(topic_5, "hello topic_5_" + i);
//                kafkaProducerService.produce(topic_6, "hello topic_6_" + i);
//                kafkaProducerService.produce(topic_7, "hello topic_7_" + i);
//                kafkaProducerService.produce(topic_8, "hello topic_8_" + i);
            }
//            kafkaConsumerService.createConsumerThread("topic_5",new ConsumerHandlerImpl());
//            kafkaConsumerService.createConsumerThread("topic_6",new ConsumerHandlerImpl());
//            kafkaConsumerService.createConsumerThread("topic_7",new ConsumerHandlerImpl());
//            kafkaConsumerService.createConsumerThread("topic_8",new ConsumerHandlerImpl());
            Thread.sleep(100000);
        }catch (Exception e){

        }

    }
    @Test
    public void test3(){
        KafkaProducerService kafkaProducerService1 = (KafkaProducerService) applicationContext.getBean("kafkaProducerService");
        KafkaProducerService kafkaProducerService2 = (KafkaProducerService) applicationContext.getBean("kafkaProducerService");
        Assert.assertEquals(kafkaProducerService1,kafkaProducerService2);
    }

    @Test
    public void test4(){
        try{
            String topic_1="test4_topic_1";
            String topic_2="test4_topic_2";
            String topic_3="test4_topic_3";
            String topic_4="test4_topic_4";
            String topic_5="test4_topic_5";
            String topic_6="test4_topic_6";
            String topic_7="test4_topic_7";
            String topic_8="test4_topic_8";

            Thread.sleep(10000);//等待启动完成
            for(int i=0;i<500;i++) {
                kafkaProducerService.produceSyn(topic_1,"1", ("hello topic_1_" + i));
                kafkaProducerService.produceSyn(topic_2,"1", ("hello topic_2_" + i));
                kafkaProducerService.produceSyn(topic_3,"1", ("hello topic_3_" + i));
                kafkaProducerService.produceSyn(topic_4,"1", ("hello topic_4_" + i));
                kafkaProducerService.produceSyn(topic_5,"1", ("hello topic_5_" + i));
                kafkaProducerService.produceSyn(topic_6,"1", ("hello topic_6_" + i));
            }
            //开始启动消费者
//            kafkaConsumerService.createConsumerThread(topic_1,consumerHandler);
//            kafkaConsumerService.createConsumerThread(topic_2,consumerHandler);
//            kafkaConsumerService.createConsumerThread(topic_3,consumerHandler);
//            kafkaConsumerService.createConsumerThread(topic_4,consumerHandler);
//            kafkaConsumerService.createConsumerThread(topic_5,consumerHandler);
//            kafkaConsumerService.createConsumerThread(topic_6,consumerHandler);
            Thread.sleep(50000);//等待消费完成

        }catch (Exception e){

        }
    }
//    @Test
//    public void test4(){
//        KafkaProducerService kafkaProducerService1 = (KafkaProducerService) applicationContext.getBean("kafkaProducerService");
//        KafkaProducerService kafkaProducerService2 = (KafkaProducerService) applicationContext.getBean("kafkaProducerService");
//        Assert.assertEquals(kafkaProducerService1,kafkaProducerService2);
//    }
}
