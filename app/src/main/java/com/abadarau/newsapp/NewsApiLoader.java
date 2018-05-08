package com.abadarau.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


class NewsApiLoader extends AsyncTaskLoader<List<NewsApiItem>> {

    public static final String API_URL = "https://content.guardianapis.com/search?q=12%20years%20a%20slave&format=json&tag=film/film,tone/reviews&from-date=2010-01-01&show-tags=contributor&show-fields=starRating,headline,thumbnail,short-url&order-by=relevance&api-key=test";
    public static final int CONNECT_TIMEOUT = 5000;
    public static final String WEB_TITLE = "webTitle";
    public static final String API_URL1 = "apiUrl";
    public static final String SECTION_ID = "sectionId";
    public static final String SECTION_NAME = "sectionName";
    public static final String PUBLICATION_DATE = "webPublicationDate";
    public static final String ID = "id";
    public static final String WEB_URL = "webUrl";
    public static final String TAGS = "tags";

    private boolean apiIsDown = false;

    public NewsApiLoader(Context context) {
        super(context);
    }

    @Override
    public List<NewsApiItem> loadInBackground() {
        ArrayList<NewsApiItem> newsApiItems = new ArrayList<>();
        try {
            JSONObject apiJsonData = getApiJsonData();
            JSONArray jsonArrayItems = apiJsonData.getJSONObject("response").getJSONArray("results");
            for (int i = 0; i < jsonArrayItems.length(); i++) {
                NewsApiItem newsApiItem = new NewsApiItem();
                JSONObject jsonNewsApiItem = jsonArrayItems.getJSONObject(i);
                newsApiItem.setId(jsonNewsApiItem.getString(ID));
                newsApiItem.setApiUrl(jsonNewsApiItem.getString(API_URL1));
                newsApiItem.setSectionId(jsonNewsApiItem.getString(SECTION_ID));
                newsApiItem.setSectionName(jsonNewsApiItem.getString(SECTION_NAME));
                newsApiItem.setWebPublicationDate(jsonNewsApiItem.getString(PUBLICATION_DATE));
                newsApiItem.setWebTitle(jsonNewsApiItem.getString(WEB_TITLE));
                newsApiItem.setWebUrl(jsonNewsApiItem.getString(WEB_URL));
                JSONArray tags = jsonNewsApiItem.getJSONArray(TAGS);
                if(tags != null && tags.length() > 0){
                    newsApiItem.setAuthorName(tags.getJSONObject(0).getString(WEB_TITLE));
                }
                newsApiItems.add(newsApiItem);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return newsApiItems;
    }

    private JSONObject getApiJsonData() throws IOException, JSONException {
        return new JSONObject(getApiData());
    }

    private String getApiData() throws IOException {
        StringBuilder responseString = new StringBuilder();
        URL url = new URL(NewsApiLoader.API_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
        httpURLConnection.setReadTimeout(CONNECT_TIMEOUT);
        httpURLConnection.connect();

        if (httpURLConnection.getResponseCode() == 200) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseString.append(line).append("\n");
            }
            return responseString.toString();
        } else if (apiIsDown) {
            return getDummyData();
        }

        throw new IOException("There was an error with the connecting, got response code: " + httpURLConnection.getResponseCode());
    }

    private String getDummyData() {
        return "{\"response\":{\"status\":\"ok\",\"userTier\":\"developer\",\"total\":213,\"startIndex\":1,\"pageSize\":10,\"currentPage\":1,\"pages\":22,\"orderBy\":\"relevance\",\"results\":[{\"id\":\"film/2018/jan/28/12-strong-review-chris-hemsworth-doped-up-action\",\"type\":\"article\",\"sectionId\":\"film\",\"sectionName\":\"Film\",\"webPublicationDate\":\"2018-01-28T08:00:13Z\",\"webTitle\":\"12 Strong review – doped-up special forces action\",\"webUrl\":\"https://www.theguardian.com/film/2018/jan/28/12-strong-review-chris-hemsworth-doped-up-action\",\"apiUrl\":\"https://content.guardianapis.com/film/2018/jan/28/12-strong-review-chris-hemsworth-doped-up-action\",\"fields\":{\"headline\":\"12 Strong review – doped-up special forces action\",\"starRating\":\"2\",\"shortUrl\":\"https://gu.com/p/8xjaz\",\"thumbnail\":\"https://media.guim.co.uk/a272fe03d0d51f098c636aea60eb584a71a84ce1/0_142_2250_1350/500.jpg\"},\"tags\":[{\"id\":\"profile/wendy-ide\",\"type\":\"contributor\",\"sectionId\":\"film\",\"sectionName\":\"Film\",\"webTitle\":\"Wendy Ide\",\"webUrl\":\"https://www.theguardian.com/profile/wendy-ide\",\"apiUrl\":\"https://content.guardianapis.com/profile/wendy-ide\",\"references\":[],\"bylineImageUrl\":\"https://uploads.guim.co.uk/2018/01/17/Wendy-Ide.jpg\",\"bylineLargeImageUrl\":\"https://uploads.guim.co.uk/2018/01/17/Wendy_Ide,_L.png\",\"firstName\":\"Wendy\",\"lastName\":\"Ide\"}],\"isHosted\":false,\"pillarId\":\"pillar/arts\",\"pillarName\":\"Arts\"},{\"id\":\"music/2018/jan/14/eric-clapton-life-in-12-bars-review\",\"type\":\"article\",\"sectionId\":\"music\",\"sectionName\":\"Music\",\"webPublicationDate\":\"2018-01-14T08:00:22Z\",\"webTitle\":\"Eric Clapton: Life in 12 Bars review – mournful tonight\",\"webUrl\":\"https://www.theguardian.com/music/2018/jan/14/eric-clapton-life-in-12-bars-review\",\"apiUrl\":\"https://content.guardianapis.com/music/2018/jan/14/eric-clapton-life-in-12-bars-review\",\"fields\":{\"headline\":\"Eric Clapton: Life in 12 Bars review – mournful tonight\",\"starRating\":\"2\",\"shortUrl\":\"https://gu.com/p/7q8xm\",\"thumbnail\":\"https://media.guim.co.uk/23000f13bca9b49d1c7ce8ce50f5bc0991231605/0_206_5155_3095/500.jpg\"},\"tags\":[{\"id\":\"profile/wendy-ide\",\"type\":\"contributor\",\"sectionId\":\"film\",\"sectionName\":\"Film\",\"webTitle\":\"Wendy Ide\",\"webUrl\":\"https://www.theguardian.com/profile/wendy-ide\",\"apiUrl\":\"https://content.guardianapis.com/profile/wendy-ide\",\"references\":[],\"bylineImageUrl\":\"https://uploads.guim.co.uk/2018/01/17/Wendy-Ide.jpg\",\"bylineLargeImageUrl\":\"https://uploads.guim.co.uk/2018/01/17/Wendy_Ide,_L.png\",\"firstName\":\"Wendy\",\"lastName\":\"Ide\"}],\"isHosted\":false,\"pillarId\":\"pillar/arts\",\"pillarName\":\"Arts\"},{\"id\":\"film/2017/may/25/12-jours-review-raymond-depardon-documentary-psychiatric-hospital-judge\",\"type\":\"article\",\"sectionId\":\"film\",\"sectionName\":\"Film\",\"webPublicationDate\":\"2017-05-25T15:37:15Z\",\"webTitle\":\"12 Jours review – a devastating glimpse into broken souls\",\"webUrl\":\"https://www.theguardian.com/film/2017/may/25/12-jours-review-raymond-depardon-documentary-psychiatric-hospital-judge\",\"apiUrl\":\"https://content.guardianapis.com/film/2017/may/25/12-jours-review-raymond-depardon-documentary-psychiatric-hospital-judge\",\"fields\":{\"headline\":\"12 Jours review – a devastating glimpse into broken souls\",\"starRating\":\"4\",\"shortUrl\":\"https://gu.com/p/6g6hn\",\"thumbnail\":\"https://media.guim.co.uk/1dbf594e183ebe5428fe88c82784c55908b4753c/0_0_3598_2160/500.jpg\"},\"tags\":[{\"id\":\"profile/wendy-ide\",\"type\":\"contributor\",\"sectionId\":\"film\",\"sectionName\":\"Film\",\"webTitle\":\"Wendy Ide\",\"webUrl\":\"https://www.theguardian.com/profile/wendy-ide\",\"apiUrl\":\"https://content.guardianapis.com/profile/wendy-ide\",\"references\":[],\"bylineImageUrl\":\"https://uploads.guim.co.uk/2018/01/17/Wendy-Ide.jpg\",\"bylineLargeImageUrl\":\"https://uploads.guim.co.uk/2018/01/17/Wendy_Ide,_L.png\",\"firstName\":\"Wendy\",\"lastName\":\"Ide\"}],\"isHosted\":false,\"pillarId\":\"pillar/arts\",\"pillarName\":\"Arts\"},{\"id\":\"film/2018/jan/25/12-strong-review-chris-hemsworths-afghanistan\",\"type\":\"article\",\"sectionId\":\"film\",\"sectionName\":\"Film\",\"webPublicationDate\":\"2018-01-25T12:24:30Z\",\"webTitle\":\"12 Strong review – Chris Hemsworth's cavalry save the day in Afghanistan\",\"webUrl\":\"https://www.theguardian.com/film/2018/jan/25/12-strong-review-chris-hemsworths-afghanistan\",\"apiUrl\":\"https://content.guardianapis.com/film/2018/jan/25/12-strong-review-chris-hemsworths-afghanistan\",\"fields\":{\"headline\":\"12 Strong review – Chris Hemsworth's cavalry save the day in Afghanistan\",\"starRating\":\"2\",\"shortUrl\":\"https://gu.com/p/8xttn\",\"thumbnail\":\"https://media.guim.co.uk/56a1e8930837dcfc87e2be4fe5e8793113fbfaf5/0_0_1747_1048/500.jpg\"},\"tags\":[{\"id\":\"profile/peterbradshaw\",\"type\":\"contributor\",\"webTitle\":\"Peter Bradshaw\",\"webUrl\":\"https://www.theguardian.com/profile/peterbradshaw\",\"apiUrl\":\"https://content.guardianapis.com/profile/peterbradshaw\",\"references\":[],\"bio\":\"<p>Peter Bradshaw is the Guardian's film critic</p>\",\"bylineImageUrl\":\"https://uploads.guim.co.uk/2018/01/10/Peter-Bradshaw.jpg\",\"bylineLargeImageUrl\":\"https://uploads.guim.co.uk/2018/01/10/Peter_Bradshaw,_L.png\",\"firstName\":\"Peter\",\"lastName\":\"Bradshaw\",\"twitterHandle\":\"PeterBradshaw1\"}],\"isHosted\":false,\"pillarId\":\"pillar/arts\",\"pillarName\":\"Arts\"},{\"id\":\"film/2018/jan/10/eric-clapton-life-in-12-bars-review-documentary-lili-fini-zanuck\",\"type\":\"article\",\"sectionId\":\"film\",\"sectionName\":\"Film\",\"webPublicationDate\":\"2018-01-10T15:00:16Z\",\"webTitle\":\"Eric Clapton: Life in 12 Bars review – absorbing tribute to the blues legend\",\"webUrl\":\"https://www.theguardian.com/film/2018/jan/10/eric-clapton-life-in-12-bars-review-documentary-lili-fini-zanuck\",\"apiUrl\":\"https://content.guardianapis.com/film/2018/jan/10/eric-clapton-life-in-12-bars-review-documentary-lili-fini-zanuck\",\"fields\":{\"headline\":\"Eric Clapton: Life in 12 Bars review – absorbing tribute to the blues legend\",\"starRating\":\"4\",\"shortUrl\":\"https://gu.com/p/7q6mf\",\"thumbnail\":\"https://media.guim.co.uk/d6fd9ede00f675ec36e8311f17852bfc792069e8/110_94_2890_1734/500.jpg\"},\"tags\":[{\"id\":\"profile/peterbradshaw\",\"type\":\"contributor\",\"webTitle\":\"Peter Bradshaw\",\"webUrl\":\"https://www.theguardian.com/profile/peterbradshaw\",\"apiUrl\":\"https://content.guardianapis.com/profile/peterbradshaw\",\"references\":[],\"bio\":\"<p>Peter Bradshaw is the Guardian's film critic</p>\",\"bylineImageUrl\":\"https://uploads.guim.co.uk/2018/01/10/Peter-Bradshaw.jpg\",\"bylineLargeImageUrl\":\"https://uploads.guim.co.uk/2018/01/10/Peter_Bradshaw,_L.png\",\"firstName\":\"Peter\",\"lastName\":\"Bradshaw\",\"twitterHandle\":\"PeterBradshaw1\"}],\"isHosted\":false,\"pillarId\":\"pillar/arts\",\"pillarName\":\"Arts\"},{\"id\":\"film/2018/apr/13/even-when-i-fall-review-ex-circus-slaves-turn-the-tables-on-traffickers\",\"type\":\"article\",\"sectionId\":\"film\",\"sectionName\":\"Film\",\"webPublicationDate\":\"2018-04-13T08:00:32Z\",\"webTitle\":\"Even When I Fall review – ex-circus slaves turn the tables on traffickers\",\"webUrl\":\"https://www.theguardian.com/film/2018/apr/13/even-when-i-fall-review-ex-circus-slaves-turn-the-tables-on-traffickers\",\"apiUrl\":\"https://content.guardianapis.com/film/2018/apr/13/even-when-i-fall-review-ex-circus-slaves-turn-the-tables-on-traffickers\",\"fields\":{\"headline\":\"Even When I Fall review – ex-circus slaves turn the tables on traffickers\",\"starRating\":\"3\",\"shortUrl\":\"https://gu.com/p/8dheq\",\"thumbnail\":\"https://media.guim.co.uk/561ced75d6aa3c20cfd110fff66e45713ce32548/187_0_1727_1036/500.jpg\"},\"tags\":[{\"id\":\"profile/peterbradshaw\",\"type\":\"contributor\",\"webTitle\":\"Peter Bradshaw\",\"webUrl\":\"https://www.theguardian.com/profile/peterbradshaw\",\"apiUrl\":\"https://content.guardianapis.com/profile/peterbradshaw\",\"references\":[],\"bio\":\"<p>Peter Bradshaw is the Guardian's film critic</p>\",\"bylineImageUrl\":\"https://uploads.guim.co.uk/2018/01/10/Peter-Bradshaw.jpg\",\"bylineLargeImageUrl\":\"https://uploads.guim.co.uk/2018/01/10/Peter_Bradshaw,_L.png\",\"firstName\":\"Peter\",\"lastName\":\"Bradshaw\",\"twitterHandle\":\"PeterBradshaw1\"}],\"isHosted\":false,\"pillarId\":\"pillar/arts\",\"pillarName\":\"Arts\"},{\"id\":\"film/2018/may/03/lean-on-pete-review-horse-wild-west-andrew-haigh\",\"type\":\"article\",\"sectionId\":\"film\",\"sectionName\":\"Film\",\"webPublicationDate\":\"2018-05-03T14:00:43Z\",\"webTitle\":\"Lean on Pete review – beast and boy bond in the new wild west | Peter Bradshaw's film of the week\",\"webUrl\":\"https://www.theguardian.com/film/2018/may/03/lean-on-pete-review-horse-wild-west-andrew-haigh\",\"apiUrl\":\"https://content.guardianapis.com/film/2018/may/03/lean-on-pete-review-horse-wild-west-andrew-haigh\",\"fields\":{\"headline\":\"Lean on Pete review – beast and boy bond in the new wild west\",\"starRating\":\"4\",\"shortUrl\":\"https://gu.com/p/8hv6e\",\"thumbnail\":\"https://media.guim.co.uk/8d44792164946af539f8e834cbc38bb80abea549/0_192_5760_3456/500.jpg\"},\"tags\":[{\"id\":\"profile/peterbradshaw\",\"type\":\"contributor\",\"webTitle\":\"Peter Bradshaw\",\"webUrl\":\"https://www.theguardian.com/profile/peterbradshaw\",\"apiUrl\":\"https://content.guardianapis.com/profile/peterbradshaw\",\"references\":[],\"bio\":\"<p>Peter Bradshaw is the Guardian's film critic</p>\",\"bylineImageUrl\":\"https://uploads.guim.co.uk/2018/01/10/Peter-Bradshaw.jpg\",\"bylineLargeImageUrl\":\"https://uploads.guim.co.uk/2018/01/10/Peter_Bradshaw,_L.png\",\"firstName\":\"Peter\",\"lastName\":\"Bradshaw\",\"twitterHandle\":\"PeterBradshaw1\"}],\"isHosted\":false,\"pillarId\":\"pillar/arts\",\"pillarName\":\"Arts\"},{\"id\":\"film/2018/feb/22/finding-your-feet-review-celia-imrie-imelda-staunton-timothy-spall\",\"type\":\"article\",\"sectionId\":\"film\",\"sectionName\":\"Film\",\"webPublicationDate\":\"2018-02-22T06:00:27Z\",\"webTitle\":\"Finding Your Feet review – starry cast save creaky golden-years Britcom\",\"webUrl\":\"https://www.theguardian.com/film/2018/feb/22/finding-your-feet-review-celia-imrie-imelda-staunton-timothy-spall\",\"apiUrl\":\"https://content.guardianapis.com/film/2018/feb/22/finding-your-feet-review-celia-imrie-imelda-staunton-timothy-spall\",\"fields\":{\"headline\":\"Finding Your Feet review – starry cast save creaky golden-years Britcom\",\"starRating\":\"2\",\"shortUrl\":\"https://gu.com/p/85m2h\",\"thumbnail\":\"https://media.guim.co.uk/f9e17bf6b748292c1dc58e9040b2c6068632a65c/164_203_1192_715/500.jpg\"},\"tags\":[{\"id\":\"profile/peterbradshaw\",\"type\":\"contributor\",\"webTitle\":\"Peter Bradshaw\",\"webUrl\":\"https://www.theguardian.com/profile/peterbradshaw\",\"apiUrl\":\"https://content.guardianapis.com/profile/peterbradshaw\",\"references\":[],\"bio\":\"<p>Peter Bradshaw is the Guardian's film critic</p>\",\"bylineImageUrl\":\"https://uploads.guim.co.uk/2018/01/10/Peter-Bradshaw.jpg\",\"bylineLargeImageUrl\":\"https://uploads.guim.co.uk/2018/01/10/Peter_Bradshaw,_L.png\",\"firstName\":\"Peter\",\"lastName\":\"Bradshaw\",\"twitterHandle\":\"PeterBradshaw1\"}],\"isHosted\":false,\"pillarId\":\"pillar/arts\",\"pillarName\":\"Arts\"},{\"id\":\"film/2017/nov/03/bill-viola-the-road-to-st-pauls-review-video-art\",\"type\":\"article\",\"sectionId\":\"film\",\"sectionName\":\"Film\",\"webPublicationDate\":\"2017-11-03T08:00:18Z\",\"webTitle\":\"Bill Viola: The Road to St Paul's review – come all ye video art converts\",\"webUrl\":\"https://www.theguardian.com/film/2017/nov/03/bill-viola-the-road-to-st-pauls-review-video-art\",\"apiUrl\":\"https://content.guardianapis.com/film/2017/nov/03/bill-viola-the-road-to-st-pauls-review-video-art\",\"fields\":{\"headline\":\"Bill Viola: The Road to St Paul's review – come all ye video art converts\",\"starRating\":\"3\",\"shortUrl\":\"https://gu.com/p/7fm6p\",\"thumbnail\":\"https://media.guim.co.uk/76b2637752583043c20c121f811aa93a922bc8e9/338_203_1347_808/500.jpg\"},\"tags\":[{\"id\":\"profile/cathclarke\",\"type\":\"contributor\",\"webTitle\":\"Cath Clarke\",\"webUrl\":\"https://www.theguardian.com/profile/cathclarke\",\"apiUrl\":\"https://content.guardianapis.com/profile/cathclarke\",\"references\":[],\"firstName\":\"clarke\",\"lastName\":\"cath\"}],\"isHosted\":false,\"pillarId\":\"pillar/arts\",\"pillarName\":\"Arts\"},{\"id\":\"film/2017/sep/15/manhunt-review-john-woo-toronto-film-festival-tiff\",\"type\":\"article\",\"sectionId\":\"film\",\"sectionName\":\"Film\",\"webPublicationDate\":\"2017-09-15T16:10:47Z\",\"webTitle\":\"Manhunt review – John Woo rolls back the years with big pharma bullet-barrage\",\"webUrl\":\"https://www.theguardian.com/film/2017/sep/15/manhunt-review-john-woo-toronto-film-festival-tiff\",\"apiUrl\":\"https://content.guardianapis.com/film/2017/sep/15/manhunt-review-john-woo-toronto-film-festival-tiff\",\"fields\":{\"headline\":\"Manhunt review – John Woo rolls back the years with big pharma bullet-barrage\",\"starRating\":\"4\",\"shortUrl\":\"https://gu.com/p/784k4\",\"thumbnail\":\"https://media.guim.co.uk/a330b0bfbae64512bd780f5d2e56d4cb497ffc0e/158_0_1000_600/500.jpg\"},\"tags\":[{\"id\":\"profile/peterbradshaw\",\"type\":\"contributor\",\"webTitle\":\"Peter Bradshaw\",\"webUrl\":\"https://www.theguardian.com/profile/peterbradshaw\",\"apiUrl\":\"https://content.guardianapis.com/profile/peterbradshaw\",\"references\":[],\"bio\":\"<p>Peter Bradshaw is the Guardian's film critic</p>\",\"bylineImageUrl\":\"https://uploads.guim.co.uk/2018/01/10/Peter-Bradshaw.jpg\",\"bylineLargeImageUrl\":\"https://uploads.guim.co.uk/2018/01/10/Peter_Bradshaw,_L.png\",\"firstName\":\"Peter\",\"lastName\":\"Bradshaw\",\"twitterHandle\":\"PeterBradshaw1\"}],\"isHosted\":false,\"pillarId\":\"pillar/arts\",\"pillarName\":\"Arts\"}]}}";
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
