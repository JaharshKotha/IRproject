
package edu.asu.irs13;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

public class Tf_idf_values {

	public static void main(String[] args) {

		try {
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
			int[] ans= new int[10];
			
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
						Document d = r.document(ans[cnt]);
						String url = d.getFieldable("path").stringValue(); // the 'path' field of the Document object holds the URL
						System.out.println(url.replace("%%", "/"));
						++cnt;
						if(cnt==9)
						{break;}
					}

				}
				int i=0,j=0,k=0,len=0,y=0,z=0,d=0,h=0;
				LinkAnalysis.numDocs = 25054;
				LinkAnalysis l = new LinkAnalysis();
				
				int[] adjlist=new int[25054];
				for(i=0;i<25054;i++)
				{
					adjlist[i]=0;
				}
				i=0;
				for(int x:ans)
				{
					adjlist[x]=1;
				}
				//Creating the base set
				for(int x:ans)
				{
					
				int[] links1 = l.getLinks(x);
				
				for(int pb:links1)
				{
					
					if(adjlist[pb]==0)
					{
						adjlist[pb]=1;
						++len;
					}
				}
				
				int[] cit3 = l.getCitations(x);
				for(int pb:cit3)
				{
					if(adjlist[pb]==0)
					{
						adjlist[pb]=1;
						++len;
					}
				}
				
				}
				len=len+10;
				int[] ref=new int[len];
				int[][] adj=new int[len+1][len];
				double[] I=new double[len];
				double[] O=new double[len];
				//Taking a copy of baseset
				for(i=0;i<25054;i++)
				{
					if(adjlist[i]==1)
					{
						ref[z]=i;
						
						++z;
					}
				}
				
				
				for(j=0;j<len+1;j++)
				{
					for(k=0;k<len;k++)
					{
						adj[j][k]=0;
					}
				}
				//Initialize first row of the adjacency matrix to the baseset
				for(i=0;i<len;i++)
				{
					adj[0][i]=ref[i];
				}
				
				i=1;
				//Creating the adjacency matrix for the baseset
				for(y=0;y<len;y++)
				{
				
				int[] links3 = l.getLinks(ref[y]);
				
				for(int pb:links3)
				{
					for(d=0;d<len;d++)
					{
						if(adj[0][d]==pb)
						{
							adj[i][d]=1;
						}
					}
					
				}
				
				++i;
				
				}
				//Calculating the initial values of authority before pushing into iterations
				for(i=1;i<len+1;i++)
				{
					for(j=0;j<len;j++)
					{
					if(adj[i][j]==1)
					{++h;}
					}
					I[i-1]=h;
					h=0;
				}
				//Iterating through the values
				
				for(h=0;h<2000;h++)
				{
				for(i=0;i<len;i++)
				{
					for(j=0;j<len;j++)
					{
						if(adj[i][j]==1)
						{
							O[i]=O[i]+I[j];
						}
					}
				}
				
				double pow=0,sum=0;
				//Normalize after every iteration
				for(i=0;i<len;i++)
				{
					pow=I[i]*I[i];
					sum=sum+pow;
					
				}
				double su=Math.sqrt(sum);
				
				for(i=0;i<len;i++)
				{
					I[i]=I[i]/su;
				}
				
				pow=0;sum=0;
				for(i=0;i<len;i++)
				{
					pow=O[i]*O[i];
					sum=sum+pow;
					
				}
				 su=Math.sqrt(sum);
					//Normalize after every iteration
				for(i=0;i<len;i++)
				{
					O[i]=O[i]/su;
				}
				//Compute values for the next iteration and store them
				for(i=0;i<len;i++)
				{
					for(j=0;j<len;j++)
					{
						if(adj[i][j]==1)
						{
							I[i]=I[i]+O[j];
						}
					}
				}
				}
				
				double[][] sort_I=new double[2][len];
				double[][] sort_O=new double[2][len];
			
				
				for(i=0;i<len;i++)
				{
					sort_I[1][i]=ref[i];
					sort_I[0][i]=I[i];
					sort_O[1][i]=ref[i];
					sort_O[0][i]=O[i];
					
				}
			
				System.out.print("\n");
				double m=0,n=0,o=0,p=0;
				int pos=0;
				//Sort the Authority and Hub scores for top results
				for(i=0;i<len;++i)
				{
					m=sort_I[0][i];
				    pos=i;
					for(j=i;j<len;j++)
					{
						if(sort_I[0][j]<m)
						{
						m=sort_I[0][j];
						n=sort_I[1][j];
						pos=j;
						}
						
					}
					
					
					p=sort_I[0][i];
					o=sort_I[1][i];
					sort_I[0][i]=m;
					sort_I[1][i]=n;
					sort_I[0][pos]=p;
					sort_I[1][pos]=o;
					
				}
				
				m=0;n=0;o=0;p=0;pos=0;
				for(i=0;i<len;++i)
				{
					m=sort_O[0][i];
				    pos=i;
					for(j=i;j<len;j++)
					{
						if(sort_O[0][j]<m)
						{
						m=sort_O[0][j];
						n=sort_O[1][j];
						pos=j;
						}
						
					}
					
					
					p=sort_O[0][i];
					o=sort_O[1][i];
					sort_O[0][i]=m;
					sort_O[1][i]=n;
					sort_O[0][pos]=p;
					sort_O[1][pos]=o;
					
				}
				System.out.print("Hub\n");
				for(i=3;i<13;i++)
				{
					
					System.out.print(sort_O[1][i]+"\n");
					Document dov = r.document((int)sort_O[1][i]);
					String url = dov.getFieldable("path").stringValue(); // the 'path' field of the Document object holds the URL
					System.out.println(url.replace("%%", "/"));
				}
				System.out.print("\n\n");
				System.out.print("Authority\n");
				for(i=0;i<10;i++)
				{
					--len;
					System.out.print(sort_I[1][len]+"\n");
					Document dov = r.document((int)sort_I[1][i]);
					String url = dov.getFieldable("path").stringValue(); // the 'path' field of the Document object holds the URL
					System.out.println(url.replace("%%", "/"));
				
				}
			
				
					
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}