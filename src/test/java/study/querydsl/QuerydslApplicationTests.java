package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Hello;
import study.querydsl.entity.QHello;

import javax.persistence.EntityManager;


@SpringBootTest
@Transactional // 테스트가 종료되면 바로 Rollback을 시키기 때문에 테스트의 경우 실제 DB에 반영되지 않는다.
@Commit // 테스트가 종료되더라도 Rollback시키지 않고 DB에 반영되게 하고 싶을 경우 사용
class QuerydslApplicationTests {

	@Autowired
	EntityManager em;

	@Test
	void contextLoads() {
		Hello hello = new Hello();
		em.persist(hello);

		JPAQueryFactory query = new JPAQueryFactory(em);
		QHello qHello = QHello.hello;
		// QHello qHello = new QHello("h"); //querydsl사용시 쿼리와 관련된 내용은 Q타입을 사용

		//querydsl
		Hello result = query
				.selectFrom(qHello)
				.fetchOne();

		//검증
		Assertions.assertThat(result).isEqualTo(hello); // result가 hello랑 같은지 테스트
		Assertions.assertThat(result.getId()).isEqualTo(hello.getId()); //lombok테스트

	}

}
