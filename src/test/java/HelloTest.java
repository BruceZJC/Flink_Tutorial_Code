import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HelloTest {
    @Test
    public void testHello(){
        Hello hlo = new Hello();
        String ipt = hlo.sayHello("XiaoMing");
        assertEquals("Hello, XiaoMing", ipt);
    }
}
