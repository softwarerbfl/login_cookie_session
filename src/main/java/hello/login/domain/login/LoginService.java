package hello.login.domain.login;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;

    /**
     * @return null 이면 로그인 실패
     */
    public Member login(String loginId, String password) {
//        Optional<Member> findMember = memberRepository.findByLogInId(loginId);
//        Member member = findMember.get();
//        //회원이 입력한 아이디와 비번이 일치하면 member return
//        if (member.getPassword().equals(password)){
//            return member;
//        }
//        //아닌 경우 null return
//        else{
//            return null;
//        }


        Optional<Member> byLoginId = memberRepository.findByLogInId(loginId);
        return byLoginId.filter(m -> m.getPassword().equals(password)) //Optional 객체는 filter기능을 사용할 수 있음
                .orElse(null); //이를 사용하여 filter조건을 만족하면 해당 객체를, 만족하지 않는다면 null를 return하도록 할 수 있다.
    }
}
