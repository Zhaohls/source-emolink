package com.emolink.service;

import com.emolink.dto.LoginDTO;
import com.emolink.dto.PasswordDTO;
import com.emolink.dto.RegisterDTO;
import com.emolink.dto.UserUpdateDTO;
import com.emolink.vo.LoginVO;
import com.emolink.vo.UserVO;

public interface UserService {

    void register(RegisterDTO dto);

    LoginVO login(LoginDTO dto);

    UserVO getCurrentUserInfo(Long userId);

    void updateUser(Long userId, UserUpdateDTO dto);

    void updatePassword(Long userId, PasswordDTO dto);
}

