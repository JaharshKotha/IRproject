package edu.asu.irs13;

import java.io.*;


public class Part2 {

	public static final String linksFile = "IntLinks.txt";
	public static final String citationsFile = "IntCitations.txt";
	public static int numDocs = 25053;

	private int[][] links;
	private int[][] citations;
	
	public Part2()
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
		int i=0,j=0,k=0,len=0,y=0,z=0,d=0,h=0;
		LinkAnalysis.numDocs = 25054;
		LinkAnalysis l = new LinkAnalysis();
		int[] ans=new int[]{1,2,3,4,5,6,7,8,9,10};
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
		for(i=0;i<len;i++)
		{
			adj[0][i]=ref[i];
		}
		
		i=1;
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
		for(i=0;i<len;i++)
		{
			System.out.println(I[i]);
		}
		System.out.println("llenth="+len);
		
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
		
		for(i=0;i<len;i++)
		{
			O[i]=O[i]/su;
		}
		
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
		for(i=0;i<len;i++)
		{
			System.out.print(sort_I[0][i]+"\n");
		}
		System.out.print("\n");
		double m=0,n=0,o=0,p=0;
		int pos=0;
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
		for(i=0;i<len;i++)
		{
			System.out.print(sort_I[1][i]+" = "+sort_I[0][i]+",\n");
		}
		System.out.print("\n\n");
		for(i=0;i<len;i++)
		{
			System.out.print(sort_O[1][i]+" = "+sort_O[0][i]+",\n");
		}
	
		
		System.out.print("plz");
	}
}
