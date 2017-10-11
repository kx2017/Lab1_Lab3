import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;


public class Graph {
	public static final int MAX = 99999;
	private List<String> words;  //�����б�
	private Map<String, Integer> wordMap;  //������ӳ��Ϊ���
	private int[][] value;  //��ʾͼ�Ķ�ά����
	private boolean[][] pathFlag;  //��ʾ���·���ľ��󣬱��ڽ�·��������ʾ

	/**
	 * ��ȡ�ļ��������ļ�����
	 * @param filePath Ҫ��ȡ���ļ�·��
	 * @return ��ȡ�ɹ�����true
	 */
	private boolean createDirectedGraph(String filename) {
		File inputFile = new File(filename);
		try {
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(inputFile), "utf8"));
			words = new ArrayList<>();
			wordMap = new HashMap<>();
			List<String> tempWordList = new ArrayList<>();  //��ʱ�������е��ʣ��Ӷ������ߺ�Ȩ��
			while (bReader.ready()) {
				//���ж���
				String line = bReader.readLine().replaceAll("[^ a-zA-Z,.?!:;\"]+", "");
				line = line.replaceAll("\\W+", " ");
				String[] lineArray = line.split(" ");
				for (String word : lineArray) {
					if (! word.equals("")) {  //�ո���
						word = word.toLowerCase();  //����ΪСд
						tempWordList.add(word);
						if (! wordMap.containsKey(word)) {
							//��δ����words�������µ�ӳ��
							words.add(word);
							wordMap.put(word, words.size()-1);
						}
					}
				}
			}
			//�������󲢼���Ȩ��
			value = new int[words.size()][words.size()];
			pathFlag = new boolean[words.size()][words.size()];
			for(int i = 0; i < words.size(); i ++){
				for(int j = 0; j < words.size(); j ++){
					pathFlag[i][j] = false;
				}
			}
			String startWord = words.get(0);  //�ߵ����
			for (int i = 1; i < tempWordList.size(); i++) {
				String endWord = tempWordList.get(i);
				value[wordMap.get(startWord)][wordMap.get(endWord)]++;  //Ȩ��+1
				startWord = endWord;
			}
			bReader.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * ��ѯ�������ʼ���ŽӴ�
	 * @param startWord ����1
	 * @param endWord ����2
	 * @return ����1��2�粻�����򷵻�Null�����򷵻��ŽӴ�List
	 */
	public List<String> queryBridgeWords(String startWord, String endWord) {
		startWord = startWord.replaceAll("[^ a-zA-Z,.?!:;\"]+", "");
		endWord = endWord.replaceAll("[^ a-zA-Z,.?!:;\"]+", "");
		if (wordMap.containsKey(startWord) && wordMap.containsKey(endWord)) {
			List<String> result = new ArrayList<>();
			int startIndex = wordMap.get(startWord);  //���
			int endIndex = wordMap.get(endWord);  //�յ�
			for (int i = 0; i < value[startIndex].length; i++) {
				//�������������ڽӵ�
				if (value[startIndex][i] > 0 && value[i][endIndex] > 0) {
					//iΪ�Žӵ�
					result.add(words.get(i));
				}
			}
			return result;
		} else {
			return null;
		}

	}

	/**
	 * �����ı������ŽӴʻ�����ı�
	 * @param oldText ���ı�
	 * @return ���ı�
	 */
	public String generateNewText(String oldText) {
		StringBuilder builder = new StringBuilder("");
		oldText = oldText.replaceAll("[^ a-zA-Z,.?!:;\"]+", "");
		String[] wordArray = oldText.replaceAll("\\W+", " ").replaceAll("^ ",
				"").replaceAll("$ ", "").toLowerCase().split(" ");  //�ݴ���
		String start = wordArray[0];
		Random random = new Random();
		for (int i = 1; i < wordArray.length; i++) {
			String end = wordArray[i];
			List<String> bridgeWords = queryBridgeWords(start, end);  //�ŽӴ��б�
			builder.append(start);
			builder.append(" ");
			if (bridgeWords != null && !bridgeWords.isEmpty()) {  //�����ŽӴ�
				int rand = Math.abs(random.nextInt()) % bridgeWords.size();  //���
				builder.append(bridgeWords.get(rand));
				builder.append(" ");
			}
			start = end;
		}
		builder.append(wordArray[wordArray.length-1]);  //�����һ�����ʼ���
		return builder.toString();
	}

