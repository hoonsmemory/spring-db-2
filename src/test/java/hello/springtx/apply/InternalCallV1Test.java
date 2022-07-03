package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired CallService callService;

    @Test
    @DisplayName("external 메서드 호출")
    public void externalCall() throws Exception {
        callService.external();
    }

    @Test
    @DisplayName("internal 메서드 호출")
    public void internalCall() throws Exception {
        callService.internal();
    }

    @Test
    @DisplayName("프록시 적용이 되었는지 확인")
    public void printProxy() throws Exception {
        log.info("callService class={}", callService.getClass());
    }


    @TestConfiguration
    static class InternalCallV1TestConfig {

        @Bean
        public CallService callService() {
            return new CallService();
        }
    }


    static class CallService {
        public void external() {
            log.info("call external");
            printTxInfo();
            internal();
        }

        @Transactional
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active = {}", txActive);
        }
    }



}
