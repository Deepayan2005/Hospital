import org.json.JSONObject;

import java.io.*;
import java.util.Base64;

public class Main{
    public static void main(String[] args) throws Exception {

        Hospital hospital = new Hospital();
        //hospital.getDoctorsList();

        /*JSONObject jsonObject = new JSONObject();
        jsonObject.put("deptcode", "225");
        jsonObject.put("deptunitcode", "22511");
        jsonObject.put("hospitalcode", "20133");
        jsonObject.put("patcrno", "201332200035071");
        jsonObject.put("latitude", "");
        jsonObject.put("longitude", "");
        jsonObject.put("token", "");
        jsonObject.put("iskiosk", "0");

        hospital.bookAppointment(jsonObject);*/

        //hospital.getHospitalList();

        hospital.getPatientVisitData("201332200002912");

        hospital.getPatientPrescription("20133",
                "201332200002912","19111001", "7","2026-06-23 10:31:18.0");


    }
}

