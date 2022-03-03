package com.yl.lib.privacysentry.calendar

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * @author yulun
 * @since 2022-01-13 19:53
 * 测试日历相关 代码从 https://github.com/kylechandev/CalendarProviderManager拷贝
 */
class CalendarManager {

    object Manager {
        private val builder = StringBuilder()

        /*
           TIP: 要向系统日历插入事件,前提系统中必须存在至少1个日历账户
         */


        /*
           TIP: 要向系统日历插入事件,前提系统中必须存在至少1个日历账户
         */
        // ----------------------- 创建日历账户时账户名使用 ---------------------------
        private var CALENDAR_NAME = "KyleC"
        private var CALENDAR_ACCOUNT_NAME = "KYLE"
        private var CALENDAR_DISPLAY_NAME = "KYLE的账户"


        // ------------------------------- 日历账户 -----------------------------------

        // ------------------------------- 日历账户 -----------------------------------
        /**
         * 获取日历账户ID(若没有则会自动创建一个)
         *
         * @return success: 日历账户ID  failed : -1  permission deny : -2
         */
        fun obtainCalendarAccountID(context: Context): Long {
            val calID = checkCalendarAccount(context)
            return if (calID >= 0) {
                calID
            } else {
                createCalendarAccount(context)
            }
        }

        /**
         * 检查是否存在日历账户
         *
         * @return 存在：日历账户ID  不存在：-1
         */
        private fun checkCalendarAccount(context: Context): Long {
            context.getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI,
                null, null, null, null
            ).use { cursor ->
                // 不存在日历账户
                if (null == cursor) {
                    return -1
                }
                val count: Int = cursor.getCount()
                // 存在日历账户，获取第一个账户的ID
                return if (count > 0) {
                    cursor.moveToFirst()
                    cursor.getInt(cursor.getColumnIndex(CalendarContract.Calendars._ID)).toLong()
                } else {
                    -1
                }
            }
        }

