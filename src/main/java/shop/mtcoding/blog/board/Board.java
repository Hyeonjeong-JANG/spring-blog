package shop.mtcoding.blog.board;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Table(name="board_tb")
@Data
@Entity // 테이블 생성하기 위해 필요한 어노테이션
public class Board { // User 1 -> Board N
    @Id // PK 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment 전략
    private int id;

    @Column(length = 30)
    private String title;
    private String content;

    private int userId; // 테이블에 만들어 질때 user_id

    private LocalDateTime createdAt;
}
