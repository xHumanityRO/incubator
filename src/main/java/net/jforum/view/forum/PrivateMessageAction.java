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
 * Created on 20/05/2004 - 21:05:45
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.view.forum;

import java.util.List;

import net.jforum.Command;
import net.jforum.SessionFacade;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.PrivateMessageDAO;
import net.jforum.dao.UserDAO;
import net.jforum.entities.Post;
import net.jforum.entities.PrivateMessage;
import net.jforum.entities.PrivateMessageType;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.SmiliesRepository;
import net.jforum.util.I18n;
import net.jforum.util.SafeHtml;
import net.jforum.util.concurrent.Executor;
import net.jforum.util.mail.EmailSenderTask;
import net.jforum.util.mail.PrivateMessageSpammer;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.TemplateKeys;
import net.jforum.util.stats.Stats;
import net.jforum.util.stats.StatsEvent;
import net.jforum.view.forum.common.PostCommon;
import net.jforum.view.forum.common.ViewCommon;

/**
 * @author Rafael Steil
 */
public class PrivateMessageAction extends Command
{
	public void inbox()
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}
		
		User user = new User();
		user.setId(SessionFacade.getUserSession().getUserId());

 		int postsPerPage = SystemGlobals.getIntValue(ConfigKeys.POSTS_PER_PAGE);
 		int start = ViewCommon.getStartPage();
 		
 		PrivateMessageDAO pmdao = DataAccessDriver.getInstance().newPrivateMessageDAO();
 		
 		int totalMessages = pmdao.getTotalInbox(user.getId());
 		
 		List<PrivateMessage> pmList = pmdao.selectFromInbox(user.getId(), start, postsPerPage);

		this.setTemplateName(TemplateKeys.PM_INBOX);
		this.context.put("inbox", true);
		this.context.put("pmList", pmList);
		this.context.put("pageTitle", I18n.getMessage("ForumBase.privateMessages")+" "+I18n.getMessage("PrivateMessage.inbox"));
		this.putTypes();		
 
 		ViewCommon.contextToPagination(start, totalMessages, postsPerPage);
 		this.context.put("postsPerPage", Integer.valueOf(postsPerPage));

		new StatsEvent("PM inbox page", request.getRequestURL()).record();
	}
	
	public void sentbox()
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}
		
		User user = new User();
		user.setId(SessionFacade.getUserSession().getUserId());
		
		int postsPerPage = SystemGlobals.getIntValue(ConfigKeys.POSTS_PER_PAGE);
		int start = ViewCommon.getStartPage();
		
		PrivateMessageDAO pmdao = DataAccessDriver.getInstance().newPrivateMessageDAO();
		
		int totalMessages = pmdao.getTotalSent(user.getId());
		
		List<PrivateMessage> pmList = DataAccessDriver.getInstance().newPrivateMessageDAO().selectFromSent(user.getId(), start, postsPerPage);

		this.context.put("sentbox", true);
		this.context.put("pmList", pmList);
		this.setTemplateName(TemplateKeys.PM_SENTBOX);
		this.context.put("pageTitle", I18n.getMessage("ForumBase.privateMessages")+" "+I18n.getMessage("PrivateMessage.sentbox"));
		this.putTypes();

		ViewCommon.contextToPagination(start, totalMessages, postsPerPage);
		this.context.put("postsPerPage", Integer.valueOf(postsPerPage));

		new StatsEvent("PM sent page", request.getRequestURL()).record();
	}

	private void putTypes()
	{
		this.context.put("NEW", Integer.valueOf(PrivateMessageType.NEW));
		this.context.put("READ", Integer.valueOf(PrivateMessageType.READ));
		this.context.put("UNREAD", Integer.valueOf(PrivateMessageType.UNREAD));
	}

	public void send() 
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}

		User user = DataAccessDriver.getInstance().newUserDAO().selectById(SessionFacade.getUserSession().getUserId());

		ViewCommon.prepareUserSignature(user);

		this.sendFormCommon(user);
	}

	public void sendTo()
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}
		
		User user = DataAccessDriver.getInstance().newUserDAO().selectById(SessionFacade.getUserSession().getUserId());

		int userId = this.request.getIntParameter("user_id");

		if (userId > 0){
			User recipient = DataAccessDriver.getInstance().newUserDAO().selectById(userId);

			this.context.put("pmRecipient", recipient);
			this.context.put("toUserId", Integer.valueOf(recipient.getId()));
			this.context.put("toUsername", recipient.getUsername());
			this.context.put("pageTitle", I18n.getMessage("PrivateMessage.title") 
				+ " " + I18n.getMessage("PrivateMessage.to") 
				+ " " + recipient.getUsername());
		}

		this.sendFormCommon(user);
	}
	
	private void sendFormCommon(User user)
	{
		this.setTemplateName(TemplateKeys.PM_SENDFORM);
		
		this.context.put("user", user);
		this.context.put("moduleName", "pm");
		this.context.put("action", "sendSave");
		this.context.put("htmlAllowed", true);
		this.context.put("attachmentsEnabled", false);
		this.context.put("maxAttachments", SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_MAX_POST));
		this.context.put("maxAttachmentsSize", Integer.valueOf(0));
		this.context.put("moderationLoggingEnabled", false);
		this.context.put("smilies", SmiliesRepository.getSmilies());
	}
	
	public void sendSave()
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}
		
		UserDAO userDao = DataAccessDriver.getInstance().newUserDAO();
		
		String toUserIdStr = this.request.getParameter("toUserId");
		String toUsername = this.request.getParameter("toUsername");

		int toUserId = -1;
		
		// If we don't have a user id, then probably the user
		// inserted the username by hand in the form's field
		if (toUserIdStr == null || "".equals(toUserIdStr.trim())) {
			List<User> list = userDao.findByName(toUsername, true);
			
			if (!list.isEmpty()) {
				User user = list.get(0);
				toUserId = user.getId();
			}
		}
		else {
			toUserId = Integer.parseInt(toUserIdStr);
		}
		
		// We failed to get the user id?
		if (toUserId == -1) {
			this.setTemplateName(TemplateKeys.PM_SENDSAVE_USER_NOTFOUND);
			this.context.put("message", I18n.getMessage("PrivateMessage.userIdNotFound"));
			return;
		}

		PrivateMessage pm = new PrivateMessage();
		pm.setPost(PostCommon.fillPostFromRequest());

		// Sender
		User fromUser = new User();
		fromUser.setId(SessionFacade.getUserSession().getUserId());
		pm.setFromUser(fromUser);

		// Recipient
		User toUser = userDao.selectById(toUserId);
		pm.setToUser(toUser);

		boolean preview = "1".equals(this.request.getParameter("preview"));

		if (preview) {
			this.context.put("preview", true);
			this.context.put("post", pm.getPost());
			
			Post postPreview = new Post(pm.getPost());
			this.context.put("postPreview", PostCommon.preparePostForDisplay(postPreview));
			this.context.put("pm", pm);

			this.send();			
		}
		else {
			// Check the elapsed time since the last post (or private message) from the user
			int delay = SystemGlobals.getIntValue(ConfigKeys.POSTS_NEW_DELAY);

			if (delay > 0) {
				Long lastPostTime = (Long) SessionFacade.getAttribute(ConfigKeys.LAST_POST_TIME);

				if (lastPostTime != null && (System.currentTimeMillis() < (lastPostTime.longValue() + delay))) {
					this.setTemplateName(TemplateKeys.PM_SENDSAVE_USER_NOTFOUND);
					this.context.put("message", I18n.getMessage("PostForm.tooSoon"));
					return;
				}
			}

            DataAccessDriver.getInstance().newPrivateMessageDAO().send(pm);

			this.setTemplateName(TemplateKeys.PM_SENDSAVE);
			this.context.put("message", I18n.getMessage("PrivateMessage.messageSent", 
				new String[] { this.request.getContextPath() 
					+ "/pm/inbox"
					+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION)}));

			// If the target user is in the forum, then increment his private message count
			UserSession.changeOnUser(toUser.getId(), us -> us.setPrivateMessages(us.getPrivateMessages() + 1));

			if (toUser.getEmail() != null 
				&& toUser.getEmail().trim().length() > 0
				&& SystemGlobals.getBoolValue(ConfigKeys.MAIL_NOTIFY_ANSWERS)) {
				Executor.execute(new EmailSenderTask(new PrivateMessageSpammer(toUser)));
			}

			new StatsEvent(Stats.ForbidDetailDisplay.SENT_PMS.toString(),
					"From " + pm.getFromUser().getUsername() +
					" to " + pm.getToUser().getUsername()).record();

			if (delay > 0) {
				SessionFacade.setAttribute(ConfigKeys.LAST_POST_TIME, Long.valueOf(System.currentTimeMillis()));
			}
		}
	}

	public void findUser()
	{
		boolean showResult = false;
		String username = SafeHtml.makeSafe(this.request.getParameter("username"));

		if (username != null && !username.equals("")) {
			List<User> namesList = DataAccessDriver.getInstance().newUserDAO().findByName(username, false);
			this.context.put("namesList", namesList);
			showResult = true;
		}

		this.setTemplateName(TemplateKeys.PM_FIND_USER);
		
		this.context.put("username", username);
		this.context.put("showResult", showResult);
	}
	
	public void read()
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}
		
		int id = this.request.getIntParameter("id");
		
		PrivateMessage pm = new PrivateMessage();
		pm.setId(id);
		
		pm = DataAccessDriver.getInstance().newPrivateMessageDAO().selectById(pm);
		
		// Don't allow the read of messages that don't belongs to the current user
		UserSession us = SessionFacade.getUserSession();
		int userId = us.getUserId();
		
		if (pm.getToUser().getId() == userId || pm.getFromUser().getId() == userId) {
			pm.getPost().setText(PostCommon.preparePostForDisplay(pm.getPost()).getText());
			
			// Update the message status, if needed
			if (pm.getType() == PrivateMessageType.NEW) {
				pm.setType(PrivateMessageType.READ);
				DataAccessDriver.getInstance().newPrivateMessageDAO().updateType(pm);
				
				int totalMessages = us.getPrivateMessages();

				if (totalMessages > 0) {
					UserSession.changeOnUser(us.getUserId(), uSession -> uSession.setPrivateMessages(totalMessages - 1));
				}
			}
			
			User user = pm.getFromUser();
			ViewCommon.prepareUserSignature(user);
            
			this.context.put("pm", pm);
			this.setTemplateName(TemplateKeys.PM_READ);
		}
		else {
			this.setTemplateName(TemplateKeys.PM_READ_DENIED);
			this.context.put("message", I18n.getMessage("PrivateMessage.readDenied"));
		}
	}
	
	public void review()
	{
		this.read();
		this.setTemplateName(TemplateKeys.PM_READ_REVIEW);
	}
	
	public void delete()
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}
		
		String ids[] = this.request.getParameterValues("id");
		
		if (ids != null && ids.length > 0) {
			PrivateMessage[] deleteList = new PrivateMessage[ids.length];
			
			int unreadCount = 0;
			PrivateMessageDAO dao = DataAccessDriver.getInstance().newPrivateMessageDAO();
			
			for (int i = 0; i < ids.length; i++) {
				PrivateMessage pm = dao.selectById(new PrivateMessage(Integer.parseInt(ids[i])));
				
				if (pm.getType() == PrivateMessageType.NEW) {
					unreadCount++;
				}
				
				deleteList[i] = pm;
			}

			UserSession us = SessionFacade.getUserSession();

			dao.delete(deleteList, us.getUserId());

			// Subtracts the number of deleted messages
			final int total = (us.getPrivateMessages() - unreadCount < 0) ? 0 : (us.getPrivateMessages() - unreadCount);

			UserSession.changeOnUser(us.getUserId(), uSession -> us.setPrivateMessages(total));
		}

		this.setTemplateName(TemplateKeys.PM_DELETE);
		this.context.put("message", I18n.getMessage("PrivateMessage.deleteDone", 
			new String[] { this.request.getContextPath() 
				+ "/pm/inbox"
				+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) }));
	}
	
	public void reply()
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}
		
		int id = this.request.getIntParameter("id");
		
		PrivateMessage pm = new PrivateMessage();
		pm.setId(id);
		pm = DataAccessDriver.getInstance().newPrivateMessageDAO().selectById(pm);
		
		int userId = SessionFacade.getUserSession().getUserId();
		
		if (pm.getToUser().getId() != userId && pm.getFromUser().getId() != userId) {
			this.setTemplateName(TemplateKeys.PM_READ_DENIED);
			this.context.put("message", I18n.getMessage("PrivateMessage.readDenied"));
			return;
		}
		
		pm.getPost().setSubject(I18n.getMessage("PrivateMessage.replyPrefix") + pm.getPost().getSubject());
		
		this.context.put("pm", pm);
		this.context.put("pmReply", true);
		
		this.sendFormCommon(DataAccessDriver.getInstance().newUserDAO().selectById(
				SessionFacade.getUserSession().getUserId()));
	}
	
	public void quote()
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}
		
		int id = this.request.getIntParameter("id");
		
		PrivateMessage pm = new PrivateMessage();
		pm.setId(id);
		pm = DataAccessDriver.getInstance().newPrivateMessageDAO().selectById(pm);

		int userId = SessionFacade.getUserSession().getUserId();
		
		if (pm.getToUser().getId() != userId && pm.getFromUser().getId() != userId) {
			this.setTemplateName(TemplateKeys.PM_READ_DENIED);
			this.context.put("message", I18n.getMessage("PrivateMessage.readDenied"));
			return;
		}
		
		pm.getPost().setSubject(I18n.getMessage("PrivateMessage.replyPrefix") + pm.getPost().getSubject());
		
		this.sendFormCommon(DataAccessDriver.getInstance().newUserDAO().selectById(userId));
		
		this.context.put("quote", "true");
		this.context.put("quoteUser", pm.getFromUser().getUsername());
		this.context.put("post", pm.getPost());
		this.context.put("pm", pm);
	}
	
	/** 
	 * @see net.jforum.Command#list()
	 */
	@Override public void list()
	{
		this.inbox();
	}
}
