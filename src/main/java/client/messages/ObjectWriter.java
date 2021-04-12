package client.messages;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectWriter {
    public void saveMessages(Message message, String userName){
        try(ObjectOutputStream oos =
                    new ObjectOutputStream(new FileOutputStream(String.format("src/main/resources/lib/%s_localHistory.txt", userName)))) {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
