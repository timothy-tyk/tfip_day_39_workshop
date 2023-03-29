package server.server.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.xml.bind.DatatypeConverter;


@Service
public class MarvelService {
  @Autowired
  MarvelRedisService marvelRedisSvc;
  public static final String publicKey="4b4e9c10815ec34495754c5a4df9d676";
  public static final String privateKey="7d1b1862a9d00ea6c1ffa58e2ce2a9255d2fc1c2";
  // public static final long timeStamp = new Date().getTime();

  public static final String getCharactersUrl="https://gateway.marvel.com:443/v1/public/characters";

  public String generateHash() throws NoSuchAlgorithmException{
    long timeStamp = new Date().getTime();
    MessageDigest md = MessageDigest.getInstance("MD5");
    String toHash = timeStamp+privateKey+publicKey;
    System.out.println(toHash);
    md.update(toHash.getBytes());
    byte[] digest = md.digest();
    String hash = DatatypeConverter.printHexBinary(digest);
    return hash.toLowerCase();
    // StringBuffer sb = new StringBuffer();
    // for(byte b:digest){
    //   sb.append(Integer.toHexString((int)(b & 0xff)));
    // }
    // System.out.println(sb.toString());
    // return sb.toString();
  }

  public ResponseEntity<String> getCharacters(String query) throws NoSuchAlgorithmException{
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
}
