package com.emolink.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emolink.common.Constants;
import com.emolink.common.PageResult;
import com.emolink.dto.MessageDTO;
import com.emolink.entity.PartnerMatch;
import com.emolink.entity.PartnerMessage;
import com.emolink.entity.User;
import com.emolink.exception.BusinessException;
import com.emolink.mapper.PartnerMatchMapper;
import com.emolink.mapper.PartnerMessageMapper;
import com.emolink.mapper.UserMapper;
import com.emolink.service.PartnerService;
import com.emolink.vo.MatchStatusVO;
import com.emolink.vo.MessageVO;
import com.emolink.websocket.PartnerSocketNotifier;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PartnerServiceImpl implements PartnerService {

    private final PartnerMatchMapper partnerMatchMapper;
    private final PartnerMessageMapper partnerMessageMapper;
    private final UserMapper userMapper;
    private final PartnerSocketNotifier partnerSocketNotifier;

    public PartnerServiceImpl(PartnerMatchMapper partnerMatchMapper,
                              PartnerMessageMapper partnerMessageMapper,
                              UserMapper userMapper,
                              PartnerSocketNotifier partnerSocketNotifier) {
        this.partnerMatchMapper = partnerMatchMapper;
        this.partnerMessageMapper = partnerMessageMapper;
        this.userMapper = userMapper;
        this.partnerSocketNotifier = partnerSocketNotifier;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized MatchStatusVO joinMatch(Long userId) {
        PartnerMatch activeMatch = findActiveMatch(userId);
        if (activeMatch != null) {
            return toMatchStatusVO(userId, activeMatch);
        }

        PartnerMatch waitingMatch = partnerMatchMapper.selectOne(Wrappers.<PartnerMatch>lambdaQuery()
                .eq(PartnerMatch::getStatus, Constants.STATUS_WAITING)
                .isNull(PartnerMatch::getUserBId)
                .ne(PartnerMatch::getUserAId, userId)
                .orderByAsc(PartnerMatch::getCreatedAt)
                .last("LIMIT 1"));
        if (waitingMatch != null) {
            waitingMatch.setUserBId(userId);
            waitingMatch.setStatus(Constants.STATUS_MATCHED);
            waitingMatch.setMatchedAt(LocalDateTime.now());
            partnerMatchMapper.updateById(waitingMatch);

            partnerSocketNotifier.sendMatchNotification(waitingMatch.getUserAId(), "MATCHED",
                    waitingMatch.getId(), "匹配成功！开始和你的伙伴聊天吧~");
            partnerSocketNotifier.sendMatchNotification(userId, "MATCHED",
                    waitingMatch.getId(), "匹配成功！开始和你的伙伴聊天吧~");
            return toMatchStatusVO(userId, waitingMatch);
        }

        PartnerMatch newMatch = new PartnerMatch();
        newMatch.setUserAId(userId);
        newMatch.setStatus(Constants.STATUS_WAITING);
        newMatch.setCreatedAt(LocalDateTime.now());
        partnerMatchMapper.insert(newMatch);
        return toMatchStatusVO(userId, newMatch);
    }

    @Override
    public MatchStatusVO getMatchStatus(Long userId) {
        PartnerMatch activeMatch = findActiveMatch(userId);
        if (activeMatch == null) {
            MatchStatusVO vo = new MatchStatusVO();
            vo.setStatus(Constants.STATUS_NONE);
            return vo;
        }
        return toMatchStatusVO(userId, activeMatch);
    }

    @Override
    public PageResult<MessageVO> getMessagePage(Long userId, Long matchId, long pageNum, long pageSize) {
        PartnerMatch match = getOwnedMatchOrThrow(userId, matchId);
        Page<PartnerMessage> page = partnerMessageMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<PartnerMessage>lambdaQuery()
                        .eq(PartnerMessage::getMatchId, match.getId())
                        .orderByAsc(PartnerMessage::getCreatedAt));
        return PageResult.from(page, page.getRecords().stream()
                .map(message -> toMessageVO(userId, message))
                .toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void endMatch(Long userId, Long matchId) {
        PartnerMatch match = getOwnedMatchOrThrow(userId, matchId);
        if (Constants.STATUS_ENDED.equals(match.getStatus())) {
            return;
        }
        match.setStatus(Constants.STATUS_ENDED);
        match.setEndedAt(LocalDateTime.now());
        partnerMatchMapper.updateById(match);

        Long partnerUserId = getPartnerUserId(match, userId);
        if (partnerUserId != null) {
            partnerSocketNotifier.sendMatchNotification(partnerUserId, "ENDED", match.getId(), "对方已结束本次对话");
        }
        partnerSocketNotifier.sendMatchNotification(userId, "ENDED", match.getId(), "已结束对话");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageVO sendMessage(Long senderId, MessageDTO dto) {
        PartnerMatch match = getOwnedMatchOrThrow(senderId, dto.getMatchId());
        if (!Constants.STATUS_MATCHED.equals(match.getStatus())) {
            throw new BusinessException(400, "当前不在有效配对中");
        }

        PartnerMessage message = new PartnerMessage();
        message.setCreatedAt(LocalDateTime.now());
        message.setMatchId(dto.getMatchId());
        message.setSenderId(senderId);
        message.setContent(dto.getContent());
        message.setIsRead(0);
        partnerMessageMapper.insert(message);

        MessageVO senderView = toMessageVO(senderId, message);
        Long targetUserId = getPartnerUserId(match, senderId);
        if (targetUserId != null) {
            partnerSocketNotifier.sendMessage(targetUserId, toMessageVO(targetUserId, message));
        }
        return senderView;
    }

    private PartnerMatch findActiveMatch(Long userId) {
        return partnerMatchMapper.selectOne(Wrappers.<PartnerMatch>lambdaQuery()
                .and(wrapper -> wrapper.eq(PartnerMatch::getUserAId, userId).or().eq(PartnerMatch::getUserBId, userId))
                .in(PartnerMatch::getStatus, Constants.STATUS_WAITING, Constants.STATUS_MATCHED)
                .orderByDesc(PartnerMatch::getCreatedAt)
                .last("LIMIT 1"));
    }

    private PartnerMatch getOwnedMatchOrThrow(Long userId, Long matchId) {
        PartnerMatch match = partnerMatchMapper.selectById(matchId);
        if (match == null) {
            throw new BusinessException(404, "匹配记录不存在");
        }
        if (!userId.equals(match.getUserAId()) && !userId.equals(match.getUserBId())) {
            throw new BusinessException(403, "无权访问该匹配记录");
        }
        return match;
    }

    private MatchStatusVO toMatchStatusVO(Long currentUserId, PartnerMatch match) {
        MatchStatusVO vo = new MatchStatusVO();
        vo.setStatus(match.getStatus());
        vo.setMatchId(match.getId());
        vo.setMatchedAt(match.getMatchedAt());
        Long partnerUserId = getPartnerUserId(match, currentUserId);
        if (partnerUserId != null) {
            User partner = userMapper.selectById(partnerUserId);
            vo.setPartnerAnonymousName(partner == null ? null : partner.getAnonymousName());
        }
        return vo;
    }

    private Long getPartnerUserId(PartnerMatch match, Long currentUserId) {
        if (match == null) {
            return null;
        }
        if (currentUserId.equals(match.getUserAId())) {
            return match.getUserBId();
        }
        return match.getUserAId();
    }

    private MessageVO toMessageVO(Long currentUserId, PartnerMessage message) {
        MessageVO vo = new MessageVO();
        BeanUtils.copyProperties(message, vo);
        vo.setIsSelf(currentUserId.equals(message.getSenderId()));
        return vo;
    }
}
