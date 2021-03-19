package server.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {
    private List<String> messages;

    public Message() {
        this.messages = new ArrayList<>();
    }

    public void addMessage(String message){
        messages.add(message);
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }
}
