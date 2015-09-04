/*
 * Copyright (c) 2008-2015 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/license for details.
 */

package com.haulmont.cuba.web.app.ui.statistics;

import com.haulmont.cuba.core.entity.JmxInstance;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Formatter;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.Timer;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author krivenko
 * @version $Id$
 */
public class ThreadsMonitoringWindow extends AbstractWindow {

    @Inject
    protected ThreadsDatasource threadsDs;

    @Inject
    protected Table threadsTable;

    @Override
    public void init(Map<String, Object> params) {
        threadsDs.refresh(params);
        threadsDs.addItemChangeListener(e -> updateStacktrace(e.getItem()));

        JmxInstance node = (JmxInstance) params.get("node");
        setCaption(formatMessage("threadsMonitoring.caption", node.getNodeName()));

        threadsTable.getColumn("cpu").setFormatter(new PercentFormatter());
    }

    protected void updateStacktrace(ThreadSnapshot item) {
        if (item != null) {
            item.setStackTrace(threadsDs.getStackTrace(item.getThreadId()));
        }
    }

    @SuppressWarnings("unused")
    public void onRefresh(Timer timer) {
        threadsDs.refresh();
        updateStacktrace(threadsDs.getItem());
    }

    protected static class PercentFormatter implements Formatter {
        @Override
        public String format(Object value) {
            String res = null;
            if (value instanceof Double) {
                double doubleValue = (double) value;
                res = String.format("%.1f %%", doubleValue * 100);
            }
            return res != null ? res : (value != null ? value.toString() : null);
        }
    }
}