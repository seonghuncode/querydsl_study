package study.querydsl;


import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
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

    //페이징
    @Test
    public void paging1(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); //em을 넘겨우어야 데이터를 찾을 수있다

        List<Member> result = queryFactory
                .selectFrom(QMember.member)
                .orderBy(QMember.member.username.desc())
                .offset(1) //1를 스킵한다는 의미
                .limit(2)
                .fetch();

        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void paging2(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); //em을 넘겨우어야 데이터를 찾을 수있다

        QueryResults<Member> queryResults = queryFactory
                .selectFrom(QMember.member)
                .orderBy(QMember.member.username.desc())
                .offset(1) //1를 스킵한다는 의미
                .limit(2)
                .fetchResults();

        Assertions.assertThat(queryResults.getTotal()).isEqualTo(4);
        Assertions.assertThat(queryResults.getLimit()).isEqualTo(2);
        Assertions.assertThat(queryResults.getOffset()).isEqualTo(1);
        Assertions.assertThat(queryResults.getResults().size()).isEqualTo(2);
    }

    //집합 함수 사용 방법
    @Test
    public void aggregation(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); //em을 넘겨우어야 데이터를 찾을 수있다

        List<Tuple> reuslt = queryFactory //집합 함수로 원하는 것을 추출할 경우 Tuple로 반환 받는다.(여러개 타입을 받을 경우)
                .select(
                        QMember.member.count(),
                        QMember.member.age.sum(),
                        QMember.member.age.avg(),
                        QMember.member.age.max(),
                        QMember.member.age.min()
                )
                .from(QMember.member)
                .fetch();

        Tuple tuple = reuslt.get(0);
        Assertions.assertThat(tuple.get(QMember.member.count())).isEqualTo(4);
        Assertions.assertThat(tuple.get(QMember.member.age.sum())).isEqualTo(100);
        Assertions.assertThat(tuple.get(QMember.member.age.avg())).isEqualTo(25);
        Assertions.assertThat(tuple.get(QMember.member.age.max())).isEqualTo(40);
        Assertions.assertThat(tuple.get(QMember.member.age.min())).isEqualTo(10);
    }

    //집계함수 group by 사용 (팀의 이름과 각 팀의 평균 연령을 구하라)
    @Test
    public void group() throws Exception{
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); //em을 넘겨우어야 데이터를 찾을 수있다

        List<Tuple> result = queryFactory
                .select(QTeam.team.name, QMember.member.age.avg())
                .from(QMember.member)
                .join(QMember.member.team, QTeam.team)
                .groupBy(QTeam.team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        Assertions.assertThat(teamA.get(QTeam.team.name)).isEqualTo("teamA");
        Assertions.assertThat(teamA.get(QMember.member.age.avg())).isEqualTo(15); // (10 + 20) /2

        Assertions.assertThat(teamB.get(QTeam.team.name)).isEqualTo("teamB");
        Assertions.assertThat(teamB.get(QMember.member.age.avg())).isEqualTo(35); // (30 + 40) / 2
    }


    //queryDSL에서의 조인 방법
    // TeamA에 소속된 모든 회원
    @Test
    public void join(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); //em을 넘겨우어야 데이터를 찾을 수있다

        List<Member> result = queryFactory
                .selectFrom(QMember.member)
                .join(QMember.member.team, QTeam.team)
                .where(QTeam.team.name.eq("teamA"))
                .fetch();

        Assertions.assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");

    }

    //연관관계가 없는 경우 join하는 경우
    //세타 조인 (회원의 이름이 팀 이름과 같은 회원 조회)
    // 모든 member, team을 조인해서 이름이 같은 것들만 가지고 온다
    // 외부 조인은 불가능 하다
    @Test
    public void theta_join(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); //em을 넘겨우어야 데이터를 찾을 수있다

        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Member> result = queryFactory
                .select(QMember.member)
                .from(QMember.member, QTeam.team)
                .where(QMember.member.username.eq(QTeam.team.name))
                .fetch();

        Assertions.assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }


    //Join-on을 사용하는 방법 (조인 대상 필터링)
    // 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회(member를 기준으로 존재하는 데이터를 가지고 온다.)
    //JPQL : select m, t from Member m left join m.team t on t.name = 'temaA'
    @Test
    public void  join_on_filtering(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); //em을 넘겨우어야 데이터를 찾을 수있다
        
        List<Tuple> result = queryFactory
                .select(QMember.member, QTeam.team)
                .from(QMember.member)
                .leftJoin(QMember.member.team, QTeam.team).on(QTeam.team.name.eq("teamA"))
                .fetch();
        
        for(Tuple tuple : result){
            System.out.println("tuple = " + tuple);
        }
    }



    //Join-on을 사용하는 방법 (연관관계 없는 엔티티 외부 조인)
    // 회원의 이름이 팀 이름과 같은 대상을 외부 조인
    @Test
    public void join_on_no_relation(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); //em을 넘겨우어야 데이터를 찾을 수있다

        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> result = queryFactory
                .select(QMember.member, QTeam.team)
                .from(QMember.member)
                .leftJoin(QTeam.team).on(QMember.member.username.eq(QTeam.team.name))
                .fetch();

       for(Tuple tuple : result){
           System.out.println("tuple = " + tuple);
       }
    }


}
