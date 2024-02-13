package shop.mtcoding.blog.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncodeTest {
    @Test
    public void encode_test() {
        BCryptPasswordEncoder en = new BCryptPasswordEncoder();
        String rawPassword = "1234";
        String encPassword = en.encode(rawPassword);
        System.out.println(encPassword);
    }
}

// $2a$10$EGPvp/vME1bYEZM1ZrQpQOCXztFXfVJrpqIckoRVd2oiRKlP/I5d6
// $2a$10$CpNfabbbfbdTjWcWsa1Jb.8PZnEdaOznnyZ7k6nMQ9zmwO0n8ZY9W
// 할 때마다 값이 다름