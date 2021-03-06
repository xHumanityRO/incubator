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
 * Created on Feb 23, 2003 / 2:56:58 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.search.SearchArgs;
import net.jforum.search.SearchResult;

/**
 * Model interface for {@link net.jforum.entities.Topic}.
 * This interface defines methods which are expected to be
 * implemented by a specific data access driver. The intention is
 * to provide all functionality needed to update, insert, delete and
 * select some specific data.
 *
 * @author Rafael Steil
 * @version $Id$
 */
public interface TopicDAO 
{
	/**
	 * Fixes the fields <i>topic_first_post_id</i> and 
	 * <i>topic_last_post_id</i>.
	 * 
	 * @param topicId The topic id to fix
	 */
	void fixFirstLastPostId(int topicId) ;
	
	/**
	 * Gets a specific <code>Topic</code>.
	 * 
	 * @param topicId The Topic ID to search
	 * @return <code>Topic</code>object containing all the information
	 * @see #selectAllByForum(int forumId)
	 */
	Topic selectById(int topicId) ;
	
	/**
	 * Gets a topic's information from the topics table only.
	 * No other information, like usernames, are fetched. 
	 * 
	 * @param topicId The topic id to get
	 * @return A topic instance
	 */
	Topic selectRaw(int topicId) ;
	
	/**
	 * Selects all topics associated to a specific forum
	 * 
	 * @param forumId The forum id to select the topics
	 * @return <code>ArrayList</code> with all topics found. Each entry is a <code>net.jforum.Topic</code> object
	 */
	List<Topic> selectAllByForum(int forumId) ;
	
	List<Map<String, Object>> selectTopicTitlesByIds(Collection<?> idList) ;
	
	/**
	 * Selects all topics associated to a specific forum, limiting the total number
	 * of records returned.
	 * 
	 * @param forumId The forum id to select the topics
	 * @return <code>ArrayList</code> with all topics found. Each entry is a <code>net.jforum.Topic</code> object
     * @param startFrom int
     * @param count int
	 */
	List<Topic> selectAllByForumByLimit(int forumId, int startFrom, int count) ;

    /**
     * Selects all topics associated to a specific user and belonging to
     * given forums
     * @param userId int User ID.
     * @param startFrom int
     * @param count int
     * @return  List
     */
	List<Topic> selectByUserByLimit(int userId,int startFrom, int count) ;

	/**
	 * How many topics were created by a given user
	 * @param userId the user id to check
	 * @return the number of topics created by the user
	 */
	int countUserTopics(int userId) ;
	
	/**
	 * How many topics were created
	 * @return the number of topics created
	 */
	int countAllTopics() ;

	/**
	 * Delete a Topic.
	 * 
	 * @param topic The Topic ID to delete
	 * @param fromModeration boolean
	 */
	void delete(Topic topic, boolean fromModeration) ;
	
	/**
	 * Deletes a set of topics
	 * @param topics The topics to delete. Each entry must be
	 * an instance of net.jforum.entities.Topic
	 * @param fromModeration boolean
	 */
	void deleteTopics(List<Topic> topics, boolean fromModeration) ;
	
	/**
	 * Deletes all topics from a forum
	 * @param forumId int
	 */
	void deleteByForum(int forumId) ;
	
	/**
	 * Updates a Topic.
	 * 
	 * @param topic Reference to a <code>Topic</code> object to update
	 */
	void update(Topic topic) ;
	
	/**
	 * Adds a new Topic.
	 * 
	 * @param topic Reference to a valid and configured <code>Topic</code> object
	 * @return The new ID
	 */
	int addNew(Topic topic) ;
	
	/**
	 * Increments the number of times the topic was saw
	 * 
	 * @param topicId The topic ID to increment the total number of views
	 */
	void incrementTotalViews(int topicId) ;
	
	/**
	 * Increments the number of replies the topic has
	 * 
	 * @param topicId The topic ID to increment the total number of replies
	 */
	void incrementTotalReplies(int topicId) ;

	/**
	 * Decrements the number of replies the topic has
	 * 
	 * @param topicId The topic ID to decrement the total number of replies
	 */
	void decrementTotalReplies(int topicId) ;
	
	/**
	 * Sets the ID of the last post of the topic
	 * 
	 * @param topicId Topic ID
	 * @param postId Post ID
	 */
	void setLastPostId(int topicId, int postId) ;
	
