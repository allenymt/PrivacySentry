package com.yl.lib.privacysentry.contact

import android.Manifest
import android.content.*
import android.net.Uri
import android.provider.ContactsContract
import com.yl.lib.sentry.hook.util.PrivacyLog
import java.lang.StringBuilder
import android.provider.CallLog

import android.content.pm.PackageManager
import android.database.Cursor
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author yulun
 * @since 2022-01-17 14:42
 */
class ContactManager {
    object Manager{
        /**
         * 获取通讯录中所有联系人的简单信息
         * @throws Throwable
         */
        fun testGetAllContact(context: Context) {
            //获取联系人信息的Uri
            val uri: Uri = ContactsContract.Contacts.CONTENT_URI
            //获取ContentResolver
            val contentResolver: ContentResolver = context.getContentResolver()
            //查询数据，返回Cursor
            val cursor = contentResolver.query(uri, null, null, null, null)
            while (cursor!!.moveToNext()) {
                val sb = StringBuilder()
                //获取联系人的ID
                val contactId =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                //获取联系人的姓名
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                //构造联系人信息
                sb.append("contactId=").append(contactId).append(",Name=").append(name)
                //查询电话类型的数据操作
                val phones = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null, null
                )
                while (phones!!.moveToNext()) {
                    val phoneNumber = phones.getString(
                        phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        )
                    )
                    //添加Phone的信息
                    sb.append(",Phone=").append(phoneNumber)
                }
                phones.close()

                //查询Email类型的数据操作
                val emails = contentResolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,
                    null, null
                )
                while (emails!!.moveToNext()) {
                    val emailAddress = emails.getString(
                        emails.getColumnIndex(
                            ContactsContract.CommonDataKinds.Email.DATA
                        )
                    )
                    //添加Email的信息
                    sb.append(",Email=").append(emailAddress)
                }
                emails.close()
                PrivacyLog.i("读取联系人 ${sb.toString()}")
            }
            cursor.close()
        }

        /**添加联系人的第一种方法：
         * 首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
         * 这时后面插入data表的依据，只有执行空值插入，才能使插入的联系人在通讯录里面可见
         */
        fun testInsert(context: Context) {
            val values = ContentValues()
            //首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
            val rawContactUri: Uri =
                context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values)!!
            //获取id
            val rawContactId = ContentUris.parseId(rawContactUri)
            //往data表入姓名数据
            values.clear()
            values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId) //添加id
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) //添加内容类型（MIMETYPE）
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, "凯风自南") //添加名字，添加到first name位置
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values)
            //往data表入电话数据
            values.clear()
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, "13921009789")
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values)
            //往data表入Email数据
            values.clear()
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
            values.put(ContactsContract.CommonDataKinds.Email.DATA, "kesenhoo@gmail.com")
            values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values)
        }

        /**添加联系人的第二种方法：
         * 批量添加联系人
         * @throws Throwable
         */
        fun testSave(context: Context) {
            //官方文档位置：reference\android\provider\ContactsContract.RawContacts.html
            //建立一个ArrayList存放批量的参数
            val ops = ArrayList<ContentProviderOperation>()
            val rawContactInsertIndex: Int = ops.size
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build()
            )
            //官方文档位置：reference\android\provider\ContactsContract.Data.html
            //withValueBackReference后退引用前面联系人的id
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, "小明")
                    .build()
            )
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "13671323809")
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "手机号")
                    .build()
            )
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, "kesen@gmail.com")
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build()
            )
            val results: Array<ContentProviderResult> = context.getContentResolver()
                .applyBatch(ContactsContract.AUTHORITY, ops)
            for (result in results) {
                PrivacyLog.i("增加联系人 sb.toString()")
            }
        }

    }
}