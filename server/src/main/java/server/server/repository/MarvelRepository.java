package server.server.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import server.server.model.Comment;

@Repository
public class MarvelRepository {
  @Autowired
  MongoTemplate mongoTemplate;

  public static final String COLLECTION_NAME="comments";
  public static final String FIELDS_CID="cId";

  public Document addComment(Comment com){
    Document doc = mongoTemplate.insert(Comment.toDoc(com), COLLECTION_NAME);
    return doc;
  }

  public List<Document> getComments(Integer cId){
    Criteria criteria = Criteria.where(FIELDS_CID).is(cId);
    Query query = Query.query(criteria).limit(10).with(Sort.by(Direction.DESC, "timestamp"));
    return mongoTemplate.find(query, Document.class, COLLECTION_NAME);

  }
}
