import com.galaev.fixfm.FixFmApp;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;

/**
 * User: anton
 * Date: 27/11/2013
 * Time: 03:49
 */
public class ParamsTest {

    String token = "050d82671f7d373db99df16e63b77050";
    FixFmApp sut = new FixFmApp();

    @Test
    public void firstTest() throws IOException {
//        sut.setToken(token);
//        sut.authenticate();

        JsonObject obj = Json.createObjectBuilder().add("track", Json.createObjectBuilder().add("playcount", "1").build()).build();
        int playcount = obj.getJsonObject("track").getInt("playcount");
        System.out.println(playcount);
    }


}
