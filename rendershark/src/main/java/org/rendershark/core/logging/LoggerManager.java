package org.rendershark.core.logging;

import java.lang.management.ManagementFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LoggerManager is a helper that provides access (read+change) to the level of a logger.
 * It is typically used for exposure through JMX.
 * 
 * @author ernestmicklei 
 */
@Singleton
public class LoggerManager implements LoggerManagerMBean {
    private static final Logger LOG = LoggerFactory.getLogger(LoggerManager.class);

    @Inject
    public void install() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("org.rendershark:type=LoggerManager"); 
            LoggerManager mgr = new LoggerManager();
            mbs.registerMBean(mgr, name);
            LOG.info("Install LoggerManager MBean");
        } catch (Exception e) {
            LOG.error("Failed to install LoggerManager", e);
        }
    }
    
    /**
     * @param packageName | className
     * @param level
     */
    /**
     * Change the level for a logger by its packageName or className
     */
    public void setLoggerLevel(String packageOrClassName, String level) {
        org.apache.log4j.Logger log4jLogger = LogManager.getLogger(packageOrClassName);
        Level currentLevel = log4jLogger.getLevel();
        LOG.info("Changing the logging level for [{}] from [{}] to [{}]", new Object[] { packageOrClassName, currentLevel, level });
        log4jLogger.setLevel(Level.toLevel(level));
    }

    /**
     * Get the level for a logger by its packageName or className
     * @param packageOrClassName
     * @return
     */
    public String getLoggerLevel(String packageOrClassName) {
        org.apache.log4j.Logger log4jLogger = LogManager.getLogger(packageOrClassName);
        Level currentLevel = log4jLogger.getLevel();
        return currentLevel == null ? "<no such logger:" + packageOrClassName + ">" : currentLevel.toString();
    }
}
