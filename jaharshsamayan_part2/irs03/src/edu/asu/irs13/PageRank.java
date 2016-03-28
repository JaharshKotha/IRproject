package edu.asu.irs13;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;


public class PageRank {

	public static final String linksFile = "IntLinks.txt";
	public static final String citationsFile = "IntCitations.txt";
	public static int numDocs = 25053;

	private int[][] links;
	private int[][] citations;
	
	public PageRank()
	{
		try
		{
			// Read in the links file
			links = new int[numDocs][];
			BufferedReader br = new BufferedReader(new FileReader(linksFile));
			String s = "";
			while ((s = br.readLine())!=null)
			{
				String[] words = s.split("->"); // split the src->dest1,dest2,dest3 string
				int src = Integer.parseInt(words[0]);
				if (words.length > 1 && words[1].length() > 0)
				{
					String[] dest = words[1].split(",");
					links[src] = new int[dest.length];
					for (int i=0; i<dest.length; i++)
					{
						links[src][i] = Integer.parseInt(dest[i]);
					}
				}
				else
				{
					links[src] = new int[0];
				}
			}
			br.close();
			
			// Read in the citations file
			citations = new int[numDocs][];
			br = new BufferedReader(new FileReader(citationsFile));
			s = "";
			while ((s = br.readLine())!=null)
			{
				String[] words = s.split("->"); // split the src->dest1,dest2,dest3 string
				int src = Integer.parseInt(words[0]);
				if (words.length > 1 && words[1].length() > 0)
				{
					String[] dest = words[1].split(",");
					citations[src] = new int[dest.length];
					for (int i=0; i<dest.length; i++)
					{
						citations[src][i] = Integer.parseInt(dest[i]);
					}
				}
				else
				{
					citations[src] = new int[0];
				}

			}
			br.close();
		}
		catch(NumberFormatException e)
		{
			System.err.println("links file is corrupt: ");
			e.printStackTrace();			
		}
		catch(IOException e)
		{
			System.err.println("Failed to open links file: ");
			e.printStackTrace();
		}
	}
	
	public int[] getLinks(int docNumber)
	{
		return links[docNumber];
	}
	
	public int[] getCitations(int docNumber)
	{
		return citations[docNumber];
	}
	
