package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Hello;
import study.querydsl.entity.QHello;

import javax.persistence.EntityManager;


@SpringBootTest
@Transactional
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
