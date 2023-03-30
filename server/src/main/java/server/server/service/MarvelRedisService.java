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
    List<CharacterResult> results = jObj.getJsonObject("data").getJsonArray("results").stream().map(v -> CharacterResult.fromJson(v.asJsonObject())).toList();
    for(CharacterResult res: results){
      redisTemplate.opsForValue().set(res.getId().toString(), res);
      redisTemplate.expire(res.getId().toString(), 1, TimeUnit.HOURS);
    }
  }

  public CharacterResult checkForCharacter(Integer cId){
    CharacterResult ch = (CharacterResult)redisTemplate.opsForValue().get(cId.toString());
    if(ch==null) return null;
    return ch;
  }

}
