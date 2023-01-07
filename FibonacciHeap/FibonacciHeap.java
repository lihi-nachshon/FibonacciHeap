/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap {
    private int size;
    private HeapNode first;
    private HeapNode min;
    private int marked;
    private int numTrees;
    private static int totalLinks;
    private static int totalCuts;
    private static final Integer INF = Integer.MAX_VALUE;
   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    * Time Complexity O(1).
    */
    public boolean isEmpty(){
    	return size == 0;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    * Time Complexity O(1).
    */
    public HeapNode insert(int key){
        HeapNode newNode = new HeapNode(key);
        if(size == 0){
            this.first = newNode;
            this.min = newNode;
            newNode.next = newNode;
            newNode.prev = newNode;
            this.size += 1;
            this.numTrees ++;
            return newNode;
        }
        this.insertNode(newNode);
        this.size += 1;
        return newNode;
    }
    public void insertNode(HeapNode newNode){
        HeapNode firstNode = this.first;
        HeapNode lastNode = firstNode.prev;
        newNode.prev = lastNode;
        lastNode.next = newNode;
        newNode.next = firstNode;
        firstNode.prev = newNode;
        this.first = newNode;
        if(this.min.key > newNode.key){
            this.min = newNode;
        }
        this.numTrees ++;
    }
   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin(){
        if(this.size <= 1){
            this.first = null;
            this.min = null;
            this.size = 0;
            this.numTrees = 0;
            return;
        }
        HeapNode minNode = this.min;
        if(minNode.equals(this.first)){
            if(minNode.rank > 0){
                this.first = minNode.child;
            }
            else {
                this.first = minNode.next;
            }
        }
        if(numTrees > 1){
            HeapNode nextNode = minNode.next;
            HeapNode prevNode = minNode.prev;
            if(minNode.child != null){
                HeapNode firstChildNode = minNode.child;
                HeapNode lastChildNode =  firstChildNode.prev;
                this.makeChildrenRoots(firstChildNode, minNode.rank);
                firstChildNode.parent = null;
                prevNode.next = firstChildNode;
                firstChildNode.prev = prevNode;
                lastChildNode.next = nextNode;
                nextNode.prev = lastChildNode;
            }
            else{
                nextNode.prev = prevNode;
                prevNode.next = nextNode;
            }
        }
        this.numTrees += minNode.rank - 1;
        minNode.restart();
        this.size--;

        int LOG_PHY = (int)Math.floor(Math.log((double)this.size + 1)/Math.log((1+Math.sqrt(5))/2));
        HeapNode[] rankArr = new HeapNode[LOG_PHY];
     	HeapNode node = this.first;
        int n = numTrees;
        for(int i = 0; i < n; i++){

                HeapNode next = node.next;
                int r = node.rank;
                if(rankArr[r] == null){
                    rankArr[r] = node;
                }
                else{
                    while(rankArr[r] != null) {
                        HeapNode otherNode = rankArr[r];
                        if(node.key > otherNode.key){
                            HeapNode temp = node;
                            node = otherNode;
                            otherNode = temp;
                        }
                        this.link(node, otherNode);
                        rankArr[r] = null;
                        r++;
                    }
                    rankArr[r] = node;
                }
                node = next;
        }
        this.updateHeapFromArray(rankArr);
        this.findNewMin();
    }

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    * Time Complexity O(1).
    */
    public HeapNode findMin(){
    	return this.min;// should be replaced by student code
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    * Time Complexity O(1).
    */
    public void meld(FibonacciHeap heap2){
    	 if(heap2.size == 0){
           return;
         }
         if(this.size == 0){
             this.first = heap2.first;
             this.min = heap2.min;
             this.size  = heap2.size;
             this.marked = heap2.marked;
             this.numTrees = heap2.numTrees;
             return;
         }
         HeapNode thisFirst = this.first;
         HeapNode thisLast = this.first.prev;
         HeapNode heapFirst = heap2.first;
         HeapNode heapLast = heap2.first.prev;
         thisFirst.prev = heapLast;
         heapLast.next = thisFirst;
         thisLast.next = heapFirst;
         heapFirst.prev = thisLast;
         this.min = this.min.key > heap2.min.key ? heap2.min : this.min;
         this.size  = this.size + heap2.size;
         this.marked = this.marked + heap2.marked;
         this.numTrees = this.numTrees + heap2.numTrees;
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    * Time Complexity O(1).
    */
    public int size(){
    	return this.size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * (Note: The size of of the array depends on the maximum order of a tree.)  
    * Time Complexity O(n)
    */
    public int[] countersRep(){
        int LOG_PHY = (int)Math.floor(Math.log((double)this.size + 1)/Math.log((1+Math.sqrt(5))/2));
    	int[] arr = new int[LOG_PHY];
        HeapNode node = this.first;
        while(node != null){
            arr[node.rank] += 1;
        }
        return arr;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    * Time Complexity WC O(n)
    * Time Complexity amortized O(logn)
    */
    public void delete(HeapNode x){
    	this.decreaseKey(x, INF);
        this.deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta){
    	x.key -= delta;
        if(x.parent.key < x.key) {
            return;
        }
        this.cascadingCut(x, x.parent);
    }

   /**
    * public int nonMarked() 
    *
    * This function returns the current number of non-marked items in the heap
    * Time Complexity O(1)
    */
    public int nonMarked(){
        return this.size - this.marked; // should be replaced by student code
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap.
    * Time Complexity O(1)
    */
    public int potential(){
        return this.numTrees + 2 * this.marked; // should be replaced by student code
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    * Time Complexity O(1)
    */
    public static int totalLinks(){
    	return totalLinks;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods).
    * Time Complexity O(1)
    */
    public static int totalCuts(){
    	return totalCuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H.
    * Time Complexity O(k*deg(H))
    */
    public static int[] kMin(FibonacciHeap H, int k){
        int[] arr = new int[k];
        FibonacciHeap helper = new FibonacciHeap();
        HeapNode copyMin = helper.insert(H.min.key);
        copyMin.info = H.min.child;
        for(int i = 0; i < k - 1; i++){
            HeapNode minNode = helper.min;
            arr[i] = minNode.key;
            HeapNode nodeChild = minNode.info;
            while(nodeChild != null){
                HeapNode copyNode = helper.insert(nodeChild.key);
                copyNode.info = nodeChild.child;
                nodeChild = nodeChild.next;
            }
            helper.deleteMin();
        }
        return arr;
    }

    public void makeChildrenRoots(HeapNode node, int rank){
        for(int i = 0; i < rank; i++){
            if(node.mark){
                node.mark = false;
                marked--;
            }
            node.parent = null;
        }
    }

    public void link(HeapNode minNode, HeapNode maxNode){
        if(minNode.child != null){
            HeapNode childMin = minNode.child;
            HeapNode lastChildMin = childMin.prev;
            maxNode.next = childMin;
            childMin.prev = maxNode;
            lastChildMin.next = maxNode;
            maxNode.prev = lastChildMin;

        }
        else{
            maxNode.next = maxNode;
            maxNode.prev = maxNode;
        }

        maxNode.parent = minNode;
        minNode.child = maxNode;
        minNode.next = null;
        minNode.prev = null;
        minNode.rank++;
        numTrees--;
        totalLinks++;
    }

    public void updateHeapFromArray(HeapNode[] rankArr){
        HeapNode prevNode = null;
        numTrees = 0;
        for(HeapNode node : rankArr){
            if(node != null && prevNode == null){
                this.first = node;
                prevNode = node;
                numTrees++;
            }
            else if(node != null){
                prevNode.next = node;
                node.prev = prevNode;
                prevNode = node;
                numTrees++;
            }
        }
        if(prevNode != null){
            this.first.prev = prevNode;
            prevNode.next = this.first;
        }
        else {
            this.first = null;
        }
    }

    public void findNewMin(){
        HeapNode node = this.first;
        HeapNode min = this.first;
        for(int i=0; i < numTrees; i++){
            if(node.key < min.key){
                min = node;
            }
            node = node.next;
        }
        this.min = min;
    }

    public void cascadingCut(HeapNode x, HeapNode y){
        this.cut(x, y);
        if(y.parent != null){
            if(y.mark = false){
                y.mark = true;
                marked++;
            }
            else{
                cascadingCut(y, y.parent);
            }
        }
    }

    public void cut(HeapNode x, HeapNode y){
        totalCuts++;
        x.parent = null;
        if(x.mark){
            marked--;
        }
        x.mark = false;
        y.rank = y.rank-1;
        if(x.next == x){
            y.child = null;
        }
        else{
            y.child = x.next;
            x.prev.next = x.next;
            x.next.prev = x.prev;
        }
        this.insertNode(x);

    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{

        public int key;
        public HeapNode info;
        public int rank;
        public boolean mark;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;

    	public HeapNode(int key){
            this.key = key;
    	}

    	public int getKey() {
    		return this.key;
    	}

        public void restart() {
            key = 0;
            info = null;
            rank= 0;
            mark = false;
            child = null;
            next = null;
            prev = null;
            parent = null;

        }


    }
}
