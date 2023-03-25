package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final SessionManager sessionManager;


    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

//    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form,
                        BindingResult result,
                        HttpServletResponse response) {
        if (result.hasErrors()) {
            return "login/loginForm";
        }
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        // 로그인 과정을 거치다가 오류가 난 경우
        // 즉, 아이디에 대한 비번이 맞지 않은 경우
        if (loginMember == null) {
            result.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리

        //쿠키에 시간 정보를 주지 않으면 세션 쿠키(브라우저 종료시 모두 종료)
        //cookie의 값은 항상 String 으로 들어가야 한다.
        Cookie cookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(cookie);

        return "redirect:/";

    }

    // 세션과 쿠키를 사용한 로그인 구현
//    @PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm form,
                        BindingResult result,
                        HttpServletResponse response) {
        if (result.hasErrors()) {
            return "login/loginForm";
        }
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        // 로그인 과정을 거치다가 오류가 난 경우
        // 즉, 아이디에 대한 비번이 맞지 않은 경우
        if (loginMember == null) {
            result.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리
        //sessionManager에서 쿠키생성작업까지 다 해주기 때문에 기존 코드 삭제
        //세션 관리자를 통해 세션을 생성하고 회원 데이터 보관
        sessionManager.createSession(loginMember, response);

        return "redirect:/";

    }
    @PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute LoginForm form,
                          BindingResult result,
                          HttpServletRequest request) {
        if (result.hasErrors()) {
            return "login/loginForm";
        }
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        // 로그인 과정을 거치다가 오류가 난 경우
        // 즉, 아이디에 대한 비번이 맞지 않은 경우
        if (loginMember == null) {
            result.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리
        //세션 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = request.getSession();

        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);


        return "redirect:/";

    }
    //로그아웃 로직 추가
//    @PostMapping("/logout")
    public String logout(HttpServletResponse response){
        expireCookie(response, "memberId");
        return "redirect:/";
    }

    //쿠키와 세션을 사용한 로그아웃 구현
//    @PostMapping("/logout")
    public String logoutV2(HttpServletRequest request){
        sessionManager.expire(request);
        return "redirect:/";
    }
    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request){
        //세션을 삭제한다.
        HttpSession session = request.getSession();
        //세션을 찾게되면 invalidate하게 만든다.
        if (session!=null) {
            session.invalidate();
        }
        return "redirect:/";
    }
    private void expireCookie(HttpServletResponse response, String cookieName){
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0); // 쿠키의 종료날짜를 0으로 지정
        response.addCookie(cookie);

    }
}
