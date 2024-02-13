package shop.mtcoding.blog._core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

//import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;
@Configuration // 컴포넌트 스캔이 된다. 설정파일의 머시기가 되는 빈이야. 라는 뜻이야. 이제 아이오씨 메모리에 뜸.
// 컨트롤러 레파지토리 서비스 컴포넌트 컨피규어레이션 얘네만 컴포넌트 스캔이 된다.
public class SecurityConfig {

    // 어떤 패스워드 인코드를 쓰는지
    @Bean
    public BCryptPasswordEncoder encoder() { // PasswordEncoder로 넣으면 나중에 다른 인코더로 해도 코드 수정할 필요 없음.
        return new BCryptPasswordEncoder(); // IoC 등록, 시큐리티가 로그인할 떄 어떤 해시로 비교해야 하는지 알게 됨.
    }

    @Bean
    public WebSecurityCustomizer ignore() { // 정적 파일 security filter에서 제외시키기
        return w -> w.ignoring().requestMatchers( "/static/**", "/h2-console/**"); // 얘네는 필터링 하지 말고 그냥 들어가게 해주라는 설정이야.
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable());

        http.authorizeHttpRequests(a -> {
            a.requestMatchers(RegexRequestMatcher.regexMatcher("/board/\\d+")).permitAll()
                    .requestMatchers("/user/**", "/board/**").authenticated()
                    .anyRequest().permitAll();
        });

        http.formLogin(f -> {
            f.loginPage("/loginForm").loginProcessingUrl("/login").defaultSuccessUrl("/").failureUrl("/loginForm"); // loginProcessingUrl("/login"): 시큐리티가 들고 있는 로그인을 주는 것이야.
            // defaultSuccessUrl("/"): 성공하면 디폴트로 메인으로 간다.
            // failureForwardUrl("/loginForm"): 실패하면 로그인 페이지로 간다.
        });
        return http.build();
    }
}