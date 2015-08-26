package xyz.gghost.jskype.internal.packet;

import sun.misc.IOUtils;
import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.internal.packet.BasePacket;
import xyz.gghost.jskype.internal.packet.Header;
import xyz.gghost.jskype.internal.packet.RequestType;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

/**
 * Created by Ghost on 26/08/2015.
 */
public class BasePacketUploader extends BasePacket {
    public BasePacketUploader(SkypeAPI api) {
        super(api);
    }
    public String makeRequest(LocalAccount usr, InputStream ss) {
        try {

            //read the file
            byte[] bytes;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = ss.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            bytes = buffer.toByteArray();
            //end read of file


            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod((type == RequestType.GET ? "GET" : (type == RequestType.POST ? "POST" : (type == RequestType.PUT ? "PUT" : (type == RequestType.DELETE ? "DELETE" : "OPTIONS")))));

            con.setRequestProperty("Content-Type", isForm ? "application/x-www-form-urlencoded" : (file ? "application/octet-stream" : "application/json; charset=utf-8"));
            con.setRequestProperty("Content-Length", Integer.toString(bytes.length));
            con.setRequestProperty("User-Agent", "0/7.7.0.103// libhttpX.X");
            con.setRequestProperty("Cookie", api.cookies);
            con.setDoOutput(true);

            for (Header s : headers) {
                con.addRequestProperty(s.getType(), s.getData());
            }


            OutputStream wr = con.getOutputStream();

            wr.write(bytes);
            wr.flush();
            wr.close();



            int responseCode = con.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString() == null ? "" : response.toString();

            } else if (responseCode == 401) {
                System.out.println("\n\nBad login...");
                System.out.println(this.url + " returned 401. \nHave you been running jSkype for more than 2 days?\nWithin 4 seconds the ping-er should relog you in.\n\n");
                return "---";
            } else if (responseCode == 204) {
                return "";
            } else {
                //Debug info
                System.out.println("Error contacting skype\nUrl: "+ url + "\nCode: "+responseCode + "\nData: " + data );
                for (Header header : headers){
                    System.out.println(header.getType() + ": " + header.getData());
                }
                return null;
            }
        } catch (Exception e) {
            System.out.println("================================================");
            System.out.println("========Unable to request  the skype api========");
            System.out.println("================================================");
            e.printStackTrace();
            return null;
        }
    }
}
