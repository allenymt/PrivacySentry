package com.yl.lib.privacy_replace;

import com.yl.lib.privacy_annotation.PrivacyClassReplace;
import com.yl.lib.sentry.hook.PrivacySentry;
import com.yl.lib.sentry.hook.util.PrivacyLog;
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author yulun
 * @since 2022-11-18 15:01
 */
@PrivacyClassReplace(originClass = FileInputStream.class)
public class PrivacyFileInputStream extends FileInputStream {
    public PrivacyFileInputStream(String name) throws FileNotFoundException {
        super(name);
        record(name);
    }

    public PrivacyFileInputStream(File file) throws FileNotFoundException {
        super(file);
        record(file.getAbsolutePath());
    }

    public PrivacyFileInputStream(FileDescriptor fdObj) {
        super(fdObj);
        record(fdObj.toString());
    }

    private void record(String path) {
        PrivacyProxyUtil.Util.INSTANCE.doFilePrinter("FileInputStream", "访问文件", "path is " + path,  false);
    }
}
