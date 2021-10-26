package com.smart_learn.data.guest.room.dao;

import androidx.room.Dao;

import com.smart_learn.data.guest.room.dao.helpers.BasicDao;
import com.smart_learn.data.guest.room.entitites.Notification;

@Dao
public interface NotificationDao extends BasicDao<Notification> {
}
