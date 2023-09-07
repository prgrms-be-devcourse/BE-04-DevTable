package com.mdh.devtable.waiting.infra.persistence;

import com.mdh.devtable.waiting.infra.persistence.dto.WaitingInfo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlainWaitingLine implements WaitingLine {

    // 매장의 아이디, 매장의 웨이팅 라인
    private final Map<Long, SortedSet<WaitingInfo>> waitingLine = new ConcurrentHashMap<>();

    public void save(Long shopId, Long waitingId, LocalDateTime createdDate) {
        // 해당 매장에 대한 라인이 없다면
        waitingLine.putIfAbsent(shopId,
                new TreeSet<>(Comparator.comparing(WaitingInfo::waitingStartTime)));

        var waitingInfo = new WaitingInfo(waitingId, createdDate);
        var waitingInfos = waitingLine.get(shopId);
        waitingInfos.add(waitingInfo);
    }

    public int findRank(Long shopId, Long waitingId, LocalDateTime createdDate) {
        var waitingInfo = new WaitingInfo(waitingId, createdDate);
        var waitingInfos = waitingLine.get(shopId);

        return waitingInfos.headSet(waitingInfo).size() + 1;
    }

    @Override
    public void cancel(Long shopId, Long waitingId, LocalDateTime createdDate) {
        if (!waitingLine.containsKey(shopId)) {
            throw new IllegalStateException("현재 매장에 등록 된 웨이팅이 아닙니다. shopId : " + shopId + "waitingId : " + waitingId);
        }

        WaitingInfo waitingInfo = new WaitingInfo(waitingId, createdDate);
        var waitingInfos = waitingLine.get(shopId);
        waitingInfos.remove(waitingInfo);
    }
}