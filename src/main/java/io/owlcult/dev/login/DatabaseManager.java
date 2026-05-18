package io.owlcult.dev.login;

import com.mojang.logging.LogUtils;

import io.owlcult.dev.login.model.Player;

import java.sql.*;
import org.slf4j.Logger;

public class DatabaseManager {

    static Connection conn;
    static Logger LOGGER = LogUtils.getLogger();

    public static void init_connection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection("jdbc:sqlite:owllogin.db");

                if (conn != null) LOGGER.info("Connected to database");
                else LOGGER.warn("Connection is null");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {
        Statement st;
        try {
            st = conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("Statement created, initializing table...");

        try {
            st.execute(
                "CREATE TABLE IF NOT EXISTS users (\n" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "nickname VARCHAR(30) UNIQUE NOT NULL,\n" +
                    "password_hash VARCHAR(255) UNIQUE NOT NULL)"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("Table initialized");
    }

    public void push(Player p) {
        LOGGER.info("New push request, pushing...");

        String sql = "INSERT INTO users(nickname, password_hash) VALUES(?, ?)";

        try {
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, p.nickname);
            pst.setString(2, p.password_hash);

            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("Data pushed, all done");
    }

    public Player get(String nickname) {
        Player result = new Player();
        result.nickname = nickname;

        String sql = "SELECT password_hash FROM users WHERE nickname = ?";

        try {
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, nickname);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) result.password_hash = rs.getString(
                    "password_hash"
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
