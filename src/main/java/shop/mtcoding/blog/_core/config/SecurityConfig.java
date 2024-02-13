package shop.mtcoding.blog._core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 컴포넌트 스캔이 된다. 설정파일의 머시기가 되는 빈이야. 라는 뜻이야. 이제 아이오씨 메모리에 뜸.
// 컨트롤러 레파지토리 서비스 컴포넌트 컨피규어레이션 얘네만 컴포넌트 스캔이 된다.
public class SecurityConfig {

    // 어떤 패스워드 인코드를 쓰는지
    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer ignore() {
        return w -> w.ignoring().requestMatchers("/board/*", "/static/**", "/h2-console/**"); // 얘네는 필터링 하지 말고 그냥 들어가게 해주라는 설정이야.
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable());
        http.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers("/user/updateForm", "/board/**").authenticated().anyRequest().permitAll(); // /user/updateForm"과 "/board/**"에 대한 요청은 인증된 사용자만 허용하고, 그 외의 모든 요청은 허용하는 설정
        });

        http.formLogin(f -> {
            f.loginPage("/loginForm").loginProcessingUrl("/login").defaultSuccessUrl("/").failureUrl("/loginForm"); // loginProcessingUrl("/login"): 시큐리티가 들고 있는 로그인을 주는 것이야.
            // defaultSuccessUrl("/"): 성공하면 디폴트로 메인으로 간다.
            // failureForwardUrl("/loginForm"): 실패하면 로그인 페이지로 간다.
        });
        return http.build();
    }
}