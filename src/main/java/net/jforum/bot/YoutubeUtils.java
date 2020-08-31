package net.jforum.bot;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;

public class YoutubeUtils {

	private static final Logger LOGGER = Logger.getLogger(YoutubeUtils.class);

    private static final String YOUTUBE_URL_REGEX = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";

	private static final String API_KEY = "AIzaSyCAqb-w6Kg8_eJPKyP9JdU1oRG23I4Zcv0";
    
    private static YouTube youtube;
    
    public static VideoListResponse getVideoDetails(String videoId) throws GeneralSecurityException, IOException, GoogleJsonResponseException {
    	 youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
             public void initialize(HttpRequest request) throws IOException {
             }
         }).setApplicationName("xHumanity").build();

    	 YouTube.Videos.List request = youtube.videos().list(List.of("snippet","contentDetails","statistics"));
    	 request.setKey(API_KEY);
    	 
        VideoListResponse response = request.setId(List.of(videoId)).execute();
        return response;
    }
    
    public static String getVideoIdFromYoutubeUrl(String url) {
	    String videoId = null;
	    Pattern pattern = Pattern.compile(YOUTUBE_URL_REGEX, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(url);
	    if(matcher.find()){
	        videoId = matcher.group(1);
	    }
	    return videoId;
	}
    
    public static boolean isYoutubeLink(String url) {
    	return Pattern.matches(YOUTUBE_URL_REGEX, url);
    }
}