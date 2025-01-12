package f_commanding;

public class RingBuffer <T> {

  Object[] buffer;
  int writeIndex = 0 ;
  int readIndex = 0;
  int capacity = 0;
  int size = 0;
  public RingBuffer(int capacity){
    buffer = new Object[capacity];
    this.capacity = capacity;
  }

  void push(T value){
    buffer[writeIndex] = value;
    writeIndex = (writeIndex+1)%capacity;
    size ++;
    if (size > capacity){
      size = capacity;
      readIndex=(readIndex+1)&capacity; //you've wrapped around to the read index...drop that record like it's hot
    }
  }

  T poll(){
    return (T) buffer[readIndex];
  }

  T take(){
    if(size==0)
      return null;
    T value = poll();
    readIndex= (readIndex+1)%capacity;
    size--;
    return value;
  }

}
