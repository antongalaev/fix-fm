import com.galaev.fixfm.FixFmApp;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: anton
 * Date: 27/11/2013
 * Time: 03:49
 */
public class ParamsTest {

    String token = "22dae4b3aedd3d45c18132bcddfb1cb9";
    FixFmApp sut = new FixFmApp();

    @Test
    public void firstTest() throws IOException {
        String abc = "1000";
        abc += 300;
        System.out.println(abc);

  //      sut.setToken(token);
   //     sut.authenticate();
//        List<NameValuePair> form = new ArrayList<>();
//        form.add(new BasicNameValuePair("artist[1]", "lalal"));
//        form.add(new BasicNameValuePair("artist[10]", "lalal"));
//        Collections.sort(form, new Comparator<NameValuePair>() {
//            @Override
//            public int compare(NameValuePair o1, NameValuePair o2) {
//                return o1.getName().compareTo(o2.getName());
//            }
//        });
//
//        System.out.println(form);

    }


}
