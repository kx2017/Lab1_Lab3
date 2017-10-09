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
	private List<String> words;  //单词列表
	private Map<String, Integer> wordMap;  //将单词映射为编号
	private int[][] value;  //表示图的二维矩阵
	private boolean[][] pathFlag;  //表示最短路径的矩阵，便于将路径高亮显示

	/**
	 * 读取文件，处理文件内容
	 * @param filePath 要读取的文件路径
	 * @return 读取成功返回true
	 */
	private boolean createDirectedGraph(String filename) {
		File inputFile = new File(filename);
		try {
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(inputFile), "utf8"));
			words = new ArrayList<>();
			wordMap = new HashMap<>();
			List<String> tempWordList = new ArrayList<>();  //临时保存所有单词，从而分析边和权重
			while (bReader.ready()) {
				//按行读入
				String line = bReader.readLine().replaceAll("[^ a-zA-Z,.?!:;\"]+", "");
				line = line.replaceAll("\\W+", " ");
				String[] lineArray = line.split(" ");
				for (String word : lineArray) {
					if (! word.equals("")) {  //空格处理
						word = word.toLowerCase();  //均视为小写
						tempWordList.add(word);
						if (! wordMap.containsKey(word)) {
							//尚未加入words，建立新的映射
							words.add(word);
							wordMap.put(word, words.size()-1);
						}
					}
				}
			}
			//建立矩阵并计算权重
			value = new int[words.size()][words.size()];
			pathFlag = new boolean[words.size()][words.size()];
			for(int i = 0; i < words.size(); i ++){
				for(int j = 0; j < words.size(); j ++){
					pathFlag[i][j] = false;
				}
			}
			String startWord = words.get(0);  //边的起点
			for (int i = 1; i < tempWordList.size(); i++) {
				String endWord = tempWordList.get(i);
				value[wordMap.get(startWord)][wordMap.get(endWord)]++;  //权重+1
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
	 * 查询两个单词间的桥接词
	 * @param startWord 单词1
	 * @param endWord 单词2
	 * @return 单词1或2如不存在则返回Null，否则返回桥接词List
	 */
	public List<String> queryBridgeWords(String startWord, String endWord) {
		startWord = startWord.replaceAll("[^ a-zA-Z,.?!:;\"]+", "");
		endWord = endWord.replaceAll("[^ a-zA-Z,.?!:;\"]+", "");
		if (wordMap.containsKey(startWord) && wordMap.containsKey(endWord)) {
			List<String> result = new ArrayList<>();
			int startIndex = wordMap.get(startWord);  //起点
			int endIndex = wordMap.get(endWord);  //终点
			for (int i = 0; i < value[startIndex].length; i++) {
				//遍历起点的所有邻接点
				if (value[startIndex][i] > 0 && value[i][endIndex] > 0) {
					//i为桥接点
					result.add(words.get(i));
				}
			}
			return result;
		} else {
			return null;
		}

	}

	/**
	 * 根据文本及其桥接词获得新文本
	 * @param oldText 旧文本
	 * @return 新文本
	 */
	public String generateNewText(String oldText) {
		StringBuilder builder = new StringBuilder("");
		oldText = oldText.replaceAll("[^ a-zA-Z,.?!:;\"]+", "");
		String[] wordArray = oldText.replaceAll("\\W+", " ").replaceAll("^ ",
				"").replaceAll("$ ", "").toLowerCase().split(" ");  //容错处理
		String start = wordArray[0];
		Random random = new Random();
		for (int i = 1; i < wordArray.length; i++) {
			String end = wordArray[i];
			List<String> bridgeWords = queryBridgeWords(start, end);  //桥接词列表
			builder.append(start);
			builder.append(" ");
			if (bridgeWords != null && !bridgeWords.isEmpty()) {  //存在桥接词
				int rand = Math.abs(random.nextInt()) % bridgeWords.size();  //随机
				builder.append(bridgeWords.get(rand));
				builder.append(" ");
			}
			start = end;
		}
		builder.append(wordArray[wordArray.length-1]);  //把最后一个单词加上
		return builder.toString();
	}

	/**
	 * Dijkstra方法查找两点间最短路径
	 * @param v0  起点
	 * @param v1 终点
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
            // 循环求得与v0距离最近的节点prev和最短距离min
            int min = N;
            for (int j = 0; j < N; j++) {
                if (!visited[j] && dist[j] < min) {
                    min = dist[j];
                    prev = j;
                }
            }
            visited[prev] = true;
            // 根据prev修正其他所有节点到v0的前驱节点及距离
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
    		System.out.println(words.get(v0) + " -> " + words.get(v1) + " 最短路径的长为" + dist[v1]);
            Stack<Integer> s = new Stack<Integer>();
            int u = v1;
            while(u != v0){  //将路径压栈
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
        	System.out.println(words.get(v0)+" -> "+words.get(v1)+" 不可达\n");
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
	 * 随机游走，输出由起始单词生成的文本，按空格停止遍历
	 * @param startWord 起始单词
	 */
	public void randomWalk(Scanner cin) {
		List<String> tempWords = new ArrayList<>();  //临时保存startWord出边指向的所有单词
		int[][] flag = new int[words.size()][words.size()];  //标志边是否已经被访问过
		String tempWord = null;
		Random random = new Random();
		int startNode = Math.abs(random.nextInt()) % words.size();  //随机
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
			if (tempWords != null && !tempWords.isEmpty()) {  //存在出边
				int rand = Math.abs(random.nextInt()) % tempWords.size();  //随机
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
			if(input.equals("")){  //按ENTER键继续游走
				System.out.print(tempWord + " ");
			}else{  //按任意键+ENTER键停止游走
				break;
			}
		}while(tempWords != null && !tempWords.isEmpty());  //不存在出边时跳出循环
		System.out.println("\n游走完毕");
	}

	/**
	 * 将图生成为.jpg文件保存
	 * @param dotFormat 图中所有路径的字符串
	 * @param fileName 生成的图文件
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
	 * 得到图中所有边的字符串
	 * @return 边字符串
	 */
	public String getAllPath() {  //得到图的所有边，并写成dot语法形式的字符串
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
	 * 创建图文件
	 */
	void showDirectedGraph(){
		String dotFormat = getAllPath();
		createDotGraph(dotFormat, "DotGraph");
		new showImage("DotGraph.jpg");
	}

	/**
	 * 得到两单词间的最短路径
	 * @param word1 起始单词
	 * @param word2 终止单词
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
	 * 得到某单词到任意单词间的最短路径
	 * @param word1 起始单词
	 */
	void calcShortestPath(String word1){
		word1 = word1.replaceAll("[^ a-zA-Z,.?!:;\"]+", "");
		Random random = new Random();
		int rand = Math.abs(random.nextInt()) % words.size();  //随机
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
	 * 测试函数
	 */
	public void test() {
		return;
	}

	/**
	 * 主程序入口
	 * @param args
	 */
	public static void main(String[] args) {
		Graph graph = new Graph();
		System.out.print("请输入文件名（以.txt结尾）：");
		Scanner in = new Scanner(System.in);
		String filename = in.next();
		File file = new File(filename);
		while(!file.exists()){
			System.out.print("文件不存在，请重新输入：");
			filename = in.next();
			file = new File(filename);
		}
		graph.createDirectedGraph(filename);
		graph.showDirectedGraph();
		boolean flag = false;
		while(!flag){
			System.out.println("1.查询桥接词");
			System.out.println("2.根据桥接词生成新文本");
			System.out.println("3.查找两个单词之间的最短路径");
			System.out.println("4.查找某个单词到任一单词的最短路径");
			System.out.println("5.随机游走（按ENTER键继续，任意键结束游走）");
			System.out.print("请选择要执行的操作: ");
			String input = in.next();
			String startWord = new String();
			String endWord = new String();
			switch(input){
			case "1":
				System.out.print("请输入两个单词（以空格隔开）：");
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
				System.out.print("请输入要转换的文本：");
				String inText = new String();
				inText = in.nextLine();
				System.out.println(graph.generateNewText(inText));
				break;
			case "3":
				System.out.print("请输入两个单词（以空格隔开）：");
				startWord = in.next();
				endWord = in.next();
				graph.calcShortestPath(startWord, endWord);
				break;
			case "4":
				System.out.print("请输入起始单词：");
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
