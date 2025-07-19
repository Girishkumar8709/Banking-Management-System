package BankingManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    private Connection connection;
    private Scanner scanner;

    AccountManager(Connection connection,Scanner scanner){
        this.connection= connection;
        this.scanner=scanner;
    }

    public void credit_money(long account_number)throws SQLException{
        scanner.nextLine();
        System.out.print("Enter Amount :");
        double amount= scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security pin :");
        String security_pin=scanner.nextLine();

        try{
            connection.setAutoCommit(false);
            if(account_number!= 0){
                String query="select * from accounts where account_number = ? and security_pin = ?";
                PreparedStatement preparedStatement=connection.prepareStatement(query);
                preparedStatement.setLong(1,account_number);
                preparedStatement.setString(2,security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    String credit_query="update accounts set balance =  balance + ? where account_number = ?";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(credit_query);
                    preparedStatement1.setDouble(1,amount);
                    preparedStatement1.setLong(2,account_number);

                    int rowsAffected = preparedStatement1.executeUpdate();
                    if(rowsAffected > 0){
                        System.out.println("Rs."+amount+"credited Successfully");
                        connection.commit();
                        connection.setAutoCommit(true);
                        return;
                    }else {
                        System.out.println("Transaction Failed!!");
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                }else {
                    System.out.println("Invalid Security pin!!!");
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }


    public void debit_money(long account_number) throws SQLException{
        scanner.nextLine();
        System.out.print("Enter Amount :");
        double amount= scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter you security pin :");
        String security_pin=scanner.nextLine();

        try{
            connection.setAutoCommit(false);
            if(account_number!=0){
                String query="select * from accounts where account_number = ? and security_pin = ?";
                PreparedStatement preparedStatement=connection.prepareStatement(query);
                preparedStatement.setLong(1,account_number);
                preparedStatement.setString(2,security_pin);
                ResultSet resultSet=preparedStatement.executeQuery();

                if(resultSet.next()){
                    double current_balance=resultSet.getDouble("balance");
                    if(amount<=current_balance){
                        String debit_query="update accounts set balance = balance - ? where account_number = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(debit_query);
                        preparedStatement1.setDouble(1,amount);
                        preparedStatement1.setLong(2,account_number);
                        int rowsAffected=preparedStatement1.executeUpdate();

                        if(rowsAffected>0){
                            System.out.println("RS."+amount+"debited Successfully!!");
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        }else {
                            System.out.println("Transition Failed!!");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }

                    }else {
                        System.out.println("Insufficient Balance!!");
                    }
                } else {
                    System.out.println("Invalid Pin");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);


    }

    public void transfer_money(long sender_account_number) throws SQLException{
        scanner.nextLine();
        System.out.print("Enter Receiver Account number :");
        long receiver_account_number=scanner.nextLong();
        scanner.nextLine();
        System.out.print("Enter Amount :");
        double amount=scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin :");
        String security_pin=scanner.nextLine();

        try{
            connection.setAutoCommit(false);
            if(sender_account_number!=0 && receiver_account_number!=0){
                String query="select * from accounts where account_number = ? and security_pin = ?";
                PreparedStatement preparedStatement=connection.prepareStatement(query);
                preparedStatement.setLong(1,sender_account_number);
                preparedStatement.setString(2,security_pin);
                ResultSet resultSet=preparedStatement.executeQuery();

                if(resultSet.next()){
                    double current_balance=resultSet.getDouble("balance");
                    if(amount<=current_balance){
                        String debit_query="update accounts set balance = balance - ? where account_number = ?";
                        String credit_query="update accounts set balance = balance + ? where account_number = ?";
                        PreparedStatement creditPrepareStatement = connection.prepareStatement(credit_query);
                        PreparedStatement debitPreapreStatement = connection.prepareStatement(debit_query);
                        creditPrepareStatement.setDouble(1,amount);
                        creditPrepareStatement.setLong(2,receiver_account_number);
                        debitPreapreStatement.setDouble(1,amount);
                        debitPreapreStatement.setLong(2,sender_account_number);

                        int rowsAffected1= debitPreapreStatement.executeUpdate();
                        int rowsAffected2= creditPrepareStatement.executeUpdate();

                        if(rowsAffected1>0 && rowsAffected2>0){
                            System.out.println("Transition Successfully!!");
                            System.out.println("RS."+amount+"Transferred Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        }else {
                            System.out.println("Transition failed");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }

                    }else {
                        System.out.println("Insufficient Balance!!");
                    }
                }else {
                    System.out.println("Invalid Security Pin!!");
                }
            }else {
                System.out.println("Invalid account number");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public  void getBalance(long account_number){
        scanner.nextLine();
        System.out.print("Enter Security pin :");
        String security_pin=scanner.nextLine();

        try{
            String query="select balance from accounts where account_number = ? and security_pin = ?";
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setLong(1,account_number);
            preparedStatement.setString(2,security_pin);
            ResultSet resultSet= preparedStatement.executeQuery();

            if(resultSet.next()){
                double balance= resultSet.getDouble("balance");
                System.out.println("Balance :"+balance);
            }else {
                System.out.println("Invalid Pin");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