	/**
	 * Gets the last post id associated to the topic
	 * 
	 * @param topicId The topic id
     * @return int
	 */
	int getMaxPostId(int topicId) ;
	
	/**
	 * Gets the number of posts the topic has.
	 * 
	 * @param topicId The topic id
	 * @return The number of posts
	 */
	int getTotalPosts(int topicId) ;
	
	/**
	 * Get the users to notify
	 * 
	 * @param topic The topic 
	 * @return <code>ArrayList</code> of <code>User</code> objects. Each
	 * entry is a user who will receive the topic answer notification
	 * */
	List<User> notifyUsers(Topic topic) ;
	
	/**
	 * Subscribe a set of users for notification of new post in the topic
	 * @param topicId the topic id
	 * @param users the relation of {@link User} instances to subscribe
	 */
	void subscribeUsers(int topicId, List<User> users);
	
	/**
	 * Subscribe the user for notification of new post in the topic
	 *  
	 * @param topicId The topic id
	 * @param userId The user id
	 */
	void subscribeUser(int topicId, int userId) ;
	
	/**
	 * Return the subscription status of the user on the topic.
	 * 
	 * @param topicId The topic id
	 * @param userId The user id
	 * @return true if the user is waiting notification on the topic
	 */
	boolean isUserSubscribed(int topicId, int userId) ;
	
	/**
	 * Remove the user's subscription of the topic
	 * 
	 * @param topicId The topic id
	 * @param userId the User id
	 */
	void removeSubscription(int topicId, int userId) ;
	
	/**
	 * Clean all subscriptions of some topic
	 * 
	 * @param topicId The topic id
	 */
	void removeSubscriptionByTopic(int topicId) ;
	
	/**
	 * Change the topic read status 
	 * 
	 * @param topicId The topic id
	 * @param userId The user id
	 * @param read <code>true</code> or <code>false</code>
	 */
	void updateReadStatus(int topicId, int userId, boolean read) ;
	
	/**
	 * Lock or unlock a topic. 
	 * 
	 * @param topicId The topic id to perform the action on
	 * @param status Use <code>Topic.STATUS_LOCKED</code> to lock the topic, or
	 * <code>Topic.STATUS_UNLOCKED</code> to unlock. 
	 */
	void lockUnlock(int[] topicId, int status) ;

	/**
	 * Selects recent topics 
	 *
	 * @param limit The number of topics to retrieve
     * @return List
	 */
	List<Topic> selectRecentTopics (int limit) ;

	/**
	 * Selects recent topics 
	 *
     * @param start the index of topics where to start
	 * @param limit The number of topics to retrieve
     * @return List
	 */
	List<Topic> selectRecentTopics (int start, int limit) ;

	/**
	 * Selects hottest topics
	 *
	 * @param limit The number of topics to retrieve
	    * @return List
	 */
	List<Topic> selectHottestTopics (int limit) ;
	
	/**
	 * Sets the ID of the first post of the topic
	 * 
	 * @param topicId Topic ID
	 * @param postId Post ID
	 */
	void setFirstPostId(int topicId, int postId) ;

	/**
	 * Gets the first post id associated to the topic
	 * 
	 * @param topicId The topic id
     * @return int
	 */
	int getMinPostId(int topicId) ;
	
	/**
	 * Sets the moderation flag for all topics of a given forum.
	 * 
	 * @param forumId The forum id
	 * @param status boolean
	 */
	void setModerationStatus(int forumId, boolean status) ;

	/**
	 * Sets the moderation flag for a given topic.
	 * 
	 * @param topicId The topic id
	 * @param status boolean
	 */
	void setModerationStatusByTopic(int topicId, boolean status) ;

	/**
	 * Get all unique posters of some topic
	 * @param topicId int
	 * @return A Map instance with all topic posts. Key is the userid, 
	 * value is a {@link net.jforum.entities.User} instance with minimum
	 * data filled
	 */
	Map<Integer, User> topicPosters(int topicId) ;
	
	/**
	 * @param args
	 * @return SearchResult
	 */
	SearchResult<Topic> findTopicsByDateRange(SearchArgs args) ;

	/**
	 * Returns all topics that are watched by a given user.
	 * @param userId The user id
	 */
	List<Map<String, Object>> selectWatchesByUser(int userId);

}
