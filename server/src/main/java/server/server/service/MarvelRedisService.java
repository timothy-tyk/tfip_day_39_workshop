package server.server.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import server.server.model.CharacterResult;

@Service
public class MarvelRedisService {
  @Autowired
  RedisTemplate<String, Object> redisTemplate;
  public void cacheToRedis(String fullResponse){
    InputStream is = new ByteArrayInputStream(fullResponse.getBytes());
    JsonReader jReader = Json.createReader(is);
    JsonObject jObj = jReader.readObject();
    // JsonObject data = jObj.getJsonObject("data");
    List<CharacterResult> results = jObj.getJsonObject("data").getJsonArray("results").stream().map(v -> CharacterResult.fromJson(v.asJsonObject())).toList();
    for(CharacterResult res: results){
      redisTemplate.opsForValue().set(res.getName(), (Object)res);
      redisTemplate.expire(res.getName(), 1, TimeUnit.HOURS);
    }

  }
}
