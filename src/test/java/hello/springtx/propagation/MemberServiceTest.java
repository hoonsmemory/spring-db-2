package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired private MemberService memberService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private LogRepository logRepository;

    /**
     * memberService    @Transactional : OFF
     * memberRepository @Transactional : ON
     * logRepository    @Transactional : ON
     */
    @Test
    @DisplayName("트랜잭션이 각 Repository에 적용되어 있고 성공")
    void outerTxOff_success() {
        //given
        String username = "outerTxOff_success";

        //when
        memberService.joinV1(username);

        //then : 모든 데이터가 저장된다.
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService    @Transactional : OFF
     * memberRepository @Transactional : ON
     * logRepository    @Transactional : ON Exception
     */
    @Test
    @DisplayName("트랜잭션이 각 Repository에 적용되어 있고 logRepository 예외 발생으로 logRepository만 롤백")
    void outerTxOff_fail() {
        //given
        String username = "로그예외_outerTxOff_success";

        //when
        org.assertj.core.api.Assertions.assertThatThrownBy(()-> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        //then : 모든 데이터가 저장된다.
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService    @Transactional : ON
     * memberRepository @Transactional : OFF
     * logRepository    @Transactional : OFF
     */
    @Test
    @DisplayName("트랜잭션이 memberService에만 적용(단일 트랜잭션)되어 있고 성공")
    void singleTx() {
        //given
        String username = "outerTxOff_success";

        //when
        memberService.joinV1(username);

        //then : 모든 데이터가 저장된다.
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService    @Transactional : ON
     * memberRepository @Transactional : ON
     * logRepository    @Transactional : ON
     */
    @Test
    @DisplayName("트랜잭션이 전체 메서드에 적용")
    void outerTxOn_Success() {
        //given
        String username = "outerTxOff_success";

        //when
        memberService.joinV1(username);

        //then : 모든 데이터가 저장된다.
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService    @Transactional : On
     * memberRepository @Transactional : ON
     * logRepository    @Transactional : ON Exception
     */
    @Test
    @DisplayName("트랜잭션이 전체 메서드에 적용이 되었고, logRepository에서 예외가 발생되어 전체 롤백이 발생한다.")
    void outerTxOn_fail() {
        //given
        String username = "로그예외_outerTxOff_fail";

        //when
        org.assertj.core.api.Assertions.assertThatThrownBy(()-> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        //then : 모든 데이터가 롤백된다.
        Assertions.assertTrue(memberRepository.find(username).isEmpty());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService    @Transactional : On
     * memberRepository @Transactional : ON
     * logRepository    @Transactional : ON Exception
     */
    @Test
    @DisplayName("트랜잭션이 전체 메서드에 적용이 되었고, logRepository에서 예외가 발생되어 전체 롤백이 발생한다.")
    void recoverException_fail() {
        //given
        String username = "로그예외_recoverException_fail";

        //when
        org.assertj.core.api.Assertions.assertThatThrownBy(()-> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        //then : 모든 데이터가 롤백된다.
        Assertions.assertTrue(memberRepository.find(username).isEmpty());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService    @Transactional : On
     * memberRepository @Transactional : ON
     * logRepository    @Transactional : ON(REQUIRES_NEW) Exception
     */
    @Test
    @DisplayName("트랜잭션이 전체 메서드에 적용이 되었고, ")
    void recoverException_success() {
        //given
        String username = "로그예외_recoverException_success";

        //when
        memberService.joinV2(username);

        //then : member 저장, log 롤백
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }
}