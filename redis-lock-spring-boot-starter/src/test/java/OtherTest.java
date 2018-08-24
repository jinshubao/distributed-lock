import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * TODO 少年，写点啥吧！
 *
 * @author jinshubao
 * @date 2018/06/08
 */
public class OtherTest {

    @Test
    public void test0() throws InterruptedException {
        final CountDownLatch l = new CountDownLatch(1);
        l.await();
        l.countDown();
        l.countDown();
        l.countDown();
        l.countDown();
    }
}
