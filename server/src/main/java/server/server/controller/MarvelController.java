package server.server.controller;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import server.server.model.Query;
import server.server.service.MarvelService;

@Controller
@RequestMapping(path = "/api", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class MarvelController {
  @Autowired
  private MarvelService marvelSvc;

  @GetMapping(path = "/characters")
  public ResponseEntity<String> getCharacters(@RequestBody Query query) throws NoSuchAlgorithmException{
    String name = query.getName();
    return marvelSvc.getCharacters(name);
  }
}
