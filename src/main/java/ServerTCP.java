import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ServerTCP {
//asdasdasd
    public final static int SERVER_PORT = 8000;

    public static void main(String[] args) throws IOException {

        //Tạo thread check tài khoản hết hạn autologin
        Thread checkAutoLoginExpired = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //Lấy uuid và datetime đã login từ DB
                    ArrayList<String> datetimeUUIDArrayList = new DBAccounts().getDatetimeAndUUID();
                    for (int i = 0; i < datetimeUUIDArrayList.size(); i = i + 2) {
                        String uuid = datetimeUUIDArrayList.get(i);
                        String datetime = datetimeUUIDArrayList.get(i+1);
                        //Tạo định dạng datetime
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        //Chuyển thành LocalDateTime
                        LocalDateTime localDatetimeUUID = LocalDateTime.parse(datetime,formatter);
                        //Lấy thời gian hiện tại
                        LocalDateTime datetimeNow = LocalDateTime.now();
                        //Tính khoảng thời gian từ giờ trừ cho lúc login
                        Duration duration = Duration.between(datetimeNow,localDatetimeUUID);
                        //Chuyển thời gian thành phút
                        long minutes = duration.toMinutes();
//                        System.out.println(minutes);
                        //Nếu thời gian đã hết 7 ngày (10080 phút), xoá tài khoản khỏi autologin
                        if(minutes <= -10080){
                            boolean b = new DBAccounts().deleteUUID(uuid);
                            if(b) {
                                System.out.println("UUID: " + uuid + " expired autologin");
                            }
                        }
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        checkAutoLoginExpired.setName("Thread check Autologin Expired");
        checkAutoLoginExpired.start();

        ServerSocket serverSocket = null;
        try {
            System.out.println("Binding to port " + SERVER_PORT + ", please wait  ...");
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server started: " + serverSocket);
            System.out.println("Waiting for a client ...");
            while (true) {
                try {
                    //Bắt đầu chấp nhận client kết nối
                    Socket socket = serverSocket.accept();
                    System.out.println("Client accepted: " + socket);

                    InputStream inputStream = socket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    String messFromClient = dataInputStream.readUTF();
                    System.out.println(messFromClient);
                    String messFromClientArray [] = messFromClient.split("#");

                    //Xử lý sự kiện register
                    if(messFromClientArray[0].equals("register")){
                        String jsonStrAccount = messFromClientArray[1];
                        ObjectMapper objectMapper = new ObjectMapper();
                        Account account = null;
                        try {
                            account = objectMapper.readValue(jsonStrAccount, Account.class);
                            System.out.println(account);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        OutputStream os = socket.getOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(os);
                        if(account.getAccountID().equals(new DBAccounts().getAccountID(account.getAccountID()))){
                            dataOutputStream.writeUTF("accountIDExistsed");
                            System.out.println("accountID Existsed");
                        }else {
                            boolean b = new DBAccounts().insertAccount(account);
                            if(b) {
                                dataOutputStream.writeUTF("RegisterSuccess");
                                System.out.println("Register Success");
                            }
                        }
                    }

                    //Xử lý sự kiện login
                    if(messFromClientArray[0].equals("login")){
                        String accountID = messFromClientArray[1];
                        String uuid = messFromClientArray[2];
                        //Lấy password Bcrypt từ Db gửi cho Client kiểm tra
                        String password = new DBAccounts().getPassword(accountID);
                        OutputStream os = socket.getOutputStream();
                        DataOutputStream dataStreamOut = new DataOutputStream(os);
                        dataStreamOut.writeUTF(password);

                        //Nhận tin đăng nhập thành công của Client
                        messFromClient = dataInputStream.readUTF();
                        System.out.println(messFromClient);

                        //Lưu lại accountID và UUID khi LoginSuccess
                        if(messFromClient.equals("LoginSuccess")){
                            String uuidDB = new DBAccounts().getUUID(uuid);
//                            String accountIDDB = new DBAccounts().getAccountID(accountID);
                            //Tìm UUID trong DB, nếu có rồi thì cập nhật thời gian login mới
                            if(uuidDB.equals(uuid)) {
                                //Xoá uuid cũ trong DB
                                new DBAccounts().deleteUUID(uuid);
                                //Lấy thời gian hiện tại
//                                LocalDateTime datetimeNow = LocalDateTime.now();
                                //Tạo định dạng datetime
//                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                //Chuyển datetimeNow thành String
//                                String datetime = datetimeNow.format(formatter);
                                //Cập nhật thời gian đăng nhập mới vào DB
//                                new DBAccounts().updateDatetimeUUID(accountID, uuid, datetime);
                                //Insert uuid và account mới
                                new DBAccounts().insertUUID(accountID, uuid);
                            }else{
                                new DBAccounts().insertUUID(accountID, uuid);
                            }
                        }
                    }

                    //Xử lý sự kiện autologin
                    if(messFromClientArray[0].equals("autologin")){
                        String accountID = messFromClientArray[1];
                        String uuid = messFromClientArray[2];
                        if(uuid.equals(new DBAccounts().getUUID(uuid)) && accountID.equals(new DBAccounts().getAccountID(accountID))){
                            //Nếu tìm được uuidDB và accountID từ DB thì so sánh với uuid và accountID của client, nếu khớp thì gửi LoginSucess cho Client
                            OutputStream os = socket.getOutputStream();
                            DataOutputStream dataStreamOut = new DataOutputStream(os);
                            dataStreamOut.writeUTF("LoginSuccess");
                            //Cập nhật thời gian autologin
                            //Lấy thời gian hiện tại
                            LocalDateTime datetimeNow = LocalDateTime.now();
                            //Tạo định dạng datetime
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            //Chuyển datetimeNow thành String
                            String datetime = datetimeNow.format(formatter);
                            //Cập nhật thời gian đăng nhập mới vào DB
                            new DBAccounts().updateDatetimeUUID(accountID, uuid, datetime);

                        }else{
                            //Nếu không tìm được uuidDB thi gửi LoginFailed cho Client
                            OutputStream os = socket.getOutputStream();
                            DataOutputStream dataStreamOut = new DataOutputStream(os);
                            dataStreamOut.writeUTF("LoginFailed");

                        }
                    }

                    //Xử lý sự kiện Account
                    if(messFromClientArray[0].equals("account")){
                        String accountID = messFromClientArray[1];
                        Account account = new DBAccounts().getAccount(accountID);
                        ObjectMapper Obj = new ObjectMapper();
                        String jsonStr = Obj.writeValueAsString(account);
                        OutputStream os = socket.getOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(os);
                        dataOutputStream.writeUTF(jsonStr);
                        System.out.println(account);
                    }

                    //Xử lý sự kiện Edit Profile
                    if(messFromClientArray[0].equals("editprofile")){
                        String accountID = messFromClientArray[1];
                        String s = messFromClientArray[2];
                        boolean b = new DBAccounts().updateAccountInfomation(accountID, s);
                        if(b) {
                            OutputStream os = socket.getOutputStream();
                            DataOutputStream dataOutputStream = new DataOutputStream(os);
                            dataOutputStream.writeUTF("editprofilesuccess");
                        }
                    }

                    //Xử lý sự kiện Transfer Money
                    if(messFromClientArray[0].equals("transfermoney")){
                        String accountID = messFromClientArray[1];
                        String accountID2 = messFromClientArray[2];
                        BigDecimal moneyTransfer = new BigDecimal(messFromClientArray[3]);
                        OutputStream os = socket.getOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(os);
                        if(!accountID2.equals(new DBAccounts().getAccountID(accountID2))){
                            dataOutputStream.writeUTF("transfermoneyWrongID");
                        }else {
                            Account account = new DBAccounts().getAccount(accountID);
                            Account account2 = new DBAccounts().getAccount(accountID2);
                            if (account.getAccountBalance().compareTo(moneyTransfer) >= 0) {
                                account.setAccountBalance(account.getAccountBalance().subtract(moneyTransfer));
                                account2.setAccountBalance(account2.getAccountBalance().add(moneyTransfer));
                                boolean b = new DBAccounts().updateAccountBalance(accountID, account.getAccountBalance());
                                boolean b2 = new DBAccounts().updateAccountBalance(accountID2, account2.getAccountBalance());
                                if (b && b2) {
                                    ObjectMapper Obj = new ObjectMapper();
                                    String jsonStr = Obj.writeValueAsString(account);
                                    dataOutputStream.writeUTF("transfermoneysuccess#" + jsonStr);
                                }
                            }
                        }
                    }

                    //Xử lý sự kiện checkAccountID
                    if(messFromClientArray[0].equals("checkAccountID")){
                        String accountID = messFromClientArray[1];
                        OutputStream os = socket.getOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(os);
                        if(accountID.equals(new DBAccounts().getAccountID(accountID))){
                            dataOutputStream.writeUTF("AccountIDExisted");
                            System.out.println("check accountID failed");
                        }else {
                            dataOutputStream.writeUTF("AccountIDValid");
                            System.out.println("check accountID success");
                        }
                    }

                    System.out.println("Socket closed");
                    socket.close();
                } catch (IOException e) {
                    System.err.println(" Connection Error: " + e);
                } catch (IllegalArgumentException e2){
                    System.out.println("end");
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

}
