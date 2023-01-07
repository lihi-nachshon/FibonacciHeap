public class TestLihi {
    public static void main(String[] args) {

        FibonacciHeap fibonacciHeap = new FibonacciHeap();
        for (int i = 0; i < 5; i++) {
            fibonacciHeap.insert(i);
        }

        fibonacciHeap.deleteMin();
        fibonacciHeap.decreaseKey(fibonacciHeap.findMin().child.child,10);
        System.out.println(fibonacciHeap);

    }

}
