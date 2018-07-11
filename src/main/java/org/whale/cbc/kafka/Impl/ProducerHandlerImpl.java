package org.whale.cbc.kafka.Impl;

import org.springframework.stereotype.Component;
import org.whale.cbc.kafka.handler.ProducerHandler;

/**
 * Created by yakik on 2017/5/18.
 */
@Component
public class ProducerHandlerImpl implements ProducerHandler<String> {
    @Override
    public void whenProducerFailed(String topic, String value, Exception exception) {
        System.out.println(">>>consumer>>>whenCommitOffsetFailed>>>topic:"+topic+">>>value:"+value+">>>>exception:"+exception);
    }

    @Override
    public void whenProduceSucceed(String topic, String value) {

    }
}
