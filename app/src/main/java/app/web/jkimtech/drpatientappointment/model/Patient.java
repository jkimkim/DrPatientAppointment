package app.web.jkimtech.drpatientappointment.model;

public class Patient {
    private String name;
    private String adresse;
    private String tel;
    private String email;
    private String dateOfBirth;
    private String familySituation;


    public Patient(){
        //needed for firebase
    }

    public Patient(String name, String address, String tel, String email, String dateOfBirth, String familySituation) {
        this.name = name;
        this.adresse = address;
        this.tel = tel;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.familySituation = familySituation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFamilySituation() {
        return familySituation;
    }

    public void setFamilySituation(String familySituation) {
        this.familySituation = familySituation;
    }
}
