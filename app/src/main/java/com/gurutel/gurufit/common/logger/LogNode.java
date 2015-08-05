package com.gurutel.gurufit.common.logger;

/**
 * Created by tey3 on 15. 6. 24.
 */
public interface LogNode {
    public void println(int priority, String tag, String msg, Throwable tr);
}