	/**
	 * Dijkstra����������������·��
	 * @param v0  ���
	 * @param v1 �յ�
	 */
	public void Dijkstra(int v0, int v1, String label) {
		int N = words.size();
        int[] path = new int[N];
        int[] dist = new int[N];
        boolean[] visited = new boolean[N];
        for(int j = 0;j < words.size(); j++){
        	for(int k = 0; k < words.size(); k++){
        		if(value[j][k] == 0)
        		{
        			value[j][k] = MAX;
        		}
        	}
        }
        int prev = 0;
        for (int i = 0; i < dist.length; i++) {
            path[i] = v0;
            dist[i] = value[v0][i];
            visited[i] = false;
        }
        visited[v0] = true;
        for (int v = 1; v < N; v++) {
            // ѭ�������v0��������Ľڵ�prev����̾���min
            int min = N;
            for (int j = 0; j < N; j++) {
                if (!visited[j] && dist[j] < min) {
                    min = dist[j];
                    prev = j;
                }
            }
            visited[prev] = true;
            // ����prev�����������нڵ㵽v0��ǰ���ڵ㼰����
            for (int k = 0; k < N; k++) {
                if (!visited[k] && (min + value[prev][k]) < dist[k]) {
                    path[k] = prev;
                    dist[k] = min + value[prev][k];
                }
            }
        }
        for(int j = 0; j < words.size(); j ++){
			for(int k = 0; k < words.size(); k ++){
				pathFlag[j][k] = false;
			}
		}
    	if(dist[v1] != MAX){
    		System.out.println(words.get(v0) + " -> " + words.get(v1) + " ���·���ĳ�Ϊ" + dist[v1]);
            Stack<Integer> s = new Stack<Integer>();
            int u = v1;
            while(u != v0){  //��·��ѹջ
            	s.push(u);
            	int v = u;
            	u = path[u];
            	pathFlag[u][v] = true;
            }
            s.push(v0);
            while(!s.empty()){
            	System.out.print(words.get(s.pop()));
            	if(!s.empty()){
            		System.out.print(" -> ");
            	}
            }
            System.out.println("\n");
            String dotFormat = getAllPath();
        	createDotGraph(dotFormat, "DotGraph" + label);
        }else{
        	System.out.println(words.get(v0)+" -> "+words.get(v1)+" ���ɴ�\n");
        }
    	for(int j = 0;j < words.size(); j++){
        	for(int k = 0; k < words.size(); k++){
        		if(value[j][k] == MAX)
        		{
        			value[j][k] = 0;
        		}
        	}
        }
	}

	/**
	 * ������ߣ��������ʼ�������ɵ��ı������ո�ֹͣ����
	 * @param startWord ��ʼ����
	 */
	public void randomWalk(Scanner cin) {
		List<String> tempWords = new ArrayList<>();  //��ʱ����startWord����ָ������е���
		int[][] flag = new int[words.size()][words.size()];  //��־���Ƿ��Ѿ������ʹ�
		String tempWord = null;
		Random random = new Random();
		int startNode = Math.abs(random.nextInt()) % words.size();  //���
		String startWord = words.get(startNode);
		System.out.println(startWord + " ");
		do {
			int start = wordMap.get(startWord);
			tempWords.clear();
			for(int i = 0; i < value[start].length; i ++) {
				if(value[start][i] != 0 && value[start][i] != MAX) {
					tempWords.add(words.get(i));
				}
			}
			if (tempWords != null && !tempWords.isEmpty()) {  //���ڳ���
				int rand = Math.abs(random.nextInt()) % tempWords.size();  //���
				tempWord = tempWords.get(rand);
				if(flag[start][rand] == 0) {
					flag[start][rand] ++;
				}else {
					break;
				}
			}else{
				break;
			}
			startWord = tempWord;
			String input = cin.nextLine();
			if(input.equals("")){  //��ENTER����������
				System.out.print(tempWord + " ");
			}else{  //�������+ENTER��ֹͣ����
				break;
			}
		}while(tempWords != null && !tempWords.isEmpty());  //�����ڳ���ʱ����ѭ��
		System.out.println("\n�������");
	}

	/**
	 * ��ͼ����Ϊ.jpg�ļ�����
	 * @param dotFormat ͼ������·�����ַ���
	 * @param fileName ���ɵ�ͼ�ļ�
	 */
	public static void createDotGraph(String dotFormat,String fileName)
	{
	    GraphViz gv=new GraphViz();
	    gv.addln(gv.start_graph());
	    gv.add(dotFormat);
	    gv.addln(gv.end_graph());
	    String type = "jpg";
	    gv.decreaseDpi();
	    gv.decreaseDpi();
	    File out = new File(fileName+"."+ type);
	    gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
	}

