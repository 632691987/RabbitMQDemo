package com.baeldung.springamqp.confirmmode;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SimpleMailConsumer
{

    @RabbitListener(queues = RabbitConfig.MAIL_QUEUE_NAME)
    public void consume(Message message, Channel channel) throws IOException
    {
        MsgEntity msgEntity = MessageHelper.msgToObj(message, MsgEntity.class);
        log.info("收到消息: {}", msgEntity.toString());
        MessageProperties properties = message.getMessageProperties();
        long tag = properties.getDeliveryTag();
        channel.basicAck(tag, false);
    }

}
