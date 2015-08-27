package salt.samczsun;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import salt.samczsun.exception.ConnectionException;
import salt.samczsun.exception.InvalidCredentialsException;
import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkypeAuthentication {
    private static final String LOGIN_URL = "https://login.skype.com/login?client_id=578134&redirect_uri=https%3A%2F%2Fweb.skype.com";
    private static final String TOKEN_AUTH_URL = "https://api.asm.skype.com/v1/skypetokenauth";
    private static final String ENDPOINTS_URL = "https://client-s.gateway.messenger.live.com/v1/users/ME/endpoints";
    private Map<String, String> cookies;
    private String cloud = "";

    private Response postData(String username, String password) throws ConnectionException {
        try {
            Map<String, String> data = new HashMap<String, String>();
            Document loginDocument = Jsoup.connect(LOGIN_URL).get();
            Element loginForm = loginDocument.getElementById("loginForm");
            for (Element input : loginForm.getElementsByTag("input")) {
                data.put(input.attr("name"), input.attr("value"));
            }
            Date now = new Date();
            data.put("timezone_field", new SimpleDateFormat("XXX").format(now).replace(':', '|'));
            data.put("username", username);
            data.put("password", password);
            data.put("js_time", String.valueOf(now.getTime() / 1000));
            return Jsoup.connect(LOGIN_URL).data(data).method(Method.POST).execute();
        } catch (IOException e) {
            throw new ConnectionException("While submitting credentials", e);
        }
    }

    private Response getAsmToken(Map<String, String> cookies, String skypeToken) throws ConnectionException {
        try {
            return Jsoup.connect(TOKEN_AUTH_URL).cookies(cookies).data("skypetoken", skypeToken).method(Method.POST).execute();
        } catch (IOException e) {
            throw new ConnectionException("While fetching the asmtoken", e);
        }
    }

    //Deprecated - Poller handles getting new tokens and endpoint ids
    @Deprecated
    private HttpURLConnection registerEndpoint(String skypeToken) throws ConnectionException {
        try {
            ConnectionBuilder builder = new ConnectionBuilder();
            builder.setUrl(ENDPOINTS_URL);
            builder.setMethod("POST", true);
            builder.addHeader("Authentication", String.format("skypetoken=%s", skypeToken));
            builder.setData("{}");
            HttpURLConnection connection = builder.build();
            int code = connection.getResponseCode();
            if (code >= 301 && code <= 303 || code == 307) {
                builder.setUrl(connection.getHeaderField("Location"));
                updateCloud(connection.getHeaderField("Location"));
                connection = builder.build();
                code = connection.getResponseCode();
            }
            if (code == 201) {
                return connection;
            } else {
                throw generateException(connection);
            }
        } catch (IOException e) {
            throw new ConnectionException("While registering the endpoint", e);
        }
    }

    public void login(SkypeAPI api, LocalAccount account) throws Exception {
        final Map<String, String> tCookies = new HashMap<String, String>();
        Response loginResponse = postData(account.getEmail(), account.getPassword());

        tCookies.putAll(loginResponse.cookies());
        Document loginResponseDocument;
        try {
            loginResponseDocument = loginResponse.parse();
        } catch (IOException e) {
            throw new Exception("While parsing the login response");
        }
        Elements inputs = loginResponseDocument.select("input[name=skypetoken]");
        if (inputs.size() > 0) {
            String tSkypeToken = inputs.get(0).attr("value");

            Response asmResponse = getAsmToken(tCookies, tSkypeToken);
            tCookies.putAll(asmResponse.cookies());

            HttpURLConnection registrationToken = registerEndpoint(tSkypeToken);
            String[] splits = registrationToken.getHeaderField("Set-RegistrationToken").split(";");
            String tRegistrationToken = splits[0];
            String tEndpointId = splits[2].split("=")[1];

            //GHOST START
            //account.setEnd(tEndpointId); -> now void -> "Poller" thread handles this now
            account.setRegToken(tRegistrationToken);
            account.setXSkypeToken(tSkypeToken);
            api.cookies = this.serializeCookies(tCookies);

        } else if (loginResponseDocument.html().contains("https://www.google.com/recaptcha")) {
            System.out.println("Your IP is on the Skype recaptcha list. Login to your account on web.skype.com, then come back here :)");
            System.exit(-1);
            //GHOST END
        } else {
            Elements elements = loginResponseDocument.select(".message_error");
            if (elements.size() > 0) {
                Element div = elements.get(0);
                if (div.children().size() > 1) {
                    Element span = div.child(1);

                    throw new InvalidCredentialsException(span.text());
                }
            }
            throw new InvalidCredentialsException("Could not find error message. Dumping entire page. \n" + loginResponseDocument.html());
        }
    }

    public IOException generateException(HttpURLConnection connection) throws IOException {
        return new IOException(String.format("(%s, %s)", connection.getResponseCode(), connection.getResponseMessage()));
    }

    private void updateCloud(String anyLocation) {
        Pattern grabber = Pattern.compile("https?://([^-]*-)client-s");
        Matcher m = grabber.matcher(anyLocation);
        if (m.find()) {
            this.cloud = m.group(1);
        } else {
            throw new IllegalArgumentException("Could not find match in " + anyLocation);
        }
    }

    public String withCloud(String url, Object... extraArgs) {
        Object[] format = new Object[extraArgs.length + 1];
        format[0] = cloud;
        for (int i = 1; i < format.length; i++) {
            format[i] = extraArgs[i - 1].toString();
        }
        return String.format(url, format);
    }

    public String serializeCookies(Map<String, String> cookies) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            result.append(cookie.getKey()).append("=").append(cookie.getValue()).append(";");
        }
        return result.toString();
    }

    public String getCookieString() {
        return serializeCookies(cookies);
    }
}
