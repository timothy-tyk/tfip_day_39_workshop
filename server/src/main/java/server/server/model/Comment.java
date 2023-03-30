package server.server.model;

import java.util.Date;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Comment {
  private Integer characterId;
  private String comment;
  private long timeStamp;

  public Comment(){
    this.timeStamp = new Date().getTime();
  }

  public static Document toDoc(Comment com){
    Document doc = new Document();
    doc.put("cId", com.getCharacterId());
    doc.put("comment", com.getComment());
    doc.put("timestamp", com.getTimeStamp());
    return doc;
  }

  public static Comment toComment(Document doc){
    Comment com = new Comment();
    com.setCharacterId(doc.getInteger("cId"));
    com.setComment(doc.getString("comment"));
    com.setTimeStamp(doc.getLong("timestamp"));
    return com;
  }

  public static JsonObject toJson(Comment com){
    return Json.createObjectBuilder()
        .add("cId", com.getCharacterId())
        .add("comment", com.getComment())
        .add("timestamp", com.getTimeStamp())
        .build();
}

}
