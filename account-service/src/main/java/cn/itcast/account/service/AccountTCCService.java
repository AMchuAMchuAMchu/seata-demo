package cn.itcast.account.service;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * Description ==> TODO
 * BelongsProject ==> seata-demo
 * BelongsPackage ==> cn.itcast.account.service
 * Version ==> 1.0
 * CreateTime ==> 2022-11-28 14:14:35
 * Author ==> _02雪乃赤瞳楪祈校条祭_艾米丽可锦木千束木更七草荠_制作委员会_start
 */
@LocalTCC
public interface AccountTCCService {

    @TwoPhaseBusinessAction(name = "deduct",commitMethod = "confirm",rollbackMethod = "cancel")
    void deduct(@BusinessActionContextParameter(paramName = "userId") String userId,
                @BusinessActionContextParameter(paramName = "money") int money);

    boolean confirm(BusinessActionContext ctx);

    boolean cancel(BusinessActionContext ctx);



}
