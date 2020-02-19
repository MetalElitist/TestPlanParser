package testPlanPackage;

public class httpSampler {
	public String bodyData;
	public String name;
	public int id;
	
	public httpSampler(String name, int id, String bodyData) {
		this.name = name;
		this.bodyData = bodyData;
		this.id = id;
	}
}
