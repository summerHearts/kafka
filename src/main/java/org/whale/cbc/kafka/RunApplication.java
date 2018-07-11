package org.whale.cbc.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author thuglife
 * @DATE 2017/6/5
 * @DESCRIPTION :
 */
@SpringBootApplication
@ComponentScan
public class RunApplication {

    @Autowired
    static KafkaProducerService<String> kafkaProducerService;

    public static void main(String[] args) {


        SpringApplication.run(RunApplication.class, args);


    }
}
