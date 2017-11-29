import java.util.*;
import java.lang.*;

class Prims{

	public static void main(String args[]){
		Scanner sc = new Scanner(System.in);
		int v, src;

		System.out.print("Enter number of nodes: ");
		v = sc.nextInt();

		int wtMat[][] = new int[v][v];
		System.out.println("\nEnter weight matrix: (Enter 999 for infinite weight)");

		for (int i=0; i<v; i++){
			for (int j=0; j<v; j++){
				wtMat[i][j] = sc.nextInt();
			}
		}

		System.out.print("\nEnter source node: ");
		src = sc.nextInt();

		MST mst = new MST();
		mst.findMST(wtMat, v, src);
	}
}

class MST{
	public void findMST(int wtMat[][], int v, int src){
		List<Node> forest = new ArrayList<>();
		List<Node> rem = new ArrayList<>();
		int count = 1;
		int div = 5;
		int segSize, lim, threadCount, min;
		Node minNode;

		Node srcNode = new Node();
		srcNode.src = src;
		srcNode.val = src;
		srcNode.dist = 0;
		forest.add(srcNode);

		for (int i=0; i<v; i++){
			if (i != src){
				Node node = new Node();
				node.val = i;
				node.dist = wtMat[src][i];
				rem.add(node);
			}
		}

		MinThread[] threads = new MinThread[div];

		while (count < v){
			segSize = rem.size()/div;
			if (segSize == 0) segSize = rem.size();
			threadCount = 0;

			for (int i=0; i<rem.size(); i+=segSize){
				if (i+segSize < rem.size()) lim = i+segSize;
				else lim = rem.size();
				threads[i/segSize] = new MinThread(rem.subList(i, lim));
				threads[i/segSize].start();
				threadCount++;
			}

			try{
				for (int i=0; i<threadCount; i++){
					threads[i].join();
				}
			}catch (InterruptedException e){
				e.printStackTrace();
			}

			min = 999;
			minNode = null;
			for (int i=0; i<threadCount; i++){
				Node node = threads[i].getMin();
				if (node != null && node.dist < min){
					min = node.dist;
					minNode = node;
				}
			}

			if (minNode != null){
				forest.add(minNode);
				rem.remove(minNode);
			}

			for (int i=0; i<rem.size(); i++){
				if (wtMat[minNode.val][rem.get(i).val] < rem.get(i).dist){
					rem.get(i).src = minNode.val;
					rem.get(i).dist = wtMat[minNode.val][rem.get(i).val];
				}
			}
			count++;
		}

		Collections.sort(forest, new Comparator<Node>(){
			public int compare(Node n1, Node n2){
				return n1.val < n2.val ? -1 : (n1.val > n2.val ? 1 : 0);
			}
		});

		System.out.println("\nMST: ");
		System.out.println("Edge\tDistance");
		for (int i=0; i<forest.size(); i++){
			if (i != src) System.out.println(forest.get(i).src+" - "+forest.get(i).val+"\t"+forest.get(i).dist);
		}
	}

	class Node{
		int src;
		int val;
		int dist;
	}

	class MinThread extends Thread{
		List<Node> rem;
		Node minNode;
		public MinThread(List<Node> rem){
			this.rem = rem;
		}

		public void run(){
			int min = 999;
			for (int i=0; i<rem.size(); i++){
				if (rem.get(i).dist < min){
					min = rem.get(i).dist;
					minNode = rem.get(i);
				}
			}
		}

		public Node getMin(){
			return minNode;
		}
	}	
}