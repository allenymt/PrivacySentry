package com.yl.lib.plugin.sentry.transform.booster.classtransform.hook

import com.didiglobal.booster.transform.TransformContext
import com.yl.lib.plugin.sentry.util.PrivacyMoveAssetsUtil
import org.objectweb.asm.tree.ClassNode

/**
 * @author yulun
 * @since 2023-08-29 11:22
 */
class FlushHookDataTransform : BaseHookTransform(){
    override fun ignoreClass(context: TransformContext, klass: ClassNode): Boolean {
        return true
    }

    override fun onPostTransform(context: TransformContext) {
        super.onPostTransform(context)
        PrivacyMoveAssetsUtil.Asset.doFlushProxyData()
    }
}