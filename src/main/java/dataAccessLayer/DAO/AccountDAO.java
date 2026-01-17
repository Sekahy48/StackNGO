package dataAccessLayer.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import creational.DTOFactory;
import dataTransportLayer.AccountDTO;
import domain.accounts.Account;

public class AccountDAO extends AbstractDAO<AccountDTO, Account> {

    private final Connection connection = DBManager.getConnection();

    @Override
    public boolean create(Account account, int[] foreignKeys) {
        String sql = "INSERT INTO accounts (id, name, password, salt, type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, account.getId().value());
            stmt.setString(2, account.getUsername());
            stmt.setBytes(3, account.getHashedPassword()); // hash + salt combinados
            stmt.setBytes(4, account.getSalt().getValue());
            stmt.setString(5, account.getType().name());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(Account account, int id) {
        String sql = "UPDATE accounts SET name = ?, password = ?, type = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, account.getUsername());
            stmt.setBytes(2, account.getHashedPassword());
            stmt.setString(3, account.getType().name());
            stmt.setInt(4, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AccountDTO read(int thisId) {
        String sqlAccount = "SELECT id, name, type, password, salt FROM accounts WHERE id = ?";
        String sqlCollections = "SELECT id FROM collections WHERE account_id = ?";
        try (PreparedStatement stmtAccount = connection.prepareStatement(sqlAccount);
             PreparedStatement stmtCollections = connection.prepareStatement(sqlCollections)) {

            // Leer cuenta
            stmtAccount.setInt(1, thisId);
            ResultSet rsAccount = stmtAccount.executeQuery();
            if (!rsAccount.next()) return null;

            int id = rsAccount.getInt("id");
            String name = rsAccount.getString("name");
            String type = rsAccount.getString("type");

            byte[] hashedPass = rsAccount.getBytes("password");
            byte[] saltValue = rsAccount.getBytes("salt");
            // Leer colecciones
            stmtCollections.setInt(1, id);
            ResultSet rsCollections = stmtCollections.executeQuery();
            List<Integer> collections = new ArrayList<>();
            while (rsCollections.next()) {
                collections.add(rsCollections.getInt("id"));
            }

            return DTOFactory.account(name, collections, type, id, hashedPass, saltValue);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public AccountDTO read(String searchName) {
        String sqlAccount = "SELECT id, name, type, password, salt FROM accounts WHERE name = ?";
        String sqlCollections = "SELECT id FROM collections WHERE account_id = ?";
        try (PreparedStatement stmtAccount = connection.prepareStatement(sqlAccount);
             PreparedStatement stmtCollections = connection.prepareStatement(sqlCollections)) {

            // Leer cuenta
            stmtAccount.setString(1, searchName);
            ResultSet rsAccount = stmtAccount.executeQuery();
            if (!rsAccount.next()) return null;

            String name = rsAccount.getString("name");
            String type = rsAccount.getString("type");
            int id = rsAccount.getInt("id");
            byte[] hashedPass = rsAccount.getBytes("password");
            byte[] saltValue = rsAccount.getBytes("salt");
            // Leer colecciones
            stmtCollections.setInt(1, id);
            ResultSet rsCollections = stmtCollections.executeQuery();
            List<Integer> collections = new ArrayList<>();
            while (rsCollections.next()) {
                collections.add(rsCollections.getInt("id"));
            }
           return DTOFactory.account(name, collections, type, id, hashedPass, saltValue);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AccountDTO> readAll(int ignoredAccountId) {
        String sqlAccounts = "SELECT id, name, type, password, salt FROM accounts";
        String sqlCollections = "SELECT id FROM collections WHERE account_id = ?";
        List<AccountDTO> out = new ArrayList<>();

        try (PreparedStatement stmtAccounts = connection.prepareStatement(sqlAccounts);
             PreparedStatement stmtCollections = connection.prepareStatement(sqlCollections)) {

            ResultSet rsAccounts = stmtAccounts.executeQuery();
            while (rsAccounts.next()) {
                int accountId = rsAccounts.getInt("id");
                String name = rsAccounts.getString("name");
                String type = rsAccounts.getString("type");
                byte[] hashedPass = rsAccounts.getBytes("password");
                byte[] saltValue = rsAccounts.getBytes("salt");
                // Leer colecciones de la cuenta
                stmtCollections.setInt(1, accountId);
                ResultSet rsColl = stmtCollections.executeQuery();
                List<Integer> collections = new ArrayList<>();
                while (rsColl.next()) {
                    collections.add(rsColl.getInt("id"));
                }

                out.add(DTOFactory.account(name, collections, type, accountId, hashedPass, saltValue));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }
}
