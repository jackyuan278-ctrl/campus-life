package com.campuslife.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityStatusTask {

    /**
     * 每分钟扫描一次，由你实现（面试深挖区）：
     * status=1（报名中）且 now >= startTime → 2（进行中）；
     * status=2（进行中）且 now >= endTime → 3（已结束）；
     * 报名截止时间已过但活动未开始的属于正常状态，不动。
     */
    @Scheduled(cron = "0 * * * * ?")
    public void updateActivityStatus() {
        // TODO 由你实现：两条 UpdateWrapper 批量改状态，不必逐条 select
        //   set status=2 where status=1 and startTime<=now；set status=3 where status=2 and endTime<=now
        log.debug("ActivityStatusTask tick");
    }
}
