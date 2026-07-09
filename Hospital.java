import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Hospital {

    private static final String SERVER_TIME_URL =
            "https://hmis.rcil.gov.in/HISServices/service/railtelService/server-time";

    private static final String QR_STAMPING_URL =
            "https://hmis.rcil.gov.in/HISServices/service/mobile-service/callStampingService";

    // HMAC secret
    private static final String HMAC_SECRET =
            "iUreG2oFXJfB+4ufJ02yzD6Pt3DG16idwo2wWrqUptujbrXHaKtdxzO9KHuHbMFv9vCAm4nNi7ZJX6svWxwlVQ==";

    // AES Key
    private static final String AES_KEY = "x7v!A%C*F-JaNdRgUjXn2r5u8x/A?D(G";

    // AES IV
    private static final String AES_IV = "6v9y$B&E)H@McQfT";

    public void getPatientPrescription(String hospitalCode, String crno,String episodeCode,
                                       String visitNo, String entryDate) throws Exception {
        String url = "https://hmis.rcil.gov.in/HISDRDESK/services/restful/mobile-service/digi?" +
                "hosp_code=" + URLEncoder.encode(encrypt(hospitalCode), StandardCharsets.UTF_8) +
                "&Modval=" + URLEncoder.encode(encrypt("5"), StandardCharsets.UTF_8) +
                "&CrNo=" +URLEncoder.encode(encrypt(crno), StandardCharsets.UTF_8)+
                "&episodeCode=" +URLEncoder.encode(encrypt(episodeCode), StandardCharsets.UTF_8)+
                "&visitNo=" +URLEncoder.encode(encrypt(visitNo), StandardCharsets.UTF_8)+
                "&seatId=" + URLEncoder.encode(encrypt("0"), StandardCharsets.UTF_8)+
                "&Entrydate=" + URLEncoder.encode(encrypt(entryDate), StandardCharsets.UTF_8);

        String serverTime = getServerTime();
        String bearer = createBearer(serverTime);

        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + bearer);
        con.setRequestProperty("X-App-Encrypted", "true");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null)
            sb.append(line);

        br.close();

        FileOutputStream fileOutputStream = new FileOutputStream("pres.pdf");
        fileOutputStream.write(Base64.getDecoder().decode(sb.toString()));
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public void getPatientVisitData(String patientCRno) throws Exception {
        String serverTime = getServerTime();
        String bearer = createBearer(serverTime);

        String url = "https://hmis.rcil.gov.in/HISServices/service/mobile-service/prescriptionList?"+
                "crno="+URLEncoder.encode(encrypt(patientCRno), StandardCharsets.UTF_8)+
                "&hosCode="+URLEncoder.encode(encrypt("0"), StandardCharsets.UTF_8);
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + bearer);
        con.setRequestProperty("X-App-Encrypted", "true");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null)
            sb.append(line);

        br.close();
        System.out.println(sb);
    }

    public void getHospitalList() throws Exception {
        String serverTime = getServerTime();
        String bearer = createBearer(serverTime);

        HttpURLConnection con = (HttpURLConnection) new URL(
                "https://hmis.rcil.gov.in/HISServices/service/mobile-service/getHospitalList?zoneId=" +
                        URLEncoder.encode(encrypt("0"), StandardCharsets.UTF_8) +
                        "&divisionId="
                        +URLEncoder.encode(encrypt("0"), StandardCharsets.UTF_8)).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + bearer);
        con.setRequestProperty("X-App-Encrypted", "true");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null)
            sb.append(line);

        br.close();
        System.out.println(sb);
    }

    public void getDoctorsList() throws Exception{
        String serverTime = getServerTime();
        String bearer = createBearer(serverTime);

        HttpURLConnection con = (HttpURLConnection) new URL(
                "https://hmis.rcil.gov.in/HISServices/service/mobile-service/consultantByDept?deptCode="+
                        URLEncoder.encode(encrypt("0"), StandardCharsets.UTF_8)+"&hospCode="
                        +URLEncoder.encode(encrypt("20133"), StandardCharsets.UTF_8)).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + bearer);
        con.setRequestProperty("X-App-Encrypted", "true");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null)
            sb.append(line);

        br.close();
        System.out.println(sb);

    }

    public void bookAppointment(JSONObject appointmentObject) throws Exception {

        String bearer = createBearer(getServerTime());
        URL url = new URL(QR_STAMPING_URL);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Authorization", "Bearer " + bearer);
        con.setRequestProperty("X-App-Encrypted", "true");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        String body = "jsonData=" + URLEncoder.encode(encrypt(appointmentObject.toString()), 
                StandardCharsets.UTF_8);

        OutputStream os = con.getOutputStream();
        os.write(body.getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null)
            sb.append(line);

        br.close();
        System.out.println(sb);
    }

     private String getServerTime() throws Exception {

        URL url = new URL(SERVER_TIME_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
            sb.append(line);

        br.close();
        return sb.toString().trim();
    }

     private String createBearer(String serverTime) throws Exception {
        SecretKeySpec key = new SecretKeySpec(HMAC_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);
        byte[] hash = mac.doFinal(serverTime.getBytes(StandardCharsets.UTF_8));
        return "RAIL." + serverTime + "." + Base64.getEncoder().encodeToString(hash);
    }
    
     private String encrypt(String plainText) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(AES_IV.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec key = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }
}
