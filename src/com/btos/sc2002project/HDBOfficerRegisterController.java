
public class HDBOfficerRegisterController {
    private List<HDBOfficer> officers;

    public HDBOfficerRegisterController() {
        this.officers = new ArrayList<>();
    }

    public void registerOfficer(HDBOfficer officer) {
        officers.add(officer);
        System.out.println("Officer registered: " + officer.getName());
    }
}
