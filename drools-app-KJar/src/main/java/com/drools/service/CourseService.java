package com.drools.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drools.facts.Course;
import com.drools.facts.CourseList;
import com.drools.facts.Subject;

@Service
public class CourseService {

	private KieContainer kieContainer;

	@Autowired
	public CourseService(KieContainer kieContainer) {
		this.kieContainer = kieContainer;
	}

	public List<Course> getSuggestedCourse(List<Subject> subjects) {
		final CourseList courses = new CourseList();
    	StatelessKieSession statelessKieSession = kieContainer.newStatelessKieSession();
    	statelessKieSession.setGlobal("courses", courses);
    	statelessKieSession.execute(subjects);

        return courses.getItems();
	}

	private static final String URL = "http://localhost:8082/kie-server/services/rest/server";
	private static final String USER = "kieserver";
	private static final String PASSWORD = "kieserver";
	private static final String ContainJar = "drools-demo-facts-KJar";
	 

	public List<Course> getSuggestedCourseByKieServer(List<Subject> subjects) {
		final CourseList courses = new CourseList();
	
		KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(URL, USER, PASSWORD);
		config.setMarshallingFormat(MarshallingFormat.JSON);
		config.setTimeout(30000L);
	
		KieServicesClient client = KieServicesFactory.newKieServicesClient(config);
		RuleServicesClient rules = client.getServicesClient(RuleServicesClient.class);
		
		KieCommands cmdFactory = KieServices.Factory.get().getCommands();

		List<Command<?>> commands = new LinkedList<Command<?>>();
		//cmdFactory.newg
		
		commands.add(cmdFactory.newSetGlobal("courses", courses,true));
		for (Subject subject:subjects) {
		commands.add(cmdFactory.newInsert(subject));
		}
		
		commands.add(cmdFactory.newFireAllRules());
		ServiceResponse<org.kie.api.runtime.ExecutionResults> response = rules.executeCommandsWithResults(ContainJar,
				cmdFactory.newBatchExecution(commands, "defaultStatelessKieSession"));

		System.out.println(response.getMsg());
		ExecutionResults result = response.getResult();
		CourseList courseList = (CourseList)result.getValue("courses");
		return courseList.getItems();
	}

	private static Object makeApplicant(FactType factType) throws Exception {
		Object subject = factType.newInstance();
		factType.set(subject, "name", "张三");
		factType.set(subject, "age", 17);
		return subject;
	}

	protected static FactType factType(KieBase base) {
		FactType factType = base.getFactType("com.zhangmen.dbrule", "Subject");
		return factType;
	}
	private  int a = 10;
	
	public static void main(String[] args) {
		CourseService service = new CourseService(null);
		List<Subject> subjects = new ArrayList();
		Subject sub = new Subject();
		sub.setSubject("math");
		sub.setRating(15);
		Subject sub1 = new Subject();
		sub1.setSubject("physics");
		sub1.setRating(15);
		subjects.add(sub);
		subjects.add(sub1);
		service.getSuggestedCourseByKieServer(subjects);
	}

}
