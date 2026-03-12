class Node {
  constructor(val) {
    this.val = val;
    this.next = null;
  }
}

class Queue {
  constructor() {
    this.head = null;
    this.tail = null;
    this.size = 0;
  }

  enqueue(val) {
    const newNode = new Node(val);
    if (this.head === null) {
      this.head = this.tail = newNode;
    } else {
      this.tail.next = newNode;
      this.tail = newNode;
    }
    this.size++;
  }

  dequeue() {
	  if(this.size > 0) {
		const val = this.head.val;
		this.head = this.head.next;
		this.size--;
		return val;
	  }
  }

  peek() {
    return this.head.val;
  }

  getStr() {
    let str = "[";
    let currentNode = this.head;
    while (currentNode) {
      str += `${currentNode.val} ,`;
      currentNode = currentNode.next;
    }
    str = str.substr(0, str.length - 2);
//    console.log(str + "]");
  }
}


//const queue = new Queue();
//queue.enqueue(1);
//queue.enqueue(2);
//queue.enqueue(3);
//console.log( queue.dequeue() );
//console.log( queue.dequeue() );
//queue.getStr(); // [2, 3]
//console.log( queue.dequeue() );
//console.log( queue.dequeue() );
