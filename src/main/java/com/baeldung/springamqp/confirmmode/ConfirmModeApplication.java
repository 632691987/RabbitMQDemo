package com.baeldung.springamqp.confirmmode;

import java.util.UUID;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConfirmModeApplication
{
    public static void main(String[] args) {
        SpringApplication.run(ConfirmModeApplication.class, args);
    }

    @Bean
    public ApplicationRunner runner(RabbitTemplate rabbitTemplate) {
        return args -> {
            MsgEntity entity = new MsgEntity();
            String msgId = UUID.randomUUID().toString().transform(value -> value.replaceAll("-", ""));
            entity.setMsgId(msgId);
            entity.setMessage("This is a message");

            CorrelationData correlationData = new CorrelationData(msgId);
            rabbitTemplate.convertAndSend(RabbitConfig.MAIL_EXCHANGE_NAME, RabbitConfig.MAIL_ROUTING_KEY_NAME, MessageHelper.objToMsg(entity), correlationData);// 发送消息
        };
    }
}
