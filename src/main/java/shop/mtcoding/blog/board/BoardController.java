package shop.mtcoding.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import shop.mtcoding.blog.user.User;
import shop.mtcoding.blog.user.UserRequest;

import java.awt.*;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardRepository boardRepository;
    private final HttpSession session;

    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable int id, HttpServletRequest request) {
        // 조인, 서브쿼리, 오더바이를 사용하면 서버의 부하가 높아진다.
        // 1. 인증 안 되면 나가
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 모델 위임(id로 board를 조회) - 권한 체크를 하려면 대상이 필요해.
        Board board = boardRepository.findById(id);

        // 2. 권한 없으면 나가
        if (board.getUserId() != sessionUser.getId()) {
            return "error/403";
        }

        // 3. 원래 있던 글 가방에 담아 뿌리기 - 가방 이름 board
        request.setAttribute("board", board);

        return "board/updateForm";
    }

    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable int id, HttpServletRequest request) {
        // 바디 데이터 없어서 유효성 검사 안 해도 됨.
        // 1. 인증 안 되면 나가
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }
        // 2. 권한 없으면 나가(포스트맨으로 때릴지도 모르니까)
        Board board = boardRepository.findById(id);
        if (board.getUserId() != sessionUser.getId()) {
            request.setAttribute("status", 403);
            request.setAttribute("msg", "게시글을 삭제할 권한이 없습니다");
            return "error/40x";
        }

        // 3. 삭제
        boardRepository.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO requestDTO, HttpServletRequest request) {

        // 1. 인증 체크
        User sessionUser = (User) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 2. 바디 데이터 확인 및 유효성 검사
        log.info(requestDTO.toString());

        if (requestDTO.getTitle().length() > 30) {
            request.setAttribute("status", 400);
            request.setAttribute("msg", "title의 길이가 30자를 초과해선 안 돼요");
            return "error/40x";
        }

        // 3. 모델 위임
        // insert into board_tb(title, content, user_id, created_at)values(?, ?, ?, now());

        boardRepository.save(requestDTO, sessionUser.getId());

        return "redirect:/";
    }

    @GetMapping({"/", "/board"})
    public String index(HttpServletRequest request) {

        List<Board> boardList = boardRepository.findAll();
        request.setAttribute("boardList", boardList);

        return "index";
    }

    @GetMapping("/board/saveForm")
    //       /board/saveForm 요청(Get)이 온다
    //       session 영역에 sessionUser 키값에 user 객체 있는지 체크
    //       값이 null 이면 로그인 페이지로 리다이렉션
//       값이 null 이 아니면, /board/saveForm 으로 이동
    public String saveForm() {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }
        return "board/saveForm";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable int id, HttpServletRequest request) {
        // 1. 디테일 페이지 보려면 바디 데이터는 체크할 필요 없음, 권한도 체크할 필요 없음
        BoardResponse.DetailDTO responseDTO = boardRepository.findByIdWithUser(id);

        // 2. 페이지 주인 여부 체크(board의 userId와 sessionUser의 id를 비교)
        User sessionUser = (User) session.getAttribute("sessionUser");
        boolean pageOwner = false;
        System.out.println("로그인 아이디: " + sessionUser.getUsername());
        System.out.println("글쓴이 아이디: " + responseDTO.getUsername());

        System.out.println(sessionUser);
        if (sessionUser == null) {
            System.out.println("널안에있는거" + sessionUser);
        } else if (responseDTO.getUsername() == sessionUser.getUsername()) {
            pageOwner = true;
//            System.out.println("로그인,글쓴이 아이디 같다: " + responseDTO.getUsername() == sessionUser.getUsername());
//            System.out.println("pageOwner: " + pageOwner);
        }


//        if (sessionUser == null) {
//            pageOwner = false;
//        } else {
//            pageOwner = responseDTO.getUsername().equals(sessionUser.getUsername());
//            System.out.println(pageOwner);
//        }

        // 바디 데이터가 없으면 유효성 검사가 필요없지 ㅎ

        request.setAttribute("board", responseDTO);
        request.setAttribute("pageOwner", pageOwner);
        return "board/detail";
    }
}
