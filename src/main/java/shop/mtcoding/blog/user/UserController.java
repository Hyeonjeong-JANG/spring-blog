package shop.mtcoding.blog.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import shop.mtcoding.blog._core.config.security.MyLoginUser;
import shop.mtcoding.blog.board.Board;
import shop.mtcoding.blog.board.BoardRepository;


@RequiredArgsConstructor // final이 붙은 애들에 대한 생성자를 만들어줌
@Controller
public class UserController {

    // 자바는 final 변수는 반드시 초기화가 되어야함.
    private final UserRepository userRepository;
    private final HttpSession session;
    private final BCryptPasswordEncoder passwordEncoder;

    // 왜 조회인데 post임? 민간함 정보는 body로 보낸다.
    // 로그인만 예외로 select인데 post 사용
    // select * from user_tb where username=? and password=?
//    @PostMapping("/login")
//    public String login(UserRequest.LoginDTO requestDTO) {
//        System.out.println(requestDTO); // toString -> @Data
//
//        if (requestDTO.getUsername().length() < 3) {
//            return "error/400"; // ViewResolver 설정이 되어 있음. (앞 경로, 뒤 경로)
//        }
//
//        User user = userRepository.findByUsernameAndPassword(requestDTO);
//
//        if (user == null) { // 조회 안됨 (401)
//            return "error/401";
//        } else { // 조회 됐음 (인증됨)
//            session.setAttribute("sessionUser", user); // 락카에 담음 (StateFul)
//        }
//
//        return "redirect:/"; // 컨트롤러가 존재하면 무조건 redirect 외우기
//    }

    @PostMapping("/join")
    public String join(UserRequest.JoinDTO requestDTO) {
        System.out.println(requestDTO);

        String rasPassword = requestDTO.getPassword();
        String encPassword = passwordEncoder.encode(rasPassword);
        userRepository.save(requestDTO); // 모델에 위임하기
        return "redirect:/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }


    /**
     * TODO 왜 id 없어도 업데이트폼이 되는거지? 이해불가. 로그인 되어 있어서? 그래도 유저별로 저장된 것은 다르자너. 이해불가!!!
     *
     * @GetMapping("/user/updateForm")
     * public String updateForm(HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {
     *     User user = userRepository.findByUsername(myLoginUser.getUsername());
     *     request.setAttribute("user", user);
     *     return "user/updateForm";
     * }
     *
     * @param id
     * @param request
     * @param myLoginUser
     * @return
     */
    @GetMapping("/user/{id}/updateForm")
    public String updateForm(@PathVariable int id, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {

        User user = userRepository.findById(id);
        /**
         * TODO  User user = userRepository.findByUsername(myLoginUser.getUsername()); 왜 왜오애
         */
        if (user.getId() != myLoginUser.getUser().getId()) {
            return "error/403";
        }
        request.setAttribute("user", user);

        return "user/updateForm";
    }

    @PostMapping("/user/{id}/update")
    public String update(@PathVariable int id, UserRequest.UpdateDTO requestDTO) {

        userRepository.update(requestDTO, id);
        return "redirect:/user/detail/" + id;
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/";
    }
}
