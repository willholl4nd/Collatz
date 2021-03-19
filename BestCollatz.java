import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;

public class BestCollatz extends Thread {
	BigInteger start, initial;
	int biggestSequence;
	final BigInteger ZERO, ONE, TWO, THREE;
	ArrayList<String> list;
	
	public BestCollatz(BigInteger start, int biggestSequence) {
		this.start = start;
		this.initial = start;
		this.biggestSequence = biggestSequence;
		ZERO = BigInteger.ZERO;
		ONE = BigInteger.ONE;
		TWO = new BigInteger("2");
		THREE = new BigInteger("3");
	}
	
	@Override
	public void run() {
		list = new ArrayList<>();
		BigInteger count = ZERO;
		
		/*Run collatz algorithm*/
		while(!start.equals(ONE)) {
			BigInteger mod = start.mod(TWO);
			if (mod.equals(ZERO)) {
				count = count.add(ONE);
				start = start.divide(TWO);
				list.add(start+"\n");
			} else if (mod.equals(ONE)){
				start = start.multiply(THREE);
				start = start.add(ONE);
				list.add(start+"\n");
				start = start.divide(TWO);
				list.add(start+"\n");
				count = count.add(TWO);
			}
		}
	}
	
	public static void main(String[] args) {
		int numOfProc = Runtime.getRuntime().availableProcessors();
		System.out.println("Currently using " + numOfProc + " threads/cores");
		Scanner sc = new Scanner(System.in);
		System.out.println("How many new collatz sequences do you want to find? ");
		Timer t = new Timer();
		long answer = sc.nextLong();
		sc.close();
		
		t.start();
		File info = new File("info.txt");
		int inputBiggestSequence = 0;
		BigInteger starting = new BigInteger("1");
		if(info.exists()) {
			try {
				Scanner infoScanner = new Scanner(info);
				inputBiggestSequence = infoScanner.nextInt();
				long temp = infoScanner.nextLong();
				starting = new BigInteger((temp + 1) + "");
				infoScanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Finding new collatz sequences starting at " + starting);
		for (long i = 0; i < Math.round(answer / numOfProc); i++) {
			BestCollatz[] threads = new BestCollatz[numOfProc];
			for (int j = 0; j < threads.length; j++) {
				threads[j] = new BestCollatz(starting, inputBiggestSequence);
				threads[j].run();
				starting = starting.add(BigInteger.ONE);
			}
			for (int j = 0; j < threads.length; j++) {
				try {
					threads[j].join();
					try {
						/*If the sequence is bigger than the previous biggest sequence,
						we will create a file and write the sequence to it.*/
						if(threads[j].list.size() > inputBiggestSequence) {
							File dir = new File("completed collatz/");
							if(!dir.isDirectory())
								dir.mkdir();
							File f = new File("completed collatz/" + threads[j].initial + ".txt");
							PrintWriter p = new PrintWriter(f);
							System.out.println(threads[j].initial.longValue());
							p.write(threads[j].initial+"\n");
							for(String s : threads[j].list) {
								p.write(s);
							}
							p.write("\n\n\n");
							p.write("Steps = " + threads[j].list.size());
							p.close();
							inputBiggestSequence = threads[j].list.size();
						}
						
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		info.delete();
		
		try {
			PrintWriter p = new PrintWriter(new File("info.txt"));
			p.write(inputBiggestSequence + "\n");
			p.write(starting.toString());
			p.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println("Ended at the collatz sequence for " + starting.subtract(BigInteger.ONE));
		t.end();
		System.out.println("Took " + t.getTimeFromStart() + " to find " + answer + " new collatz sequences");
	}
}
