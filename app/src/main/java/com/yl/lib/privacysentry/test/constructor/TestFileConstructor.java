package com.yl.lib.privacysentry.test.constructor;

import android.os.Build;
import android.system.Os;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author yulun
 * @since 2022-11-22 16:36
 */
public class TestFileConstructor {
    public TestFileConstructor(@NonNull File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file cannot be null");
        }
        file.getAbsolutePath();
    }

    public TestFileConstructor(@NonNull String filename) throws IOException {
        if (filename == null) {
            throw new NullPointerException("filename cannot be null");
        }
        new File(filename).getAbsolutePath();
    }

    public TestFileConstructor(@NonNull FileDescriptor fileDescriptor) throws IOException {
        if (fileDescriptor == null) {
            throw new NullPointerException("fileDescriptor cannot be null");
        }
        boolean isFdDuped = false;
        try {
            // asm 7.1的时候，这个写法，会导致数组越界，升级到9.1好了，浪费了一天时间 坑爹
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fileDescriptor = Os.dup(fileDescriptor);
            }
                isFdDuped = true;
        } catch (Exception e) {
            throw new IOException("Failed to duplicate file descriptor", e);
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(fileDescriptor);
        } finally {
            if (isFdDuped) {
                in.close();
            }
        }
    }

    public TestFileConstructor(@NonNull InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new NullPointerException("inputStream cannot be null");
        }
    }
}
