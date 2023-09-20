package com.mdh.user.waiting.infra.persistence;

import com.mdh.common.waiting.persistence.WaitingLine;
import com.mdh.common.waiting.persistence.dto.WaitingInfo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlainWaitingLine implements WaitingLine {

    // 매장의 아이디, 매장의 웨이팅 라인
    private final Map<Long, TreeSet<WaitingInfo>> waitingLine = new ConcurrentHashMap<>();

    @Override
    public void save(Long shopId, Long waitingId, LocalDateTime issuedTime) {
        waitingLine.putIfAbsent(shopId,
            new TreeSet<>(Comparator.comparing(WaitingInfo::waitingStartTime)));

        var waitingInfo = new WaitingInfo(waitingId, issuedTime);
        var waitingInfos = waitingLine.get(shopId);
        waitingInfos.add(waitingInfo);
    }

    @Override
    public long findRank(Long shopId, Long waitingId, LocalDateTime issuedTime) {
        var waitingInfo = new WaitingInfo(waitingId, issuedTime);
        var waitingInfos = waitingLine.get(shopId);

        return waitingInfos.headSet(waitingInfo).size() + 1;
    }

    @Override
    public long findTotalWaiting(Long shopId) {
        var waitingInfos = waitingLine.get(shopId);
        return waitingInfos != null ? waitingInfos.size() : 0;
    }

    @Override
    public void cancel(Long shopId, Long waitingId, LocalDateTime issuedTime) {
        var waitingInfo = new WaitingInfo(waitingId, issuedTime);
        validExistWaiting(shopId, waitingInfo);

        var waitingInfos = waitingLine.get(shopId);
        waitingInfos.remove(waitingInfo);

    }

    private void validExistWaiting(Long shopId, WaitingInfo waitingInfo) {
        if (!waitingLine.containsKey(shopId)) {
            throw new IllegalStateException("현재 매장에 등록 된 웨이팅이 아닙니다. shopId : " + shopId +
                "waitingId : " + waitingInfo.waitingId());
        }

        if (!waitingLine.get(shopId).contains(waitingInfo)) {
            throw new IllegalStateException("현재 매장에 등록 된 웨이팅이 아닙니다. shopId : " + shopId +
                "waitingId : " + waitingInfo.waitingId());
        }
    }

    @Override
    public void postpone(Long shopId, Long waitingId, LocalDateTime preIssuedTime, LocalDateTime issuedTime) {
        var waitingInfo = new WaitingInfo(waitingId, preIssuedTime);
        validExistWaiting(shopId, waitingInfo);
        validPosponed(shopId, waitingId, preIssuedTime);

        var waitingInfos = waitingLine.get(shopId);
        waitingInfos.remove(waitingInfo);
        waitingInfos.add(new WaitingInfo(waitingId, issuedTime));
    }

    private void validPosponed(Long shopId, Long waitingId, LocalDateTime issuedTime) {
        if (findRank(shopId, waitingId, issuedTime) == findTotalWaiting(shopId)) {
            throw new IllegalStateException("미루기를 수행 할 수 없는 웨이팅 입니다. " + waitingId);
        }
    }
}