package server.messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ObjectReader {
    public Object extractMessages(){
        Object obj = null;
        File file = new File("src/main/resources/lib/allMessages.txt");
        if(!file.exists()) {
            try {
                file.createNewFile();
                Message message = new Message();
                new ObjectWriter().saveMessages(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("src/main/resources/lib/allMessages.txt"))) {
            obj = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