	/**
	 * �õ�ͼ�����бߵ��ַ���
	 * @return ���ַ���
	 */
	public String getAllPath() {  //�õ�ͼ�����бߣ���д��dot�﷨��ʽ���ַ���
		String paths = new String();
		StringBuilder tempPath = new StringBuilder();
		for(int i = 0; i < words.size(); i ++) {
			for(int j = 0; j < words.size();j ++) {
				if(value[i][j] != 0 && value[i][j] != MAX) {
					if(pathFlag[i][j]){
						tempPath.append(words.get(i) + "->" + words.get(j) + "[label=\"" + value[i][j] + "\", color=\"red\"];");
					}else{
						tempPath.append(words.get(i) + "->" + words.get(j) + "[label=\"" + value[i][j] + "\"];");
					}
				}
			}
		}
		paths = tempPath.toString();
		return paths;
	}

	/**
	 * ����ͼ�ļ�
	 */
	void showDirectedGraph(){
		String dotFormat = getAllPath();
		createDotGraph(dotFormat, "DotGraph");
		new showImage("DotGraph.jpg");
	}

	/**
	 * �õ������ʼ�����·��
	 * @param word1 ��ʼ����
	 * @param word2 ��ֹ����
	 */
	void calcShortestPath(String word1, String word2){
		word1 = word1.replaceAll("[^ a-zA-Z,.?!:;\"]+", "");
		word2 = word2.replaceAll("[^ a-zA-Z,.?!:;\"]+", "");
		if(words.contains(word1) && words.contains(word2)){
			Dijkstra(wordMap.get(word1), wordMap.get(word2), "Calc");
			new showImage("DotGraphCalc.jpg");
		}else{
			System.out.println("No " + word1 + " or " + word2 + " in the graph!");
		}
	}

	/**
	 * �õ�ĳ���ʵ����ⵥ�ʼ�����·��
	 * @param word1 ��ʼ����
	 */
	void calcShortestPath(String word1){
		word1 = word1.replaceAll("[^ a-zA-Z,.?!:;\"]+", "");
		Random random = new Random();
		int rand = Math.abs(random.nextInt()) % words.size();  //���
		if(words.contains(word1)){
			for(int i = 0 ; i < words.size(); i ++){
				Dijkstra(wordMap.get(word1), i, word1 + "To" + words.get(i));
				if(i == rand){
					new showImage("DotGraph" + word1 + "To" + words.get(i) + ".jpg");
				}
			}
		}else{
			System.out.println("No " + word1 + " in the graph!");
		}
	}

	/**
	 * ���Ժ���
	 */
	public void test() {
		return;
	}

	/**
	 * ���������
	 * @param args
	 */
	public static void main(String[] args) {
		Graph graph = new Graph();
		System.out.print("�������ļ�������.txt��β����");
		Scanner in = new Scanner(System.in);
		String filename = in.next();
		File file = new File(filename);
		while(!file.exists()){
			System.out.print("�ļ������ڣ����������룺");
			filename = in.next();
			file = new File(filename);
		}
		graph.createDirectedGraph(filename);
		graph.showDirectedGraph();
		boolean flag = false;
		while(!flag){
			System.out.println("1.��ѯ�ŽӴ�");
			System.out.println("2.�����ŽӴ��������ı�");
			System.out.println("3.������������֮������·��");
			System.out.println("4.����ĳ�����ʵ���һ���ʵ����·��");
			System.out.println("5.������ߣ���ENTER��������������������ߣ�");
			System.out.print("��ѡ��Ҫִ�еĲ���: ");
			String input = in.next();
			String startWord = new String();
			String endWord = new String();
			switch(input){
			case "1":
				System.out.print("�������������ʣ��Կո��������");
				startWord = in.next();
				endWord = in.next();
				List<String> bridgeWords = graph.queryBridgeWords(startWord, endWord);
				if(bridgeWords != null){
					if(bridgeWords.size() != 0){
						System.out.println("The bridge words from " + startWord + " to " + endWord +" are: ");
						for(String i : bridgeWords){
							System.out.print(i + " ");
						}
					}else{
						System.out.println("No bridge words from " + startWord + " to " + endWord);
					}
					System.out.print("\n");
				}else{
					System.out.println("No " + startWord + " or " + endWord + " in the graph!");
				}
				System.out.print("\n");
				break;
			case "2":
				in.nextLine();
				System.out.print("������Ҫת�����ı���");
				String inText = new String();
				inText = in.nextLine();
				System.out.println(graph.generateNewText(inText));
				break;
			case "3":
				System.out.print("�������������ʣ��Կո��������");
				startWord = in.next();
				endWord = in.next();
				graph.calcShortestPath(startWord, endWord);
				break;
			case "4":
				System.out.print("��������ʼ���ʣ�");
				startWord = in.next();
				graph.calcShortestPath(startWord);
				break;
			case "5":
				graph.randomWalk(in);
				break;
			default:
				flag = true;
				break;
			}
			if(flag){
				System.exit(0);;
			}
		}
		in.close();
	}
}
