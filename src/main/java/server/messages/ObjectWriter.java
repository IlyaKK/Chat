package server.messages;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectWriter {
    public void saveMessages(Message message){
        try(
                ObjectOutputStream oos =
                        new ObjectOutputStream(new FileOutputStream("src/main/resources/lib/allMessages.txt"))) {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