        /**
         * 创建一个新的日历账户
         *
         * @return success：ACCOUNT ID , create failed：-1 , permission deny：-2
         */
        private fun createCalendarAccount(context: Context): Long {
            // 系统日历表
            var uri: Uri = CalendarContract.Calendars.CONTENT_URI

            // 要创建的账户
            val accountUri: Uri

            // 开始组装账户数据
            val account = ContentValues()
            // 账户类型：本地
            // 在添加账户时，如果账户类型不存在系统中，则可能该新增记录会被标记为脏数据而被删除
            // 设置为ACCOUNT_TYPE_LOCAL可以保证在不存在账户类型时，该新增数据不会被删除
            account.put(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL
            )
            // 日历在表中的名称
            account.put(CalendarContract.Calendars.NAME, CALENDAR_NAME)
            // 日历账户的名称
            account.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDAR_ACCOUNT_NAME)
            // 账户显示的名称
            account.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_DISPLAY_NAME)
            // 日历的颜色
            account.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.parseColor("#515bd4"))
            // 用户对此日历的获取使用权限等级
            account.put(
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CAL_ACCESS_OWNER
            )
            // 设置此日历可见
            account.put(CalendarContract.Calendars.VISIBLE, 1)
            // 日历时区
            account.put(
                CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                TimeZone.getDefault().getID()
            )
            // 可以修改日历时区
            account.put(CalendarContract.Calendars.CAN_MODIFY_TIME_ZONE, 1)
            // 同步此日历到设备上
            account.put(CalendarContract.Calendars.SYNC_EVENTS, 1)
            // 拥有者的账户
            account.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDAR_ACCOUNT_NAME)
            // 可以响应事件
            account.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 1)
            // 单个事件设置的最大的提醒数
            account.put(CalendarContract.Calendars.MAX_REMINDERS, 8)
            // 设置允许提醒的方式
            account.put(CalendarContract.Calendars.ALLOWED_REMINDERS, "0,1,2,3,4")
            // 设置日历支持的可用性类型
            account.put(CalendarContract.Calendars.ALLOWED_AVAILABILITY, "0,1,2")
            // 设置日历允许的出席者类型
            account.put(CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES, "0,1,2")

            /*
                TIP: 修改或添加ACCOUNT_NAME只能由SYNC_ADAPTER调用
                对uri设置CalendarContract.CALLER_IS_SYNCADAPTER为true,即标记当前操作为SYNC_ADAPTER操作
                在设置CalendarContract.CALLER_IS_SYNCADAPTER为true时,必须带上参数ACCOUNT_NAME和ACCOUNT_TYPE(任意)
             */uri = uri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(
                    CalendarContract.Calendars.ACCOUNT_NAME,
                    CALENDAR_ACCOUNT_NAME
                )
                .appendQueryParameter(
                    CalendarContract.Calendars.ACCOUNT_TYPE,
                    CalendarContract.Calendars.CALENDAR_LOCATION
                )
                .build()
            accountUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查日历权限
                if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(
                        "android.permission.WRITE_CALENDAR"
                    )
                ) {
                    context.getContentResolver().insert(uri, account)!!
                } else {
                    return -2
                }
            } else {
                context.getContentResolver().insert(uri, account)!!
            }
            return if (accountUri == null) -1 else ContentUris.parseId(accountUri)
        }

        /**
         * 删除创建的日历账户
         *
         * @return -2: permission deny  0: No designated account  1: delete success
         */
        fun deleteCalendarAccountByName(context: Context): Int {
            val deleteCount: Int
            val uri: Uri = CalendarContract.Calendars.CONTENT_URI
            val selection = ("((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                    + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))")
            val selectionArgs = arrayOf(CALENDAR_ACCOUNT_NAME, CalendarContract.ACCOUNT_TYPE_LOCAL)
            deleteCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(
                        "android.permission.WRITE_CALENDAR"
                    )
                ) {
                    context.getContentResolver().delete(uri, selection, selectionArgs)
                } else {
                    return -2
                }
            } else {
                context.getContentResolver().delete(uri, selection, selectionArgs)
            }
            return deleteCount
        }


        // ------------------------------- 添加日历事件 -----------------------------------

        // ------------------------------- 添加日历事件 -----------------------------------
        /**
         * 添加日历事件
         *
         * @param calendarEvent 日历事件(详细参数说明请参看[CalendarEvent]构造方法)
         * @return 0: success  -1: failed  -2: permission deny
         */
        fun addCalendarEvent(context: Context, calendarEvent: CalendarEvent): Int {
            /*
                TIP: 插入一个新事件的规则：
                 1.  必须包含CALENDAR_ID和DTSTART字段
                 2.  必须包含EVENT_TIMEZONE字段,使用TimeZone.getDefault().getID()方法获取默认时区
                 3.  对于非重复发生的事件,必须包含DTEND字段
                 4.  对重复发生的事件,必须包含一个附加了RRULE或RDATE字段的DURATION字段
             */

            // 获取日历账户ID，也就是要将事件插入到的账户
            val calID = obtainCalendarAccountID(context)

            // 系统日历事件表
            val uri1: Uri = CalendarContract.Events.CONTENT_URI
            // 创建的日历事件
            val eventUri: Uri

            // 系统日历事件提醒表
            val uri2: Uri = CalendarContract.Reminders.CONTENT_URI
            // 创建的日历事件提醒
            val reminderUri: Uri

            // 开始组装事件数据
            val event = ContentValues()
            // 事件要插入到的日历账户
            event.put(CalendarContract.Events.CALENDAR_ID, calID)
            setupEvent(calendarEvent, event)
            eventUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 判断权限
                if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(
                        "android.permission.WRITE_CALENDAR"
                    )
                ) {
                    context.getContentResolver().insert(uri1, event)!!
                } else {
                    return -2
                }
            } else {
                context.getContentResolver().insert(uri1, event)!!
            }
            if (null == eventUri) {
                return -1
            }
            if (-2 != calendarEvent.getAdvanceTime()) {
                // 获取事件ID
                val eventID = ContentUris.parseId(eventUri)

                // 开始组装事件提醒数据
                val reminders = ContentValues()
                // 此提醒所对应的事件ID
                reminders.put(CalendarContract.Reminders.EVENT_ID, eventID)
                // 设置提醒提前的时间(0：准时  -1：使用系统默认)
                reminders.put(CalendarContract.Reminders.MINUTES, calendarEvent.getAdvanceTime())
                // 设置事件提醒方式为通知警报
                reminders.put(
                    CalendarContract.Reminders.METHOD,
                    CalendarContract.Reminders.METHOD_ALERT
                )
                reminderUri = context.getContentResolver().insert(uri2, reminders)!!
                if (null == reminderUri) {
                    return -1
                }
            }
            return 0
        }


        // ------------------------------- 更新日历事件 -----------------------------------

        // ------------------------------- 更新日历事件 -----------------------------------
        /**
         * 更新指定ID的日历事件
         *
         * @param newCalendarEvent 更新的日历事件
         * @return -2: permission deny  else success
         */
        fun updateCalendarEvent(
            context: Context,
            eventID: Long,
            newCalendarEvent: CalendarEvent
        ): Int {
            val updatedCount1: Int
            val uri1: Uri = CalendarContract.Events.CONTENT_URI
            val uri2: Uri = CalendarContract.Reminders.CONTENT_URI
            val event = ContentValues()
            setupEvent(newCalendarEvent, event)

            // 更新匹配条件
            val selection1 = "(" + CalendarContract.Events._ID + " = ?)"
            val selectionArgs1 = arrayOf(eventID.toString())
            updatedCount1 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(
                        "android.permission.WRITE_CALENDAR"
                    )
                ) {
                    context.getContentResolver().update(uri1, event, selection1, selectionArgs1)
                } else {
                    return -2
                }
            } else {
                context.getContentResolver().update(uri1, event, selection1, selectionArgs1)
            }
            val reminders = ContentValues()
            reminders.put(CalendarContract.Reminders.MINUTES, newCalendarEvent.getAdvanceTime())
            reminders.put(
                CalendarContract.Reminders.METHOD,
                CalendarContract.Reminders.METHOD_ALERT
            )

            // 更新匹配条件
            val selection2 = "(" + CalendarContract.Reminders.EVENT_ID + " = ?)"
            val selectionArgs2 = arrayOf(eventID.toString())
            val updatedCount2: Int =
                context.getContentResolver().update(uri2, reminders, selection2, selectionArgs2)
            return (updatedCount1 + updatedCount2) / 2
        }

        /**
         * 更新指定ID事件的开始时间
         *
         * @return If successfully returns 1
         */
        fun updateCalendarEventbeginTime(context: Context, eventID: Long, newBeginTime: Long): Int {
            val uri: Uri = CalendarContract.Events.CONTENT_URI

            // 新的数据
            val event = ContentValues()
            event.put(CalendarContract.Events.DTSTART, newBeginTime)

            // 匹配条件
            val selection = "(" + CalendarContract.Events._ID + " = ?)"
            val selectionArgs = arrayOf(eventID.toString())
            return context.getContentResolver().update(uri, event, selection, selectionArgs)
        }

        /**
         * 更新指定ID事件的结束时间
         *
         * @return If successfully returns 1
         */
        fun updateCalendarEventEndTime(context: Context, eventID: Long, newEndTime: Long): Int {
            val uri: Uri = CalendarContract.Events.CONTENT_URI

            // 新的数据
            val event = ContentValues()
            event.put(CalendarContract.Events.DTEND, newEndTime)


            // 匹配条件
            val selection = "(" + CalendarContract.Events._ID + " = ?)"
            val selectionArgs = arrayOf(eventID.toString())
            return context.getContentResolver().update(uri, event, selection, selectionArgs)
        }

        /**
         * 更新指定ID事件的起始时间
         *
         * @return If successfully returns 1
         */
        fun updateCalendarEventTime(
            context: Context, eventID: Long, newBeginTime: Long,
            newEndTime: Long
        ): Int {
            val uri: Uri = CalendarContract.Events.CONTENT_URI

            // 新的数据
            val event = ContentValues()
            event.put(CalendarContract.Events.DTSTART, newBeginTime)
            event.put(CalendarContract.Events.DTEND, newEndTime)


            // 匹配条件
            val selection = "(" + CalendarContract.Events._ID + " = ?)"
            val selectionArgs = arrayOf(eventID.toString())
            return context.getContentResolver().update(uri, event, selection, selectionArgs)
        }

        /**
         * 更新指定ID事件的标题
         *
         * @return If successfully returns 1
         */
        fun updateCalendarEventTitle(context: Context, eventID: Long, newTitle: String?): Int {
            val uri: Uri = CalendarContract.Events.CONTENT_URI

            // 新的数据
            val event = ContentValues()
            event.put(CalendarContract.Events.TITLE, newTitle)


            // 匹配条件
            val selection = "(" + CalendarContract.Events._ID + " = ?)"
            val selectionArgs = arrayOf(eventID.toString())
            return context.getContentResolver().update(uri, event, selection, selectionArgs)
        }

        /**
         * 更新指定ID事件的描述
         *
         * @return If successfully returns 1
         */
        fun updateCalendarEventDes(context: Context, eventID: Long, newEventDes: String?): Int {
            val uri: Uri = CalendarContract.Events.CONTENT_URI

            // 新的数据
            val event = ContentValues()
            event.put(CalendarContract.Events.DESCRIPTION, newEventDes)


            // 匹配条件
            val selection = "(" + CalendarContract.Events._ID + " = ?)"
            val selectionArgs = arrayOf(eventID.toString())
            return context.getContentResolver().update(uri, event, selection, selectionArgs)
        }

        /**
         * 更新指定ID事件的地点
         *
         * @return If successfully returns 1
         */
        fun updateCalendarEventLocation(
            context: Context,
            eventID: Long,
            newEventLocation: String?
        ): Int {
            val uri: Uri = CalendarContract.Events.CONTENT_URI

            // 新的数据
            val event = ContentValues()
            event.put(CalendarContract.Events.EVENT_LOCATION, newEventLocation)


            // 匹配条件
            val selection = "(" + CalendarContract.Events._ID + " = ?)"
            val selectionArgs = arrayOf(eventID.toString())
            return context.getContentResolver().update(uri, event, selection, selectionArgs)
        }

        /**
         * 更新指定ID事件的标题和描述
         *
         * @return If successfully returns 1
         */
        fun updateCalendarEventTitAndDes(
            context: Context, eventID: Long, newEventTitle: String?,
            newEventDes: String?
        ): Int {
            val uri: Uri = CalendarContract.Events.CONTENT_URI

            // 新的数据
            val event = ContentValues()
            event.put(CalendarContract.Events.TITLE, newEventTitle)
            event.put(CalendarContract.Events.DESCRIPTION, newEventDes)


            // 匹配条件
            val selection = "(" + CalendarContract.Events._ID + " = ?)"
            val selectionArgs = arrayOf(eventID.toString())
            return context.getContentResolver().update(uri, event, selection, selectionArgs)
        }

        /**
         * 更新指定ID事件的常用信息(标题、描述、地点)
         *
         * @return If successfully returns 1
         */
        fun updateCalendarEventCommonInfo(
            context: Context, eventID: Long, newEventTitle: String?,
            newEventDes: String?, newEventLocation: String?
        ): Int {
            val uri: Uri = CalendarContract.Events.CONTENT_URI

            // 新的数据
            val event = ContentValues()
            event.put(CalendarContract.Events.TITLE, newEventTitle)
            event.put(CalendarContract.Events.DESCRIPTION, newEventDes)
            event.put(CalendarContract.Events.EVENT_LOCATION, newEventLocation)


            // 匹配条件
            val selection = "(" + CalendarContract.Events._ID + " = ?)"
            val selectionArgs = arrayOf(eventID.toString())
            return context.getContentResolver().update(uri, event, selection, selectionArgs)
        }

        /**
         * 更新指定ID事件的提醒方式
         *
         * @return If successfully returns 1
         */
        private fun updateCalendarEventReminder(
            context: Context,
            eventID: Long,
            newAdvanceTime: Long
        ): Int {
            val uri: Uri = CalendarContract.Reminders.CONTENT_URI
            val reminders = ContentValues()
            reminders.put(CalendarContract.Reminders.MINUTES, newAdvanceTime)

            // 更新匹配条件
            val selection2 = "(" + CalendarContract.Reminders.EVENT_ID + " = ?)"
            val selectionArgs2 = arrayOf(eventID.toString())
            return context.getContentResolver().update(uri, reminders, selection2, selectionArgs2)
        }

        /**
         * 更新指定ID事件的提醒重复规则
         *
         * @return If successfully returns 1
         */
        private fun updateCalendarEventRRule(
            context: Context,
            eventID: Long,
            newRRule: String
        ): Int {
            val uri: Uri = CalendarContract.Events.CONTENT_URI

            // 新的数据
            val event = ContentValues()
            event.put(CalendarContract.Events.RRULE, newRRule)

            // 匹配条件
            val selection = "(" + CalendarContract.Events._ID + " = ?)"
            val selectionArgs = arrayOf(eventID.toString())
            return context.getContentResolver().update(uri, event, selection, selectionArgs)
        }


        // ------------------------------- 删除日历事件 -----------------------------------

        // ------------------------------- 删除日历事件 -----------------------------------
        /**
         * 删除日历事件
         *
         * @param eventID 事件ID
         * @return -2: permission deny  else success
         */
        fun deleteCalendarEvent(context: Context, eventID: Long): Int {
            val deletedCount1: Int
            val uri1: Uri = CalendarContract.Events.CONTENT_URI
            val uri2: Uri = CalendarContract.Reminders.CONTENT_URI

            // 删除匹配条件
            val selection = "(" + CalendarContract.Events._ID + " = ?)"
            val selectionArgs = arrayOf(eventID.toString())
            deletedCount1 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(
                        "android.permission.WRITE_CALENDAR"
                    )
                ) {
                    context.getContentResolver().delete(uri1, selection, selectionArgs)
                } else {
                    return -2
                }
            } else {
                context.getContentResolver().delete(uri1, selection, selectionArgs)
            }

            // 删除匹配条件
            val selection2 = "(" + CalendarContract.Reminders.EVENT_ID + " = ?)"
            val selectionArgs2 = arrayOf(eventID.toString())
            val deletedCount2: Int =
                context.getContentResolver().delete(uri2, selection2, selectionArgs2)
            return (deletedCount1 + deletedCount2) / 2
        }


        // ------------------------------- 查询日历事件 -----------------------------------

        // ------------------------------- 查询日历事件 -----------------------------------
        /**
         * 查询指定日历账户下的所有事件
         *
         * @return If failed return null else return List<CalendarEvent>
        </CalendarEvent> */
        fun queryAccountEvent(context: Context, calID: Long): List<CalendarEvent>? {
            val EVENT_PROJECTION = arrayOf(
                CalendarContract.Events.CALENDAR_ID,  // 在表中的列索引0
                CalendarContract.Events.TITLE,  // 在表中的列索引1
                CalendarContract.Events.DESCRIPTION,  // 在表中的列索引2
                CalendarContract.Events.EVENT_LOCATION,  // 在表中的列索引3
                CalendarContract.Events.DISPLAY_COLOR,  // 在表中的列索引4
                CalendarContract.Events.STATUS,  // 在表中的列索引5
                CalendarContract.Events.DTSTART,  // 在表中的列索引6
                CalendarContract.Events.DTEND,  // 在表中的列索引7
                CalendarContract.Events.DURATION,  // 在表中的列索引8
                CalendarContract.Events.EVENT_TIMEZONE,  // 在表中的列索引9
                CalendarContract.Events.EVENT_END_TIMEZONE,  // 在表中的列索引10
                CalendarContract.Events.ALL_DAY,  // 在表中的列索引11
                CalendarContract.Events.ACCESS_LEVEL,  // 在表中的列索引12
                CalendarContract.Events.AVAILABILITY,  // 在表中的列索引13
                CalendarContract.Events.HAS_ALARM,  // 在表中的列索引14
                CalendarContract.Events.RRULE,  // 在表中的列索引15
                CalendarContract.Events.RDATE,  // 在表中的列索引16
                CalendarContract.Events.HAS_ATTENDEE_DATA,  // 在表中的列索引17
                CalendarContract.Events.LAST_DATE,  // 在表中的列索引18
                CalendarContract.Events.ORGANIZER,  // 在表中的列索引19
                CalendarContract.Events.IS_ORGANIZER,  // 在表中的列索引20
                CalendarContract.Events._ID // 在表中的列索引21
            )

            // 事件匹配
            val uri: Uri = CalendarContract.Events.CONTENT_URI
            val uri2: Uri = CalendarContract.Reminders.CONTENT_URI
            val selection = "(" + CalendarContract.Events.CALENDAR_ID + " = ?)"
            val selectionArgs = arrayOf(calID.toString())
            val cursor: Cursor
            cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(
                        "android.permission.READ_CALENDAR"
                    )
                ) {
                    context.getContentResolver().query(
                        uri, EVENT_PROJECTION, selection,
                        selectionArgs, null
                    )!!
                } else {
                    return null
                }
            } else {
                context.getContentResolver().query(
                    uri, EVENT_PROJECTION, selection,
                    selectionArgs, null
                )!!
            }
            if (null == cursor) {
                return null
            }

            // 查询结果
            val result: MutableList<CalendarEvent> = ArrayList()

            // 开始查询数据
            if (cursor.moveToFirst()) {
                do {
                    val calendarEvent = CalendarEvent()
                    result.add(calendarEvent)
                    calendarEvent.setId(
                        cursor.getLong(
                            cursor.getColumnIndex(
                                CalendarContract.Events._ID
                            )
                        )
                    )
                    calendarEvent.setCalID(
                        cursor.getLong(
                            cursor.getColumnIndex(
                                CalendarContract.Events.CALENDAR_ID
                            )
                        )
                    )
                    calendarEvent.setTitle(
                        cursor.getString(
                            cursor.getColumnIndex(
                                CalendarContract.Events.TITLE
                            )
                        )
                    )
                    calendarEvent.setDescription(
                        cursor.getString(
                            cursor.getColumnIndex(
                                CalendarContract.Events.DESCRIPTION
                            )
                        )
                    )
                    calendarEvent.setEventLocation(
                        cursor.getString(
                            cursor.getColumnIndex(
                                CalendarContract.Events.EVENT_LOCATION
                            )
                        )
                    )
                    calendarEvent.setDisplayColor(
                        cursor.getInt(
                            cursor.getColumnIndex(
                                CalendarContract.Events.DISPLAY_COLOR
                            )
                        )
                    )
                    calendarEvent.setStatus(
                        cursor.getInt(
                            cursor.getColumnIndex(
                                CalendarContract.Events.STATUS
                            )
                        )
                    )
                    calendarEvent.setStart(
                        cursor.getLong(
                            cursor.getColumnIndex(
                                CalendarContract.Events.DTSTART
                            )
                        )
                    )
                    calendarEvent.setEnd(
                        cursor.getLong(
                            cursor.getColumnIndex(
                                CalendarContract.Events.DTEND
                            )
                        )
                    )
                    calendarEvent.setDuration(
                        cursor.getString(
                            cursor.getColumnIndex(
                                CalendarContract.Events.DURATION
                            )
                        )
                    )
                    calendarEvent.setEventTimeZone(
                        cursor.getString(
                            cursor.getColumnIndex(
                                CalendarContract.Events.EVENT_TIMEZONE
                            )
                        )
                    )
                    calendarEvent.setEventEndTimeZone(
                        cursor.getString(
                            cursor.getColumnIndex(
                                CalendarContract.Events.EVENT_END_TIMEZONE
                            )
                        )
                    )
                    calendarEvent.setAllDay(
                        cursor.getInt(
                            cursor.getColumnIndex(
                                CalendarContract.Events.ALL_DAY
                            )
                        )
                    )
                    calendarEvent.setAccessLevel(
                        cursor.getInt(
                            cursor.getColumnIndex(
                                CalendarContract.Events.ACCESS_LEVEL
                            )
                        )
                    )
                    calendarEvent.setAvailability(
                        cursor.getInt(
                            cursor.getColumnIndex(
                                CalendarContract.Events.AVAILABILITY
                            )
                        )
                    )
                    calendarEvent.setHasAlarm(
                        cursor.getInt(
                            cursor.getColumnIndex(
                                CalendarContract.Events.HAS_ALARM
                            )
                        )
                    )
                    calendarEvent.setRRule(
                        cursor.getString(
                            cursor.getColumnIndex(
                                CalendarContract.Events.RRULE
                            )
                        )
                    )
                    calendarEvent.setRDate(
                        cursor.getString(
                            cursor.getColumnIndex(
                                CalendarContract.Events.RDATE
                            )
                        )
                    )
                    calendarEvent.setHasAttendeeData(
                        cursor.getInt(
                            cursor.getColumnIndex(
                                CalendarContract.Events.HAS_ATTENDEE_DATA
                            )
                        )
                    )
                    calendarEvent.setLastDate(
                        cursor.getInt(
                            cursor.getColumnIndex(
                                CalendarContract.Events.LAST_DATE
                            )
                        )
                    )
                    calendarEvent.setOrganizer(
                        cursor.getString(
                            cursor.getColumnIndex(
                                CalendarContract.Events.ORGANIZER
                            )
                        )
                    )
                    calendarEvent.setIsOrganizer(
                        cursor.getString(
                            cursor.getColumnIndex(
                                CalendarContract.Events.IS_ORGANIZER
                            )
                        )
                    )


                    // ----------------------- 开始查询事件提醒 ------------------------------
                    val REMINDER_PROJECTION = arrayOf(
                        CalendarContract.Reminders._ID,  // 在表中的列索引0
                        CalendarContract.Reminders.EVENT_ID,  // 在表中的列索引1
                        CalendarContract.Reminders.MINUTES,  // 在表中的列索引2
                        CalendarContract.Reminders.METHOD
                    )
                    val selection2 = "(" + CalendarContract.Reminders.EVENT_ID + " = ?)"
                    val selectionArgs2 =
                        arrayOf<String>(java.lang.String.valueOf(calendarEvent.getId()))
                    context.getContentResolver().query(
                        uri2, REMINDER_PROJECTION,
                        selection2, selectionArgs2, null
                    ).use { reminderCursor ->
                        if (null != reminderCursor) {
                            if (reminderCursor.moveToFirst()) {
                                val reminders: MutableList<EventReminders> = ArrayList()
                                do {
                                    val reminders1: EventReminders =
                                        EventReminders()
                                    reminders.add(reminders1)
                                    reminders1.reminderId =
                                        reminderCursor.getLong(
                                            reminderCursor.getColumnIndex(CalendarContract.Reminders._ID)
                                        )

                                    reminders1.reminderEventID =
                                        reminderCursor.getLong(
                                            reminderCursor.getColumnIndex(CalendarContract.Reminders.EVENT_ID)
                                        )

                                    reminders1.reminderMinute =
                                        reminderCursor.getInt(
                                            reminderCursor.getColumnIndex(CalendarContract.Reminders.MINUTES)
                                        )

                                    reminders1.reminderMethod =
                                        reminderCursor.getInt(
                                            reminderCursor.getColumnIndex(CalendarContract.Reminders.METHOD)
                                        )
                                } while (reminderCursor.moveToNext())
                                calendarEvent.setReminders(reminders)
                            }
                        }
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
            return result
        }

        /**
         * 判断日历账户中是否已经存在此事件
         *
         * @param begin 事件开始时间
         * @param end   事件结束时间
         * @param title 事件标题
         */
        fun isEventAlreadyExist(context: Context, begin: Long, end: Long, title: String?): Boolean {
            val projection = arrayOf(
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END,
                CalendarContract.Instances.TITLE
            )
            val cursor: Cursor? = CalendarContract.Instances.query(
                context.getContentResolver(), projection, begin, end, title
            )
            return (null != cursor && cursor.moveToFirst()
                    && cursor.getString(
                cursor.getColumnIndex(CalendarContract.Instances.TITLE)
            ).equals(title))
        }


        // ------------------------------- 日历事件相关 -----------------------------------

        // ------------------------------- 日历事件相关 -----------------------------------
        /**
         * 组装日历事件
         */
        private fun setupEvent(calendarEvent: CalendarEvent, event: ContentValues) {
            // 事件开始时间
            event.put(CalendarContract.Events.DTSTART, calendarEvent.getStart())
            // 事件结束时间
            event.put(CalendarContract.Events.DTEND, calendarEvent.getEnd())
            // 事件标题
            event.put(CalendarContract.Events.TITLE, calendarEvent.getTitle())
            // 事件描述(对应手机系统日历备注栏)
            event.put(CalendarContract.Events.DESCRIPTION, calendarEvent.getDescription())
            // 事件地点
            event.put(CalendarContract.Events.EVENT_LOCATION, calendarEvent.getEventLocation())
            // 事件时区
            event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID())
            // 定义事件的显示，默认即可
            event.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT)
            // 事件的状态
            event.put(CalendarContract.Events.STATUS, 0)
            // 设置事件提醒警报可用
            event.put(CalendarContract.Events.HAS_ALARM, 1)
            // 设置事件忙
            event.put(
                CalendarContract.Events.AVAILABILITY,
                CalendarContract.Events.AVAILABILITY_BUSY
            )
            if (null != calendarEvent.getRRule()) {
                // 设置事件重复规则
                event.put(
                    CalendarContract.Events.RRULE,
                    getFullRRuleForRRule(
                        calendarEvent.getRRule()!!,
                        calendarEvent.getStart(), calendarEvent.getEnd()
                    )
                )
            }
        }

        /**
         * 获取完整的重复规则(包含终止时间)
         *
         * @param rRule     重复规则
         * @param beginTime 开始时间
         * @param endTime   结束时间
         */
        private fun getFullRRuleForRRule(rRule: String, beginTime: Long, endTime: Long): String? {
            builder.delete(0, builder.length)
            return when (rRule) {
                RRuleConstant.REPEAT_WEEKLY_BY_MO, RRuleConstant.REPEAT_WEEKLY_BY_TU, RRuleConstant.REPEAT_WEEKLY_BY_WE, RRuleConstant.REPEAT_WEEKLY_BY_TH, RRuleConstant.REPEAT_WEEKLY_BY_FR, RRuleConstant.REPEAT_WEEKLY_BY_SA, RRuleConstant.REPEAT_WEEKLY_BY_SU -> builder.append(
                    rRule
                ).append(Util.Util.getFinalRRuleMode(endTime)).toString()
                RRuleConstant.REPEAT_CYCLE_WEEKLY -> builder.append(rRule)
                    .append(Util.Util.getWeekForDate(beginTime)).append("; UNTIL = ")
                    .append(Util.Util.getFinalRRuleMode(endTime)).toString()
                RRuleConstant.REPEAT_CYCLE_MONTHLY -> builder.append(rRule)
                    .append(Util.Util.getDayOfMonth(beginTime))
                    .append("; UNTIL = ").append(Util.Util.getFinalRRuleMode(endTime)).toString()
                else -> rRule
            }
        }


        // ------------------------------- 通过Intent启动系统日历 -----------------------------------

        /*
            日历的Intent对象：
               动作                  描述                         附加功能
            ACTION_VIEW        打开指定时间的日历                    无
            ACTION_VIEW        查看由EVENT_ID指定的事件        开始时间，结束时间
            ACTION_EDIT        编辑由EVENT_ID指定的事件        开始时间，结束时间
            ACTION_INSERT      创建一个事件                         所有
            Intent对象的附加功能：
            Events.TITLE                                        事件标题
            CalendarContract.EXTRA_EVENT_BEGIN_TIME             开始时间
            CalendarContract.EXTRA_EVENT_END_TIME               结束时间
            CalendarContract.EXTRA_EVENT_ALL_DAY                是否全天
            Events.EVENT_LOCATION                               事件地点
            Events.DESCRIPTION                                  事件描述
            Intent.EXTRA_EMALL                                  受邀者电子邮件,用逗号分隔
            Events.RRULE                                        事件重复规则
            Events.ACCESS_LEVEL                                 事件私有还是公有
            Events.AVAILABILITY                                 预定事件是在忙时计数还是闲时计数
         */

        // ------------------------------- 通过Intent启动系统日历 -----------------------------------
        /*
            日历的Intent对象：
               动作                  描述                         附加功能
            ACTION_VIEW        打开指定时间的日历                    无
            ACTION_VIEW        查看由EVENT_ID指定的事件        开始时间，结束时间
            ACTION_EDIT        编辑由EVENT_ID指定的事件        开始时间，结束时间
            ACTION_INSERT      创建一个事件                         所有
            Intent对象的附加功能：
            Events.TITLE                                        事件标题
            CalendarContract.EXTRA_EVENT_BEGIN_TIME             开始时间
            CalendarContract.EXTRA_EVENT_END_TIME               结束时间
            CalendarContract.EXTRA_EVENT_ALL_DAY                是否全天
            Events.EVENT_LOCATION                               事件地点
            Events.DESCRIPTION                                  事件描述
            Intent.EXTRA_EMALL                                  受邀者电子邮件,用逗号分隔
            Events.RRULE                                        事件重复规则
            Events.ACCESS_LEVEL                                 事件私有还是公有
            Events.AVAILABILITY                                 预定事件是在忙时计数还是闲时计数
         */
        /**
         * 通过Intent启动系统日历新建事件界面插入新的事件
         *
         *
         * TIP: 这将不再需要声明读写日历数据的权限
         *
         * @param beginTime 事件开始时间
         * @param endTime   事件结束时间
         * @param title     事件标题
         * @param des       事件描述
         * @param location  事件地点
         * @param isAllDay  事件是否全天
         */
        fun startCalendarForIntentToInsert(
            context: Context, beginTime: Long, endTime: Long,
            title: String?, des: String?, location: String?,
            isAllDay: Boolean
        ) {
            checkCalendarAccount(context)


            // FIXME: 2019/3/6 VIVO手机无法打开界面，找不到对应的Activity  com.bbk.calendar
            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
                .putExtra(CalendarContract.Events.ALL_DAY, isAllDay)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, des)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
            if (null != intent.resolveActivity(context.getPackageManager())) {
                context.startActivity(intent)
            }
        }

        /**
         * 通过Intent启动系统日历来编辑指定ID的事件
         *
         *
         *
         * @param eventID 要编辑的事件ID
         */
        fun startCalendarForIntentToEdit(context: Context, eventID: Long) {
            checkCalendarAccount(context)
            val uri: Uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID)
            val intent = Intent(Intent.ACTION_EDIT).setData(uri)
            if (null != intent.resolveActivity(context.getPackageManager())) {
                context.startActivity(intent)
            }
        }

        /**
         * 通过Intent启动系统日历来查看指定ID的事件
         *
         * @param eventID 要查看的事件ID
         */
        fun startCalendarForIntentToView(context: Context, eventID: Long) {
            checkCalendarAccount(context)
            val uri: Uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID)
            val intent = Intent(Intent.ACTION_VIEW).setData(uri)
            if (null != intent.resolveActivity(context.getPackageManager())) {
                context.startActivity(intent)
            }
        }


        // ----------------------------- 日历账户名相关设置 -----------------------------------

        // ----------------------------- 日历账户名相关设置 -----------------------------------
        fun getCalendarName(): String? {
            return CALENDAR_NAME
        }

        fun setCalendarName(calendarName: String) {
            CALENDAR_NAME = calendarName
        }

        fun getCalendarAccountName(): String? {
            return CALENDAR_ACCOUNT_NAME
        }

        fun setCalendarAccountName(calendarAccountName: String) {
            CALENDAR_ACCOUNT_NAME = calendarAccountName
        }

        fun getCalendarDisplayName(): String? {
            return CALENDAR_DISPLAY_NAME
        }

        fun setCalendarDisplayName(calendarDisplayName: String) {
            CALENDAR_DISPLAY_NAME = calendarDisplayName
        }
    }


}

