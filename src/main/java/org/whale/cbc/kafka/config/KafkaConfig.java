package org.whale.cbc.kafka.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.whale.cbc.kafka.KafkaConsumerService;
import org.whale.cbc.kafka.KafkaProducerService;
import org.whale.cbc.kafka.handler.ConsumerHandler;
import org.whale.cbc.kafka.handler.ProducerHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author thuglife
 * @DATE 2017/6/5
 * @DESCRIPTION :
 */
@Configuration
@ConfigurationProperties(prefix = "kafka")
@Setter
@Getter
public class KafkaConfig {
    private String producerPropertisePath;
    private String consumerPropertisePath;
    private String producerHandler;
    private Map<String,String> topicHandlerMap;
    private int threadNums=1000;
    @Autowired
    private ApplicationContext applicationContext;
    @Bean
    public KafkaProducerService kafkaProducerService(){
            ProducerHandler producerHandlerBean = producerHandler==null||producerHandler.isEmpty()?null:(ProducerHandler) applicationContext.getBean(producerHandler);
            return new KafkaProducerService(producerPropertisePath==null?"classpath:properties/producer.properties":producerPropertisePath, producerHandlerBean);
    }
    @Bean
    public KafkaConsumerService kafkaConsumerService(){
        Map<String,ConsumerHandler> topicHandlerMap2=new HashMap<String, ConsumerHandler>();
        if(topicHandlerMap!=null){
            for(String key:topicHandlerMap.keySet()){
                topicHandlerMap2.put(key,(ConsumerHandler) applicationContext.getBean(topicHandlerMap.get(key)));
            }
        }
        KafkaConsumerService kafkaConsumerService= new KafkaConsumerService(consumerPropertisePath==null?"classpath:properties/customer.properties":consumerPropertisePath,topicHandlerMap2,threadNums);
        kafkaConsumerService.run();
        return kafkaConsumerService;
    }
}