	public static void main(String[] args)
	{
		try
		{
		HashMap<Integer, Double> sim = new HashMap<Integer, Double>();
		HashMap<Integer, Double> l2 = new HashMap<Integer, Double>();
		HashMap<String, Double> idf = new HashMap<String, Double>();
		System.out.println("Building Indexes and norms:");
		
		IndexReader r = IndexReader.open(FSDirectory
				.open(new File("index")));
		TermEnum t = r.terms();
		int N = r.maxDoc();
		int v=0;
//Compute the idf for all the terms and store it in a hash map
		while (t.next()) {
			int cnt = 0;
			Term te = t.term();
			if (te.text().matches("^[\\p{ASCII}]*$")) {
				TermDocs td = r.termDocs(te);
				while (td.next()) {
					++cnt;
				}
				Double idf_val = 0.0;
				idf_val = Math.log(N / cnt);

				idf.put(te.text(), (double) idf_val);

			}
			++v;
		}
		
		
		for(int i=0;i<25054;i++)
		{
			sim.put(i,0.0);
		}
		
		
		
		double temp=0,id=0,ch=0,temp1=0,gt=0;
		int[] ans= new int[20];
		double[] idf_val= new double[20];
		
		for(int i=0;i<25054;i++)
		{
			l2.put(i,0.0);
		}
		//Compute the L2 norms for all the documents 
		
		TermEnum ter = r.terms();
		while(ter.next())
		{
			Term te = ter.term();
			TermDocs tocs = r.termDocs(te);
			while (tocs.next()) {
				gt=l2.get(tocs.doc());
				temp=tocs.freq()*tocs.freq();
				l2.put(tocs.doc(),(temp+gt));
				
			}
			
		
		}
		
		
		 for(Map.Entry<Integer, Double> entry : l2.entrySet()){
	         temp=Math.sqrt(entry.getValue());    
			 temp=1/temp;
			 l2.put(entry.getKey(),entry.getValue());
			
			 
	        }		
		
		int cnt=0,size=0;
		Scanner sc = new Scanner(System.in);
		String str = "";
		System.out.print("query> ");
		while (!(str = sc.nextLine()).equals("quit")) {
			System.out.print("query> ");
			size = 0;
			
			
			//Idf similarity for all the terms in the query
			String[] terms = str.split("\\s+");
			for (String word : terms) {
				Term term = new Term("contents", word);
				TermDocs tocs = r.termDocs(term);
				id=idf.get(term.text());
				while (tocs.next()) {
					ch=sim.get(tocs.doc());
					sim.put(tocs.doc(),((tocs.freq()+ch)*id));
					
					
				}
			}
			
			
			 for(Map.Entry<Integer, Double> entry : sim.entrySet()){
		         temp=entry.getValue();    
				 temp1=l2.get(entry.getKey());
				 if(terms.length==2)
				 {
				 temp1=temp1*0.707;
				 }
				 temp=temp/temp1;
				 sim.put(entry.getKey(),temp);
		        }
				cnt=0;	
				// Sorting the idf hashmap for top results
			Set<Entry<Integer, Double>> set_idf = sim.entrySet();

			List<Entry<Integer, Double>> list_idf = new ArrayList<Entry<Integer, Double>>(set_idf);

			Collections.sort(list_idf,new Comparator<Map.Entry<Integer, Double>>() {
						public int compare(Map.Entry<Integer, Double> o3,
								Map.Entry<Integer, Double> o4) {
							return (o4.getValue()).compareTo(o3.getValue());
						}
					});
			System.out.println("\n\n\nWith IDF results:\n\n");
			for (Map.Entry<Integer, Double> ent : list_idf) {
				if (ent != null && ent.getKey() != null && ent.getValue() !=0) 
				{
					System.out.println(ent.getKey()+","+ent.getValue());
					ans[cnt]=ent.getKey();
					idf_val[cnt]=ent.getValue();
					++cnt;
					if(cnt==19)
					{break;}
				}

			}
		int i=0,j=0,th=0,md=0,g=0,h=0,flg=0;
		double sum=0,max=0,bm=0;
		LinkAnalysis.numDocs = 25054;
		LinkAnalysis l = new LinkAnalysis();
		
		i=0;
		double[] m=new double[25054];
		int[] sink=new int[25054];
		double[] m2=new double[25054];
		double[] K=new double[25054];
		double[] R1=new double[25054];
		double[] R2=new double[25054];
		for(i=0;i<25054;i++)
		{
			sink[i]=0;
		}
		for(i=0;i<25054;i++)
		{
			R1[i]=0.00004;
			K[i]=0.00004;
		}
		
		//find the sink nodes for a matrix not yet transposed
		for(i=0;i<25054;i++)
		{
			int[] links2 = l.getLinks(i);
			if(links2.length==0)
			{
				sink[i]=1;
			}
		}
		while(flg==0)
		{
		
			//Compute each row of the adjacency matrix 
		for(j=0;j<25054;j++)
		{
			for(i=0;i<25054;i++)
			{
				m[i]=0;
				m2[i]=0;
			}
			int[] links3 = l.getCitations(j);
			for(int pb:links3)
			{
				m[pb]=1;
			}
			for(i=0;i<25054;i++)
			{
			if(sink[i]==1)
			{
				m[i]=0.00004;
			}
			}
			
			//Computing the Stochastic matrix 
		
		double c=0.8;
	
		for(i=0;i<25054;i++)
		{
			m2[i]=(c*m[i]+(1-c)*K[i]);
		}
		
		//Find the page ranks row by row 	
		for(i=0;i<25054;i++)
		{
	
		sum=sum+(m2[i]*R1[i]);	
		}
		R2[th]=sum;
		++th;
		sum=0;
		if(th==25054)
		{
			for(i=0;i<25054;i++)
			{
				
				if((R1[i]-R2[i])>bm)
				{
					
					bm=(R2[i]-R1[i]);
				}
			}
			//power iterate this till it reaches threshold of 10 power -7 
			if(bm<0.0000001)
			{
				flg=1;
			}
			else
			{
				bm=0;
			}
		for(h=0;h<25054;h++)
		{
			R1[h]=R2[h];
			
		}
		
		th=0;
		}
		
		}
		
		
		}
		// Check out the maximum page rank in the corpus
		for(i=0;i<25054;i++)
		{
			if(R2[i]>max)
			{
				
				max=R2[i];
				md=i;
			}
		}
		double w=0.4;
		double[] fin=new double[20];
		int cont=0;
		//Combining the idf and pagerank values
		for(cont=0;cont<20;cont++)
		{
			fin[cont]=(  (w*R2[ans[cont]])+((1-w)*idf_val[cont]) );
		}
		
		int  n=0,o=0;
		double ms=0,p=0;
		int pos=0;
		//Sort the final results 
		for(i=0;i<20;++i)
		{
			ms=fin[i];
		    pos=i;
			for(j=i;j<20;j++)
			{
				if(fin[j]<ms)
				{
				ms=fin[j];
				n=ans[j];
				pos=j;
				}
				
			}
			
			
			p=fin[i];
			o=ans[i];
			fin[i]=ms;
			ans[i]=n;
			fin[pos]=p;
			ans[pos]=o;
			
		}
		System.out.print("Answers\n");
		for(i=1;i<11;i++)
		{
			System.out.println(ans[i]);
		}
		
		System.out.print("\n");
		System.out.println("Max doc:"+md+"value :"+max);
		System.out.print(R2[299]);
	
	
		
		}
		
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
