package study.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.querydsl.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //Spring Data JPA를 사용해서 동작하는 로직

    // 메서드 이름을 가지고 jpa가 쿼리를 자동으로 만들어주는 전략 (-> select m from Member m where m.username = ?)
    List<Member> findByUsername(String username);
}
