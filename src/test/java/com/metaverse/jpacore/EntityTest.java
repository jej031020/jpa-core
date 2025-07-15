package com.metaverse.jpacore;

import com.metaverse.jpacore.domain.Memo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EntityTest {
    EntityManagerFactory emf;
    EntityManager em;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("memo");
        em = emf.createEntityManager();
    }

    @Test
    @DisplayName("Entity Transaction 성공 테스트")
    void test_transaction_1() {
        EntityTransaction et = em.getTransaction();
        et.begin();

        try {
            Memo memo = new Memo(); // 저장할 엔터티 객체 생성
            memo.setId(2L);
            memo.setUsername("안녕11");
            memo.setContents("영속성 컨텍스트와 트랜잭션 이해하기");

            em.persist(memo); // EntityManager가 memo 객체를 영속성 컨텍스트에 저장한다.

            et.commit(); // 오류가 없으면 정상수행, 커밋 호출
        } catch (Exception e) {
            e.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    @Test
    @DisplayName("EntityTransaction 실패 테스트")
    void test_transaction_2() {
        EntityTransaction et = em.getTransaction();
        et.begin(); // 트랜잭션 실행

        try {
            Memo memo = new Memo();
            memo.setUsername("안녕");
            memo.setContents("Memo입니다");

            em.persist(memo);
            et.commit();
        } catch (Exception e) {
            System.out.println("식별자 값(id)를 입력하지 않아 오류가 발생했음");
            e.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    @Test
    @DisplayName("Entity 저장 : 1차 캐싱")
    void test1() {
        EntityTransaction et = em.getTransaction();
        et.begin();

        try {
            Memo memo = new Memo();
            memo.setId(3L);
            memo.setUsername("Meta");
            memo.setContents("1차 캐시 Entity 저장");

            em.persist(memo);

            et.commit();
        } catch (Exception e) {
            e.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    @Test
    @DisplayName("Entity 조회 : 캐시 저장소에 해당하는 id가 존재하지 않는 경우")
    void test2() {
        try {
            Memo memo = em.find(Memo.class, 1);
            System.out.println(memo.getId());
            System.out.println(memo.getUsername());
            System.out.println(memo.getContents());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
        emf.close();
    }

    @Test
    @DisplayName("Entity 조회 : 캐시 저장소에 해당하는 id가 존재하는 경우")
    void test3() {
        try {
            Memo memo1 = em.find(Memo.class, 3);
            System.out.println("memo1 조회 후 캐시 저장소에 저장 \n");

            Memo memo2 = em.find(Memo.class, 3);
            System.out.println(memo2.getId());
            System.out.println(memo2.getUsername());
            System.out.println(memo2.getContents());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            em.close();
        }
        emf.close();
    }

    @Test
    @DisplayName("객체 동일성 보장 테스트")
    void test4() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {
            Memo memo = new Memo();
            memo.setId(4L);
            memo.setUsername("Meta222");
            memo.setContents("객체 동일성 보장");

            em.persist(memo);

            Memo memo1 = em.find(Memo.class, 4);
            Memo memo2 = em.find(Memo.class, 4);
            Memo memo3 = em.find(Memo.class, 3);

            System.out.println(memo1 == memo2); // true
            System.out.println(memo3 == memo2); // false

            et.commit();
        } catch (Exception e) {
            et.rollback();
            throw new RuntimeException(e);
        }finally {
            em.close();
        }
        emf.close();
    }
}
