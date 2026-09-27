package com.emolink.service;

import com.emolink.common.PageResult;
import com.emolink.dto.MessageDTO;
import com.emolink.vo.MatchStatusVO;
import com.emolink.vo.MessageVO;

public interface PartnerService {

    MatchStatusVO joinMatch(Long userId);

    MatchStatusVO getMatchStatus(Long userId);

    PageResult<MessageVO> getMessagePage(Long userId, Long matchId, long pageNum, long pageSize);

    void endMatch(Long userId, Long matchId);

    MessageVO sendMessage(Long senderId, MessageDTO dto);
}

