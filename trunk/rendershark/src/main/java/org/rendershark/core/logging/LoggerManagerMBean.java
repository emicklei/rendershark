package org.rendershark.core.logging;

public interface LoggerManagerMBean {
    void setLoggerLevel(String packageOrClassName, String level);
    public String getLoggerLevel(String packageOrClassName);
}
