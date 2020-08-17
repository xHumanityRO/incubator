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
 * Created on Jan 11, 2005 11:05:57 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.entities;

import java.io.Serializable;

/**
 * @author Iulian Lungu
 * @version $Id$
 */
public class ReputationStatus implements Serializable
{
	private static final long serialVersionUID = 1264905836781253310L;

	private int id;
	
	/**
	 * Reputation. Total points received.
	 */
	private double reputationPoints;
	
	/**
	 * Sum of all votes received.
	 */
	private int totalPoints;
	
	/**
	 * Sum of all likes received.
	 */
	private int totalLikes;
	
	/**
	 * Sum of all dislikes received.
	 */
	private int totalDislikes;
	
	/**
	 * Number of votes received.
	 */
	private int votesReceived;
	
	/**
	 * Number of votes given to other users.
	 */
	private int votesGiven;
	
	
	public ReputationStatus() {
		// Empty Constructor
	}
	
	public ReputationStatus(final ReputationStatus reputation)
	{
		if (reputation != null) {
			this.id = reputation.getId();
			this.reputationPoints = reputation.getReputationPoints();
		}
	}
	
	public ReputationStatus(final int id, final double points)
	{
		this.id = id;
		this.reputationPoints = points;
	}
	
	/**
	 * @return Returns the reputationPoints.
	 */
	public double getReputationPoints()
	{
		return this.reputationPoints;
	}
	
	public void setReputationPoints(final double points)
	{
		this.reputationPoints = points;
	}
	
	/**
	 * @return Returns the userId.
	 */
	public int getId()
	{
		return this.id;
	}
	
	/**
	 * @param userId The userId to set.
	 */
	public void setId(final int userId)
	{
		this.id = userId;
	}
	
    public int getVotesReceived()
    {
        return votesReceived;
    }
    
    public void setVotesReceived(final int votesReceived)
    {
        this.votesReceived = votesReceived;
    }
    
    public int getTotalPoints()
    {
        return totalPoints;
    }
    
    public void setTotalPoints(final int totalPoints)
    {
        this.totalPoints = totalPoints;
    }
    
    public int getTotalLikes() {
		return totalLikes;
	}

	public void setTotalLikes(int totalLikes) {
		this.totalLikes = totalLikes;
	}

	public int getTotalDislikes() {
		return totalDislikes;
	}

	public void setTotalDislikes(int totalDislikes) {
		this.totalDislikes = totalDislikes;
	}

	public int getVotesGiven()
    {
        return votesGiven;
    }
    public void setVotesGiven(final int votesGiven)
    {
        this.votesGiven = votesGiven;
    }

	public void addToTotalLikesOrDislikes(int points) {
		if (points > 0) {
			this.totalLikes += points;
		} else if (points < 0) {
			this.totalDislikes += points;
		}
		
	}

	public void addReputationPoints(int points) {
		this.reputationPoints += points;
		
	}
}
