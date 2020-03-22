package com.baeldung.springamqp.nondurable.broadcast;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BroadcastMessageApp {

    private static final boolean NON_DURABLE = false;




    //-------------------------- Bind Topic ---------------------------------//

    /**
     * 三个关键字： Queue, TopicExchange, Declarables
     * 联系： BindingBuilder.bind(__QUEUE__).to(__EXCHANGE__).with(__KEY__)
     */
    private final static String TOPIC_QUEUE_1_NAME = "topic.queue1";
    private final static String TOPIC_QUEUE_2_NAME = "topic.queue2";
    private final static String TOPIC_EXCHANGE_NAME = "topic.exchange";
    private static final String BINDING_PATTERN_IMPORTANT = "*.important.*";
    private static final String BINDING_PATTERN_ERROR = "#.error";

    @Bean
    public Declarables topicBindings() {
        Queue topicQueue1 = new Queue(TOPIC_QUEUE_1_NAME, NON_DURABLE);
        Queue topicQueue2 = new Queue(TOPIC_QUEUE_2_NAME, NON_DURABLE);

        TopicExchange topicExchange = new TopicExchange(TOPIC_EXCHANGE_NAME, NON_DURABLE, false);

        /**
         * 一旦运行这句，
         * rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, ROUTING_KEY_USER_IMPORTANT_WARN, "topic important warn" + message);
         * 那么就发送到 TOPIC_QUEUE_1_NAME
         *
         *
         *
         *
         * 一旦运行这句，
         * rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, ROUTING_KEY_USER_IMPORTANT_ERROR, "topic important error" + message);
         * 那么就发送到 TOPIC_QUEUE_2_NAME
         */

        return new Declarables(
                topicQueue1,
                topicQueue2,
                topicExchange,
                BindingBuilder.bind(topicQueue1).to(topicExchange).with(BINDING_PATTERN_IMPORTANT),
                BindingBuilder.bind(topicQueue2).to(topicExchange).with(BINDING_PATTERN_ERROR));
    }
    //-------------------------- Bind Topic ---------------------------------//




    //-------------------------- Bind Queues ---------------------------------//

    /**
     * 三个关键字： Queue, FanoutExchange, Declarables
     * 联系：BindingBuilder.bind(__QUEUE__).to(__EXCHANGE__)
     */
    private final static String FANOUT_QUEUE_1_NAME = "fanout.queue1";
    private final static String FANOUT_QUEUE_2_NAME = "fanout.queue2";
    private final static String FANOUT_EXCHANGE_NAME = "fanout.exchange";

    @Bean
    public Declarables fanoutBindings() {
        Queue fanoutQueue1 = new Queue(FANOUT_QUEUE_1_NAME, NON_DURABLE);
        Queue fanoutQueue2 = new Queue(FANOUT_QUEUE_2_NAME, NON_DURABLE);

        FanoutExchange fanoutExchange = new FanoutExchange(FANOUT_EXCHANGE_NAME, NON_DURABLE, false);

        /**
         * rabbitTemplate.convertAndSend(FANOUT_EXCHANGE_NAME, "", "fanout" + message);
         * 一旦运行这句，那么就发送到 FANOUT_QUEUE_1_NAME, FANOUT_QUEUE_2_NAME
         */

        return new Declarables(
                fanoutQueue1,
                fanoutQueue2,
                fanoutExchange,
                BindingBuilder.bind(fanoutQueue1).to(fanoutExchange),
                BindingBuilder.bind(fanoutQueue2).to(fanoutExchange));
    }
    //-------------------------- Bind Queues ---------------------------------//




    //-------------------------- Queue Config start---------------------------//

    /**
     * 关键字： @RabbitListener
     */
    @RabbitListener(queues = { FANOUT_QUEUE_1_NAME })
    public void receiveMessageFromFanout1(String message) {
        System.out.println("=============================================================");
        System.out.println("Received fanout 1 message: " + message);
        System.out.println("=============================================================");
    }

    @RabbitListener(queues = { FANOUT_QUEUE_2_NAME })
    public void receiveMessageFromFanout2(String message) {
        System.out.println("=============================================================");
        System.out.println("Received fanout 2 message: " + message);
        System.out.println("=============================================================");
    }

    @RabbitListener(queues = { TOPIC_QUEUE_1_NAME })
    public void receiveMessageFromTopic1(String message) {
        System.out.println("=============================================================");
        System.out.println("Received topic 1 (" + BINDING_PATTERN_IMPORTANT + ") message: " + message);
        System.out.println("=============================================================");
    }

    @RabbitListener(queues = { TOPIC_QUEUE_2_NAME })
    public void receiveMessageFromTopic2(String message) {
        System.out.println("=============================================================");
        System.out.println("Received topic 2 (" + BINDING_PATTERN_ERROR + ") message: " + message);
        System.out.println("=============================================================");
    }
    //-------------------------- Queue Config start---------------------------//
    /**
     * Configuration finish
     *
     *
     *
     *
     *
     *
     *
     *
     * Develop start
     */

    private static String ROUTING_KEY_USER_IMPORTANT_WARN = "user.important.warn";
    private static String ROUTING_KEY_USER_IMPORTANT_ERROR = "user.important.error";

    public static void main(String[] args) {
        SpringApplication.run(BroadcastMessageApp.class, args);
    }

    @Bean
    public ApplicationRunner runner(RabbitTemplate rabbitTemplate) {
        String message = " payload is broadcast";
        return args -> {
            rabbitTemplate.convertAndSend(FANOUT_EXCHANGE_NAME, "", "fanout" + message);
            rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, ROUTING_KEY_USER_IMPORTANT_WARN, "topic important warn" + message);
            rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, ROUTING_KEY_USER_IMPORTANT_ERROR, "topic important error" + message);
        };
    }

}
