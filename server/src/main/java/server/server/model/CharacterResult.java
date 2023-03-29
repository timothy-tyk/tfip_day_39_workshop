package server.server.model;

import java.io.Serializable;

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

  public static CharacterResult fromJson(JsonObject json){
    CharacterResult ch = new CharacterResult();
    ch.setId(json.getInt("id"));
    ch.setName(json.getString("name"));
    ch.setDescription(json.getString("description"));
    JsonObject obj = json.getJsonObject("thumbnail");
    ch.setImage(obj.getString("path")+obj.getString("extension"));
    return ch;
  }

}
