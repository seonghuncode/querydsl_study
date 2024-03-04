package study.querydsl.entity;


import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //기본생성자를 lombok으로 생성 하는 어노테이션(JPA에서는 기본생성자가 핋요)
@ToString(of = {"id", "username", "age"}) //연관관계 필드에 대해서는 작성하지 않는 것이 좋다(자신이 소유한 필드만 사용하는 것을 권장)
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team; //연관관계 주인

    public Member(String username){
        this(username, 0);
    }

    public Member(String username, int age){
        this(username, age, null);
    }

    public Member (String username, int age, Team team){
        this.username = username;
        this.age = age;
        if(team != null){
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
