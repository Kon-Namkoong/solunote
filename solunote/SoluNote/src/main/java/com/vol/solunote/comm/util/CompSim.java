package com.vol.solunote.comm.util;

public class CompSim {
	
	private int n_matched = 0 ;
	private int n_length = 0 ;
	

	public void compareSimplyWord(String _comp_a, String _comp_b) {
		
		///////////////////////////////////////////////////////////////////////

		boolean b_next = true ;
		String s_comp_a = _comp_a ;
		String s_comp_b = _comp_b ;
		
		///////////////////////////////////////////////////////////////////////

//		System.out.println("특수문자 제거... ");
		if (b_next) {
			if (s_comp_a.length() <= 0) {
				b_next = false ;
			} else {
				b_next = true ;
				s_comp_a = s_comp_a.replaceAll("_", "") ;
				s_comp_a = s_comp_a.replaceAll(" ", "") ;
				s_comp_a = s_comp_a.trim() ;
			}
			if (s_comp_b.length() <= 0) {
				b_next = false ;
			} else {
				b_next = true ;
				s_comp_b = s_comp_b.replaceAll("_", "") ;
				s_comp_b = s_comp_b.replaceAll(" ", "") ;
				s_comp_b = s_comp_b.trim() ;
			}
		}
		
		///////////////////////////////////////////////////////////////////////
		
		int n_comp_a = 0 ;
		int n_comp_b = 0 ;
		String [] p_comp_a = null ;
		String [] p_comp_b = null ;
		if (b_next) {
			String [] p_temp_a = s_comp_a.split("") ;
			String [] p_temp_b = s_comp_b.split("") ;
			p_comp_a = new String[p_temp_a.length] ;
			p_comp_b = new String[p_temp_b.length] ;
			// System.out.println("String A Array ("+p_comp_a.length+")") ;
			for (int i=0; i<p_temp_a.length; i++) {
//				System.out.println("[" + total_cnt + "][String STT Array] i = " + i + " / " + p_temp_a.length) ;
				if (p_temp_a[i].trim().length() > 0) {
					p_comp_a[n_comp_a] = p_temp_a[i] ;
					n_comp_a++ ;
				}
			}
			for (int i=0; i<p_temp_b.length; i++) {
//				System.out.println("[" + total_cnt + "][String ANS Array] i = " + i + " / " + p_temp_b.length) ;
				if (p_temp_b[i].trim().length() > 0) {
					p_comp_b[n_comp_b] = p_temp_b[i] ;
					n_comp_b++ ;
				}
			}
			b_next = (n_comp_a>0 && n_comp_b>0) ? true : false ;
		}
		
		///////////////////////////////////////////////////////////////////////
		
		int [][] p_match = null ; 
		if (b_next) {
			p_match = new int[n_comp_a][n_comp_b] ;
			for (int a=0; a<n_comp_a; a++) {
				for (int b=0; b<n_comp_b; b++) {
//					System.out.println("[" + total_cnt + "][Match Initailization] a = " + a + " / " + n_comp_a + "  b = " + b + " / " + n_comp_b) ;
					p_match[a][b] = 0 ;
				}
			}
			b_next = (p_match.length > 0) ? true : false ;
		}
		
		///////////////////////////////////////////////////////////////////////
		
		if (b_next) {
			String char_a = "" ;
			String char_b = "" ;
			for (int a=0; a<n_comp_a; a++) {
				char_a = p_comp_a[a] ;
				for (int b=0; b<n_comp_b; b++) {
//					System.out.println("[" + total_cnt + "][Match Compare] a = " + a + " / " + n_comp_a + "  b = " + b + " / " + n_comp_b) ;
					char_b = p_comp_b[b] ;
					if (char_a.equals(char_b)) {
						p_match[a][b] = 1 ;
					}
				}
			}
		}
		
		///////////////////////////////////////////////////////////////////////

		if (b_next) {
			for (int a=0; (a+1)<n_comp_a; a++) {
				for (int b=0; (b+1)<n_comp_b; b++) {
//					System.out.println("[" + total_cnt + "][Match Adjust] a = " + a + " / " + n_comp_a + "  b = " + b + " / " + n_comp_b) ;
					if (p_match[a][b] == 1) {
						int start_x = a ;
						int start_y = b ;
						int pos_x = a+1 ;
						int pos_y = b+1 ;
						if (start_x < n_comp_a && start_y < n_comp_b) {
							while (p_match[pos_x][pos_y] == 1) {
								pos_x++ ;
								pos_y++ ;
								if (!(pos_x < n_comp_a && pos_y < n_comp_b)) {
									break ;
								}
							}
						}
						int cnt = pos_x - start_x ;
						for (int i=0; i<cnt; i++) {
							p_match[start_x][start_y] = cnt ;
							start_x++ ;
							start_y++ ;							
						}
					}
				}
			}
		}
		
		///////////////////////////////////////////////////////////////////////
		
		if (b_next) {
			// Horizontal Filter
			for (int a=0; a<n_comp_a; a++) {
				int n_max = -1 ;
				int n_pos = -1 ;
				for (int b=0; b<n_comp_b; b++) {
//					System.out.println("[" + total_cnt + "][Horizontal Filter] a = " + a + " / " + n_comp_a + "  b = " + b + " / " + n_comp_b) ;
					if (p_match[a][b] > 0) {
						if (p_match[a][b] > n_max) {
							n_max = p_match[a][b] ;
							n_pos = b ;
						}
						p_match[a][b] = 0 ;
					}
				}
				if (n_pos >= 0) {
					p_match[a][n_pos] = n_max ;
					// Vertical Filter
					int y = n_pos ;
					n_max = -1 ;
					n_pos = -1 ;
					for (int b=0; b<n_comp_a; b++) {
//						System.out.println("[" + total_cnt + "][Vertical Filter] a = " + a + " / " + n_comp_a + "  b = " + b + " / " + n_comp_b) ;
						if (p_match[b][y] > 0) {
							if (p_match[b][y] > n_max) {
								n_max = p_match[b][y] ;
								n_pos = b ;
							}
							p_match[b][y] = 0 ;
						}
					}
					if (n_pos >= 0) {
						p_match[n_pos][y] = n_max ;
					}
				}
			}
		}
		
		///////////////////////////////////////////////////////////////////////

		int n_match = 0 ;
		if (b_next) {
			for (int a=0; a<n_comp_a; a++) {
				for (int b=0; b<n_comp_b; b++) {
//					System.out.println("[" + total_cnt + "][Count Match] a = " + a + " / " + n_comp_a + "  b = " + b + " / " + n_comp_b) ;
					if (p_match[a][b] > 0) {
						n_match++ ;
					}
				}
			}
		}
		
		///////////////////////////////////////////////////////////////////////
		
		this.n_length = n_comp_b ;
		this.n_matched = n_match ;
	}
	
	public double getRation() {
		return this.n_matched * 100.0 / this.n_length;
	}
}