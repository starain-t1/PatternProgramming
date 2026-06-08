package loginSection.login;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class GLocalAuthManager implements GAuthManager {

    // attributes
    private static final String FILE_PATH = "data/users.txt";
    private static final int    iName     = 0;
    private static final int    iPw       = 1;
    private static final int    iImg      = 2;
    private static final int    iAge      = 3;
    private static final int    iGender   = 4;
    private static final int    iWork     = 5;

    private static GLocalAuthManager instance;
    private String        currentEmail;
    private LocalDateTime loginTime;

    // associations
    private final Map<String, String[]> users = new HashMap<>();

    // constructors
    private GLocalAuthManager() { this.loadUsers(); }

    public static GLocalAuthManager getInstance() {
        if (instance == null) instance = new GLocalAuthManager();
        return instance;
    }

    // methods — 인증
    @Override
    public boolean login(String email, String password) {
        String[] info = this.users.get(email);
        if (info == null || !info[iPw].equals(password)) return false;
        this.currentEmail = email;
        this.loginTime    = LocalDateTime.now();
        return true;
    }

    @Override
    public boolean signup(String name, String email, String password) {
        if (this.users.containsKey(email)) return false;
        this.users.put(email, new String[]{name, password, "none", "", "", "0"});
        this.saveUsers();
        return true;
    }

    // methods — 프로필 조회
    @Override public String getCurrentEmail() { return this.currentEmail; }
    @Override public String getCurrentName()  { return this.get(iName); }
    @Override public String getImagePath()    { return this.get(iImg); }
    public String        getAge()             { return this.get(iAge); }
    public String        getGender()          { return this.get(iGender); }
    public LocalDateTime getLoginTime()       { return this.loginTime; }
    public int getWorkCount() {
        try { return Integer.parseInt(this.get(iWork)); } catch (Exception e) { return 0; }
    }

    // methods — 프로필 수정
    @Override public void updateName(String name)      { this.set(iName,  name); }
    @Override public void updateImagePath(String p)    { this.set(iImg,   p == null ? "none" : p); }
    public    void        updateAge(String age)        { this.set(iAge,   age); }
    public    void        updateGender(String g)       { this.set(iGender, g); }
    public    void        incrementWorkCount()         { this.set(iWork,  String.valueOf(this.getWorkCount() + 1)); }

    @Override
    public boolean updatePassword(String oldPw, String newPw) {
        if (this.currentEmail == null) return false;
        String[] info = this.users.get(this.currentEmail);
        if (!info[iPw].equals(oldPw)) return false;
        info[iPw] = newPw;
        this.saveUsers();
        return true;
    }

    // private helpers
    private String get(int idx) {
        if (this.currentEmail == null) return "";
        String[] arr = this.users.get(this.currentEmail);
        return (arr != null && arr.length > idx) ? arr[idx] : "";
    }

    private void set(int idx, String val) {
        if (this.currentEmail == null) return;
        this.users.get(this.currentEmail)[idx] = val;
        this.saveUsers();
    }

    private void loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] p   = line.split("\t");
                if (p.length < 3) continue;
                String[] row = new String[6];
                for (int i = 0; i < 6; i++) row[i] = i < p.length ? p[i] : (i == iWork ? "0" : "");
                if (row[iImg].isEmpty()) row[iImg] = "none";
                this.users.put(p[0], row);
            }
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }

    private void saveUsers() {
        new File("data").mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Map.Entry<String, String[]> entry : this.users.entrySet()) {
                String[] r = entry.getValue();
                bw.write(entry.getKey()+"\t"+r[iName]+"\t"+r[iPw]+"\t"+r[iImg]+"\t"+r[iAge]+"\t"+r[iGender]+"\t"+r[iWork]);
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}