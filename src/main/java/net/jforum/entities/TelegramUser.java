/*
 * Copyright (c) JForum Team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following disclaimer.
 * 2) Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * Created on Feb 17, 2003 / 10:25:04 PM
 * The JForum Project
 * http://www.jforum.net 
 */
package net.jforum.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

public class TelegramUser implements Serializable
{
	private static final long serialVersionUID = -2945794235188356285L;
	private int id;
	private long chatId;
	private long userId;
	private String username;
	private String firstName;
	private String lastName;
	private Date lastVisit;
	private Date registrationDate;
	private String phoneNumber;
	private int forumUserId;
	
	public TelegramUser() {
	}

	public TelegramUser(int userId) {
		this.userId = userId;
	}
	
	public int getId() {
		return this.id;
	}

	public Date getLastVisit() {
		return this.lastVisit;
	}

	public String getRegistrationDate() 
	{
		SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		return df.format(this.registrationDate);
	}

	public String getUsername() {
		return this.username;
	}

	/**
	 * Sets the user id.
	 * 
	 * @param id The user id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the last visit time
	 * 
	 * @param lastVisit Last visit time, represented as a long value
	 */
	public void setLastVisit(Date lastVisit) {
		this.lastVisit = lastVisit;
	}

	/**
	 * Sets the registration date.
	 * 
	 * @param registrationDate The registration date to set
	 */
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	/**
	 * Sets the username.
	 * 
	 * @param username The username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public long getChatId() {
		return chatId;
	}

	public void setChatId(long chatId) {
		this.chatId = chatId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getForumUserId() {
		return forumUserId;
	}

	public void setForumUserId(int forumUserId) {
		this.forumUserId = forumUserId;
	}

}
