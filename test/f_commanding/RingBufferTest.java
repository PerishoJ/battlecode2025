package f_commanding;

import org.junit.Test;

import static org.junit.Assert.*;

public class RingBufferTest {
  @Test
  public void basicTest(){
    RingBuffer<Integer> buffer = new RingBuffer<>(2);

    buffer.push(5);
    buffer.push(7);

    assertEquals(5, (int)buffer.take());
    assertEquals(7, (int)buffer.take());
  }

  @Test
  public void wrapTest(){
    RingBuffer<Integer> buffer = new RingBuffer<>(2);

    buffer.push(5);
    buffer.push(7);
    buffer.push(8);
    buffer.push(9);


    assertEquals(8, (int)buffer.take());
    assertEquals(9, (int)buffer.take());
  }

  @Test
  public void takeTooMuchTest(){
    RingBuffer<Integer> buffer = new RingBuffer<>(2);

    buffer.push(5);
    buffer.push(7);
    buffer.take();
    buffer.take();

    assertNull(buffer.take());
  }

}