class CalendarEvent {
    // ----------------------- 事件属性 -----------------------

    // ----------------------- 事件属性 -----------------------
    /**
     * 事件在表中的ID
     */
    private var id: Long = 0

    /**
     * 事件所属日历账户的ID
     */
    private var calID: Long = 0
    private var title: String? = null
    private var description: String? = null
    private var eventLocation: String? = null
    private var displayColor = 0
    private var status = 0
    private var start: Long = 0
    private var end: Long = 0
    private var duration: String? = null
    private var eventTimeZone: String? = null
    private var eventEndTimeZone: String? = null
    private var allDay = 0
    private var accessLevel = 0
    private var availability = 0
    private var hasAlarm = 0
    private var rRule: String? = null
    private var rDate: String? = null
    private var hasAttendeeData = 0
    private var lastDate = 0
    private var organizer: String? = null
    private var isOrganizer: String? = null


    // ----------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------
    /**
     * 注：此属性不属于CalendarEvent
     * 这里只是为了方便构造方法提供事件提醒时间
     */
    private var advanceTime = 0
    // ----------------------------------------------------------------------------------------


    // ----------------------------------------------------------------------------------------
    // ----------------------- 事件提醒属性 -----------------------
    private var reminders: List<EventReminders?>? = null

    constructor() {}

