package org.bfchain.libappmgr.schedule

interface UpdateTask {
    fun scheduleUpdate(interval: Long)
    fun cancle()
}