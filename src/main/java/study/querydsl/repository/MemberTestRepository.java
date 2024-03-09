package study.querydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.repository.support.Querydsl4RepositorySupport;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Repository
public class MemberTestRepository extends Querydsl4RepositorySupport {

    public MemberTestRepository(){
        super(Member.class);
    }


    //기능
    public List<Member> basicSelect(){
        return select(QMember.member)
                .from(QMember.member)
                .fetch();
    }

    public List<Member> basicSelectFrom(){
        return selectFrom(QMember.member)
                .fetch();
    }



    // 코드1
    public Page<Member> searchPageByApplyPage(MemberSearchCondition condition, Pageable pageable){
        JPAQuery<Member> query = selectFrom(QMember.member)
                .leftJoin(QMember.member.team, QTeam.team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                );
        List<Member> content = getQuerydsl().applyPagination(pageable, query)
                .fetch();
        return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
    }


    // 코드2 (위의 코드1과 완전히 같은 기능의 코드드)
   public Page<Member> applyPagination(MemberSearchCondition condition, Pageable pageable){
        Page<Member> result = applyPagination(pageable, query
                -> query.selectFrom(QMember.member)
                .leftJoin(QMember.member.team, QTeam.team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
        );
        return result;
    }



    //
    public Page<Member> applyPagination2(MemberSearchCondition condition, Pageable pageable){
        Page<Member> result = applyPagination(pageable, contentQuery
                -> contentQuery.selectFrom(QMember.member)
                .leftJoin(QMember.member.team, QTeam.team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                ), countQuery -> countQuery
                .select(QMember.id)
                .from(QMember.member)
                .leftJoin(QMember.member.team, QTeam.team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
        );
        return result;
    }


    private BooleanExpression usernameEq(String username) {
        return hasText(username) ? QMember.member.username.eq(username) : null;
    }

    private com.querydsl.core.types.dsl.BooleanExpression teamNameEq(String teamName) {
        return hasText(teamName) ? QTeam.team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? QMember.member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? QMember.member.age.loe(ageLoe) : null;
    }


}
