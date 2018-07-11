package org.whale.cbc.kafka.handler;

/**
 * @Author kevin
 * @DATE 2017/5/16
 * @DESCRIPTION :
 */
public interface ProducerHandler<T> {
    /**
     * 发布消息异常时方法
     * @param topic
     * @param value
     * @param exception
     */
    void whenProducerFailed(String topic, T value, Exception exception);
    void whenProduceSucceed(String topic, T value);
}
