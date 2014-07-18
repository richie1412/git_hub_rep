package com.wxxr.test;

import junit.framework.Assert;
import android.content.Context;
import android.test.AndroidTestCase;

import com.wxxr.callhelper.qg.service.LouHuaHuizhiModule;

public class TestAllReminder extends AndroidTestCase {

    public void testIsMatch() {

        LouHuaHuizhiModuleExt m = new LouHuaHuizhiModuleExt();
        m.initLouhuaRule();
        boolean result = m.isMatch("15897987737在06月27日19:09呼叫您1次。", "13810052592");
        //湖北    .*[0-9]{11,}在.*月.*日.*呼叫您.*次.*
        Assert.assertEquals(true, result);
        result = m.isMatch("中国移动提醒您：12345678910在1月1日呼叫您2次，请回复！", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("张三在06月27日19:09呼叫您1次", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("15897987737呼叫你。", "13810052592");
        Assert.assertEquals(false, result);
        //.*[0-9]{11,}在.*共呼叫您.*次.*
        result = m.isMatch("15897987737在06月27日19:09共呼叫您1次", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("中国移动提醒您：15897987737在06月27日19:09共呼叫您1次，请回复", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("张三在06月27日19:09呼叫您1次", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("张三在06月27日19:09呼叫您1次", "13810052592");
        Assert.assertEquals(false, result);
        //.*您好！您所拨打的电话已转入来电提醒，您的本次呼叫记录将在第一时间以短信告知对方.*
        result = m.isMatch("您好！您所拨打的电话已转入来电提醒，您的本次呼叫记录将在第一时间以短信告知对方", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("xxxx您好！您所拨打的电话已转入来电提醒，您的本次呼叫记录将在第一时间以短信告知对方。xxxx", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("您所拨打的电话已转入来电提醒，您的本次呼叫记录将在第一时间以短信告知对方。", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("您所拨打的电话已转入来电提醒，xxxx,您的本次呼叫记录将在第一时间以短信告知对方。", "13810052592");
        Assert.assertEquals(false, result);
        //北京   .*中国移动北京公司来电提醒：.*呼叫过您.*
        result = m.isMatch("中国移动北京公司来电提醒：13810052592于XX年XX月XX日XX时XX分呼叫过您", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("中国移动北京公司来电提醒：13810052592于XX年XX月XX日XX时XX分呼叫过您，请回复！", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("13810052592于XX年XX月XX日XX时XX分呼叫过您", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("中国移动北京公司来xx电提醒您：13810052592", "13810052592");
        Assert.assertEquals(false, result);
        //.*中国移动北京公司来电提醒：.*呼叫过您.*最后一次呼叫时间为.*

        result = m.isMatch("中国移动北京公司来电提醒：13810052592X于xx月xx日xx时xx分开始呼叫过您XX次，最后一次呼叫时间为XX月XX日XX时XX分", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("中国移动北京公司来电提醒：开始呼叫过您XX次，最后一次呼叫时间为XX月XX日XX时XX分", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("13810052592X于xx月xx日xx时xx分开始呼叫过您XX次，最后一次呼叫时间为XX月XX日XX时XX分", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("中国移动北京xxx公司来电提醒:13810052592X于xx月xx日xx时xx分开始呼叫过您XX次", "13810052592");
        Assert.assertEquals(false, result);
        //辽宁 .*来电提醒：.*给您打过电话 ，可方便时回电.*
        result = m.isMatch("来电提醒：张三给您打过电话 ，可方便时回电.", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("来电提醒：在**月**日12：59给您打过电话 ，可方便时回电。", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("***********在**月**日12：59给您打过电话 ，可方便时回电。", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("来电提醒：***********在**月**日12：59给您打过电话 ", "13810052592");
        Assert.assertEquals(false, result);
        //浙江.*欢迎使用来电提醒业务，本次呼叫将以点对点短信方式提醒您呼叫的用户，谢谢使用，再见*.
        result = m.isMatch("欢迎使用来电提醒业务，本次呼叫将以点对点短信方式提醒您呼叫的用户，谢谢使用，再见", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("56789043x欢迎使用来电提醒业务，本次呼叫将以点对点短信方式提醒您呼叫的用户，谢谢使用，再见", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("欢迎使用来电提醒业务，谢谢使用，再见", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("欢迎使用来电提醒业务，本次呼叫将以点对点短信方式提醒您呼叫的用户", "13810052592");
        Assert.assertEquals(false, result);
        //  内蒙古：  .*用户.*（.*）在.*月.*日.*呼叫过您，请您及时与之联系.*-----数量增加
        result = m.isMatch("用户13847113951（内蒙古呼和浩特）在03月28日11:43呼叫过您，请您及时与之联系", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("用户（内蒙古呼和浩特）在03月28日11:43呼叫过您，请您及时与之联系", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("13847113951（内蒙古呼和浩特）在03月28日11:43呼叫过您，请您及时与之联系", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("用户13847113951在03月28日11:43呼叫了过您，请您及时与之联系", "13810052592");
        Assert.assertEquals(false, result);
        //  .*用户.*在.*月.*日.*呼叫过您.*留言.*条.*拨12550309收听.*
        result = m.isMatch("用户13847113951（内蒙古呼和浩特）在03月28日11:43呼叫过您，请您及时与之联系", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("用户（内蒙古呼和浩特）在03月28日11:43呼叫过您，请您及时与之联系", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("用户15147512986在04月10日17:31呼叫过您的电话，留言1条，拨12550309收听", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("用户15147512986（内蒙古呼和浩特）在04月10日17:31呼叫过您的电话，", "13810052592");
        Assert.assertEquals(false, result);
        // .*用户.*（.*）呼叫过您.次，最近一次来电时间为.*请您及时与之联系.*
        result = m.isMatch("用户18747982166（内蒙古呼和浩特）呼叫过您2次，最近一次来电时间为04月11日14:06，请您及时与之联系。，拨12550309收听,", "13810052592");
        result = m.isMatch("用户13847113951（内蒙古呼和浩特）在03月28日11:43呼叫过您，请您及时与之联系", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("用户（内蒙古呼和浩特）在03月28日11:43呼叫过您，请您及时与之联系", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("用户18747982166（内蒙古呼和浩特）呼叫过您2次，最近一次来电时间为04月11日14:06，", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("18747982166（内蒙古呼和浩特）呼叫过您2次，最近一次来电时间为04月11日14:06，请您及时与之联系。", "13810052592");
        Assert.assertEquals(false, result);

        //  .*用户.*（.*）呼叫过您.次，留言.条，拨12550309收听,最近呼叫于.*月.*日.*
        result = m.isMatch("来电提醒：用户18747982166（内蒙古呼和浩特）呼叫过您2次，留言1条，拨12550309收听,最近呼叫于04月11日14:0", "13810052592");
        result = m.isMatch("用户13847113951（内蒙古呼和浩特）在03月28日11:43呼叫过您，请您及时与之联系", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("用户（内蒙古呼和浩特）在03月28日11:43呼叫过您，请您及时与之联系", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch(" 用户18747982166（内蒙古呼和浩特）呼叫过您2次，拨12550309收听,最近呼叫于04月11日14:00", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch(" 用户18747982166（内蒙古呼和浩特）呼叫过您2次，留言1条，最近呼叫于04月11日14:00", "13810052592");
        Assert.assertEquals(false, result);
        //河北.*中国移动来电提醒业务提醒您：.*请及时回复.*
        result = m.isMatch("中国移动来电提醒业务提醒您：xx在xx年xx月xx时xx分给您来电，请及时回复。", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("中国移动来电提醒业务提醒您：张三给您来电，请及时回复。", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("xx在xx年xx月xx时xx分给您来电，请及时回复。", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("中国移动来电提醒业务提醒您：xx在xx年xx月xx时xx分给您来电", "13810052592");
        Assert.assertEquals(false, result);
        //山西     .*用户.*在.*月.*日.*拨打过您的电话，请及时回复.*
        result = m.isMatch("xxxx的用户13456789043在2013年07月01日11时19分拨打过您的电话，请及时回复", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("用户13456789043在2013年07月01日11时19分拨打过您的电话，请及时回复", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("xxxx的用户13456789043在2013年07月01日11时19分拨打过您的电话，", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("xxxx的用户13456789043拨打过您的电话，请及时回复", "13810052592");
        Assert.assertEquals(false, result);
        // .*用户.*在.*月.*日.*拨打过您的电话，请及时回复.*
        result = m.isMatch("xx（省名）xx（地区）的用户13456789043在2013年07月01日11时19分拨打过您的电话，请及时回复", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("用户13456789043在2013年07月01日11时19分拨打过您的电话，请及时回复", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("xx（省名）xx（地区）的用户13456789043在2013年07月01日11时19分拨打过您的电话，", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("xx（省名）xx（地区）的用户13456789043拨打过您的电话，请及时回复", "13810052592");
        Assert.assertEquals(false, result);
        //   .*用户.*共呼叫您.*最近一次呼叫时间为.*请及时回复.*
        result = m.isMatch("xx（省名）xx（地区）的用户13456789043在2013年07月01日11时19分拨打过您的电话，请及时回复", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("用户13456789043在2013年07月01日11时19分拨打过您的电话，请及时回复", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("xx（省名）xx（地区）的用户13456789043共呼叫您4次，最近一次呼叫时间为2013年07月01日11时19分，", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("xx（省名）xx（地区）的用户13456789043共呼叫您4次，请及时回复", "13810052592");
        Assert.assertEquals(false, result);
        // 贵州   1.           .*[0-9]{11}在.*来电.*
        result = m.isMatch("15685113021在2013/07/0214：30：39来电。", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("12345678900在2013/07/0214：30：39来电。", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("在2013/07/0214：30：39来了电话。", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("15685113021在2013/07/0214：30：39给您打过电话。", "13810052592");
        Assert.assertEquals(false, result);
        //   .*[0-9]{11}在.*来电，累计呼叫.次.*

        result = m.isMatch("15685113021在2013/07/0214：30：+39来电，累计呼叫3次。,", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("15685113021在来电，累计呼叫3次。", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("1568511在2013/07/0214：30：+39来电，回复一下，累计呼叫3次。", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("在2013/07/0214：30：+39来电，", "13810052592");
        Assert.assertEquals(false, result);

        //        .*[0-9]{11}在.*来电。.*

        result = m.isMatch("15685113021在2013/07/0214：30：39来电。请及时回复", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("15685113021在2013/07/0214：30：39来电。", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("15685113021在2013/07/0214：30：39", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("在2013/07/0214：30：39来电。xx（省名）xx（地区）的用户13456789043共呼叫您4次，请及时回复", "13810052592");
        Assert.assertEquals(false, result);
        //      .*[0-9]{11}在.*来电，累计呼叫.次.*

        result = m.isMatch("15685113021在2013/07/0214：30：39来电，累计呼叫3次，请及时回复", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("15685113021在来电，累计呼叫3次，请及时回复", "13810052592");
        Assert.assertEquals(true, result);
        result = m.isMatch("在2013/07/0214：30：39来电，累计呼叫3次", "13810052592");
        Assert.assertEquals(false, result);
        result = m.isMatch("156813021在2013/07/0214：30：39来电", "13810052592");
        Assert.assertEquals(false, result);

    }

    private class LouHuaHuizhiModuleExt extends LouHuaHuizhiModule {
        protected Context getAndroidContext() {
            return getContext();
        }
    }
}
