package package1;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;

public class Supervisor extends User {
    int numberOfProjectManaged = 0;
    List<Project> projectList;
    List<Request> pendingRequest;
    private static List<String> availableRequests = new ArrayList<>(Arrays.asList("1. Change Supervisor")); 
    private SupervisorRequest requestType;
    static List<Supervisor> allSupervisor;
    List<Student> studentManaged;
    Scanner sc = new Scanner(System.in);


    public Supervisor(String userID, String password, String name, String email){
        super(userID, password, name, email);
    }

    public int createProject(Supervisor supervisor, String projectTitle){
        Project newProject = new Project(this, projectTitle);
        projectList.add(newProject);
        if(Project.addToProjectList(newProject) == 0) return 0;
        return 1;
    }

    public void displayProjects(){
        for (Project project: projectList){
            project.displayProject();
        }
    }

    @Override
    public int chooseAndSetRequest(int requestID) {

        try {

            switch (requestID) {
                case 1:
                    this.requestType = new RequestChangeSupervisor(null,null,null,null);
                    break;
                
                default:
                    System.out.println("Error: Invalid request ID");
                    return 0;
            }

        } catch (Exception e) {
            System.out.println("Error: Invalid request ID");
            return 0;
        }

        return 1;
    }

    public int modifyProjectTitle(int projectID, String newTitle){
        Project project = Project.getProjectByID(projectID);
        if(doesProjectBelongToSupervisor(project)){
            project.changeProjectTitle(newTitle);
            return 1;
        }
        return 0;
    }

    public boolean capReached() {
        if (numberOfProjectManaged >= 2) {
            return true;
        } else
            return false;
    }

    public static void displayAvailableRequests() {
        for (String request : availableRequests) {
            System.out.println(request);
        }
    }

    public static int addAvailableRequest(String request) {
        if(availableRequests.contains(request)||request==null) return 0;
        availableRequests.add(request);
        return 1;
    }

    public void viewAllPendingRequest(){
        if(pendingRequest.size() == 0){
            System.out.println("There are no pending Request");
        }
        DisplayRequest.displayPendingRequest(pendingRequest);
    }

    public int makeRequest(String recipientID, int projectID, String replacementSupervisorID) {
        Project project = selectProject(projectID);
        Supervisor replacementSupervisor = selectSupervisor(replacementSupervisorID);
        FYPCoordinator FYPCoor = selectRecipient(recipientID);
        if (project == null||FYPCoor==null||replacementSupervisor==null)
            return 0;
        if(requestType!=null){
            requestType.create(this, FYPCoor, project, replacementSupervisor); // create request
            if(requestType.sendRequest()==1){
                return 1; // request sent
            }
            else{
                return 0; // request not sent
            }
        }
        

        return 1;
    }

    public FYPCoordinator selectRecipient(String recipientID){
        return FYPCoordinator.getCoordinatorByID(recipientID);
    }

    public Supervisor selectSupervisor(String replacementSupervisorID){
        return Supervisor.getSupervisorByID(replacementSupervisorID);
    }

    public void displayAllSupervisor(){
        for (Supervisor supervisor: allSupervisor){
            String name = supervisor.getUserName();
            String UserID = supervisor.getUserID();
            String userEmail = supervisor.getEmail();
            System.out.println("Name: " + name + ", UserID: " + UserID + "Email: " + userEmail + "\n");
    }
}


    public int makeAllProjectsAvailable(){
        return Project.massModifyProjectStatus(this.getUserID(), ProjectStatus.AVAILABLE);
    }

    public int makeAllProjectsUnavailable(){
        return Project.massModifyProjectStatus(this.getUserID(), ProjectStatus.UNAVAILABLE);
    }

    public List<Student> getStudentsManaged(){
        List<Student> arr = new ArrayList<>();
        for(Project project: projectList){
            if(project.getProjectStatus() == ProjectStatus.ALLOCATED){
                arr.add(project.getStudent());
            }
        }
        return arr;
    }


    public static Supervisor getSupervisorByName(String name){
        for(Supervisor supervisor: allSupervisor){
            if(supervisor.getUserName().equals(name)){
                return supervisor;
            }
        }
        return null;
    }


    public static Supervisor getSupervisorByID(String UserID){
        for(Supervisor supervisor: allSupervisor){
            if(supervisor.getUserID() == UserID){
                return supervisor;
            }
        }
        return null;
    }


    public static int addInitialSupervisor(List<Supervisor> supervisorLists){
        if(supervisorLists == null) return 0;
        for(Supervisor supervisor : supervisorLists) {
            allSupervisor.add(supervisor);
        }
        return 1;
    }

    public static int addToSupervisorList(Supervisor supervisor) {
        if (supervisor == null)
            return 0;
        allSupervisor.add(supervisor);
        return 1;
    }

    public static int removeFromSupervisorList(Supervisor supervisor){
        boolean found = false;
        for(Supervisor supervisors: allSupervisor){
            if(supervisor.equals(supervisors)){
                found = true;
                break;
            }
        }
        if(!found) return 0;
        allSupervisor.remove(supervisor);
        return 1;
    }

    private Project selectProject(int projectID) {
        return Project.getProjectByID(projectID);
    }

    public int addPendingRequest(Request request) {
        if (request == null)
            return 0;
        pendingRequest.add(request);
        return 1;
    }

    public boolean doesProjectBelongToSupervisor(Project project){
        for(Project projects: projectList){
            if(project.equals(projects)){

                return true;
            }
        }
        return false;
    }

    public int addStudentManaged(Student student) {
        if (capReached())
            return 0;
        numberOfProjectManaged++;
        studentManaged.add(student);
        return 1;
    }

    @Override
    public boolean isSupervisor() {
        return true;
    }

    public int removeStudentManaged(Student student) {
        if (numberOfProjectManaged == 0)
            return 0;
        numberOfProjectManaged--;
        if(studentManaged.remove(student)) return 1;
        else return 0;
    }
   
    public int removePendingRequest(Request request){
        for(Request requests: pendingRequest){
            if(request.equals(requests)){
                pendingRequest.remove(request);
                return 1;
            }
        }
        return 0;
    }

    public int addProject(Project project){
        if(project == null) return 0;
        createProject(project.getSupervisor(), project.getProjectTitle());
        return 1;
    }

    public int removeProject(Project project){
        for(Project projects: projectList){
            if(project.equals(projects)){
                projectList.remove(projects);
                Project.removeFromProjectList(projects);
                return 1;
            }
        }
        return 0;
    }

}