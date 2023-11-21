package com.wsj.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wsj.takeout.common.R;
import com.wsj.takeout.entity.User;
import com.wsj.takeout.service.UserService;
import com.wsj.takeout.utils.SMSUtils;
import com.wsj.takeout.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 移动端用户管理
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 短信验证码发送
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成随机四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
            //调用
            SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);
            session.setAttribute(phone,code);
            return R.success("手机验证码短信发送成功");
        }
        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session获取保存的验证码
        Object codeInSession = session.getAttribute(phone);
        //进行验证码的比对（页面提交的验证码和session中保存的验证码比对）
        if(codeInSession != null&&codeInSession.equals(code)){
            //如果能够比对成功，说明登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登陆失败");
    }
    /**
     * 退出功能
     * ①在controller中创建对应的处理方法来接受前端的请求，请求方式为post；
     * ②清理session中的用户id
     * ③返回结果（前端页面会进行跳转到登录页面）
     * @return
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        //清理session中的用户id
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
