public class TestLihi {
    public static void main(String[] args) {

        FibonacciHeap fibonacciHeap = new FibonacciHeap();
        for (int i = 0; i < 5000; i++) {
            fibonacciHeap.insert(i);
        }

        for (int i = 0; i < 1001; i++) {
            fibonacciHeap.deleteMin();
        }


        System.out.println(fibonacciHeap);

    }

}
