/*
 * Copyright (c)Rafael Steil
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
 * Created on Feb 23, 2003 / 1:02:01 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.entities;

import java.io.Serializable;
import java.util.Date;

import net.jforum.view.forum.common.ViewCommon;

/**
 * Represents every message post in the system.
 * 
 * @author Rafael Steil
 */
public class Post implements Serializable 
{
	private static final long serialVersionUID = -6067049478602005132L;
	private int id;
	private int topicId;
	private int forumId;
	private int userId;
	private Date time;
	private String text;
	private String subject;
	private String postUsername;
	private boolean bbCodeEnabled = true;
	private boolean htmlEnabled = true;
	private boolean smiliesEnabled = true;
	private boolean signatureEnabled = true;
	private Date editTime;
	private int editCount;
	private String userIp;
	private boolean canEdit;
	private KarmaStatus karma;
	private ReputationStatus reputation;
	private boolean hasAttachments;
	private boolean moderate;
	private int topicType;

	public Post() { }
	
	public Post(int postId)
	{
		this.id = postId;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param post The Post to make a copy from
	 */
	public Post(Post post)
	{
		this.bbCodeEnabled = post.isBbCodeEnabled();
		this.canEdit = post.isCanEdit();
		this.editCount = post.getEditCount();
		this.editTime = post.getEditTime();
		this.forumId = post.getForumId();
		this.htmlEnabled = post.isHtmlEnabled();
		this.id = post.getId();
		this.postUsername = post.getPostUsername();
		this.signatureEnabled = post.isSignatureEnabled();
		this.smiliesEnabled = post.isSmiliesEnabled();
		this.subject = post.getSubject();
		this.text = post.getText();
		this.time = post.getTime();
		this.topicId = post.getTopicId();
		this.userId = post.getUserId();
		this.userIp = post.getUserIp();
		this.karma = (new KarmaStatus(post.getKarma()));
		this.reputation = (new ReputationStatus(post.getReputation()));
		this.moderate = post.isModerationNeeded();
		this.hasAttachments = post.hasAttachments();
	}
	
	public void setModerate(boolean status)
	{
		this.moderate = status;
	}
	
	public boolean isModerate()
	{
		return this.isModerationNeeded();
	}
	
	public boolean isModerationNeeded()
	{
		return this.moderate;
	}
	
	public KarmaStatus getKarma()
	{
		return this.karma;
	}
	
	public void setKarma(KarmaStatus karma)
	{
		this.karma = karma;
	}
	
	public ReputationStatus getReputation() {
		return reputation;
	}

	public void setReputation(ReputationStatus reputation) {
		this.reputation = reputation;
	}

	/**
	 * Checks if the BB code is enabled
	 * 
	 * @return boolean value representing the result
	 */
	public boolean isBbCodeEnabled() {
		return this.bbCodeEnabled;
	}

	/**
	 * Gets the total number of times the post was edited
	 * 
	 * @return int value with the total number of times the post was edited
	 */
	public int getEditCount() {
		return this.editCount;
	}

	/**
	 * Gets the edit time of the post
	 * 
	 * @return long value representing the time
	 */
	public Date getEditTime() {
		return this.editTime;
	}

	/**
	* Gets formated formatted post edit time
	* 
	* @return String value representing the time
	*/
	public String getFormattedEditTime() {
		String result = "";
		if (this.editTime != null) {
			result = ViewCommon.formatDate(this.editTime);
		}
		return result;
	}

	/**
	 * Gets the forum's id the post is associated
	 * 
	 * @return int value with the id of the forum
	 */
	public int getForumId() {
		return this.forumId;
	}

	/**
	 * Checks if HTML is enabled in the topic
	 * 
	 * @return boolean value representing the result
	 */
	public boolean isHtmlEnabled() {
		return this.htmlEnabled;
	}

	/**
	 * Gets the ID of the post
	 * 
	 * @return int value with the ID
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Gets the username of the user ( an anonymous user ) that have posted the message
	 * 
	 * @return String with the username
	 */
	public String getPostUsername() {
		return this.postUsername;
	}

	/**
	 * Checks if signature is allowed in the message
	 * 
	 * @return boolean representing the result
	 */
	public boolean isSignatureEnabled() {
		return this.signatureEnabled;
	}

	/**
	 * Checks if smart Smilies are enabled :)
	 * 
	 * @return boolean representing the result
	 */
	public boolean isSmiliesEnabled() {
		return this.smiliesEnabled;
	}

	/**
	 * Gets the time, represented as long, of the message post
	 * 
	 * @return long representing the post time
	 */
	public Date getTime() {
		return this.time;
	}

	/**
	* Gets formated formatted post time
	* 
	* @return String value representing the time
	*/
	public String getFormattedTime() {
		String result = "";
		if (this.time != null) {
			result = ViewCommon.formatDate(this.time);
		}
		return result;
	}

	/**
	 * Gets the id of the topic this message is associated
	 * 
	 * @return int value with the topic id
	 */
	public int getTopicId() {
		return this.topicId;
	}

	/**
	 * Gets the ID of the user that have posted the message
	 * 
	 * @return int value with the user id
	 */
	public int getUserId() {
		return this.userId;
	}

	public int getTopicType() {
		return this.topicType;
	}

	/**
	 * Gets the IP of the user who have posted the message
	 * 
	 * @return String value with the user IP
	 */
	public String getUserIp() {
		return this.userIp;
	}
	/**
	 * Sets the status for BB code in the message
	 * 
	 * @param bbCodeEnabled <code>true</code> or <code>false</code>, depending the intention
	 */
	public void setBbCodeEnabled(boolean bbCodeEnabled) {
		this.bbCodeEnabled = bbCodeEnabled;
	}

	/**
	 * Sets the count times the message was edited
	 * 
	 * @param editCount The count time
	 */
	public void setEditCount(int editCount) {
		this.editCount = editCount;
	}

	/**
	 * Sets the edit time the message was last edited
	 * 
	 * @param editTime long value representing the time
	 */
	public void setEditTime(Date editTime) {
		this.editTime = editTime;
	}

	/**
	 * Sets the id of the forum this message belongs to
	 * 
	 * @param forumId The forum's id
	 */
	public void setForumId(int forumId) {
		this.forumId = forumId;
	}

	/**
	 * Sets the status for HTML code in the message
	 * 
	 * @param htmlEnabled <code>true</code> or <code>false</code>, depending the intention
	 */
	public void setHtmlEnabled(boolean htmlEnabled) {
		this.htmlEnabled = htmlEnabled;
	}

	/**
	 * Sets the id for the message
	 * 
	 * @param id The id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the username of the anonymous user that have sent the message
	 * 
	 * @param postUsername String with the username
	 */
	public void setPostUsername(String postUsername) {
		this.postUsername = postUsername;
	}

	/**
	 * Sets the status for signatures in the message
	 * 
	 * @param signatureEnabled <code>true</code> or <code>false</code>, depending the intention
	 */
	public void setSignatureEnabled(boolean signatureEnabled) {
		this.signatureEnabled = signatureEnabled;
	}

	/**
	 * Sets the status for smilies in the message
	 * 
	 * @param smiliesEnabled <code>true</code> or <code>false</code>, depending the intention
	 */
	public void setSmiliesEnabled(boolean smiliesEnabled) {
		this.smiliesEnabled = smiliesEnabled;
	}

	/**
	 * Sets the time the message was sent
	 * 
	 * @param time The time 
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * Sets the id of the topic that the message belongs to
	 * 
	 * @param topicId The id of the topic
	 */
	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	/**
	 * Sets the id of the user that sent the message
	 * 
	 * @param userId The user Id
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setTopicType(int topicType) {
		this.topicType = topicType;
	}
	
	/**
	 * Gets the message of the post
	 * 
	 * @return String containing the text
	 */
	public String getText() {
		return this.text;
	}

    /**
     * Post processing for HTML differences in mobile view. In particular, if
     * the post starts/ends with a quote, the mobile view needs a space to display the background.
     *
     * @return processed HTML string
     */
    public String getMobileText() {
        String result = getText();
        result = result.replaceFirst("^\\s*<blockquote", "&nbsp;<blockquote");
        result = result.replaceFirst("</blockquote>$", "</blockquote>&nbsp;");
        return result;
    }

	/**
	 * Sets the text of the post
	 * 
	 * @param text The text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Gets the subject of the post 
	 * 
	 * @return String with the subject
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * Sets the subject for the message
	 * 
	 * @param subject The subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Sets the IP of the user
	 * 
	 * @param userIp The IP address of the user
	 */
	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}
	public boolean isCanEdit() {
		return this.canEdit;
	}
	
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	
	/**
	 * @return Returns the hasAttachments.
	 */
	public boolean hasAttachments()
	{
		return this.hasAttachments;
	}
	
	/**
	 * @param hasAttachments The hasAttachments to set.
	 */
	public void hasAttachments(boolean hasAttachments)
	{
		this.hasAttachments = hasAttachments;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override public boolean equals(Object o)
	{
		if (!(o instanceof Post)) {
			return false;
		}
		
		return ((Post)o).getId() == this.id;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override public int hashCode()
	{
		return this.id;
	}
}
