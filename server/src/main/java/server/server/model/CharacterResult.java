package server.server.model;

import java.io.Serializable;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterResult implements Serializable{
  private Integer id;
  private String name;
  private String description;
  private String image;
  // private List<Comment> comments;

  public static CharacterResult fromJson(JsonObject json){
    CharacterResult ch = new CharacterResult();
    ch.setId(json.getInt("id"));
    ch.setName(json.getString("name"));
    ch.setDescription(json.getString("description"));
    JsonObject obj = json.getJsonObject("thumbnail");
    ch.setImage(obj.getString("path")+"."+obj.getString("extension"));
    return ch;
  }

  public static JsonObject toJson(CharacterResult cr){
    JsonObject json = Json.createObjectBuilder()
                      .add("id", cr.getId())
                      .add("name", cr.getName())
                      .add("description", cr.getDescription())
                      .add("image", cr.getImage())
                      .build();
    return json;
  }

}
