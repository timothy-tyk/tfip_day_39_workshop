package server.server.model;

import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDetailComments {
  private CharacterResult character;
  private List<Comment> comments;

  public static JsonObject toJson(CharacterDetailComments chdc){
    JsonArrayBuilder jArrB = Json.createArrayBuilder();
    for (Comment com: chdc.getComments()){
      jArrB.add(Comment.toJson(com));
    }
    
    return Json.createObjectBuilder()
            .add("character", CharacterResult.toJson(chdc.getCharacter()))
            .add("comments", jArrB.build())
            .build();
  }
}
