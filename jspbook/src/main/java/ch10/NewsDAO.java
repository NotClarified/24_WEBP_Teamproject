package ch10;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class NewsDAO {
    // JDBC 드라이버와 URL 설정
    String jdbc_driver = "com.mysql.cj.jdbc.Driver";
    String jdbc_url = "jdbc:mysql://localhost/jspdb?allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";

    // 데이터베이스 연결 열기
    public Connection open() {
        Connection conn = null;
        try {
            // JDBC 드라이버 로드
            Class.forName(jdbc_driver);
            conn = DriverManager.getConnection(jdbc_url, "root", "1234");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    // 모든 뉴스를 가져오는 함수
    public List<News> getAll() throws Exception {
        List<News> newsList = new ArrayList<>();
        String sql = "SELECT * FROM news";

        // 로깅
        Logger logger = Logger.getLogger(NewsDAO.class.getName());

        try (Connection conn = open();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                News n = new News();
                n.setAid(rs.getInt("aid"));
                n.setTitle(rs.getString("title"));
                n.setDate(rs.getString("date")); // cdate -> date로 변경
                newsList.add(n);
            }

            if (newsList.isEmpty()) {
                logger.info("No news found in the database.");
            } else {
                logger.info("News list retrieved with " + newsList.size() + " items.");
            }
        }
        return newsList;
    }

    // 특정 뉴스를 ID로 가져오는 함수
    public News getNews(int aid) throws SQLException {
        String sql = "SELECT * FROM news WHERE aid = ?";
        News n = new News();

        try (Connection conn = open();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, aid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    n.setAid(rs.getInt("aid"));
                    n.setTitle(rs.getString("title"));
                    n.setImg(rs.getString("img"));
                    n.setDate(rs.getString("date"));
                    n.setContent(rs.getString("content"));
                }
            }
        }
        return n;
    }

    // 새로운 뉴스를 추가하는 함수
    public void addNews(News n) throws Exception {
        String sql = "INSERT INTO news (title, img, content) VALUES (?, ?, ?)";

        try (Connection conn = open();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, n.getTitle());
            pstmt.setString(2, n.getImg());
            pstmt.setString(3, n.getContent());
            pstmt.executeUpdate();
        }
    }

    // 뉴스를 삭제하는 함수
    public void delNews(int aid) throws SQLException {
        String sql = "DELETE FROM news WHERE aid = ?";

        try (Connection conn = open();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, aid);
            pstmt.executeUpdate();
        }
    }

    // 뉴스를 업데이트하는 함수
    public void updateNews(News n) throws Exception {
        String sql = "UPDATE news SET title = ?, img = ?, content = ? WHERE aid = ?";

        try (Connection conn = open();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, n.getTitle());
            pstmt.setString(2, n.getImg());
            pstmt.setString(3, n.getContent());
            pstmt.setInt(4, n.getAid());
            pstmt.executeUpdate();
        }
    }
}
