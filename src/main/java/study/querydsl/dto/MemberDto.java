package study.querydsl.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {
    //Member의 값을 조회하기 위한 dto
    // 조회할때 username, age만 출력하고 싶은 경우

    private String username;
    private int age;

    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }

}
