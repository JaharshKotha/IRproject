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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

public class jranker {
	public HashMap<String, Double> idf = new HashMap<String, Double>();
//HashMap for storing IDF 
	public void compute_idf() {
		try {
			IndexReader r = IndexReader.open(FSDirectory
					.open(new File("index")));
			TermEnum t = r.terms();
			int N = r.maxDoc();

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
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
//Returns the idf value for a term ; only used for debugging purpose
	public void get_idf_val(String s) {
		double x = 0.0;
		x = idf.get(s);
		System.out.println("This is the idf of " + x);

	}

	public HashMap<Integer, HashMap<String, Double>> docs;

	// One time run for building my hash map in pre-computing the document
	// length of each document and storing it separately
	public void build_doc_vector() {
		docs = new HashMap<Integer, HashMap<String, Double>>();

		try {
			IndexReader reader = IndexReader.open(FSDirectory.open(new File(
					"index")));
			TermEnum termenum = reader.terms();
			// Iterate through all the terms
			while (termenum.next()) {
				if (termenum.term().text().matches("^[\\p{ASCII}]*$")
						&& termenum.term().field().equals("contents")) {

					Term termval = termenum.term();
					TermDocs termdocs = reader.termDocs(termval);

					int i = 0;
					while (termdocs.next() && i < 600) {
						i++;

						HashMap<String, Double> terms;
						if (docs.containsKey(termdocs.doc()))
							terms = docs.get(termdocs.doc());
						else
							terms = new HashMap<String, Double>();

						terms.put(termval.text(), (double) termdocs.freq());
						docs.put(termdocs.doc(), terms);
					}

				} else {
					continue;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//Returns the Document term frequency value for a term ; only used for debugging purpose
	public void getval(int i) {
		HashMap<String, Double> get_terms;
		get_terms = docs.get(i);
		if (get_terms != null) {
			for (String key : get_terms.keySet()) {

				System.out.println(key + "  " + get_terms.get(key));
			}
		}

	}
	//Computes the L2 norm of the documents

	public HashMap<Integer, Double> doc_len_map = new HashMap<Integer, Double>();

	public void doclen() {
		HashMap<String, Double> doc_len;
		double x, y = 0.0, z;
		try {

			for (Map.Entry<Integer, HashMap<String, Double>> entry : docs
					.entrySet()) {
				doc_len = entry.getValue();
				y = 0.0;
				for (Map.Entry<String, Double> j : doc_len.entrySet()) {
					x = j.getValue();
					y = y + (x * x);

				}
				z = Math.sqrt(y);
				doc_len_map.put(entry.getKey(), z);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public HashMap<String, Double> q = new HashMap<String, Double>();
//Builds the query vector
	public void build_query(String s) {
		double freq = 0.0, x = 0.0, y;
		String[] terms = s.split("\\s+");
		for (String word : terms) {
			freq = 1;
			if (q.containsKey(word)) {
				freq = q.get(word);
				  ++freq;
			}
			
			q.put(word, freq);

		}
		for (Map.Entry<String, Double> entry : q.entrySet()) {
			x = x + entry.getValue();
			
		}
		y = Math.sqrt(x * x);
		q.put("modq", y);
		
	}
//Computes the dot product 
	public double dot_prod(int id) {
		double ans = 0.0, x, modq, moddoc, y = 0.0;
		HashMap<String, Double> doc_terms;
		doc_terms = docs.get(id);

		try {
			
			for (Map.Entry<String, Double> entry : q.entrySet()) {

				if (doc_terms.containsKey(entry.getKey())) {
					
					x = doc_terms.get(entry.getKey()) * entry.getValue();
					y = y + x;
				}
			}
           
			modq = q.get("modq");
			moddoc = doc_len_map.get(id);
			
			ans = (y / (modq * moddoc));
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ans;

	}
	//Computes Dot product with IDF
	public double dot_prod_with_idf(int id) {
		double ans = 0.0, x, modq, moddoc, y = 0.0;
		HashMap<String, Double> doc_terms;
		doc_terms = docs.get(id);

		try {
			

			for (Map.Entry<String, Double> entry : q.entrySet()) {

				if (doc_terms.containsKey(entry.getKey())) {
					
					x = doc_terms.get(entry.getKey()) * entry.getValue() * idf.get(entry.getKey());
					y = y + x;
				}
			}
           
			modq = q.get("modq");
			moddoc = doc_len_map.get(id);
			
			ans = (y / (modq * moddoc));
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ans;

	}

	public static void main(String[] args) {

		try {
			System.out.println("Building Indexes and norms:");
			long start = System.currentTimeMillis();;
			
			double res = 0.0,res_idf=0.0;
			IndexReader r = IndexReader.open(FSDirectory
					.open(new File("index")));
			jranker j = new jranker();
			j.build_doc_vector();
			
			j.doclen();

			j.compute_idf();
			long end = System.currentTimeMillis();;
			
			
			System.out.println("Time taken to build indexes :" + (end-start) + "ms");
			//Maps to store values from the haspmap answer and sort efficiently using inbuilt container methods.
			Map<Double, Double> answers = new HashMap<Double, Double>();
			Map<Double, Double> answers_idf = new HashMap<Double, Double>();
			int[] d = new int[26000];
			int size = 0, m = 0, n = 0, flag = 0,cnt=0;
			Scanner sc = new Scanner(System.in);
			String str = "";
			System.out.print("query> ");
			while (!(str = sc.nextLine()).equals("quit")) {
				
				size = 0;
				
				j.build_query(str);
				
				String[] terms = str.split("\\s+");
				for (String word : terms) {
					Term term = new Term("contents", word);
					TermDocs tocs = r.termDocs(term);
					while (tocs.next()) {
						for (n = 0; n <= size; n++) {
							if (d[n] == tocs.doc()) {
								flag = 1;
							}
						}
						if (flag == 0) {
							d[size] = tocs.doc();
							++size;
						} else {
							flag = 0;
						}
					}
				}
				long s1 = System.currentTimeMillis();;
				
				for (m = 0; m < size; m++) {
					try{
					res = j.dot_prod(d[m]);
					answers.put((double) d[m], res);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				long s2 = System.currentTimeMillis();;
				
				System.out.println("Time taken for TF :" + "(start)"+s1+"   "+"(end)"+s2 + "ms (subject to change everytime)");
				
				for (m = 0; m < size; m++) {
					res_idf=j.dot_prod_with_idf(d[m]);
					answers_idf.put((double) d[m],res_idf);
				}
				
				long s3 = System.currentTimeMillis();;
				System.out.println("Time taken for IDF :" + "(start)"+s2+"   "+"(end)"+s3 + "ms (subject to change everytime)");
				System.out.println("\nTF ranking:");
				Set<Entry<Double, Double>> set = answers.entrySet();

				List<Entry<Double, Double>> list = new ArrayList<Entry<Double, Double>>(
						set);

				Collections.sort(list,new Comparator<Map.Entry<Double, Double>>() {
							public int compare(Map.Entry<Double, Double> o1,
									Map.Entry<Double, Double> o2) {
								return (o2.getValue()).compareTo(o1.getValue());
							}
						});
				for (Map.Entry<Double, Double> entry : list) {
					if (entry != null && entry.getKey() != null && entry.getValue() !=0) 
					{
						System.out.println(entry.getKey());
					}

				}
				
				Set<Entry<Double, Double>> set_idf = answers_idf.entrySet();

				List<Entry<Double, Double>> list_idf = new ArrayList<Entry<Double, Double>>(set_idf);

				Collections.sort(list_idf,new Comparator<Map.Entry<Double, Double>>() {
							public int compare(Map.Entry<Double, Double> o3,
									Map.Entry<Double, Double> o4) {
								return (o4.getValue()).compareTo(o3.getValue());
							}
						});
				System.out.println("\n\n\nWith IDF results:\n\n");
				for (Map.Entry<Double, Double> ent : list_idf) {
					if (ent != null && ent.getKey() != null && ent.getValue() !=0) 
					{
						System.out.println(ent.getKey());
						++cnt;
					}

				}
				long s4 = System.currentTimeMillis();;
				System.out.println("Time taken to fetch ,sort and display :" + (s4-s3) + "ms (subject to change everytime)" +cnt);
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}