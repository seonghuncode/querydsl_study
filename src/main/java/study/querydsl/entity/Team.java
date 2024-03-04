package study.querydsl.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //기본생성자를 lombok으로 생성 하는 어노테이션(JPA에서는 기본생성자가 핋요)
@ToString(of = {"id", "name"})
public class Team {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    //양방향 연관관계의 경우 주인 설정
    @OneToMany(mappedBy = "team") //연관관계 주인이 아니다
    private List<Member> members = new ArrayList<>();

    public Team(String name){
        this.name = name;
    }

}
