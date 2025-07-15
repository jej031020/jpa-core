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
            memo.setId(1L);
            memo.setUsername("Meta");
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
            memo.setUsername("Meta");
            memo.setContents("Memo입니다");

            em.persist(memo);
            et.commit();
        } catch (Exception e) {
            System.out.println("식별자 값(id)를 입력하지 않아 오류가 발생했음");
            e.printStackTrace();
            et.rollback();
        } finally {
            emf.close();
        }
    }
}