package com.vol.solunote.comm.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.StringsComparator;

/*
 * 
	public static void main(String[] args) {
			HtmlVisitor htmlVisitor = new HtmlVisitor();
			StringsComparator comparator = new StringsComparator(((String)map.get("trainText")).trim(), ((String)map.get("sttText")).trim());
			comparator.getScript().visit(htmlVisitor);
			
			map.put("trainText", htmlVisitor.leftString());
			map.put("sttText", htmlVisitor.rightString());
			
			
			
			
			HtmlVisitor htmlVisitor = new HtmlVisitor();
			htmlVisitor.diff((String)map.get("trainText"), (String)map.get("sttText"));

			map.put("trainText", htmlVisitor.getLeft());
			map.put("sttText", htmlVisitor.getRight());
	}
 * 
 */


public class HtmlVisitor implements CommandVisitor<Character> {
	
//	public static void main(String[] args) {
////		HtmlVisitor htmlVisitor = new HtmlVisitor();
////		StringsComparator comparator = new StringsComparator(((String)map.get("trainText")).trim(), ((String)map.get("sttText")).trim());
////		comparator.getScript().visit(htmlVisitor);
////		
////		map.put("trainText", htmlVisitor.leftString());
////		map.put("sttText", htmlVisitor.rightString());
////		
//		
//		
//		
//		HtmlVisitor htmlVisitor = new HtmlVisitor();
//		htmlVisitor.diff("자 일단은 기본적으로",
//						"일단은 기본적으로     "
//				);
//		System.out.println("[" + htmlVisitor.getLeft() + "]");
//		System.out.println("[" + htmlVisitor.getRight() + "]");
//	}
	
	
	private String startTag = "<em>";
	private String endTag = "</em>";

	private List<MetaChar> left = new ArrayList<>();
	private List<MetaChar> right = new ArrayList<>();


	public void diff(String lText, String rText) {
		StringsComparator comparator = new StringsComparator(lText.trim(), rText.trim());
		comparator.getScript().visit(this);		
	}
	
	public String getLeft() {
		return metaString(left);
	}
	
	public String getRight() {
		return metaString(right);
	}

	private String metaString(List<MetaChar> list) {
		
		StringBuilder buffer = new StringBuilder();
		
		boolean flag = false;
		boolean b = false;
		
		for( MetaChar meta : list ) {
			b = meta.isB();
			Character c = meta.getC();
			
			if ( flag == false && b == true ) {
				buffer.append(startTag);
				buffer.append(c);
			} else if ( flag == true && b == false ) {
				buffer.append(endTag);
				buffer.append(c);
			} else {
				buffer.append(c);
			}
			
			flag = b;				
		}
		
		if ( flag == true ) {
			buffer.append(endTag);
		}
		
		return buffer.toString();
	}

	
	@Override
	public void visitKeepCommand(Character c) {
		// Character is present in both files.
		left.add(new MetaChar(c));
		right.add(new MetaChar(c));
	}


	@Override
	public void visitInsertCommand(Character c) {
		/*
		 * Character is present in right file but not in left. Method name
		 * 'InsertCommand' means, c need to insert it into left to match right.
		 */
//		right = right + "(" + c + ")";
		
		right.add(new MetaChar(c, true));
	}

	@Override
	public void visitDeleteCommand(Character c) {
		/*
		 * Character is present in left file but not right. Method name 'DeleteCommand'
		 * means, c need to be deleted from left to match right.
		 */
//		left = left + "{" + c + "}";
		
		left.add(new MetaChar(c, true));
	}

}