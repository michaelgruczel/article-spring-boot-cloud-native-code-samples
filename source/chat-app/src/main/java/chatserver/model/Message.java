package chatserver.model;

import org.springframework.data.redis.core.RedisHash;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("messages")
public class Message {

    @Id
    private Long id;
    private String author;
    private String content;

    public Message() {}

    public Message(Long id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public Message(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}