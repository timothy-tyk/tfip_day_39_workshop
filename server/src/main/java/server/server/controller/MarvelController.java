package server.server.controller;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import server.server.model.CharacterDetailComments;
import server.server.model.CharacterResult;
import server.server.model.Comment;
import server.server.model.Query;
import server.server.service.MarvelRedisService;
import server.server.service.MarvelService;

@Controller
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class MarvelController {
  @Autowired
  private MarvelService marvelSvc;
  @Autowired
  private MarvelRedisService redisSvc;

  @GetMapping(path = "/characters", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getCharacters(@RequestBody Query query){
    String name = query.getName();
    return marvelSvc.getCharacters(name);
  }

  @GetMapping(path = "/character/{characterId}")
  public ResponseEntity<String> getCharacterById(@PathVariable Integer characterId){
    CharacterResult ch = redisSvc.checkForCharacter(characterId);
    if(ch!=null){
      List<Comment> comments = marvelSvc.getComments(characterId).stream().map(doc -> Comment.toComment(doc)).toList();
      CharacterDetailComments chdc = new CharacterDetailComments(ch,comments);
      return ResponseEntity.ok().body(CharacterDetailComments.toJson(chdc).toString());
    }
    else{
      marvelSvc.getCharacterById(characterId);
      CharacterResult chr = redisSvc.checkForCharacter(characterId);
      List<Comment> comments = marvelSvc.getComments(characterId).stream().map(doc -> Comment.toComment(doc)).toList();
      CharacterDetailComments chdc = new CharacterDetailComments(chr,comments);
      return ResponseEntity.ok().body(CharacterDetailComments.toJson(chdc).toString());
    }
  }

  @PostMapping(path = "/character/{characterId}")
  public ResponseEntity<String> postCommentToCharacter(@PathVariable Integer characterId, @RequestBody Comment comment){
    comment.setCharacterId(characterId);
    return marvelSvc.addCommentToCharacter(comment);
  }
}
