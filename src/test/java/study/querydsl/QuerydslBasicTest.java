package study.querydsl;


import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    @BeforeEach //각각의 테스트 케이스를 실행하기 전에 실행하는 부분
    public void before(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    //테스트케이스1(JPQL -> QueryDSL)
    @Test
    public void startJPQL(){
        //member1을 찾아라
        String qlString = "select m from Member m " +
                "where m.username = :username";

        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test //queryDSL로 작성한 코드드
    public void startQuerydsl(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); //em을 넘겨우어야 데이터를 찾을 수있다

        //Q파일을 사용하기 위해 Gradle > Tasks > other > compileQuerydsl클릭(build>generated아래 Q파일 생성됨)

        Member findMember = queryFactory
                .select(QMember.member)
                .from(QMember.member)
                .where(QMember.member.username.eq("member1"))
                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }


    //검색 조건 쿼리
    @Test
    public void search(){

        JPAQueryFactory queryFactory = new JPAQueryFactory(em); //em을 넘겨우어야 데이터를 찾을 수있다

        Member findMember = queryFactory
                .selectFrom(QMember.member)
                .where(QMember.member.username.eq("member1")
                    .and(QMember.member.age.eq(10)))
                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test //위의 search에서 and부분을 ","로 사용해도 가능
    public void searchAndParam(){

        JPAQueryFactory queryFactory = new JPAQueryFactory(em); //em을 넘겨우어야 데이터를 찾을 수있다

        Member findMember = queryFactory
                .selectFrom(QMember.member)
                .where(QMember.member.username.eq("member1"),
                        (QMember.member.age.eq(10)))
                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }


    //결과조회 쿼리 종류
    @Test
    public void resultFetch(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); //em을 넘겨우어야 데이터를 찾을 수있다

//        //member의 목록을 list로 조회
//        List<Member> fetch = queryFactory
//                .selectFrom(QMember.member)
//                .fetch();
//
//        //단건조회
//        Member fetchOne = queryFactory
//                .selectFrom(QMember.member)
//                .fetchOne();
//
//        Member fetchFirst = queryFactory
//                .selectFrom(QMember.member)
//                .fetchFirst();

        QueryResults<Member> results = queryFactory
                .selectFrom(QMember.member)
                .fetchResults();

        results.getTotal();
        List<Member> content = results.getResults();

        //select절을 count로 변환해서 반환
        long total = queryFactory
                .selectFrom(QMember.member)
                .fetchCount();
    }

    /*
    회원 정렬 순서
    1. 화원 나이 내림차순(desc)
    2. 회원 이름 올림차순(asc)
    단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
    @Test
    public void sort(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); //em을 넘겨우어야 데이터를 찾을 수있다

        //테스트 데이터 삽입
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(QMember.member)
                .where(QMember.member.age.eq(100))
                .orderBy(QMember.member.age.desc(), QMember.member.username.asc().nullsLast())
                .fetch();

        //검증
        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);
        Assertions.assertThat(member5.getUsername()).isEqualTo("member5");
        Assertions.assertThat(member6.getUsername()).isEqualTo("member6");
        Assertions.assertThat(memberNull.getUsername()).isNull();
    }


}
