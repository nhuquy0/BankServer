import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

public class DBAccounts {
    Connection conn = null;
    Statement stmt = null;
    private ArrayList<Account> accountArrayList;
    public DBAccounts() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quydata?" +
                    "user=root&password=Thedead1Z");
            // crate statement
            stmt = conn.createStatement();
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    //Thêm record
    public boolean insertAccount(Account account){
        String sql = "INSERT INTO accounts(accountID,passbcrypt,accountName,firstName,lastName,address,city,country,phoneNumber,email) "
                + "VALUES(" + "'" + account.getAccountID() + "'," + "'" + account.getPassword() + "'," +  "'" + account.getAccountName() + "'," + "'" + account.getFirstName() + "'," + "'" + account.getLastName() + "'," + "'" + account.getAddress() + "'," + "'" + account.getCity() + "'," + "'" + account.getCountry() + "'," + "'" + account.getPhoneNumber() + "'," + "'" + account.getEmail() + "')";
        try{
        stmt.executeUpdate(sql);
        conn.close();
        return true;
        }catch (SQLException ex) {
            // handle any errors
            System.out.println( "SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }
    }

    public void insertUUID(String accountID, String uuid){
        String sql = "INSERT INTO uuids(UUID, accountID) "
                + "select " + "'" + uuid + "'" + ", "  + "accountID" +
        " from accounts " + "where accountID = " + "'" + accountID + "'" + " limit 1";
        try{
            stmt.executeUpdate(sql);
            conn.close();

        }catch (SQLException ex) {
            // handle any errors
            System.out.println( "SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    //Nhập vào column cần tìm, nhập vào string cần tìm
    public ArrayList searchAccount(String accountID, String accountName, String firstName, String lastName,
            String address, String city, String country, String phoneNumber){
        accountArrayList = new ArrayList<>();
        try {
            // get data from table 'accounts' for condition
            ResultSet rs = stmt.executeQuery("select * from accounts WHERE "+
                    "accountID" + " LIKE '%" + accountID + "%'" + " and " +
                    "accountName" + " LIKE '%" + accountName + "%'" + " and " +
                    "firstName" + " LIKE '%" + firstName + "%'" + " and " +
                    "lastName" + " LIKE '%" + lastName + "%'" + " and " +
                    "address" + " LIKE '%" + address + "%'" + " and " +
                    "city" + " LIKE '%" + city + "%'" + " and " +
                    "country" + " LIKE '%" + country + "%'" + " and " +
                    "phoneNumber" + " LIKE '%" + phoneNumber + "%'"
            );
            // show data
            while (rs.next()) {
                Account account = new Account();
                account.setAccountID(rs.getString(1));
                account.setAccountName(rs.getString(2));
                String col3 = String.format("%.0f", rs.getBigDecimal(3));
                account.setAccountBalance(new BigDecimal(col3));
                account.setFirstName(rs.getString(4));
                account.setLastName(rs.getString(5));
                account.setAddress(rs.getString(6));
                account.setCity(rs.getString(7));
                account.setCountry(rs.getString(8));
                account.setPhoneNumber(rs.getString(9));
                account.setEmail(rs.getString(10));
                accountArrayList.add(account);
                System.out.println(rs.getString(1) + "\t" + rs.getString(2)
                        + "\t" + rs.getBigDecimal(3)+ "\t" + rs.getString(4)
                        + "\t" + rs.getString(5)+ "\t" + rs.getString(6)
                        + "\t" + rs.getString(7)+ "\t" + rs.getString(8)
                        + "\t" + rs.getString(9)+ "\t" + rs.getString(10));

            }
            conn.close();
        }catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return accountArrayList;
    }

    public Account getAccount(String accountID) {
        Account account = null;
        try {
            // get accountBalance from table 'accounts'
            ResultSet rs = stmt.executeQuery("select * from accounts WHERE " +
                    "accountID" + " = '" + accountID + "'"
            );
            while (rs.next()) {
                account = new Account();
                account.setAccountID(rs.getString(1));
                account.setAccountName(rs.getString(2));
                String col3 = String.format("%.0f", rs.getBigDecimal(3));
                account.setAccountBalance(new BigDecimal(col3));
                account.setFirstName(rs.getString(4));
                account.setLastName(rs.getString(5));
                account.setAddress(rs.getString(6));
                account.setCity(rs.getString(7));
                account.setCountry(rs.getString(8));
                account.setPhoneNumber(rs.getString(9));
                account.setEmail(rs.getString(10));
                System.out.println(rs.getString(1) + "\t" + rs.getString(2)
                        + "\t" + rs.getBigDecimal(3) + "\t" + rs.getString(4)
                        + "\t" + rs.getString(5) + "\t" + rs.getString(6)
                        + "\t" + rs.getString(7) + "\t" + rs.getString(8)
                        + "\t" + rs.getString(9) + "\t" + rs.getString(10));
            }

            conn.close();
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return account;
    }

    public String getAccountID(String accountID){
        String subAccountID = "";
        try{
            // get accountBalance from table 'accounts'
            ResultSet rs = stmt.executeQuery("select accountID from accounts WHERE "+
                    "accountID" + " = '" + accountID + "'"
            );
            while (rs.next()) {
                subAccountID = rs.getString(1);
            }

            conn.close();
        }catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return subAccountID;
    }

    public String getPassword(String accountID){
        String password = "";
        try{
            // get accountBalance from table 'accounts'
            ResultSet rs = stmt.executeQuery("select passbcrypt from accounts WHERE "+
                    "accountID" + " = '" + accountID + "'"
            );
            while (rs.next()) {
                password = rs.getString(1);
            }

            conn.close();
        }catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return password;
    }

    public String getUUID(String uuid){
        String uuidDB = "";
        try{
            // get accountBalance from table 'accounts'
            ResultSet rs = stmt.executeQuery("select UUID from uuids WHERE "+
                    "UUID" + " = '" + uuid + "'"
            );
            while (rs.next()) {
                uuidDB = rs.getString(1);
            }

            conn.close();
        }catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return uuidDB;
    }

    public ArrayList<String> getDatetimeAndUUID(){
        ArrayList<String> datetimeAndUUIDArrayList = new ArrayList<>();
        try{
            // get accountBalance from table 'accounts'
            ResultSet rs = stmt.executeQuery("select uuid, datetime from uuids"
            );
            while (rs.next()) {
                datetimeAndUUIDArrayList.add(rs.getString(1));
                datetimeAndUUIDArrayList.add(rs.getString(2));
            }

            conn.close();
        }catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return datetimeAndUUIDArrayList;
    }

    //Get AccountBalance
    public BigDecimal getAccountBalance(String accountID){

        BigDecimal accountBalance = null;
        try{
            // get accountBalance from table 'accounts'
            ResultSet rs = stmt.executeQuery("select * from accounts WHERE "+
                    "accountID" + " LIKE '%" + accountID + "%'"
            );
            while (rs.next()) {
                String col3 = String.format("%.0f", rs.getBigDecimal(3));
                accountBalance = new BigDecimal(col3);
//                accountBalance = rs.getBigDecimal(3);
            }

            conn.close();
        }catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return accountBalance;
    }

    //Update AccountBalance
    public boolean updateAccountBalance(String accountID, BigDecimal accountBalance){
            String sql = "UPDATE accounts " +
                    "SET accountBalance = " + accountBalance +
                    " WHERE accountID = " + "'" + accountID + "'";
            try {
                stmt.executeUpdate(sql);
                conn.close();
                return true;
            }catch (SQLException ex) {
                // handle any errors
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                return false;
            }
    }

    //Cập nhật datetime
    public void updateDatetimeUUID(String accountID, String uuid, String datetime){
        String sql = "UPDATE uuids " +
                "SET datetime = " + "'" + datetime + "'" +
                " WHERE accountID = " + "'" + accountID + "'" + " and " + "UUID = " + "'" + uuid + "'";
        try {
            stmt.executeUpdate(sql);
            conn.close();
        }catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public boolean updateAccountInfomation(String accountID, String s) {

        String sql = "UPDATE accounts " +
                "SET " + s +
                "WHERE accountID = " + "'" + accountID + "'";
        try {
            stmt.executeUpdate(sql);
            conn.close();
            return true;
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }
    }

    //Xoá UUID hết hạn autologin
    public boolean deleteUUID(String uuid){
        String sql = "DELETE FROM uuids " +
                "WHERE UUID = " + "'" + uuid + "'";
        try {
            stmt.executeUpdate(sql);
            conn.close();
            return true;
        }catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }
    }
}
