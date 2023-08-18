package com.yl.lib.plugin.sentry.transform.booster.classtransform.hook

import com.didiglobal.booster.transform.TransformContext
import com.yl.lib.plugin.sentry.transform.booster.asmtransform.AbsClassTransformer
import com.yl.lib.plugin.sentry.transform.manager.HookFieldManager
import com.yl.lib.plugin.sentry.transform.manager.HookMethodManager
import com.yl.lib.plugin.sentry.transform.manager.ReplaceClassManager
import com.yl.lib.plugin.sentry.util.formatName
import org.objectweb.asm.tree.ClassNode

/**
 * @author yulun
 * @since 2023-08-11 14:29
 */
open class BaseHookTransform : AbsClassTransformer() {
    override fun ignoreClass(context: TransformContext, klass: ClassNode): Boolean {
        var ignore = super.ignoreClass(context, klass)
        if (!ignore) {
            if (klass.invisibleAnnotations?.find {
                    it.desc == "Lcom/yl/lib/privacy_annotation/PrivacyClassProxy;" ||
                            it.desc == "Lcom/yl/lib/privacy_annotation/PrivacyClassReplace;" ||
                            it.desc == "Lcom/yl/lib/privacy_annotation/PrivacyClassBlack;"
                } != null) {
                return true
            }

            if (HookMethodManager.MANAGER.isProxyClass(klass.formatName()) ||
                HookFieldManager.MANAGER.isProxyClass(klass.formatName()) ||
                ReplaceClassManager.MANAGER.isProxyClass(klass.formatName())
            ) {
                return true
            }
        }
        return ignore
    }
}