    /**
     * 用于方便添加完整日历事件提供一个构造方法
     *
     * @param title         事件标题
     * @param description   事件描述
     * @param eventLocation 事件地点
     * @param start         事件开始时间
     * @param end           事件结束时间  If is not a repeat event, this param is must need else null
     * @param advanceTime   事件提醒时间[AdvanceTime]
     * (If you don't need to remind the incoming parameters -2)
     * @param rRule         事件重复规则  [RRuleConstant]  `null` if dose not need
     */
    constructor(
        title: String?, description: String?, eventLocation: String?,
        start: Long, end: Long, advanceTime: Int, rRule: String?
    ) {
        this.title = title
        this.description = description
        this.eventLocation = eventLocation
        this.start = start
        this.end = end
        this.advanceTime = advanceTime
        this.rRule = rRule
    }

    fun getAdvanceTime(): Int {
        return advanceTime
    }

    fun setAdvanceTime(advanceTime: Int) {
        this.advanceTime = advanceTime
    }

    fun getId(): Long {
        return id
    }

    fun setId(id: Long) {
        this.id = id
    }

    fun getCalID(): Long {
        return calID
    }

    fun setCalID(calID: Long) {
        this.calID = calID
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getDescription(): String? {
        return description
    }

    fun setDescription(description: String?) {
        this.description = description
    }

    fun getEventLocation(): String? {
        return eventLocation
    }

    fun setEventLocation(eventLocation: String?) {
        this.eventLocation = eventLocation
    }

    fun getDisplayColor(): Int {
        return displayColor
    }

    fun setDisplayColor(displayColor: Int) {
        this.displayColor = displayColor
    }

    fun getStatus(): Int {
        return status
    }

    fun setStatus(status: Int) {
        this.status = status
    }

    fun getStart(): Long {
        return start
    }

    fun setStart(start: Long) {
        this.start = start
    }

    fun getEnd(): Long {
        return end
    }

    fun setEnd(end: Long) {
        this.end = end
    }

    fun getDuration(): String? {
        return duration
    }

    fun setDuration(duration: String?) {
        this.duration = duration
    }

    fun getEventTimeZone(): String? {
        return eventTimeZone
    }

    fun setEventTimeZone(eventTimeZone: String?) {
        this.eventTimeZone = eventTimeZone
    }

    fun getEventEndTimeZone(): String? {
        return eventEndTimeZone
    }

    fun setEventEndTimeZone(eventEndTimeZone: String?) {
        this.eventEndTimeZone = eventEndTimeZone
    }

    fun getAllDay(): Int {
        return allDay
    }

    fun setAllDay(allDay: Int) {
        this.allDay = allDay
    }

    fun getAccessLevel(): Int {
        return accessLevel
    }

    fun setAccessLevel(accessLevel: Int) {
        this.accessLevel = accessLevel
    }

    fun getAvailability(): Int {
        return availability
    }

    fun setAvailability(availability: Int) {
        this.availability = availability
    }

    fun getHasAlarm(): Int {
        return hasAlarm
    }

    fun setHasAlarm(hasAlarm: Int) {
        this.hasAlarm = hasAlarm
    }

    fun getRRule(): String? {
        return rRule
    }

    fun setRRule(rRule: String?) {
        this.rRule = rRule
    }

    fun getRDate(): String? {
        return rDate
    }

    fun setRDate(rDate: String?) {
        this.rDate = rDate
    }

    fun getHasAttendeeData(): Int {
        return hasAttendeeData
    }

    fun setHasAttendeeData(hasAttendeeData: Int) {
        this.hasAttendeeData = hasAttendeeData
    }

    fun getLastDate(): Int {
        return lastDate
    }

    fun setLastDate(lastDate: Int) {
        this.lastDate = lastDate
    }

    fun getOrganizer(): String? {
        return organizer
    }

    fun setOrganizer(organizer: String?) {
        this.organizer = organizer
    }

    fun getIsOrganizer(): String? {
        return isOrganizer
    }

    fun setIsOrganizer(isOrganizer: String?) {
        this.isOrganizer = isOrganizer
    }

    fun getReminders(): List<EventReminders?>? {
        return reminders
    }

    fun setReminders(reminders: List<EventReminders?>?) {
        this.reminders = reminders
    }


    override fun hashCode(): Int {
        return (id * 37 + calID).toInt()
    }
}

/**
 * 事件提醒
 */
class EventReminders {
    // ----------------------- 事件提醒属性 -----------------------
    var reminderId: Long = 0
    var reminderEventID: Long = 0
    var reminderMinute = 0
    var reminderMethod = 0
}

class Util {
    object Util {
        /**
         * 获取日历事件结束日期
         *
         * @param time time in ms
         */
        private fun getEndDate(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            return format.format(date)
        }

