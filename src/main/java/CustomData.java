import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class CustomData implements Serializable {

    int sursa;
    String destinatar;
    String message;

    public  CustomData(){}
    public CustomData(@JsonProperty("sursa") int sursa,@JsonProperty("destinatar") String destinatar,@JsonProperty("message") String message  ) {
        this.sursa = sursa;
        this.destinatar = destinatar;
        this.message = message;
    }

    public static CustomData createInstance(int sursa, String destinatar, String message  ) {
        CustomData c = new CustomData();
        c.setSursa(sursa);
        c.setDestinatar(destinatar);
        c.setMessage(message);
        return c;
    }

    public int getSursa() {
        return sursa;
    }

    public void setSursa(int sursa) {
        this.sursa = sursa;
    }

    public String getDestinatar() {
        return destinatar;
    }

    public void setDestinatar(String destinatar) {
        this.destinatar = destinatar;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "{" +
                "\"sursa\":\"" + sursa + "\","+
                "\"destinatar\":\"" + destinatar + "\","+
                "\"message\":\"" + message + "\""+

                "};";


    }
}
