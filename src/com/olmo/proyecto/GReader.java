package com.olmo.proyecto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;

import com.olmo.proyecto.modelos.Feed;
import com.olmo.proyecto.modelos.FeedDB;
import com.olmo.proyecto.modelos.Noticia;
import com.olmo.proyecto.modelos.NoticiaDB;
import com.olmo.proyecto.modelos.Tag;
import com.olmo.proyecto.modelos.TagDB;

public class GReader {
	private static final String _AUTHPARAMS = "GoogleLogin auth=";
	private static final String _GOOGLE_LOGIN_URL = "https://www.google.com/accounts/ClientLogin";
	private static final String _READER_BASE_URL = "http://www.google.com/reader/";
	private static final String _API_URL = _READER_BASE_URL + "api/0/";
	private static final String _TOKEN_URL = _API_URL + "token";
	private static final String _USER_INFO_URL = _API_URL + "user-info";
	private static final String _USER_LABEL = "user/-/label/";
	private static final String _TAG_LIST_URL = _API_URL + "tag/list";
	private static final String _EDIT_TAG_URL = _API_URL + "tag/edit";
	private static final String _RENAME_TAG_URL = _API_URL + "rename-tag";
	private static final String _DISABLE_TAG_URL = _API_URL + "disable-tag";
	private static final String _SUBSCRIPTION_URL = _API_URL + "subscription/edit";
	private static final String _SUBSCRIPTION_LIST_URL = _API_URL + "subscription/list";
	private static final String _READING_LIST_URL = _READER_BASE_URL + "atom/user/-/state/com.google/reading-list";
	private static final String _MARCAR_LEIDO_URL = _READER_BASE_URL + "api/0/edit-tag";
	
	private Context context = null;
	
	public GReader(Context c){
		context = c;
	}

	/**
	 * Returns a Google Authentication Key Requires a Google Username and
	 * Password to be sent in the POST headers to
	 * http://www.google.com/accounts/ClientLogin
	 * 
	 * @param GoogleGoogle_Username
	 *            Google Username
	 * @param Google_Password
	 *            Google Password
	 * @return Google authorisation token
	 * @see getGoogleToken
	 */
	public static String getGoogleAuthKey(String _USERNAME, String _PASSWORD)
			throws UnsupportedEncodingException, IOException {
		Document doc = Jsoup
				.connect(_GOOGLE_LOGIN_URL)
				.data("accountType", "GOOGLE", "Email", _USERNAME, "Passwd",
						_PASSWORD, "service", "reader", "source", "Proyecto")
				.userAgent("Proyecto").timeout(4000).post();

		// RETRIEVES THE RESPONSE TEXT inc SID and AUTH. We only want the AUTH
		// key.
		String _AUTHKEY = doc
				.body()
				.text()
				.substring(doc.body().text().indexOf("Auth="),
						doc.body().text().length());
		_AUTHKEY = _AUTHKEY.replace("Auth=", "");
		return _AUTHKEY;
	}

	/**
	 * Returns a Google Token Requires a Google Username, Password and Auth key
	 * to be sent in the GET to http://www.google.com/reader/api/0/token
	 * 
	 * @param Google_Username
	 *            Google Username
	 * @param Google_Password
	 *            Google Password
	 * @return Google authorisation token
	 */
	public static String getGoogleToken() throws UnsupportedEncodingException, IOException {
		Document doc = Jsoup
				.connect(_TOKEN_URL)
				.header("Authorization",_AUTHPARAMS + ProyectoCursoActivity.googleAuthKey)
				.userAgent("Proyecto").timeout(4000).get();
		
		String _TOKEN = doc.body().text();
		return _TOKEN;
	}

	/**
	 * Returns Google Reader User Info Requires a Google Username, Password and
	 * AUTH key to be sent in the POST to
	 * http://www.google.com/reader/api/0/user-info
	 * 
	 * @param GoogleGoogle_Username
	 *            Google Username
	 * @param Google_Password
	 *            Google Password
	 * @return Google Reader User Info
	 * @see getGoogleToken
	 */
	/*
	 * public static String getUserInfo(String _USERNAME, String _PASSWORD)
	 * throws UnsupportedEncodingException, IOException { Document doc =
	 * Jsoup.connect(_USER_INFO_URL) .header("Authorization", _AUTHPARAMS +
	 * getGoogleAuthKey(_USERNAME,_PASSWORD)) .userAgent("Proyecto")
	 * .timeout(4000) .get();
	 * 
	 * // RETRIEVES THE RESPONSE USERINFO String _USERINFO = doc.body().text();
	 * return _USERINFO; }
	 */

	public static String getUserInfo() throws UnsupportedEncodingException, IOException {
		Document doc = Jsoup
				.connect(_USER_INFO_URL)
				.header("Authorization", _AUTHPARAMS + ProyectoCursoActivity.googleAuthKey)
				.userAgent("Proyecto").timeout(4000).get();

		String _USERINFO = doc.body().text();
		return _USERINFO;
	}

	/**
	 * Returns Google User ID Requires a Google Username and Password to be sent
	 * in the POST headers to http://www.google.com/accounts/ClientLogin
	 * 
	 * @return Google User ID
	 * @see getGoogleToken, getGoogleAuthKey
	 */
	/*
	 * public static String getGoogleUserID(String _USERNAME, String _PASSWORD)
	 * throws UnsupportedEncodingException, IOException { String _USERINFO =
	 * getUserInfo(_USERNAME, _PASSWORD); String _USERID = (String)
	 * _USERINFO.subSequence(11, 31); return _USERID; }
	 */
	public static String getGoogleUserID() throws UnsupportedEncodingException, IOException {
		String _USERINFO = getUserInfo();
		String _USERID = (String) _USERINFO.subSequence(11, 31);
		return _USERID;
	}

