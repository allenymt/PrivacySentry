package com.yl.lib.privacysentry.calendar

/**
 * @author yulun
 * @sinice 2022-01-13 19:57
 */
class RRuleConstant {

    companion object {
        /**
         * 每天重复 - 永远
         */
        val REPEAT_CYCLE_DAILY_FOREVER = "FREQ=DAILY;INTERVAL=1"

        /**
         * 每周某天重复
         */
        val REPEAT_CYCLE_WEEKLY = "FREQ=WEEKLY;INTERVAL=1;WKST=SU;BYDAY="

        /**
         * 每月某天重复
         */
        val REPEAT_CYCLE_MONTHLY = "FREQ=WEEKLY;INTERVAL=2;WKST=SU;BYMONTHDAY ="

        /**
         * 每周重复 - 周一
         */
        val REPEAT_WEEKLY_BY_MO = "FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=MO;UNTIL="

        /**
         * 每周重复 - 周二
         */
        val REPEAT_WEEKLY_BY_TU = "FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=TU;UNTIL="

        /**
         * 每周重复 - 周三
         */
        val REPEAT_WEEKLY_BY_WE = "FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=WE;UNTIL="

        /**
         * 每周重复 - 周四
         */
        val REPEAT_WEEKLY_BY_TH = "FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=TH;UNTIL="

        /**
         * 每周重复 - 周五
         */
        val REPEAT_WEEKLY_BY_FR = "FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=FR;UNTIL="

        /**
         * 每周重复 - 周六
         */
        val REPEAT_WEEKLY_BY_SA = "FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=SA;UNTIL="

        /**
         * 每周重复 - 周日
         */
        val REPEAT_WEEKLY_BY_SU = "FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=SU;UNTIL="

        /**
         * 每年第一天和最后一天 - 永远
         */
        val REPEAT_YEARLY_FIRST_AND_LAST_FOREVER = "FREQ=YEARLY;BYYEARDAY=1,-1"
    }
}