package shop.mtcoding.blog._core.config.security;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shop.mtcoding.blog.user.User;
import shop.mtcoding.blog.user.UserRepository;


// POST, /login, x-www-form-urlencoded, 키값이 username, password 여야 때려짐
@RequiredArgsConstructor
@Service
public class MyLoginService implements UserDetailsService {
    private final UserRepository userRepository;
    private final HttpSession session;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername: " + username);
        // 유저네임 받아서 디비랑 동일한지 확인
        User user = userRepository.findByUserName(username);
        // 세션 만들기

        if (user == null) {
            System.out.println("user는 null");
            return null; // 시큐리티가 알아서 로그인이 실패했다고 응답을 해준다.
        } else {
            System.out.println("user를 찾았어요");
            session.setAttribute("sessionUser", user); // 머스태치 용도로만 쓰는 것
            return new MyLoginUser(user); // SecurityContextHolder에 저장, SecurityContextHolder에 넣기 전에 getPassword를 때림
        }
    }
}

// 시큐리티를 달고 서버가 실행되면 유저디테일즈서비스가 이미 등록된 상탱니데 그걸 임플리먼트해서 @Service를 달면 컴포넌트 스캔이 된다. 그럼 기존에 있던 유저디테일즈서비스의 메서드가 무력화되고 loadUserByUsername가 때려진다.
