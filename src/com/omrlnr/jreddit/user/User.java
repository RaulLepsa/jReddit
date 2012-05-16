package com.omrlnr.jreddit.user;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.omrlnr.jreddit.Thing;
import com.omrlnr.jreddit.utils.Utils;

/**
 * This class represents a user connected to reddit.
 * 
 * @author <a href="http://www.omrlnr.com">Omer Elnour</a>
 */
public class User extends Thing {
	private String username, password;
	private String modhash, cookie;

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Call this function to connect the user. <br />
	 * By "connect" I mean effectively sending a POST request to reddit and
	 * getting the modhash and cookie, which are required for most reddit API
	 * functions.
	 * 
	 * @throws Exception
	 *             If connection fails.
	 */
	public void connect() throws Exception {
		ArrayList<String> hashCookiePair = hashCookiePair(username, password);
		this.modhash = hashCookiePair.get(0);
		this.cookie = hashCookiePair.get(1);
	}

	/**
	 * This functions returns true if this user has unread mail.
	 * 
	 * @return This user has mail or not
	 * @throws ParseException
	 *             If JSON parsing fails
	 * @throws IOException
	 *             If connection fails
	 */
	public boolean hasMail() throws IOException, ParseException {
		return Boolean.parseBoolean(info().get("has_mail").toString());
	}

	/**
	 * This function returns the Unix time that the user's account was created.
	 * 
	 * @return Unix time that the user's account was created
	 * @throws NumberFormatException
	 *             If the "created" property isn't a double
	 * @throws IOException
	 *             If connection fails
	 * @throws ParseException
	 *             If JSON parsing fails
	 */
	public double created() throws NumberFormatException, IOException,
			ParseException {
		return Double.parseDouble(info().get("created").toString());
	}

	/**
	 * This function returns the Unix time (in UTC/Coordinated Universal Time)
	 * that the user's account was created.
	 * 
	 * @return Unix time that the user's account was created in UTC
	 * @throws NumberFormatException
	 *             If the "created_utc" property isn't a double
	 * @throws IOException
	 *             If connection fails
	 * @throws ParseException
	 *             If JSON parsing fails
	 */
	public double createdUTC() throws NumberFormatException, IOException,
			ParseException {
		return Double.parseDouble(info().get("created_utc").toString());
	}

	/**
	 * This function returns the amount of link karma this user has. <br />
	 * Returns int because I doubt anyone has more than 2,147,483,647 link
	 * karma.
	 * 
	 * @return Link Karma
	 * @throws NumberFormatException
	 *             If the "link_karma" property isn't an inteher
	 * @throws IOException
	 *             If connection fails
	 * @throws ParseException
	 *             If JSON parsing fails
	 */
	public int linkKarma() throws NumberFormatException, IOException,
			ParseException {
		return Integer.parseInt(info().get("link_karma").toString());
	}

	/**
	 * This function returns the amount of comment karma this user has. <br />
	 * Returns int because I doubt anyone has more than 2,147,483,647 comment
	 * karma.
	 * 
	 * @return Comment Karma
	 * @throws NumberFormatException
	 *             If the "link_karma" property isn't an inteher
	 * @throws IOException
	 *             If connection fails
	 * @throws ParseException
	 *             If JSON parsing fails
	 */
	public int commentKarma() throws NumberFormatException, IOException,
			ParseException {
		return Integer.parseInt(info().get("comment_karma").toString());
	}

	/**
	 * This functions returns true if this user has a gold account.
	 * 
	 * @return This user has a gold account or not
	 * @throws ParseException
	 *             If JSON parsing fails
	 * @throws IOException
	 *             If connection fails
	 */
	public boolean isGold() throws IOException, ParseException {
		return Boolean.parseBoolean(info().get("is_gold").toString());
	}

	/**
	 * This functions returns true if this user is a reddit moderator
	 * (apparently this means a moderator of any subreddit).
	 * 
	 * @return This user is a moderator or not
	 * @throws ParseException
	 *             If JSON parsing fails
	 * @throws IOException
	 *             If connection fails
	 */
	public boolean isMod() throws IOException, ParseException {
		return Boolean.parseBoolean(info().get("is_mod").toString());
	}

	/**
	 * This function returns the user's ID. <br />
	 * The user's ID. This is only used internally, <b>right</b>?
	 * 
	 * @return This user's ID
	 * @throws IOException
	 *             If connection fails
	 * @throws ParseException
	 *             If JSON parsing fails
	 */
	public String id() throws IOException, ParseException {
		return info().get("id").toString();
	}

	/**
	 * This functions returns true if this user has unread moderator mail.
	 * 
	 * @return This user has unread moderator mail or not
	 * @throws ParseException
	 *             If JSON parsing fails
	 * @throws IOException
	 *             If connection fails
	 */
	public boolean hasModMail() throws IOException, ParseException {
		return Boolean.parseBoolean(info().get("has_mod_mail").toString());
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getModhash() {
		return modhash;
	}

	public String getCookie() {
		return cookie;
	}

	/**
	 * This function logs in to reddit and returns an ArrayList containing a
	 * modhash and cookie.
	 * 
	 * @param username
	 *            The username
	 * @param password
	 *            The password
	 * @return An array containing a modhash and cookie
	 * @throws IOException
	 *             If connection fails
	 * @throws ParseException
	 *             If parsing JSON fails
	 */
	private ArrayList<String> hashCookiePair(String username, String password)
			throws IOException, ParseException {
		ArrayList<String> values = new ArrayList<String>();
		JSONObject jsonObject = Utils.post("api_type=json&user=" + username
				+ "&passwd=" + password, new URL(
				"http://www.reddit.com/api/login/" + username), getCookie());
		JSONObject valuePair = (JSONObject) ((JSONObject) jsonObject
				.get("json")).get("data");

		values.add(valuePair.get("modhash").toString());
		values.add(valuePair.get("cookie").toString());

		return values;
	}

	/**
	 * This function returns a "response" (me.json) JSON data containing info
	 * about the user. <br />
	 * 
	 * @return JSON data containing info about the user
	 */
	private JSONObject info() throws IOException, ParseException {
		if (cookie == null || modhash == null) {
			System.err
					.printf("Please invoke the \"connect\" function before attempting to call any other API functions.");
			Runtime.getRuntime().exit(-1);
		}

		JSONObject jsonObject = Utils.get("", new URL(
				"http://www.reddit.com/api/me.json"), getCookie());
		return (JSONObject) jsonObject.get("data");
	}
}