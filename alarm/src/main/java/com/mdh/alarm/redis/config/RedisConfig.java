package com.mdh.alarm.redis.config;

import com.mdh.alarm.redis.service.AlarmSubscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    /*
     * RedisTemplate는 커넥션 위에서 레디스 커맨드를 도와준다.
     * 레디스의 데이터 저장방식은 byte[]이기 때문에 값을 저장하고 가져오기 위해서는 직렬화가 필요하다.
     * RedisTemplate 클래스는 default Serializer가 JdkSerializationRedisSerializer이기 때문에
     * 문자열 저장의 특화된 RedisTemplate의 서브 클래스 StringRedisTemplate를 사용했다.
     * StringRedisTemplate의 default Serializer는 StringSerializer이다.
     */

    @Bean(name = "redisTemplate")
    public StringRedisTemplate stringRedisTemplate() {
        var stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return stringRedisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        MessageListenerAdapter messageListener,
                                                        Topic topic) {
        var container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListener, topic);
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListener(AlarmSubscriber alarmSubscriber) {
        return new MessageListenerAdapter(alarmSubscriber);
    }

    @Bean
    public Topic alarmTopic(@Value("${spring.data.redis.topic.alarm}") String topic) {
        return PatternTopic.of(topic);
    }

}