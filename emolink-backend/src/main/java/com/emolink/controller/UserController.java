package com.emolink.controller;

import com.emolink.common.Result;
import com.emolink.dto.LoginDTO;
import com.emolink.dto.PasswordDTO;
import com.emolink.dto.RegisterDTO;
import com.emolink.dto.UserUpdateDTO;
import com.emolink.service.UserService;
import com.emolink.vo.LoginVO;
import com.emolink.vo.UserVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success("注册成功", null);
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success("登录成功", userService.login(dto));
    }

    @GetMapping("/info")
    public Result<UserVO> info(HttpServletRequest request) {
        return Result.success(userService.getCurrentUserInfo(currentUserId(request)));
    }

    @PutMapping("/update")
    public Result<Void> update(@Valid @RequestBody UserUpdateDTO dto, HttpServletRequest request) {
        userService.updateUser(currentUserId(request), dto);
        return Result.success("修改成功", null);
    }

    @PutMapping("/password")
    public Result<Void> password(@Valid @RequestBody PasswordDTO dto, HttpServletRequest request) {
        userService.updatePassword(currentUserId(request), dto);
        return Result.success("密码修改成功", null);
    }
}