        /**
         * 获取最终日历事件重复规则
         *
         * @param time time in ms
         * "T235959" [#51][com.kyle.calendarprovider.calendar.RRuleConstant]
         */
        fun getFinalRRuleMode(time: Long): String? {
            return getEndDate(time) + "T235959Z"
        }

        /**
         * 格式化星期
         */
        private fun formatWeek(week: Int): String? {
            return when (week) {
                0 -> "SU"
                1 -> "MO"
                2 -> "TU"
                3 -> "WE"
                4 -> "TH"
                5 -> "FR"
                6 -> "SA"
                else -> null
            }
        }

        /**
         * 获取重复周
         *
         * @param time time in ms
         */
        fun getWeekForDate(time: Long): String? {
            val date = Date(time)
            val calendar = Calendar.getInstance()
            calendar.time = date
            var week = calendar[Calendar.DAY_OF_WEEK] - 1
            if (week < 0) {
                week = 0
            }
            return formatWeek(week)
        }

        /**
         * 获取指定时间段在一个月中的哪一天
         *
         * @param time time in ms
         */
        fun getDayOfMonth(time: Long): Int {
            val date = Date(time)
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar[Calendar.DAY_OF_MONTH]
        }

        /**
         * check null
         */
        fun checkContextNull(context: Context?) {
            requireNotNull(context) { "context can not be null" }
        }
    }


}

// todo 加入测试
class CalendarTest {
    object Test {
        fun insert(context: Context) {
            val calendarEvent = CalendarEvent(
                "马上吃饭",
                "吃好吃的",
                "南信院二食堂",
                System.currentTimeMillis(),
                System.currentTimeMillis() + 60000,
                0, null
            )


            // 添加事件
            val result: Int = CalendarManager.Manager.addCalendarEvent(context, calendarEvent)
            if (result == 0) {
                Toast.makeText(context, "插入成功", Toast.LENGTH_SHORT).show()
            } else if (result == -1) {
                Toast.makeText(context, "插入失败", Toast.LENGTH_SHORT).show()
            } else if (result == -2) {
                Toast.makeText(context, "没有权限", Toast.LENGTH_SHORT).show()
            }
        }


