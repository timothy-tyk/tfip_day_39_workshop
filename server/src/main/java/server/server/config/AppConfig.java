package server.server.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class AppConfig {
  // MongoDB
  @Value("${mongo.url}")
  private String mongoUrl;

  private final String DATABASE_NAME="marvel";

  @Bean
  public MongoTemplate mongoTemplate(){
    MongoClient client = MongoClients.create(mongoUrl);
    return new MongoTemplate(client, DATABASE_NAME);
  }


// Redis
  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private Optional<Integer> redisPort;

  @Value("${spring.redis.username}")
  private String redisUsername;

  @Value("${spring.redis.password}")
  private String redisPassword;
  @Bean
  @Scope("singleton")
  RedisTemplate<String, Object> redisTemplate(){
    final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
    config.setHostName(redisHost);
    config.setPort(redisPort.get());

    if(!redisUsername.isEmpty()){
      config.setUsername(redisUsername);
    }
    if(!redisPassword.isEmpty()){
      config.setPassword(redisPassword);
    }
    config.setDatabase(0);
    final JedisClientConfiguration jedisClient = JedisClientConfiguration.builder().build();
    final JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
    jedisFac.afterPropertiesSet();
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
    redisTemplate.setConnectionFactory(jedisFac);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
        // set the map key/value serialization type to String
        redisTemplate.setHashKeySerializer(redisTemplate.getKeySerializer());
        redisTemplate.setValueSerializer(new JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new JsonRedisSerializer());

        return redisTemplate;
  }
  static class JsonRedisSerializer implements RedisSerializer<Object> {

        private final ObjectMapper om;

        public JsonRedisSerializer() {
            this.om = new ObjectMapper().activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                    ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        }

        @Override
        public byte[] serialize(Object t) throws SerializationException {
            try {
                return om.writeValueAsBytes(t);
            } catch (JsonProcessingException e) {
                throw new SerializationException(e.getMessage(), e);
            }
        }

        @Override
        public Object deserialize(byte[] bytes) throws SerializationException {

            if (bytes == null) {
                return null;
            }

            try {
                return om.readValue(bytes, Object.class);
            } catch (Exception e) {
                throw new SerializationException(e.getMessage(), e);
            }
        }
      }
    }

