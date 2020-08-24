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
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum;

import java.io.File;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import net.jforum.util.log.LoggerHelper;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.stats.Stats;

/**
 * @author Andowson Chang
 */

public class ContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(ContextListener.class);
	
	/* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override public void contextInitialized (ServletContextEvent sce) {
        final ServletContext application = sce.getServletContext();
        String appPath = application.getRealPath("");
        if (appPath != null && appPath.endsWith(File.separator)) {
			// On Tomcat, getRealPath ends with a "/", whereas on Jetty, it does not. The next line allows for that.
        	appPath = appPath.substring(0, appPath.lastIndexOf(File.separator));
        }
		LOGGER.info("application root is "+appPath);
        LoggerHelper.checkLoggerInitialization(appPath + "/WEB-INF", appPath + "/WEB-INF/classes");
        final String containerInfo = application.getServerInfo();
        ConfigLoader.startSystemglobals(appPath);
        final String[] info = getAppServerNameAndVersion(containerInfo);
		SystemGlobals.setValue("container.app", info[0]);
		SystemGlobals.setValue("container.version", info[1]);
        SystemGlobals.setValue("server.info", containerInfo);
        SystemGlobals.setValue("servlet.version", application.getMajorVersion()+"."+application.getMinorVersion());
        SystemGlobals.setValue("context.path", application.getContextPath());
		// initialize EventBus
		Stats.init();
        LOGGER.info(application.getContextPath() + " initialized in " + containerInfo);
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override public void contextDestroyed (ServletContextEvent sce) {
		// stop EventBus
        Stats.stop();

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            LOGGER.debug("unregister JDBC Driver " + driver.getClass().getName());
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                e.printStackTrace();
                LOGGER.error(e.getMessage(), e);
            }
        }
        LOGGER.info(sce.getServletContext().getContextPath() + " destroyed");
    }

    public static String[] getAppServerNameAndVersion (String serverInfo)
    {
		/* According to https://docs.oracle.com/javaee/7/api/javax/servlet/ServletContext.html#getServerInfo--,
			the server info is in the form "server name/server version (optional info)"
		*/
        String[] result = new String[2];
		int slash = serverInfo.indexOf("/");
		if (slash != -1) {
            result[0] = serverInfo.substring(0, slash);
            result[1] = serverInfo.substring(slash+1);
			int dot = result[1].indexOf(".");
			if (dot != -1) {
				result[1] = result[1].substring(0, dot);
			}
		} else {
			result[0] = result[1] = "???";
		}
		/*
        final Pattern p = Pattern.compile("\\d+\\.\\d+(\\.\\d+)*");
        final Matcher matcher = p.matcher(serverInfo);
        if (matcher.find()){
            result[0] = serverInfo.substring(0, matcher.start()-1);
            String version = matcher.group();
            result[1] = version.substring(0, version.indexOf('.'));
        }
		*/
        return result;
    }
}