        fun delete(context: Context) {
            // 删除事件
            // 删除事件
            val calID2: Long = CalendarManager.Manager.obtainCalendarAccountID(context)
            val events2: List<CalendarEvent>? =
                CalendarManager.Manager.queryAccountEvent(context, calID2)
            if (null != events2) {
                if (events2.isEmpty()) {
                    Toast.makeText(context, "没有事件可以删除", Toast.LENGTH_SHORT).show()
                } else {
                    val eventID = events2[0].getId()
                    val result2: Int = CalendarManager.Manager.deleteCalendarEvent(context, eventID)
                    if (result2 == -2) {
                        Toast.makeText(context, "没有权限", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "查询失败", Toast.LENGTH_SHORT).show()
            }
        }

        fun update(context: Context) {
            // 更新事件
            // 更新事件
            val calID: Long = CalendarManager.Manager.obtainCalendarAccountID(context)
            val events: List<CalendarEvent>? =
                CalendarManager.Manager.queryAccountEvent(context, calID)
            if (null != events) {
                if (events.isEmpty()) {
                    Toast.makeText(context, "没有事件可以更新", Toast.LENGTH_SHORT).show()
                } else {
                    val eventID = events[0].getId()
                    val result3: Int = CalendarManager.Manager.updateCalendarEventTitle(
                        context, eventID, "改吃晚饭的房间第三方监督司法"
                    )
                    if (result3 == 1) {
                        Toast.makeText(context, "更新成功", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "更新失败", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "查询失败", Toast.LENGTH_SHORT).show()
            }
        }

        fun query(context: Context) {
            // 查询事件
            // 查询事件
            val calID4: Long = CalendarManager.Manager.obtainCalendarAccountID(context)
            val events4: List<CalendarEvent>? =
                CalendarManager.Manager.queryAccountEvent(context, calID4)
            val stringBuilder4 = java.lang.StringBuilder()
            if (null != events4) {
                for (event in events4) {
                    stringBuilder4.append(events4.toString()).append("\n")
                }
                Toast.makeText(context, "查询成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "查询失败", Toast.LENGTH_SHORT).show()
            }
        }

        fun edit(context: Context) {
            // 启动系统日历进行编辑事件
            CalendarManager.Manager.startCalendarForIntentToInsert(
                context, System.currentTimeMillis(),
                System.currentTimeMillis() + 60000, "哈", "哈哈哈哈", "蒂埃纳",
                false
            );
        }

        fun edit2(context: Context) {
            // 启动系统日历进行编辑事件
            if (CalendarManager.Manager.isEventAlreadyExist(
                    context, 1552986006309L,
                    155298606609L, "马上吃饭"
                )
            ) {
                Toast.makeText(context, "存在", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "不存在", Toast.LENGTH_SHORT).show();
            }
        }
    }
}