package f_c2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CodecTest {

    @Test
    public void simpleCodecTest(){

        int first = 35;
        int second = 22;
        int third = 125;
        int message = new Codec()
                .encodeInt(first,6)
                .encodeInt(second,5)
                .encodeInt(third,8)
                .encode();

        Codec deser = new Codec(message);

        assertEquals(third,deser.decodeInt(8));
        assertEquals(second,deser.decodeInt(5));
        assertEquals(first,deser.decodeInt(6));

    }

}