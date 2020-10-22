package fr.lirmm.aren.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.lirmm.aren.model.Comment;
import fr.lirmm.aren.model.TagSet;
import fr.lirmm.aren.producer.Configurable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Service that provides operations for AAF xml import
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class HttpRequestService {

    @Inject
    @Configurable("idefix.url")
    private String idefixUrl;

    @Inject
    @Configurable("scalar.url")
    private String scalarUrl;

    @Inject
    @Configurable("plateform.id")
    private String plateformId;

    @Inject
    @Configurable("theme.url")
    private String themeUrl;

    /**
     *
     * @throws ParserConfigurationException
     */
    public HttpRequestService() throws ParserConfigurationException {
    }

    /**
     *
     * @param comment
     * @return
     */
    public String getScalar(Comment comment) {
        String result = "";
        try {
            CloseableHttpClient httpClient = HttpClients.createSystem();
            HttpPost request = new HttpPost(scalarUrl);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .build();

            request.setConfig(requestConfig);

            List< NameValuePair> params = new ArrayList<NameValuePair>(3);
            params.add(new BasicNameValuePair("ppl_name", "Complete"));
            params.add(new BasicNameValuePair("fragment1", comment.getSelection() + ""));
            params.add(new BasicNameValuePair("fragment2", comment.getReformulation()));
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            CloseableHttpResponse response = httpClient.execute(request);
            result = EntityUtils.toString(response.getEntity(), "UTF-8");

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HttpRequestService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpRequestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     *
     * @param debateId
     * @param theme
     * @return
     */
    public String getTheme(Long debateId, String theme) {
        String result = "";
        try {
            CloseableHttpClient httpClient = HttpClients.createSystem();
            HttpPost request = new HttpPost(themeUrl);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .build();

            request.setConfig(requestConfig);

            List< NameValuePair> params = new ArrayList<NameValuePair>(3);
            params.add(new BasicNameValuePair("ppl_name", "Complete"));
            params.add(new BasicNameValuePair("debate_id", debateId + ""));
            params.add(new BasicNameValuePair("platform_id", plateformId));
            params.add(new BasicNameValuePair("text", theme));
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            CloseableHttpResponse response = httpClient.execute(request);
            JsonNode node = new ObjectMapper().readTree(response.getEntity().getContent());
            ObjectNode obj = JsonNodeFactory.instance.objectNode();
            ArrayNode arr = JsonNodeFactory.instance.arrayNode();
            node.get("arguments").fieldNames().forEachRemaining((String val) -> {
                arr.add(Long.parseLong(val));
            });
            obj.set("comments", arr);
            obj.set("theme", node.get("theme"));
            result = obj.toString();

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HttpRequestService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpRequestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     *
     * @param text
     * @param comment
     * @return
     */
    public TagSet retrieveTags(String text, Comment comment) {
        TagSet tags = new TagSet();
        try {
            CloseableHttpClient httpClient = HttpClients.createSystem();
            HttpPost httppost = new HttpPost(idefixUrl);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(1 * 60 * 1000)
                    .setConnectTimeout(1 * 60 * 1000)
                    .setConnectionRequestTimeout(1 * 60 * 1000)
                    .build();
            httppost.setConfig(requestConfig);

            List< NameValuePair> params = new ArrayList<NameValuePair>(3);
            params.add(new BasicNameValuePair("extract_term_list", text));
            params.add(new BasicNameValuePair("extract_submit", "Extraire"));
            params.add(new BasicNameValuePair("number", "20"));
            httppost.setEntity(new UrlEncodedFormEntity(params, "windows-1252"));

            CloseableHttpResponse response = httpClient.execute(httppost);
            String responseString = EntityUtils.toString(response.getEntity(), "windows-1252");

            if (responseString.contains("Pas assez de termes, bye")) {
                return null;
            }
            tags = this.parseTags(responseString);

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HttpRequestService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpRequestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tags;
    }

    /**
     *
     * @param tags
     * @param text
     * @return
     */
    public TagSet sendTag(TagSet tags, String text) {
        TagSet newTags = new TagSet();
        try {
            CloseableHttpClient httpClient = HttpClients.createSystem();
            HttpPost httppost = new HttpPost(idefixUrl);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(1 * 60 * 1000)
                    .setConnectTimeout(1 * 60 * 1000)
                    .setConnectionRequestTimeout(1 * 60 * 1000)
                    .build();
            httppost.setConfig(requestConfig);

            List< NameValuePair> params = new ArrayList<NameValuePair>(5);
            params.add(new BasicNameValuePair("linkit_add", "Valider"));
            params.add(new BasicNameValuePair("add_items", tags.toString()));
            params.add(new BasicNameValuePair("extract_submit", "1"));
            params.add(new BasicNameValuePair("extract_term_list", text));
            params.add(new BasicNameValuePair("extract_termid_list", ""));
            httppost.setEntity(new UrlEncodedFormEntity(params, "windows-1252"));

            CloseableHttpResponse response = httpClient.execute(httppost);
            String responseString = EntityUtils.toString(response.getEntity(), "windows-1252");
            if (responseString.contains("Pas assez de termes, bye")) {
                return null;
            }
            newTags = this.parseTags(responseString);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HttpRequestService.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpRequestService.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return newTags;
    }

    private TagSet parseTags(String string) throws IOException {
        TagSet tags = new TagSet();
        Document doc = Jsoup.parse(string, "windows-1252");
        if (doc.getElementsByTag("result").size() > 0) {
            String tagString = doc.getElementsByTag("result").get(0).text();
            if (tagString.length() > 0) {
                tags = new TagSet(tagString);
            }
        }
        return tags;
    }
}
