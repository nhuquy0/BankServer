import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;

public class mainFrame extends JFrame{
    private JPanel mainPanel;
    private JLabel lblName;
    private JTabbedPane tabbedPane1;
    private JPanel tabCreate;
    private JPanel tabSearch;
    private JTextField txtAccountIDCreate;
    private JPasswordField txtPasswordCreate;
    private JTextField txtFirstNameCreate;
    private JTextField txtLastNameCreate;
    private JTextField txtAddressCreate;
    private JTextField txtPhoneNumberCreate;
    private JTextField txtEmailCreate;
    private JButton btnInsert;
    private JTextField txtAccountIDSearch;
    private JTextField txtAccountNameSearch;
    private JTextField txtFirstNameSearch;
    private JTextField txtLastNameSearch;
    private JTextField txtAddressSearch;
    private JTextField txtPhoneNumberSearch;
    private JButton btnSearch;
    private JLabel lblAccountID;
    private JLabel lblAccountName;
    private JLabel lblAddress;
    private JLabel lblPhoneNumber;
    private JLabel lblAccountBalance;
    private JLabel lblEmail;
    private JButton btnRecharge;
    private JButton btnWithdrawMoney;
    private JButton btnTransferMoney;
    private JButton btnEditProfile;
    private JButton btnClearSearch;
    private JButton btnClearCreate;
    private JLabel lblStatusCreate;
    private JList<String> jListAccount;
    private JLabel lblStatusInteract;
    private JComboBox<String> cbbCityCreate;
    private JComboBox<String> cbbCountryCreate;
    private JComboBox<String> cbbCitySearch;
    private JComboBox<String> cbbCountrySearch;

    ArrayList accountsArrayList;
    Account account = null;

    public mainFrame(){
        setContentPane(mainPanel);
        setTitle("App");
        setSize(1440, 400);// thiết lập kích thước cho của sổ
        txtPasswordCreate.setEchoChar('*');
        btnRecharge.setEnabled(false);
        btnEditProfile.setEnabled(false);
        btnWithdrawMoney.setEnabled(false);
        btnTransferMoney.setEnabled(false);
        String[] city = {"","Ho Chi Minh","Da Nang","Ha Noi","Ca Mau"};
        for (String s : city) {
            cbbCityCreate.addItem(s);
            cbbCitySearch.addItem(s);
        }
        String[] country = {"","Viet Nam", "Singapore", "Thailand","China","Indonesia"};
        for (String s : country) {
            cbbCountryCreate.addItem(s);
            cbbCountrySearch.addItem(s);
        }
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);// hiển thị cửa sổ


        btnInsert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String accountID = txtAccountIDCreate.getText();
                String dbAccountID = new DBAccounts().getAccountID(accountID);
                if(accountID.equals(dbAccountID)){
                    lblStatusCreate.setForeground(Color.RED);
                    lblStatusCreate.setText("accountID existsed! ");
                    return;
                }
                String password = null;
                if(!txtPasswordCreate.getPassword().equals("")) {
                    password = BCrypt.hashpw(String.valueOf(txtPasswordCreate.getPassword()), BCrypt.gensalt(12));
                }
                String firstName = txtFirstNameCreate.getText();
                String lastName = txtLastNameCreate.getText();
                String address = txtAddressCreate.getText();
                String city = (String) cbbCityCreate.getSelectedItem();
                String country = (String) cbbCountryCreate.getSelectedItem();
                String phoneNumber = txtPhoneNumberCreate.getText();
                String email = txtEmailCreate.getText();
                if(accountID.equals("") || firstName.equals("") || lastName.equals("") || address.equals("") || city.equals("") || country.equals("") || phoneNumber.equals("")) {
                    lblStatusCreate.setForeground(Color.RED);
                    lblStatusCreate.setText("Please input information is * ");
                    return;
                }
                Account account = new Account(accountID, password, firstName, lastName, address, city, country, phoneNumber, email);

