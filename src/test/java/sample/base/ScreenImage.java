package sample.base;

public class ScreenImage {
	private int id;
	private String path;
	private String name;
	private String link;
	private String testSuite;
	private String testClass;
	private String testMethod;
	
	public ScreenImage(int id, String path, String name, String link, String testSuite, String testClass, String testMethod){
		this.id = id;
		this.path = path;
		this.name = name;
		this.link = link;
		this.testSuite = testSuite;
		this.testClass = testClass;
		this.testMethod = testMethod;
	}
	
	public int getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public String getLink() {
		return link;
	}

	public String getTestSuite() {
		return testSuite;
	}

	public String getTestClass() {
		return testClass;
	}

	public String getTestMethod() {
		return testMethod;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setTestSuite(String testSuite) {
		this.testSuite = testSuite;
	}

	public void setTestClass(String testClass) {
		this.testClass = testClass;
	}

	public void setTestMethod(String testMethod) {
		this.testMethod = testMethod;
	}
	
}
