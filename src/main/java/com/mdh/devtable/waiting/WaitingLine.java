package com.mdh.devtable.waiting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

@Component
public class WaitingLine {

    // 매장의 아이디, 매장의 웨이팅 라인
    private static final Map<Long, SortedSet<WaitingInfo>> waitingLine = new HashMap<>();

    public void save(Long shopId, Long waitingId, LocalDateTime createdDate) {
        // 해당 매장에 대한 라인이 없다면
        if (!waitingLine.containsKey(shopId)) {
            var waitingInfos = new TreeSet<>(Comparator.comparing(WaitingInfo::getWaitingStartTime));
            waitingLine.put(shopId, waitingInfos);
        }
        var waitingInfo = new WaitingInfo(waitingId, createdDate);
        var waitingInfos = waitingLine.get(shopId);
        waitingInfos.add(waitingInfo);
    }

    public int findRank(Long shopId, Long waitingId, LocalDateTime createdDate) {
        var waitingInfo = new WaitingInfo(waitingId, createdDate);
        var waitingInfos = waitingLine.get(shopId);
        return waitingInfos.headSet(waitingInfo).size() + 1;
    }

    @Getter
    @AllArgsConstructor
    static class WaitingInfo {
        private Long waitingId;
        private LocalDateTime waitingStartTime;
    }
}