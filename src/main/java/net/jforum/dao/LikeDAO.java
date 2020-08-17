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
 * Created on Jan 11, 2005 11:00:06 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.jforum.entities.Like;
import net.jforum.entities.ReputationStatus;
import net.jforum.entities.User;

/**
 * @author Iulian Lungu
 * @version $Id$
 */
public interface LikeDAO
{
	/**
	 * Insert a new Like.
	 * 
	 * @param like The like to add. The instance should at
	 * least have set the like status, the user who is receiving
	 * the like and the user which is setting the like.
	 */
	void addLike(Like like) ;
	
	/**
	 * Gets the reputation status of some user.
	 * 
	 * @param userId The user id to get the reputation status
	 * @return A <code>net.jforum.entities.ReputationStatus</code> instance
	 */
	ReputationStatus getUserReputation(int userId) ;
	
	/**
	 * Updates the reputation status for some user. 
	 * This method will store the user's reputation in the
	 * users table. 
	 * 
	 * @param userId The id of the user to update
	 */
	void updateUserReputation(int userId) ;
	
	/**
	 * Checks if the user can add the like.
	 * The method will search for existing entries in
	 * the like table associated with the user id and post id
	 * passed as argument. If found, it means that the user 
	 * already has voted, so we cannot allow him to vote one
	 * more time.
	 * 
	 * @param userId The user id to check
	 * @param postId The post id to check
	 * @return <code>true</code> if the user hasn't voted on the
	 * post yet, or <code>false</code> otherwise. 
	 */
	boolean userCanAddLike(int userId, int postId) ;
	
	/**
	 * Gets the reputation status of some post.
	 * 
	 * @param postId The post id to get the reputation status
	 * @return A <code>net.jforum.entities.ReputationStatus</code> instance
	 */
	ReputationStatus getPostReputation(int postId) ;
	
	/**
     * Deletes the like belonging to some post.
     * This method will remove the post's like from the
     * like table.
     *
     * @param postId The id of the post to delete
     */
	void deletePostLike(int postId) ;
	
	/**
	 * Updates a Like
	 * @param like The like instance to update
	 */
	void update(Like like) ;
	
	/**
	 * Gets the votes the user made on some topic.
	 * @param topicId The topic id.
	 * @param userId 
	 * 
	 * @return A <code>java.util.Map</code>, where the key is the post id and the
	 * value id the rate made by the user.
	 */
	Map<Integer, Integer> getUserVotes(int topicId, int userId) ;
	
	/**
	 * @param user User
	 */
	void getUserTotalReputation(User user) ;
	
	
	/**
	 * Total points received, grouped by user and filtered by a range of dates.
	 * 
	 * @param firstPeriod Date
	 * @param lastPeriod Date
     * @param start int
     * @param orderField orderField
	 * @return Returns a List of users ant your total votes.
	 */
	List<User> getMostRatedUserByPeriod(int start, Date firstPeriod, Date lastPeriod, String orderField) ;
}