                new DBAccounts().insertAccount(account);
                lblStatusCreate.setForeground(Color.BLUE);
                lblStatusCreate.setText("Created account successful");
            }
        });

        btnEditProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField txtFirstName = new JTextField();
                txtFirstName.setText(account.getFirstName());
                JTextField txtLastName = new JTextField();
                txtLastName.setText(account.getLastName());
                JTextField txtAddress = new JTextField();
                txtAddress.setText(account.getAddress());
                JTextField txtPhoneNumber = new JTextField();
                txtPhoneNumber.setText(account.getPhoneNumber());
                JTextField txtEmail = new JTextField();
                txtEmail.setText(account.getEmail());
                JComboBox<String> cbbCityEdit = cbbCityCreate;
                JComboBox<String> cbbCountryEdit = cbbCountryCreate;

                JPanel panel = new JPanel(new GridLayout(0, 2));

                panel.add(new JLabel("AccountID:"));
                panel.add(new JLabel(account.getAccountID()));
                panel.add(new JLabel("Account Name:"));
                panel.add(new JLabel(account.getAccountName()));
                panel.add(new JLabel("First Name *:"));
                panel.add(txtFirstName);
                panel.add(new JLabel("Last Name *:"));
                panel.add(txtLastName);
                panel.add(new JLabel("Address *:"));
                panel.add(txtAddress);
                panel.add(new JLabel("City *:"));
                cbbCityEdit.setSelectedItem(account.getCity());
                panel.add(cbbCityEdit);
                panel.add(new JLabel("Country *:"));
                cbbCountryEdit.setSelectedItem(account.getCountry());
                panel.add(cbbCountryEdit);
                panel.add(new JLabel("Phone Number *:"));
                panel.add(txtPhoneNumber);
                panel.add(new JLabel("Email:"));
                panel.add(txtEmail);
                int result = JOptionPane.showConfirmDialog(null, panel, "Edit Profile",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String firstName = txtFirstName.getText();
                    String lastName = txtLastName.getText();
                    String address = txtAddress.getText();
                    String phoneNumber = txtPhoneNumber.getText();
                    String email = txtEmail.getText();
                    String city = (String) cbbCityEdit.getSelectedItem();
                    String country = (String) cbbCountryEdit.getSelectedItem();
                    if(firstName.equals("") || lastName.equals("") || address.equals("") || city.equals("") || country.equals("") || phoneNumber.equals("")){
                        lblStatusInteract.setForeground(Color.RED);
                        lblStatusInteract.setText("marked * fields cannot be empty");
                        return;
                    }

                    if(!firstName.equals(account.getFirstName()) || !lastName.equals(account.getLastName()) || !address.equals(account.getAddress()) || !city.equals(account.getCity()) || !country.equals(account.getCountry()) || !phoneNumber.equals(account.getPhoneNumber()) || !email.equals(account.getEmail())){
                        //Tạo chuỗi phụ để thêm vào chuỗi truy vấn chính
                        //Nếu các trường nhập vào khác account đã load thì thêm vào chuỗi cập nhật vào DB
                        String s = "";
                        if(!firstName.equals(account.getFirstName())){
                            s = s + "firstName = " + "'" + firstName + "', ";
                            account.setFirstName(firstName);
                        }
//
                        if(!lastName.equals(account.getLastName())){
                            s = s + "lastName = " + "'" + lastName + "', ";
                            account.setLastName(lastName);
                        }

                        if(!address.equals(account.getAddress())){
                            s = s + "address = " + "'" + address + "', ";
                            account.setAddress(address);
                        }

                        if(!city.equals(account.getCity())){
                            s = s + "city = " + "'" + city + "', ";
                            account.setCity(city);
                        }

                        if(!country.equals(account.getCountry())){
                            s = s + "country = " + "'" + country + "', ";
                            account.setCountry(country);
                        }

                        if(!phoneNumber.equals(account.getPhoneNumber())){
                            s = s + "phoneNumber = " + "'" + phoneNumber + "', ";
                            account.setPhoneNumber(phoneNumber);
                        }

                        if(!email.equals(account.getEmail())){
                            s = s + "email = " + "'" + email + "', ";
                            account.setEmail(email);
                        }

                        //Bỏ đi 2 ký tự cuối là ", "
                        if(s.length()>=2) {
                            s = s.substring(0, s.length() - 2);
                        }
                        System.out.println(s);

                        boolean b = new DBAccounts().updateAccountInfomation(account.getAccountID(),s);
                        if(b) {
                            lblAccountID.setText(account.getAccountID());
                            lblAccountName.setText(account.getAccountName());
                            lblAddress.setText(account.getAddress() + ", " + account.getCity() + ", " + account.getCountry());
                            lblPhoneNumber.setText(String.valueOf(account.getPhoneNumber()));
                            lblAccountBalance.setText(String.valueOf(account.getAccountBalance()));
                            lblEmail.setText(account.getEmail());
                        }
                    }
                }
            }
        });

        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String accountID = txtAccountIDSearch.getText();
                String accountName = txtAccountNameSearch.getText();
                String firstName = txtFirstNameSearch.getText();
                String lastName = txtLastNameSearch.getText();
                String address = txtAddressSearch.getText();
                String city = (String) cbbCitySearch.getSelectedItem();
                String country = (String) cbbCountrySearch.getSelectedItem();
                String phoneNumber = txtPhoneNumberSearch.getText();
                if(accountID.equals("") && accountName.equals("") && firstName.equals("") && lastName.equals("") && address.equals("") && city.equals("") && country.equals("") && phoneNumber.equals("")) {
                    return;
                }
                if(accountID.equals("") && accountName.equals("") && phoneNumber.equals("")){
                    return;
                }
                accountsArrayList = new DBAccounts().searchAccount(accountID, accountName, firstName, lastName, address, city, country, phoneNumber);
                //Transfer ArrayList to String[] used in Jlist
                String[] strAccountsArray = new String[accountsArrayList.size()];
                if (strAccountsArray.length > 0) {
                    for (int i = 0; i < accountsArrayList.size(); i++) {
                        strAccountsArray[i] = accountsArrayList.get(i).toString();
                    }
                    jListAccount.setListData(strAccountsArray);
                }
            }
        });

        //Double-click to show information on label
        jListAccount.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (accountsArrayList != null) {
                    JList list = (JList) e.getSource();
                    if (e.getClickCount() == 2) {
                        // Double-click detected
                        int index = list.locationToIndex(e.getPoint());
                        account = (Account) accountsArrayList.get(index);
                        if (account != null) {
                            lblAccountID.setText(account.getAccountID());
                            lblAccountName.setText(account.getAccountName());
                            lblAddress.setText(account.getAddress() + ", " + account.getCity() + ", " + account.getCountry());
                            lblPhoneNumber.setText(String.valueOf(account.getPhoneNumber()));
                            lblAccountBalance.setText(String.valueOf(account.getAccountBalance()));
                            lblEmail.setText(account.getEmail());

                            btnRecharge.setEnabled(true);
                            btnEditProfile.setEnabled(true);
                            btnWithdrawMoney.setEnabled(true);
                            btnTransferMoney.setEnabled(true);
                        }
                    }
                }
            }
        });

        btnRecharge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BigDecimal tenThousand = new BigDecimal("10000.0");
                String value = JOptionPane.showInputDialog("Input money recharge:");
                if(value != null) {
                    BigDecimal moneyRecharge = new BigDecimal(value);
                    if(moneyRecharge.compareTo(tenThousand)>=0) {
                        System.out.println(account.getAccountID());
                        BigDecimal accountBalance = new DBAccounts().getAccountBalance(account.getAccountID());
                        accountBalance = accountBalance.add(moneyRecharge);
                        boolean b = new DBAccounts().updateAccountBalance(account.getAccountID(), accountBalance);
                        if(b) {
                            lblAccountBalance.setText(String.valueOf(accountBalance));
                            lblStatusInteract.setForeground(Color.BLUE);
                            lblStatusInteract.setText("You have successfully recharge " + moneyRecharge + " VNĐ in accountID: " + account.getAccountID() + "; accountName: " + account.getAccountName() + ".");
                        }
                    }else{
                        lblStatusInteract.setForeground(Color.RED);
                        lblStatusInteract.setText("Money withdraw >= 10000");
                    }
                }
            }
        });

        btnWithdrawMoney.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BigDecimal tenThousand = new BigDecimal("10000.0");
                String value = JOptionPane.showInputDialog("Input money withdraw:");
                if(value != null) {
                    BigDecimal moneyWithdraw = new BigDecimal(value);
                    if(moneyWithdraw.compareTo(tenThousand)>=0) {
                        BigDecimal accountBalance = new DBAccounts().getAccountBalance(account.getAccountID());
                        if(accountBalance.compareTo(moneyWithdraw) >= 0) {
                            accountBalance = accountBalance.subtract(moneyWithdraw);
                            boolean b = new DBAccounts().updateAccountBalance(account.getAccountID(), accountBalance);
                            if(b) {
                                lblAccountBalance.setText(String.valueOf(accountBalance));
                                lblStatusInteract.setForeground(Color.BLUE);
                                lblStatusInteract.setText("You have successfully withdraw " + moneyWithdraw + " VNĐ in accountID: " + account.getAccountID() + "; accountName: " + account.getAccountName() + ".");
                            }
                        }else{
                            lblStatusInteract.setForeground(Color.RED);
                            lblStatusInteract.setText("The account does not have enough money");
                        }
                    }else{
                        lblStatusInteract.setForeground(Color.RED);
                        lblStatusInteract.setText("Money withdraw >= 10000");
                    }
                }
            }
        });

        btnTransferMoney.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BigDecimal tenThousand = new BigDecimal("10000.0");

                JTextField txtAccountID = new JTextField();
                JTextField txtMoneyTransfer = new JTextField();
                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("AccountID:"));
                panel.add(txtAccountID);
                panel.add(new JLabel("Input money transfer:"));
                panel.add(txtMoneyTransfer);
                int result = JOptionPane.showConfirmDialog(null, panel, "Test",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String accountID = txtAccountID.getText();
                    String value = txtMoneyTransfer.getText();
                    if(!accountID.equals("") && !value.equals("")) {
                        BigDecimal moneyTransfer = new BigDecimal(value);
                        if (moneyTransfer.compareTo(tenThousand) >= 0) {
                            BigDecimal accountBalance1 = new DBAccounts().getAccountBalance(account.getAccountID());
                            if(accountBalance1.compareTo(moneyTransfer) >= 0) {
                                BigDecimal accountBalance2 = new DBAccounts().getAccountBalance(accountID);
                                accountBalance1 = accountBalance1.subtract(moneyTransfer);
                                accountBalance2 = accountBalance2.add(moneyTransfer);
                                boolean b = new DBAccounts().updateAccountBalance(account.getAccountID(), accountBalance1);
                                boolean b1 = new DBAccounts().updateAccountBalance(accountID, accountBalance2);
                                if(b && b1) {
                                    lblAccountBalance.setText(String.valueOf(accountBalance1));
                                    lblStatusInteract.setForeground(Color.BLUE);
                                    lblStatusInteract.setText("You have successfully transfer " + moneyTransfer + " VNĐ to accountID: " + accountID + ".");
                                }
                            }else{
                                lblStatusInteract.setForeground(Color.RED);
                                lblStatusInteract.setText("The account does not have enough money");
                            }
                        } else {
                            lblStatusInteract.setForeground(Color.RED);
                            lblStatusInteract.setText("Money transfer >= 10000");
                        }
                    }else{
                        lblStatusInteract.setForeground(Color.RED);
                        lblStatusInteract.setText("Field can't empty");
                    }
                }
            }
        });

        btnClearCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtFirstNameCreate.setText("");
                txtLastNameCreate.setText("");
                txtAddressCreate.setText("");
                cbbCityCreate.setSelectedIndex(0);
                cbbCountryCreate.setSelectedIndex(0);
                txtPhoneNumberCreate.setText("");
                txtEmailCreate.setText("");
                lblStatusCreate.setForeground(Color.BLACK);
                lblStatusCreate.setText("Waiting for notification...");
            }
        });

        btnClearSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtAccountNameSearch.setText("");
                txtFirstNameSearch.setText("");
                txtLastNameSearch.setText("");
                txtAddressSearch.setText("");
                cbbCitySearch.setSelectedIndex(0);
                cbbCountrySearch.setSelectedIndex(0);
                txtPhoneNumberSearch.setText("");

                lblAccountID.setText("");
                lblAccountName.setText("");
                lblAddress.setText("");
                lblPhoneNumber.setText("");
                lblAccountBalance.setText("");
                lblEmail.setText("");
                lblStatusInteract.setForeground(Color.BLACK);
                lblStatusInteract.setText("Please input least one of the fields is maked * ");

                account = null;
                accountsArrayList = null;
                jListAccount.setListData(new String[0]);

                btnRecharge.setEnabled(false);
                btnEditProfile.setEnabled(false);
                btnWithdrawMoney.setEnabled(false);
                btnTransferMoney.setEnabled(false);
            }
        });
    }

    public void reloadJlistAndAccount(){

    }

    public static void main(String[] args) {
        mainFrame gui = new mainFrame();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
