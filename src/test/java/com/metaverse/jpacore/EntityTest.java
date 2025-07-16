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
            memo.setId(5L);
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

    @Test
    @DisplayName("Entity 삭제")
    void test5() {
        EntityTransaction et = em.getTransaction();
        et.begin();

        try {
            Memo memo = em.find(Memo.class, 1);

            em.remove(memo);

            et.commit();
        } catch (Exception e) {
            et.rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
        emf.close();
    }

    @Test
    @DisplayName("쓰기 지연 저장소 확인")
    void test6() {
        EntityTransaction et = em.getTransaction();
        et.begin();

        try {
            Memo memo1 = new Memo();
            memo1.setId(2L);
            memo1.setUsername("Meta1");
            memo1.setContents("쓰기 지연 저장소");
            em.persist(memo1);

            Memo memo2 = new Memo();
            memo2.setId(3L);
            memo2.setUsername("Meta2");
            memo2.setContents("저장이 되는지 확인");
            em.persist(memo2);

            System.out.println("트랜잭션 커밋 전");
            et.commit();
            System.out.println("트랜잭션 커밋 후");
        } catch (Exception e) {
            et.rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
        emf.close();
    }

    @Test
    @DisplayName("commit() 이후 flush() 메서드 확인")
    void test7() {
        EntityTransaction et = em.getTransaction();
        et.begin();

        try {
            Memo memo = new Memo();
            memo.setId(5L);
            memo.setUsername("Meta3");
            memo.setContents("flush() 메서드 호출");
            em.persist(memo);

            System.out.println("flush() 전");
            em.flush();
            System.out.println("flush() 후");

            System.out.println("트랜잭션 Commit 전");
            et.commit();
            System.out.println("트랜잭션 Commit 후");
        } catch (Exception e) {
            et.rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
        emf.close();
    }

    @Test
    @DisplayName("변경 감지 Dirty checking")
    void test8() {
        EntityTransaction et = em.getTransaction();
        et.begin();

        try {
            System.out.println("변경할 데이터를 조회합니다.");
            Memo memo = em.find(Memo.class, 4);
            System.out.println("memo.getId() = " + memo.getId());
            System.out.println("memo.getUsername() = " + memo.getUsername());
            System.out.println("memo.getContents() = " + memo.getContents());

            System.out.println("\n수정 진행");
            memo.setUsername("Meta5");
            memo.setContents("변경 감지 확인");

            System.out.println("트랜잭션 Commit 전");
            et.commit();
            System.out.println("트랜잭션 Commit 후");
        } catch (Exception e) {
            et.rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
        emf.close();
    }

    @Test
    @DisplayName("비영속, 영속, 준영속")
    void test9() {
        EntityTransaction et = em.getTransaction();
        et.begin();

        try {
            // 비영속
            Memo memo = new Memo();
            memo.setId(6L);
            memo.setUsername("Meta6");
            memo.setContents("영속성 컨텍스트가 관리할 데이터");

            // 영속
            em.persist(memo);
            Memo foundMemo = em.find(Memo.class, 6);
            System.out.println("em.contains(foundMemo) = " + em.contains(foundMemo));

            // 준영속
            System.out.println("detach() 호출 후");
            em.detach(foundMemo);
            //  em.clear();
            // clear()는 영속성 컨텍스트에서 관리되는 모든 엔터티를 준영속으로 해제하는 기능
            // em.close();
            // 영속성 컨텍스트 자체를 닫는 기능 -> 관리되던 엔터티들은 모두 준영속으로 해제됨
            // em.merge(foundMemo)
            // 즌영속 상태에서 다시 영속 상태로 바꿈
            System.out.println("em.contains(foundMemo) = " + em.contains(foundMemo));

            // 준영속 상태의 엔터티 수정 시도
            System.out.println("\n준영속 후 수정 시도");
            foundMemo.setUsername("Meta5");
            foundMemo.setContents("변경 감지 확인");

            // 트랜잭션 커밋
            System.out.println("트랜잭션 Commit 전");
            et.commit();
            System.out.println("트랜잭션 Commit 후");
        } catch (Exception e) {
            et.rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
        emf.close();
    }
}
