package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;
//    @GetMapping("/")
    public String home() {
        return "home";
    }
//    @GetMapping("/")
    public String homeLogin(@CookieValue(name="memberId", required = false) Long memberId, Model model){
        if (memberId==null){
            return "home";
        }
        // 로그인
        Member loginMember = memberRepository.findById(memberId);
        if (loginMember==null){
            return "home";
        }
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model) {
        //session Manager에 저장된 회원 정보 조회
        Member member = (Member) sessionManager.getSession(request);

        if (member==null){
            return "home";
        }

        //로그인
        model.addAttribute("member", member);
        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model) {
        //세션이 없으면 home
        HttpSession session = request.getSession(false);
        if (session==null){
            return "home";
        }

        //회원 정보가 잘못되었거나 없으면 home
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if (member==null){
            return "home";
        }

        //세션이 유지되었으면 로그인된 페이지로 이동
        model.addAttribute("member", member);
        return "loginHome";
    }

    /**
     * 스프링에서 제공해주는 @SessionAttribute 애노테이션 사용
     * 세션을 가장 편리하게 사용할 수 있는 도구
     * name 인자에 해당하는 쿠키 값이 존재하면 그 객체를, 없다면 null을 반환한다.
     */
    @GetMapping("/")
    public String homeLoginV3Spring(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false)
            Member loginMember,
            Model model) {
        //세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }
        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

}