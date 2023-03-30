package server.server.service;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import server.server.model.Comment;
import server.server.repository.MarvelRepository;

@Service
public class MarvelService {
  // @Value("{marvel.public.key}")
  // private String publicKey;
  // @Value("{marvel.private.key}")
  // private String privateKey;
  @Autowired
  MarvelRedisService marvelRedisSvc;
  public static final String publicKey="4b4e9c10815ec34495754c5a4df9d676";
  public static final String privateKey="7d1b1862a9d00ea6c1ffa58e2ce2a9255d2fc1c2";

  public static final String getCharactersUrl="https://gateway.marvel.com:443/v1/public/characters";

  public String generateHash(){
    long timeStamp = new Date().getTime();
    // MessageDigest md = MessageDigest.getInstance("MD5");
    String toHash = timeStamp+privateKey+publicKey;
    System.out.println(toHash);
    String hash = DigestUtils.md5DigestAsHex(toHash.getBytes());
    return hash.toLowerCase();
  }

  public ResponseEntity<String> getCharacters(String query){
    String marvelUrl = UriComponentsBuilder.fromUriString(getCharactersUrl)
                      .queryParam("nameStartsWith", query)
                      .queryParam("ts", new Date().getTime())
                      .queryParam("apikey", publicKey)
                      .queryParam("hash", generateHash())
                      .toUriString();
    System.out.println(marvelUrl);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.exchange(marvelUrl, HttpMethod.GET, null,String.class);
    // System.out.println(response.getBody());
    marvelRedisSvc.cacheToRedis(response.getBody());
    return response;
  }

  public ResponseEntity<String> getCharacterById(Integer cId){
    String characterById = getCharactersUrl+"/"+cId;
    String marvelUrl = UriComponentsBuilder.fromUriString(characterById)
                      .queryParam("ts", new Date().getTime())
                      .queryParam("apikey", publicKey)
                      .queryParam("hash", generateHash())
                      .toUriString();
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> resp =  restTemplate.exchange(marvelUrl, HttpMethod.GET, null, String.class);
    marvelRedisSvc.cacheToRedis(resp.getBody());
    return resp;
  }
  @Autowired
  MarvelRepository marvelRepo;
  public ResponseEntity<String> addCommentToCharacter(Comment comment){
    Document doc = marvelRepo.addComment(comment);
    return ResponseEntity.ok().body(doc.toString());
  }

  public List<Document> getComments(Integer cId){
    return marvelRepo.getComments(cId);
  }
}
