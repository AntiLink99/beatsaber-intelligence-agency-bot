package bot.main;

import java.time.LocalDateTime;

public class MessageDTO {
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public MessageDTO(String author, LocalDateTime date, String body) {
        this.author = author;
        this.date = date;
        this.body = body;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    String author;
    LocalDateTime date;
    String body;

}
