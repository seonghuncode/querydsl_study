package study.querydsl.repository;

import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;

import java.util.List;

public interface MemberRepositoryCustom {
    //spring data jpa를 사용하면서 직접 구현한 기능을 사용하고 싶을 경우
    List<MemberTeamDto> search(MemberSearchCondition condition);
}
