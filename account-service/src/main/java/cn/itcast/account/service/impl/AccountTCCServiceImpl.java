package cn.itcast.account.service.impl;

import cn.itcast.account.entity.AccountFreeze;
import cn.itcast.account.mapper.AccountFreezeMapper;
import cn.itcast.account.mapper.AccountMapper;
import cn.itcast.account.service.AccountTCCService;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description ==> TODO
 * BelongsProject ==> seata-demo
 * BelongsPackage ==> cn.itcast.account.service.impl
 * Version ==> 1.0
 * CreateTime ==> 2022-11-28 14:27:49
 * Author ==> _02雪乃赤瞳楪祈校条祭_艾米丽可锦木千束木更七草荠_制作委员会_start
 */
@Service
@Slf4j
public class AccountTCCServiceImpl implements AccountTCCService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AccountFreezeMapper freezeMapper;

    @Override
    public void deduct(String userId, int money) {

        String xid = RootContext.getXID();

        if (freezeMapper.selectById(xid) != null) {
            return;
        }

        accountMapper.deduct(userId, money);

        AccountFreeze accountFreeze = new AccountFreeze();

        accountFreeze.setUserId(userId);

        accountFreeze.setFreezeMoney(money);

        accountFreeze.setState(AccountFreeze.State.TRY);

        accountFreeze.setXid(xid);

        freezeMapper.insert(accountFreeze);

    }

    @Override
    public boolean confirm(BusinessActionContext ctx) {
        String xid = ctx.getXid();
        int count = freezeMapper.deleteById(xid);

        return count == 1;
    }

    @Override
    public boolean cancel(BusinessActionContext ctx) {

        String xid = ctx.getXid();

        AccountFreeze accountFreeze = freezeMapper.selectById(xid);


        if (accountFreeze == null) {

            String userId = ctx.getActionContext("userId").toString();

            AccountFreeze accountFreeze1 = new AccountFreeze();

            accountFreeze1.setUserId(userId);

            accountFreeze1.setFreezeMoney(0);

            accountFreeze1.setState(AccountFreeze.State.CANCEL);

            accountFreeze1.setXid(xid);

            freezeMapper.insert(accountFreeze1);

            return true;


        }

        if (accountFreeze.getState() == AccountFreeze.State.CANCEL) {
            return true;
        }

        accountMapper.refund(accountFreeze.getUserId(), accountFreeze.getFreezeMoney());

        accountFreeze.setFreezeMoney(0);

        accountFreeze.setState(AccountFreeze.State.CANCEL);

        int count = freezeMapper.updateById(accountFreeze);

        return count == 1;
    }
}
