package net.jforum.dao.generic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

import net.jforum.JForumExecutionContext;
import net.jforum.PooledConnection;
import net.jforum.dao.TelegramUserDAO;
import net.jforum.entities.TelegramUser;
import net.jforum.exceptions.DatabaseException;
import net.jforum.util.DbUtils;
import net.jforum.util.preferences.SystemGlobals;

public class GenericTelegramUserDAO extends AutoKeys implements TelegramUserDAO {

	private static final Logger LOGGER = Logger.getLogger(GenericTelegramUserDAO.class);

	@Override
	public TelegramUser selectByUserId(long userId) {
		String q = SystemGlobals.getSql("TelegramUserModel.selectByUserId");
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			//pstmt = JForumExecutionContext.getConnection().prepareStatement(q);
			pstmt = PooledConnection.getImplementation().getConnection().prepareStatement(q);
			pstmt.setLong(1, userId);

			rs = pstmt.executeQuery();
			TelegramUser user = new TelegramUser();

			if (rs.next()) {
				this.fillUserFromResultSet(user, rs);

				rs.close();
				pstmt.close();
			}

			return user;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			DbUtils.close(rs, pstmt);
		}
	}

	@Override
	public TelegramUser selectByName(String username) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = JForumExecutionContext.getConnection()
					.prepareStatement(SystemGlobals.getSql("TelegramUserModel.selectByName"));
			pstmt.setString(1, username);

			rs = pstmt.executeQuery();
			TelegramUser user = null;

			if (rs.next()) {
				user = new TelegramUser();
				this.fillUserFromResultSet(user, rs);
			}

			return user;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			DbUtils.close(rs, pstmt);
		}
	}

	protected void fillUserFromResultSet(TelegramUser user, ResultSet rs) throws SQLException {
		user.setId(rs.getInt("id"));
		user.setUserId(rs.getLong("user_id"));
		user.setChatId(rs.getLong("chat_id"));
		user.setForumUserId(rs.getInt("forum_user_id"));
		user.setUsername(rs.getString("username"));
		user.setFirstName(rs.getString("first_name"));
		user.setLastName(rs.getString("last_name"));
		user.setPhoneNumber(rs.getString("phone_number"));
		user.setLastVisit(rs.getTimestamp("user_lastvisit"));
		user.setRegistrationDate(new Date(rs.getTimestamp("user_regdate").getTime()));
	}

	@Override
	public void associateWithForumUser(long userId, int forumUserId) {
		PreparedStatement pstmt = null;
		try {
			pstmt = PooledConnection.getImplementation().getConnection()
					.prepareStatement(SystemGlobals.getSql("TelegramUserModel.associateWithForumUser"));
			pstmt.setInt(1, forumUserId);
			pstmt.setLong(2, userId);

			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			DbUtils.close(pstmt);
			try {
				JForumExecutionContext.getConnection().commit();
			} catch (SQLException e) {
				throw new DatabaseException(e);
			}
		}
	}

	@Override
	public void update(TelegramUser user) {
		PreparedStatement pstmt = null;
		try {
			pstmt = PooledConnection.getImplementation().getConnection()
					.prepareStatement(SystemGlobals.getSql("TelegramUserModel.update"));

			pstmt.setLong(1, user.getChatId());
			pstmt.setInt(2, user.getForumUserId());
			pstmt.setString(3, user.getUsername());
			pstmt.setString(4, user.getFirstName());
			pstmt.setString(5, user.getLastName());
			pstmt.setString(6, user.getPhoneNumber());

			if (user.getLastVisit() == null) {
				user.setLastVisit(new Date());
			}

			pstmt.setTimestamp(7, new Timestamp(user.getLastVisit().getTime()));
			pstmt.setLong(8, user.getUserId());

			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			DbUtils.close(pstmt);
			try {
				JForumExecutionContext.getConnection().commit();
			} catch (SQLException e) {
				throw new DatabaseException(e);
			}
		}
	}

	@Override
	public int addNew(TelegramUser user) {
		PreparedStatement pstmt = null;
		try {
			pstmt = this.getStatementForAutoKeys("TelegramUserModel.addNew");

			this.initNewUser(user, pstmt);
			
			this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("TelegramUserModel.lastGeneratedUserId"));
			int id = this.executeAutoKeysQuery(pstmt);

			user.setId(id);
			return id;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			DbUtils.close(pstmt);
			try {
				JForumExecutionContext.getConnection().commit();
			} catch (SQLException e) {
				throw new DatabaseException(e);
			}
		}
	}

	protected void initNewUser(TelegramUser user, PreparedStatement pstmt) throws SQLException {
		pstmt.setLong(1, user.getUserId());
		pstmt.setLong(2, user.getChatId());
		pstmt.setString(3, user.getUsername());
		pstmt.setString(4, user.getFirstName());
		pstmt.setString(5, user.getLastName());
		pstmt.setString(6, user.getPhoneNumber());
		pstmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
	}

	@Override
	public void addNewWithId(TelegramUser user) {
		PreparedStatement pstmt = null;
		try {
			pstmt = this.getStatementForAutoKeys("TelegramUserModel.addNewWithId");

			this.initNewUser(user, pstmt);
			pstmt.setInt(8, user.getId());

			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			DbUtils.close(pstmt);
			try {
				JForumExecutionContext.getConnection().commit();
			} catch (SQLException e) {
				throw new DatabaseException(e);
			}
		}
	}

}
