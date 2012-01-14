package editor.editors.propertyOutline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.Position;

import edu.cmu.cs.sasylf.ast.NonTerminal;

public class PropertyElement {
	private String category;
	private String content;
	private PropertyElement parentElement;
	private List<PropertyElement> children;
	private int start;
	private int end;
	private Position position;
	
	public PropertyElement(String category, String content) {
		this.category = category;
		this.content = content;
	}
	
	

	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void addChild(PropertyElement element) {
		if(this.children == null) {
			this.children = new ArrayList<PropertyElement>();
		}
		this.children.add(element);
	}
	
	public List<PropertyElement> getChildren() {
		return children;
	}
	
	public void setChildren(List<PropertyElement> children) {
		this.children = children;
	}

	public PropertyElement getParentElement() {
		return parentElement;
	}

	public void setParentElement(PropertyElement parentElement) {
		this.parentElement = parentElement;
	}	
	
	public boolean hasChildren() {
		if(this.children == null || this.children.size() == 0) {
			return false;
		}
		return true;
	}

	public String toString() {
		return this.category + " " + this.content;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}
	
}