	public static void getNoticias(Context context) throws UnsupportedEncodingException, IOException {
		Document doc = Jsoup
				.connect(_READING_LIST_URL)
				.header("Authorization", _AUTHPARAMS + ProyectoCursoActivity.googleAuthKey)
				.userAgent("Proyecto").timeout(5000).get();

		Elements noticias = doc.getElementsByTag("entry");

		NoticiaDB db = new NoticiaDB(context);
		db.open();
		
		FeedDB feeddb = new FeedDB(context);
		feeddb.open();
		
		HashSet<String> all_noticias = db.getAllHashGid();

		for (Element noticia : noticias) {
			if(!all_noticias.contains(noticia.getElementsByTag("id").first().text())){
				Noticia item = new Noticia();
	
				item.setGid(noticia.getElementsByTag("id").first().text());
				item.setTitulo(noticia.getElementsByTag("title").first().text());
				item.setAutor(noticia.getElementsByTag("author").text());
				item.setContenido(noticia.getElementsByTag("summary").text());
				item.setUrl(noticia.getElementsByTag("link").first().attr("href"));
				item.setTimestamp(Long.valueOf(noticia.attr("gr:crawl-timestamp-msec")).longValue());
				item.setFeed(feeddb.getFeed(noticia.getElementsByTag("source").attr("gr:stream-id")));
	
				db.insertNoticia(item);
			}
		}

		feeddb.close();
		db.close();
	}

	public static void getTags(Context context) throws UnsupportedEncodingException, IOException {
		String _TAG_LABEL = null;
		try {
			_TAG_LABEL = "user/" + getGoogleUserID() + "/label/";
		} catch (IOException e) {
			e.printStackTrace();
		}

		Document doc = Jsoup
				.connect(_TAG_LIST_URL)
				.header("Authorization", _AUTHPARAMS + ProyectoCursoActivity.googleAuthKey)
				.userAgent("Proyecto").timeout(5000).get();

		Elements tags = doc.getElementsByTag("list").get(0).getElementsByTag("object");

		TagDB db = new TagDB(context);
		db.open();
		
		HashSet<String> all_tags = db.getAllHashGid();

		for (Element tag : tags) {
			if (Func_Strings.FindWordInString(tag.getElementsByAttributeValue("name", "id").text(),_TAG_LABEL)) {
				if(!all_tags.contains(tag.getElementsByTag("string").get(0).text())){
					Tag item = new Tag();
	
					item.setGid(tag.getElementsByTag("string").get(0).text());
					item.setShortid(tag.getElementsByTag("string").get(1).text());
					item.setNombre(tag.getElementsByTag("string").get(0).text().substring(32));
	
					db.insertTag(item);
				}
			}
		}

		db.close();
	}

	public static void getFeeds(Context context)
			throws UnsupportedEncodingException, IOException {
		
		Document doc = Jsoup
				.connect(_SUBSCRIPTION_LIST_URL)
				.header("Authorization", _AUTHPARAMS + ProyectoCursoActivity.googleAuthKey)
				.userAgent("Proyecto").timeout(5000).get();

		Elements feeds = doc.getElementsByTag("list").get(0).children();
		
		TagDB tagdb = new TagDB(context);
		tagdb.open();
		
		FeedDB db = new FeedDB(context);
		db.open();
		
		HashSet<String> all_feeds = db.getAllHashGid();
		
		for (Element feed : feeds) {
			if(!all_feeds.contains(feed.getElementsByAttributeValue("name", "id").first().text())){
				Feed item = new Feed();
				
				item.setGid(feed.getElementsByAttributeValue("name", "id").first().text());
				item.setNombre(feed.getElementsByAttributeValue("name", "title").text());
				item.setShortid(feed.getElementsByAttributeValue("name", "sortid").text());
				item.setWeb(feed.getElementsByAttributeValue("name", "htmlUrl").text());
				item.setFeed(item.getGid().substring(5));
				
				ArrayList<Tag> tags = new ArrayList<Tag>();
				Elements tags_elem = feed.getElementsByTag("list").get(0).getElementsByTag("object");
				
				for (Element tag_elem : tags_elem){
					tags.add(tagdb.getTag(tag_elem.getElementsByTag("string").first().text()));
				}
				item.setTags(tags);
				
				db.insertFeed(item);
			}
		}

		tagdb.close();
		db.close();
		
	}
	
	public void marcarLeido(int id){
		/*
		 * $req = POST $baseUrl, [
i => $guid, s => $url, a => 'user/-/state/com.google/read', ac => 'edit-tags', T => $token
];
		 */
		NoticiaDB db = new NoticiaDB(context);
		db.open();
		Noticia noticia = db.getNoticia(id);
		db.close();
		
		Map<String, String> datamap = new HashMap<String, String>();
		
		try{
			datamap.put("i", noticia.getGid());
			datamap.put("a", "user/-/state/com.google/read");
			datamap.put("ac", "edit-tags");
			datamap.put("T", GReader.getGoogleToken());
		}
		catch(IOException e){
			
		}
		
		try{	
			Jsoup
				.connect(_MARCAR_LEIDO_URL)
				.header("Authorization", _AUTHPARAMS + ProyectoCursoActivity.googleAuthKey)
				.userAgent("Proyecto").data(datamap).timeout(5000).post();
		}
		catch(IOException e){
			
		}
		
	}
}
